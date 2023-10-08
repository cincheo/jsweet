/* 
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.transpiler;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.extension.AnnotationManager;
import org.jsweet.transpiler.extension.AnnotationManager.Action;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.util.DirectedGraph;
import org.jsweet.transpiler.util.Util;

import standalone.com.sun.source.tree.AssignmentTree;
import standalone.com.sun.source.tree.BlockTree;
import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.tree.ExpressionStatementTree;
import standalone.com.sun.source.tree.MemberSelectTree;
import standalone.com.sun.source.tree.MethodInvocationTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.NewClassTree;
import standalone.com.sun.source.tree.StatementTree;
import standalone.com.sun.source.tree.Tree;
import standalone.com.sun.source.tree.VariableTree;
import standalone.com.sun.source.tree.WildcardTree;
import standalone.com.sun.source.util.Trees;

/**
 * The transpiler context, which is an extension of the Java compiler context.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class JSweetContext {

    protected static Logger logger = Logger.getLogger(Java2TypeScriptTranslator.class);

    private static class AnnotationFilterDescriptor {
        public final Collection<Pattern> inclusionPatterns;
        public final Collection<Pattern> exclusionPatterns;
        public final String parameter;

        public AnnotationFilterDescriptor(Collection<Pattern> inclusionPatterns, Collection<Pattern> exclusionPatterns,
                String parameter) {
            super();
            this.inclusionPatterns = inclusionPatterns;
            this.exclusionPatterns = exclusionPatterns;
            this.parameter = parameter;
        }

        @Override
        public String toString() {
            return "FILTER" + (parameter == null ? "" : "('" + parameter + "')") + ": INCLUDES=" + inclusionPatterns
                    + ", EXCLUDES=" + exclusionPatterns;
        }
    }

    /**
     * The constant variables (variables assigned only at initialization) scanner
     * and accessor.
     * 
     * <p>
     * TODO: This scanner is run globally (for all compilation units) before
     * translation. It could be run locally to each compilation unit, except for
     * default methods that are injected in the target classes.
     */
    public ConstAnalyzer constAnalyzer = null;

    private Map<String, TypeMirror> jdkSubclasses = new HashMap<>();

    public StaticInitilializerAnalyzer referenceAnalyzer;

    /**
     * Maps the name of a class to the JDK type it extends.
     */
    public void addJdkSubclass(String subclassName, TypeMirror extendedJdkClass) {
        jdkSubclasses.put(subclassName, extendedJdkClass);
    }

    /**
     * Maps the name of a class to the JDK type it extends.
     */
    public Map<String, TypeMirror> getJdkSubclasses() {
        return jdkSubclasses;
    }

    /**
     * Gets the JDK type extended by the given subclass, if any.
     */
    public TypeMirror getJdkSuperclass(String subclassName, Set<String> excludedJdkTypes) {
        TypeMirror jdkType = jdkSubclasses.get(subclassName);
        if (jdkType == null) {
            return null;
        } else {
            if (!excludedJdkTypes.contains(jdkType.toString())) {
                return jdkType;
            } else {
                return null;
            }
        }
    }

    private List<AnnotationManager> annotationManagers = new ArrayList<>();
    private Map<String, String> typesMapping = new HashMap<String, String>();

    private List<BiFunction<ExtendedElement, String, Object>> functionalTypeTreeMappings = new ArrayList<>();
    private List<Function<TypeMirror, String>> functionalTypeMappings = new ArrayList<>();

    protected Map<String, String> langTypesMapping = new HashMap<String, String>();
    protected Set<String> langTypesSimpleNames = new HashSet<String>();
    protected Set<String> baseThrowables = new HashSet<String>();

    public static class GlobalMethodInfos {
        public final ClassTree classTree;
        public final MethodTree methodTree;
        public final CompilationUnitTree compilationUnitTree;

        private GlobalMethodInfos(ClassTree classTree, MethodTree methodTree, CompilationUnitTree compilationUnitTree) {
            this.classTree = classTree;
            this.methodTree = methodTree;
            this.compilationUnitTree = compilationUnitTree;
        }
    }

    private Map<String, GlobalMethodInfos> globalMethods = new HashMap<>();
    private Map<String, ClassTree> decoratorAnnotations = new HashMap<>();

    /**
     * Registers a global method in the context.
     * 
     * @see #lookupGlobalMethod(String)
     */
    public void registerGlobalMethod(ClassTree owner, MethodTree method, CompilationUnitTree compilationUnit) {

        ExecutableElement methodElement = Util.getElement(method);

        String name = ((TypeElement) methodElement.getEnclosingElement()).getQualifiedName().toString();
        name += "." + method.getName();
        name = name.replace(JSweetConfig.GLOBALS_CLASS_NAME + ".", "");
        globalMethods.put(name, new GlobalMethodInfos(owner, method, compilationUnit));
    }

    /**
     * Looks up a registered method from its fully qualified name.
     * 
     * @param fullyQualifiedName fully qualified owning type name + "." + method
     *                           name
     * @return an array containing the class and method AST objects of any matching
     *         method (in theory several methods could match because of overloading
     *         but we ignore it here)
     * @see #registerGlobalMethod(MethodTree)
     */
    public GlobalMethodInfos lookupGlobalMethod(String fullyQualifiedName) {
        return globalMethods.get(fullyQualifiedName);
    }

    /**
     * Registers a decorator annotation in the context.
     */
    public void registerDecoratorAnnotation(ClassTree annotationDeclaration, CompilationUnitTree compilationUnit) {
        TypeElement annotationElement = Util.getElement(annotationDeclaration);
        decoratorAnnotations.put(annotationElement.getQualifiedName().toString(), annotationDeclaration);
    }

    /**
     * Looks up a decorator annotation in the context.
     */
    public ClassTree lookupDecoratorAnnotation(String fullyQualifiedName) {
        return decoratorAnnotations.get(fullyQualifiedName);
    }

    /**
     * Adds a type mapping in the context.
     * 
     * @param sourceTypeName the fully qualified name of the source type
     * @param targetTypeName the fully Qualified name of the type the source type is
     *                       mapped to
     */
    public final void addTypeMapping(String sourceTypeName, String targetTypeName) {
        typesMapping.put(sourceTypeName, targetTypeName);
    }

    /**
     * Adds a set of name-based type mappings. This method is equivalent to calling
     * {@link #addTypeMapping(String, String)} for each entry of the given map.
     */
    public final void addTypeMappings(Map<String, String> nameMappings) {
        typesMapping.putAll(nameMappings);
    }

    /**
     * Returns true if the given type name is mapped through the
     * {@link #addTypeMapping(String, String)} or
     * {@link #addTypeMapping(String, String)} function.
     */
    public final boolean isMappedType(String sourceTypeName) {
        return typesMapping.containsKey(sourceTypeName);
    }

    /**
     * Returns the type the given type name is mapped through the
     * {@link #addTypeMapping(String, String)} or
     * {@link #addTypeMapping(String, String)} function.
     */
    public final String getTypeMappingTarget(String sourceTypeName) {
        return typesMapping.get(sourceTypeName);
    }

    /**
     * Adds a functional type mapping.
     * 
     * @param mappingFunction a function that takes the type element, the type name,
     *                        and returns a mapped type (either under the form of a
     *                        string, or another TypeMirror, or a tree).
     */
    public final void addTypeMapping(BiFunction<ExtendedElement, String, Object> mappingFunction) {
        functionalTypeTreeMappings.add(mappingFunction);
    }

    /**
     * Returns the functional type mappings.
     */
    public final List<BiFunction<ExtendedElement, String, Object>> getFunctionalTypeMappings() {
        return functionalTypeTreeMappings;
    }

    /**
     * Adds a functional type mapping. <br />
     * NOTE: If TypeMirror is generic, the mapper should include generic
     * specification if relevant
     * 
     * @param mappingFunction a function that takes the type, and returns a mapped
     *                        type as string.
     */
    public final void addTypeMapping(Function<TypeMirror, String> mappingFunction) {
        functionalTypeMappings.add(mappingFunction);
    }

    /**
     * Returns the functional type mappings.
     */
    public final List<Function<TypeMirror, String>> getFunctionalTypeMirrorMappings() {
        return functionalTypeMappings;
    }

    /**
     * Adds an annotation manager that will tune (add or remove) annotations on the
     * AST. Lastly added managers have precedence over firstly added ones.
     */
    public final void addAnnotationManager(AnnotationManager annotationManager) {
        annotationManagers.add(annotationManager);
    }

    /**
     * Removes the given annotation manager from the existing annotation manager
     * chain.
     * 
     * @see #addAnnotationManager(AnnotationManager)
     */
    public final void removeAnnotationManager(AnnotationManager annotationManager) {
        annotationManagers.remove(annotationManager);
    }

    private static boolean testStringAt(StringBuilder sb, int i, String string) {
        if (i < 0) {
            return false;
        } else if (i + string.length() > sb.length()) {
            return false;
        } else {
            return sb.subSequence(i, i + string.length()).equals(string);
        }
    }

    private static String toRegexp(String pattern) {
        boolean argsEnv = false;
        StringBuilder sb = new StringBuilder(pattern);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            switch (c) {
            case '(':
                argsEnv = true;
                sb.insert(i++, '\\');
                break;
            case ')':
                argsEnv = false;
                sb.insert(i++, '\\');
                break;
            case '.':
                if (testStringAt(sb, i + 1, ".")) {
                    sb.deleteCharAt(i);
                    sb.deleteCharAt(i);
                    sb.insert(i++, ".*");
                } else {
                    sb.insert(i++, '\\');
                }
                break;
            case '*':
                if (testStringAt(sb, i + 1, "*")) {
                    sb.deleteCharAt(i);
                    sb.deleteCharAt(i);
                    sb.insert(i++, ".*");
                } else {
                    sb.deleteCharAt(i);
                    if (argsEnv) {
                        sb.insert(i, "[^,]*");
                        i += 4;
                    } else {
                        sb.insert(i, "[^.]*");
                        i += 4;
                    }
                }
                break;
            }
        }
        return sb.toString();
    }

    private Pattern annotationWithParameterPattern = Pattern.compile("@([^(]*)\\((.*)\\)");
    private Map<String, Collection<AnnotationFilterDescriptor>> annotationFilters = new HashMap<>();

    private Collection<AnnotationFilterDescriptor> getAnnotationFilterDescriptors(String annotationType) {
        Collection<AnnotationFilterDescriptor> descrs = annotationFilters.get(annotationType);
        if (descrs == null) {
            descrs = new ArrayList<>();
            annotationFilters.put(annotationType, descrs);
        }
        return descrs;
    }

    private boolean hasAnnotationFilters() {
        return !annotationFilters.isEmpty();
    }

    /**
     * A thread local holding the JSweet transpilation context for the current thread.
     */
    public static ThreadLocal<JSweetContext> current = new ThreadLocal<JSweetContext>();

    /**
     * A thread local holding the current compilation unit.
     */
    public static ThreadLocal<CompilationUnitTree> currentCompilationUnit = new ThreadLocal<CompilationUnitTree>();
    
    /**
     * Creates a new JSweet transpilation context.
     * 
     * @param options the JSweet transpilation options
     */
    public JSweetContext(JSweetOptions options) {
        this.options = options;
        if (options.getConfiguration() != null) {
            for (Entry<String, Object> entry : options.getConfiguration().entrySet()) {
                addConfigurationEntry(entry);
            }
        }
        for (Entry<String, Collection<AnnotationFilterDescriptor>> e : annotationFilters.entrySet()) {
            logger.info("annotation filter descriptor: " + e);
        }
        current.set(this);
    }

    /**
     * Adds an annotation on the AST through global filters.
     * 
     * <p>
     * Example:
     * 
     * <pre>
     * context.addAnnotation(FunctionalInterface.class, "*.MyInterface");
     * </pre>
     * 
     * <p>
     * Filters are simplified regular expressions matching on the Java AST. Special
     * characters are the following:
     * 
     * <ul>
     * <li>*: matches any token/identifier in the signature of the AST element</li>
     * <li>**: matches any list of tokens in signature of the AST element (same as
     * ..)</li>
     * <li>..: matches any list of tokens in signature of the AST element (same as
     * **)</li>
     * <li>!: negates the filter (first character only)</li>
     * </ul>
     * 
     * <p>
     * For example, to match:
     * 
     * <ul>
     * <li>all the elements in the x.y.z package: x.y.z.*</li>
     * <li>all the elements and subelements (fields, methods, ...) in the x.y.z
     * package: x.y.z.**</li>
     * <li>all the methods in the x.y.z.A class: x.y.z.A.*(..)</li>
     * <li>all the methods taking 2 arguments in the x.y.z.A class:
     * x.y.z.A.*(*,*)</li>
     * <li>all fields called aField in all the classes: **.aField</li>
     * </ul>
     * 
     * @param annotationType the annotation type
     * @param filters        the annotation is activated if one of the filters match
     *                       and no negative filter matches
     */
    public final void addAnnotation(Class<? extends Annotation> annotationType, String... filters) {
        addAnnotation(annotationType.getName(), filters);
    }

    /**
     * Adds an annotation on the AST through global filters.
     * 
     * The annotation to be added is described by its type and by a value, which is
     * passed as is to the annotation's value. If the annotation type does not
     * accept a value parameter, no annotations will be added.
     * 
     * @see #addAnnotation(String, String...)
     */
    public final void addAnnotationWithValue(Class<? extends Annotation> annotationType, Object value,
            String... filters) {
        addAnnotation(annotationType.getName() + "('" + value.toString() + "')", filters);
    }

    /**
     * Adds an annotation on the AST through global filters.
     * 
     * <p>
     * Example:
     * 
     * <pre>
     * context.addAnnotation("@Erased", "*.writeObject(*)");
     * context.addAnnotation("@Name('newName')", "*.MyDeclarationToBeRenamed");
     * </pre>
     * 
     * <p>
     * Filters are simplified regular expressions matching on the Java AST. Special
     * characters are the following:
     * 
     * <ul>
     * <li>*: matches any character in the signature of the AST element</li>
     * <li>!: negates the filter (first character only)</li>
     * </ul>
     * 
     * @param annotationDescriptor the annotation type name, optionally preceded
     *                             with a @, and optionally defining a value (fully
     *                             qualified name is not necessary for JSweet
     *                             annotations)
     * @param filters              the annotation is activated if one of the filters
     *                             match and no negative filter matches
     */
    public final void addAnnotation(String annotationDescriptor, String... filters) {
        if (!annotationDescriptor.startsWith("@")) {
            annotationDescriptor = "@" + annotationDescriptor;
        }
        Map<String, Object> map = new HashMap<>();
        Entry<String, Object> entry = new SimpleEntry<>(annotationDescriptor, map);
        for (String filter : filters) {
            String listKind = filter.startsWith("!") ? "exclude" : "include";
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) map.get(listKind);
            if (list == null) {
                list = new ArrayList<>();
                map.put(listKind, list);
            }
            list.add(listKind.equals("exclude") ? filter.substring(1).trim() : filter.trim());
        }
        addConfigurationEntry(entry);
    }

    private void addConfigurationEntry(Entry<String, Object> untypedEntry) {
        if (untypedEntry.getKey().startsWith("@")) {
            @SuppressWarnings("unchecked")
            Entry<String, Map<String, Object>> entry = (Entry<String, Map<String, Object>>) (Object) untypedEntry;
            String annotationType = null;
            Matcher m = annotationWithParameterPattern.matcher(entry.getKey());
            String parameter = null;
            if (m.matches()) {
                if (m.group(1).contains(".")) {
                    annotationType = m.group(1);
                } else {
                    annotationType = JSweetConfig.LANG_PACKAGE + "." + m.group(1);
                }
                parameter = m.group(2);
            } else {
                if (entry.getKey().contains(".")) {
                    annotationType = entry.getKey().substring(1);
                } else {
                    annotationType = JSweetConfig.LANG_PACKAGE + "." + entry.getKey().substring(1);
                }
            }
            Object include = entry.getValue().get("include");
            Collection<AnnotationFilterDescriptor> filterDescriptors = getAnnotationFilterDescriptors(annotationType);
            Collection<Pattern> inclusionPatterns = null;
            Collection<Pattern> exclusionPatterns = null;
            if (include != null) {
                inclusionPatterns = new ArrayList<>();
                if (include instanceof Collection) {
                    for (Object o : (Collection<?>) include) {
                        try {
                            inclusionPatterns.add(Pattern.compile(toRegexp(o.toString())));
                        } catch (Exception e) {
                            logger.warn("invalid pattern '" + o + "' for " + entry.getKey() + ".include");
                        }
                    }
                } else {
                    try {
                        inclusionPatterns.add(Pattern.compile(toRegexp(include.toString())));
                    } catch (Exception e) {
                        logger.warn("invalid pattern '" + include + "' for " + entry.getKey() + ".include");
                    }
                }
            } else {
                logger.warn("annotation entry " + entry.getKey() + " does not have a mandatory 'include' entry");
            }
            Object exclude = entry.getValue().get("exclude");
            if (exclude != null) {
                exclusionPatterns = new ArrayList<>();
                if (exclude instanceof Collection) {
                    for (Object o : (Collection<?>) exclude) {
                        try {
                            exclusionPatterns.add(Pattern.compile(toRegexp(o.toString())));
                        } catch (Exception e) {
                            logger.warn("invalid pattern '" + o + "' for " + entry.getKey() + ".exclude");
                        }
                    }
                } else {
                    try {
                        exclusionPatterns.add(Pattern.compile(toRegexp(exclude.toString())));
                    } catch (Exception e) {
                        logger.warn("invalid pattern '" + exclude + "' for " + entry.getKey() + ".exclude");
                    }
                }
            }
            filterDescriptors.add(new AnnotationFilterDescriptor(inclusionPatterns, exclusionPatterns, parameter));

        } else {
            switch (untypedEntry.getKey()) {
            case "typeMapping":
                @SuppressWarnings("unchecked")
                Entry<String, Map<String, Object>> entry = (Entry<String, Map<String, Object>>) (Object) untypedEntry;
                for (Entry<String, Object> e : entry.getValue().entrySet()) {
                    addTypeMapping(e.getKey(), (String) e.getValue());
                }
                break;
            }
        }

    }

    private boolean usingJavaRuntime = false;

    public final Locale locale = Locale.getDefault();

    /**
     * JSweet transpilation options.
     */
    public final JSweetOptions options;

    /**
     * A cache of method overloads.
     * 
     * @see OverloadScanner
     * @see OverloadScanner.Overload
     */
    private Map<TypeElement, Map<String, Overload>> overloads = new HashMap<>();

    /**
     * A cache of static method overloads.
     * 
     * @see OverloadScanner
     * @see OverloadScanner.Overload
     */
    private Map<TypeElement, Map<String, Overload>> staticOverloads = new HashMap<>();

    /**
     * Dump all the overloads gathered by {@link OverloadScanner}.
     */
    public void dumpOverloads(PrintStream out) {
        for (Entry<TypeElement, Map<String, Overload>> e1 : overloads.entrySet()) {
            out.println("*** " + e1.getKey());
            for (Entry<String, Overload> e2 : e1.getValue().entrySet()) {
                out.println("  - " + e2.getValue());
            }
        }
        for (Entry<TypeElement, Map<String, Overload>> e1 : staticOverloads.entrySet()) {
            out.println("*** " + e1.getKey() + " [STATIC]");
            for (Entry<String, Overload> e2 : e1.getValue().entrySet()) {
                out.println("  - " + e2.getValue());
            }
        }
    }

    /**
     * Returns all the overloads in this context.
     */
    public Set<Overload> getAllOverloads() {
        Set<Overload> result = new HashSet<>();
        overloads.values().forEach(m -> result.addAll(m.values()));
        staticOverloads.values().forEach(m -> result.addAll(m.values()));
        return result;
    }

    /**
     * Gets or create an overload instance for the given class and method.
     */
    public Overload getOrCreateOverload(TypeElement clazz, ExecutableElement method) {
        Map<TypeElement, Map<String, Overload>> actualOverloads = method.getModifiers().contains(Modifier.STATIC)
                ? staticOverloads
                : overloads;
        Map<String, Overload> m = actualOverloads.get(clazz);
        if (m == null) {
            m = new HashMap<>();
            actualOverloads.put(clazz, m);
        }
        String name = method.getSimpleName().toString();
        Overload overload = m.get(name);
        if (overload == null) {

            overload = new Overload(this);
            overload.methodName = name;
            m.put(name, overload);
        }
        return overload;
    }

    /**
     * Gets an overload instance for the given class and method.
     */
    public Overload getOverload(TypeElement clazz, ExecutableElement method) {
        Map<TypeElement, Map<String, Overload>> actualOverloads = method.getModifiers().contains(Modifier.STATIC)
                ? staticOverloads
                : overloads;
        Map<String, Overload> m = actualOverloads.get(clazz);
        if (m == null) {
            return null;
        }
        Overload overload = m.get(method.getSimpleName().toString());
        if (overload == null) {
            return null;
        }
        return overload;
    }

    /**
     * Tells if that method is part of an invalid overload in its declaring class.
     */
    public boolean isInvalidOverload(ExecutableElement method) {
        Overload overload = getOverload((TypeElement) method.getEnclosingElement(), method);
        return overload != null && overload.getMethodsCount() > 1 && !overload.isValid;
    }

    /**
     * Contains the classes that have a wrong constructor overload.
     */
    public Set<TypeElement> classesWithWrongConstructorOverload = new HashSet<>();

    /**
     * The Java compiler types for fast access.
     */
    public Types types;

    /**
     * The Java compiler trees for fast access.
     */
    public Trees trees;

    /**
     * The Java compiler trees for fast access.
     */
    public Elements elements;

    /**
     * Gets the util API, which provides a set of utilities.
     */
    public Util util = new Util(this);

    /**
     * A flag to tell if the transpiler is in module mode or not.
     */
    public boolean useModules = false;

    /**
     * A flag to tell if the transpiler should transpiler modules to old fashioned
     * require instructions rather than the ES2015 flavored syntax. import foo =
     * require("foo"); instead of import * as foo from 'foo';
     */
    public boolean useRequireForModules = true;

    /**
     * The source files that are being transpiled.
     */
    public SourceFile[] sourceFiles;

    /**
     * The source file paths that are being used for transpilation but not
     * generated.
     */
    public Set<String> excludedSourcePaths;

    /**
     * Returns true if the source path corresponds to a source file that is part of
     * the extra input (not generated).
     */
    public boolean isExcludedSourcePath(String sourcePath) {
        return excludedSourcePaths != null && excludedSourcePaths.contains(sourcePath);
    }

    /**
     * The compilation units that correspond to the source files.
     */
    public List<CompilationUnitTree> compilationUnits;

    private List<String> usedModules = new ArrayList<>();

    /**
     * A flag that indicates if we are building a bundle. In bundle mode, static
     * fields will be initialized at the end of the bundle.
     */
    public boolean bundleMode = false;

    public boolean moduleBundleMode = false;

    /**
     * Holds all the static fields that are lazy intitialized.
     */
    public Set<VariableElement> lazyInitializedStatics = new HashSet<>();

    private Map<TypeElement, Integer> staticInitializerCounts = new HashMap<>();

    /**
     * Increments the count of static initialization blocks for the given class.
     */
    public void countStaticInitializer(TypeElement clazz) {
        staticInitializerCounts.put(clazz,
                (staticInitializerCounts.containsKey(clazz) ? staticInitializerCounts.get(clazz) : 0) + 1);
    }

    /**
     * Gets the static initializer count for the given class.
     */
    public int getStaticInitializerCount(TypeElement clazz) {
        Integer count = null;
        return (count = staticInitializerCounts.get(clazz)) == null ? 0 : count;
    }

    /**
     * Register a module that is used by the transpiled program.
     * 
     * @param moduleName the module being used
     */
    public void registerUsedModule(String moduleName) {
        if (!usedModules.contains(moduleName)) {
            usedModules.add(moduleName);
        }
    }

    /**
     * The list of modules used by the transpiled program.
     */
    public List<String> getUsedModules() {
        return usedModules;
    }

    private Map<String, Set<String>> importedNamesInModules = new HashMap<>();
    private Map<String, Map<Element, String>> importedElementsInModules = new HashMap<>();

    /**
     * Register a name that is imported by the given package of the transpiled
     * program.
     * 
     * <pre>
     * import targetName = require("sourceName");
     * </pre>
     * 
     * @param moduleName    the module that is importing the name
     * @param sourceElement the source element if any (null if not applicable)
     * @param targetName    the target name being imported
     */
    public void registerImportedName(String moduleName, Element sourceElement, String targetName) {
        Set<String> importedNames = importedNamesInModules.get(moduleName);
        if (importedNames == null) {
            importedNames = new HashSet<>();
            importedNamesInModules.put(moduleName, importedNames);
        }
        if (!importedNames.contains(targetName)) {
            importedNames.add(targetName);
        }
        if (sourceElement != null) {
            Map<Element, String> importedElements = importedElementsInModules.get(moduleName);
            if (importedElements == null) {
                importedElements = new HashMap<>();
                importedElementsInModules.put(moduleName, importedElements);
            }
            if (!importedElements.containsKey(sourceElement)) {
                importedElements.put(sourceElement, targetName);
            }

        }
    }

    /**
     * The list of names imported by the given module of the transpiled program.
     */
    public Set<String> getImportedNames(String moduleName) {
        Set<String> importedNames = importedNamesInModules.get(moduleName);
        if (importedNames == null) {
            importedNames = new HashSet<>();
            importedNamesInModules.put(moduleName, importedNames);
        }
        return importedNames;
    }

    /**
     * The list of package names imported by the given m of the transpiled program.
     */
    public Map<Element, String> getImportedElements(String moduleName) {
        Map<Element, String> importedElements = importedElementsInModules.get(moduleName);
        if (importedElements == null) {
            importedElements = new HashMap<>();
            importedElementsInModules.put(moduleName, importedElements);
        }
        return importedElements;
    }

    /**
     * Clears the names imported by the given module.
     */
    public void clearImportedNames(String moduleName) {
        Set<String> importedNames = new HashSet<>();
        importedNamesInModules.put(moduleName, importedNames);
        Map<Element, String> importedModulesForNames = new HashMap<>();
        importedElementsInModules.put(moduleName, importedModulesForNames);
    }

    private Map<String, List<Element>> exportedElements = new HashMap<>();
    private Map<Element, String> exportedNames = new HashMap<>();

    /**
     * Gets the exported elements for all the modules defined in the program.
     */
    public Map<String, List<Element>> getExportedElements() {
        return exportedElements;
    }

    /**
     * Returns the idenfier of the given exported symbol, including Module
     * annotation's name if specified
     */
    public String getExportedElementName(Element exportedElement) {
        String name = exportedNames.get(exportedElement);
        String forcedName = getAnnotationValue(exportedElement, JSweetConfig.ANNOTATION_MODULE, "exportedElement",
                String.class, null);
        if (StringUtils.isNotBlank(forcedName)) {
            name = forcedName;
        }

        return name;
    }

    /**
     * Adds an exported element for a module.
     */
    public void addExportedElement(String moduleName, Element exportedElement, CompilationUnitTree compilationUnit) {
        List<Element> exportedNamesForModule = exportedElements.get(moduleName);
        if (exportedNamesForModule == null) {
            exportedNamesForModule = new ArrayList<Element>();
            exportedElements.put(moduleName, exportedNamesForModule);
        }

        exportedNames.put(exportedElement, getRootRelativeName(
                useModules ? getImportedElements(compilationUnit.getSourceFile().getName()) : null, exportedElement));
        exportedNamesForModule.add(exportedElement);
    }

    /**
     * Source files containing a main method.
     */
    public List<File> entryFiles = new ArrayList<>();

    /**
     * A graph containing the module dependencies when using modules (empty
     * otherwise).
     */
    public DirectedGraph<PackageElement> packageDependencies = new DirectedGraph<>();

    /**
     * Stores the root package names (i.e. packages contained in the default package
     * or in a package annotated with the {@link jsweet.lang.Root} annotation).
     */
    public Set<String> topLevelPackageNames = new HashSet<>();

    /**
     * Store root packages (i.e. packages contained in the default package or in a
     * package annotated with the {@link jsweet.lang.Root} annotation, including
     * null, i.e. default package).
     */
    public HashSet<PackageElement> rootPackages = new HashSet<>();

    /**
     * A flag to keep track of wether a multiple root packages problem was already
     * reported (shall report only once).
     */
    public boolean reportedMultipleRootPackages = false;

    /**
     * Globally imported name (in the global namespace).
     */
    public Set<String> globalImports = new HashSet<>();

    /**
     * Imported top packages (used to avoid clashes with local variables when bundle
     * is on).
     */
    public Set<String> importedTopPackages = new HashSet<>();

    /**
     * A flag that indicates if the transpilation is in "strict" mode, which means
     * that the <code>jsweet-core-strict</code> jar is in the classpath.
     */
    public boolean strictMode = false;

    /**
     * A flag that indicates if the transpiler should transform old-fashionned apply
     * method to lambda or use the new convention ($apply)
     */
    public boolean deprecatedApply = false;

    private List<Entry<String, String>> footerStatements = new LinkedList<Entry<String, String>>();

    /**
     * Clears the footer statements.
     */
    public void clearFooterStatements() {
        footerStatements.clear();
    }

    /**
     * Gets the footer statements.
     */
    public String getFooterStatements() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> footerStatement : footerStatements) {
            sb.append("\n");
            sb.append(footerStatement.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Adds a footer statement.
     */
    public void addFooterStatement(String key, String footerStatement) {
        footerStatements.add(new AbstractMap.SimpleEntry<String, String>(key, footerStatement));
    }

    /**
     * Adds a footer statement.
     */
    public void addFooterStatement(String footerStatement) {
        footerStatements.add(new AbstractMap.SimpleEntry<String, String>("", footerStatement));
    }

    /**
     * Adds a footer statement at the first position.
     */
    public void addTopFooterStatement(String key, String footerStatement) {
        footerStatements.add(0, new AbstractMap.SimpleEntry<String, String>(key, footerStatement));
    }

    /**
     * Adds a footer statement at the first position.
     */
    public void addTopFooterStatement(String footerStatement) {
        footerStatements.add(0, new AbstractMap.SimpleEntry<String, String>("", footerStatement));
    }

    /**
     * A flag to force import generation at the top of the file.
     */
    public boolean forceTopImports() {
        forceTopImports = true;
        return forceTopImports;
    }

    private boolean forceTopImports = false;

    private Map<String, String> headers = new LinkedHashMap<String, String>();

    /**
     * Clear the headers.
     */
    public void clearHeaders() {
        headers.clear();
        forceTopImports = false;
    }

    /**
     * Gets the headers.
     */
    public String getHeaders() {
        if (forceTopImports) {
            for (Entry<String, String> statement : new ArrayList<>(footerStatements)) {
                if (statement.getKey().startsWith("import.")) {
                    footerStatements.remove(statement);
                    headers.put(statement.getKey(), statement.getValue());
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        if (!headers.isEmpty()) {
            for (String header : headers.values()) {
                sb.append(header);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Sets a header (associated to a key).
     * 
     * @see #getHeader(String)
     */
    public void addHeader(String key, String header) {
        headers.put(key, header);
    }

    /**
     * Returns a header for the given key (null if never set).
     * 
     * @see #addHeader(String, String)
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    private Map<String, String> globalsMapping = new HashMap<>();

    /**
     * Adds a globals mapping.
     */
    public void addGlobalsMapping(String from, String to) {
        globalsMapping.put(from, to);
    }

    /**
     * Clear globals mappings.
     */
    public void clearGlobalsMappings() {
        globalsMapping.clear();
    }

    /**
     * Returns the TypeScript string for globals mapping.
     */
    public String getGlobalsMappingString() {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, String> e : globalsMapping.entrySet()) {
            b.append("var " + e.getValue() + " = " + e.getKey() + ";\n");
        }
        return b.toString();
    }

    private final Map<TypeElement, Set<DefaultMethodEntry>> defaultMethods = new HashMap<>();
    private final Map<MethodTree, CompilationUnitTree> defaultMethodsCompilationUnits = new HashMap<>();

    public static class DefaultMethodEntry {
        public final CompilationUnitTree compilationUnit;
        public final ClassTree enclosingClassTree;
        public final TypeElement enclosingClassElement;
        public final MethodTree methodTree;
        public final ExecutableElement methodElement;
        public final ExecutableType methodType;

        protected DefaultMethodEntry(CompilationUnitTree compilationUnit, ClassTree enclosingClassTree,
                TypeElement enclosingClassElement, MethodTree methodTree, ExecutableElement methodElement) {
            this.compilationUnit = compilationUnit;
            this.enclosingClassTree = enclosingClassTree;
            this.enclosingClassElement = enclosingClassElement;
            this.methodTree = methodTree;
            this.methodElement = methodElement;
            this.methodType = (ExecutableType) methodElement.asType();
        }

        @Override
        public int hashCode() {
            return methodTree.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof DefaultMethodEntry && methodTree == ((DefaultMethodEntry) other).methodTree;
        }
    }

    /**
     * Gets the default methods declared in the given type.
     */
    public Set<DefaultMethodEntry> getDefaultMethods(TypeElement type) {
        return defaultMethods.get(type);
    }

    /**
     * Gets the compilation unit the given default method belongs to.
     */
    public CompilationUnitTree getDefaultMethodCompilationUnit(MethodTree defaultMethod) {
        return defaultMethodsCompilationUnits.get(defaultMethod);
    }

    /**
     * Stores a default method AST for the given type.
     */
    public void addDefaultMethod(CompilationUnitTree compilationUnit, ClassTree classTree, MethodTree defaultMethod) {
        TypeElement classElement = Util.getElement(classTree);
        Set<DefaultMethodEntry> methods = defaultMethods.get(classElement);
        if (methods == null) {
            methods = new HashSet<>();
            defaultMethods.put(classElement, methods);
        }
        ExecutableElement methodElement = Util.getElement(defaultMethod);
        TypeElement enclosingClassElement = Util.getElement(classTree);
        methods.add(new DefaultMethodEntry(compilationUnit, classTree, enclosingClassElement, defaultMethod,
                methodElement));
        defaultMethodsCompilationUnits.put(defaultMethod, compilationUnit);
    }

    private Map<VariableElement, String> fieldNameMapping = new HashMap<>();

    /**
     * Adds a name mapping to a field (rename it to avoid name clashes).
     */
    public void addFieldNameMapping(VariableElement field, String name) {
        fieldNameMapping.put(field, name);
    }

    /**
     * Gets a field name mapping if any (null otherwise).
     */
    public String getFieldNameMapping(VariableElement field) {
        return fieldNameMapping.get(field);
    }

    /**
     * Tells if the given field has a field name mapping.
     * 
     * @see #addFieldNameMapping(VariableElement, String)
     * @see #getFieldNameMapping(Element)
     */
    public boolean hasFieldNameMapping(VariableElement field) {
        return fieldNameMapping.containsKey(field);
    }

    private Map<ExecutableElement, String> methodNameMapping = new HashMap<>();

    /**
     * Adds a name mapping to a method (rename it to avoid name clashes).
     */
    public void addMethodNameMapping(ExecutableElement method, String name) {
        methodNameMapping.put(method, name);
    }

    /**
     * Gets a method name mapping if any (null otherwise).
     */
    public String getMethodNameMapping(Element method) {
        return methodNameMapping.get(method);
    }

    /**
     * Tells if the given method has a method name mapping.
     */
    public boolean hasMethodNameMapping(Element method) {
        return methodNameMapping.containsKey(method);
    }

    private Map<TypeElement, String> classNameMapping = new HashMap<>();

    /**
     * Adds a name mapping to a class (rename it to avoid name clashes).
     */
    public void addClassNameMapping(TypeElement clazz, String name) {
        classNameMapping.put(clazz, name);
    }

    /**
     * Gets a class name mapping if any (null otherwise).
     */
    public String getClassNameMapping(TypeElement clazz) {
        return classNameMapping.get(clazz);
    }

    /**
     * Tells if the given class has a class name mapping.
     * 
     * @see #addClassNameMapping(TypeElement, String)
     * @see #getClassNameMapping(Element)
     */
    public boolean hasClassNameMapping(TypeElement clazz) {
        return classNameMapping.containsKey(clazz);
    }

    /**
     * Tells JSweet to ignore wildcard bounds. For instance if ignored:
     * 
     * <pre>
     * void f(C<? extends String> c)
     * </pre>
     * 
     * will transpile to:
     * 
     * <pre>
     * f(c: C<any>)
     * </pre>
     * 
     * otherwise:
     * 
     * <pre>
     * void f(C<? extends String> c)
     * </pre>
     * 
     * will transpile to:
     * 
     * <pre>
     * f<__T1 extends string>(c: C<__T1>)
     * </pre>
     * 
     */
    public boolean ignoreWildcardBounds = true;

    private Map<WildcardTree, String> wildcardNames = new HashMap<>();

    private Map<ExecutableElement, List<WildcardTree>> wildcards = new HashMap<>();

    /**
     * Registers a wilcard for a given container (type parameterized element).
     */
    public void registerWildcard(ExecutableElement holder, WildcardTree wildcard) {
        if (wildcard.getBound() == null) {
            return;
        }
        List<WildcardTree> l = wildcards.get(holder);
        if (l == null) {
            l = new ArrayList<>();
            wildcards.put(holder, l);
        }
        l.add(wildcard);
        wildcardNames.put(wildcard, "__T" + l.size());
    }

    /**
     * Gets the wildcard name if any.
     */
    public String getWildcardName(WildcardTree wildcard) {
        return wildcardNames.get(wildcard);
    }

    /**
     * Gets the registered wildcards for the given type parameter holder.
     */
    public List<WildcardTree> getWildcards(ExecutableElement holder) {
        return wildcards.get(holder);
    }

    private static Pattern libPackagePattern = Pattern.compile(JSweetConfig.LIBS_PACKAGE + "\\.[^.]*");

    /**
     * Returns true if the given symbol is a root package (annotated with @Root or a
     * definition package).
     */
    public boolean isRootPackage(Element element) {
        return hasAnnotationType(element, JSweetConfig.ANNOTATION_ROOT) || (element instanceof PackageElement
                && libPackagePattern.matcher(((PackageElement) element).getQualifiedName().toString()).matches());
    }

    /**
     * Tells if the given type is a Java interface.
     */
    public boolean isInterface(Element typeElement) {
        return util.isInterface(typeElement) || hasAnnotationType(typeElement, JSweetConfig.ANNOTATION_INTERFACE);
    }
    
    public boolean elementHasAnnotationType(Element element, String... annotationTypes) {
        return hasAnnotationType(element, annotationTypes);
    }

    /**
     * Tells if the given symbol is annotated with one of the given annotation
     * types.
     */
    public boolean hasAnnotationType(Element element, String... annotationTypes) {
        if (element == null) {
            return false;
        }

        String[] types = annotationTypes;
        for (AnnotationManager annotationIntrospector : annotationManagers) {
            for (String annotationType : types) {
                Action state = annotationIntrospector.manageAnnotation(element, annotationType);
                if (state == Action.ADD) {
                    return true;
                } else if (state == Action.REMOVE) {
                    types = ArrayUtils.removeElement(annotationTypes, annotationType);
                }
            }
        }

        if (hasAnnotationFilters()) {

            String signature = getElementSignatureForAnnotationFilters(element);

            for (String annotationType : annotationTypes) {
                Collection<AnnotationFilterDescriptor> filterDescriptors = annotationFilters.get(annotationType);
                if (filterDescriptors != null) {
                    for (AnnotationFilterDescriptor filterDescriptor : filterDescriptors) {
                        if (filterDescriptor.inclusionPatterns == null) {
                            logger.error("no inclusion patterns found for annotation filter: " + annotationType);
                        }
                        for (Pattern include : filterDescriptor.inclusionPatterns) {
                            if (include.matcher(signature).matches()) {
                                boolean excluded = false;
                                Collection<Pattern> excludePatterns = filterDescriptor.exclusionPatterns;
                                if (excludePatterns != null) {
                                    for (Pattern exclude : excludePatterns) {
                                        if (exclude.matcher(signature).matches()) {
                                            excluded = true;
                                            break;
                                        }
                                    }
                                }
                                if (!excluded) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (extraAnnotations != null) {
            for (String annotationType : annotationTypes) {
                Set<Element> elements = extraAnnotations.get(annotationType);
                if (elements != null && elements.contains(element)) {
                    return true;
                }
            }
        }

        return hasActualAnnotationType(element, annotationTypes);
    }

    private Map<String, Set<Element>> extraAnnotations;

    /**
     * Adds an extra annotation type to a symbol (no args). See
     * {@link #hasAnnotationType(Element, String...)}.
     * 
     * @param symbol             the symbol to add the annotation to
     * @param annotationTypeName the annotation type name
     */
    public void addExtraAnnotationType(Element symbol, String annotationTypeName) {
        if (extraAnnotations == null) {
            extraAnnotations = new HashMap<>();
        }
        Set<Element> elements = extraAnnotations.get(annotationTypeName);
        if (elements == null) {
            elements = new HashSet<>();
            extraAnnotations.put(annotationTypeName, elements);
        }
        elements.add(symbol);
    }

    private String getElementSignatureForAnnotationFilters(Element element) {
        String signature = element.toString();
        if (!(element instanceof TypeElement) && element.getEnclosingElement() != null) {
            String enclosingQualifiedName = util.getQualifiedName(element.getEnclosingElement());
            if (isNotBlank(enclosingQualifiedName)) {
                signature = enclosingQualifiedName + "." + signature;
            }
        }
        return signature;
    }

    /**
     * Gets the actual (simple) name of a symbol from a JSweet convention, so
     * including potential <code>jsweet.lang.Name</code> annotation.
     */
    public String getActualName(Element symbol) {
        String name = symbol.getSimpleName().toString();
        String originalName = "";
        if (hasAnnotationType(symbol, JSweetConfig.ANNOTATION_NAME)) {
            originalName = getAnnotationValue(symbol, JSweetConfig.ANNOTATION_NAME, String.class, null);
        }
        if (!isBlank(originalName)) {
            name = originalName;
        } else {
            if (hasMethodNameMapping(symbol)) {
                name = getMethodNameMapping(symbol);
            }
        }
        return name;
    }

    private void getRootRelativeName(Map<Element, String> nameMapping, StringBuilder sb, Element element) {
        if (element == null //
                || useModules && element instanceof PackageElement
                        && !element.toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
            return;
        }
        if (!isRootPackage(element)) {
            if (sb.length() > 0 && !"".equals(element.toString())) {
                sb.insert(0, ".");
            }

            String name = element.getSimpleName().toString();

            if (nameMapping != null && nameMapping.containsKey(element)) {
                name = nameMapping.get(element);
            } else if (hasAnnotationType(element, JSweetConfig.ANNOTATION_NAME)) {
                String originalName = getAnnotationValue(element, JSweetConfig.ANNOTATION_NAME, String.class, null);
                if (!isBlank(originalName)) {
                    name = originalName;
                }
            } else if (element.getKind() == ElementKind.PACKAGE) {
                // remove reserved keywords from package names
                name = avoidJSKeyword(name);
            }

            sb.insert(0, name);
            element = (element instanceof PackageElement) ? util.getParentPackage((PackageElement) element)
                    : element.getEnclosingElement();
            if (element != null) {
                getRootRelativeName(nameMapping, sb, element);
            }
        }
    }

    private String avoidJSKeyword(String name) {
        if (JSweetConfig.JS_KEYWORDS.contains(name)) {
            name = JSweetConfig.JS_KEYWORD_PREFIX + name;
        }
        return name;
    }

    /**
     * Gets the top-level package enclosing the given symbol. The top-level package
     * is the one that is enclosed within a root package (see
     * <code>jsweet.lang.Root</code>) or the one in the default (unnamed) package.
     */
    public PackageElement getTopLevelPackage(Element element) {
        if (element == null || (element instanceof PackageElement) && isRootPackage(element)) {
            return null;
        }
        Element parent = (element instanceof PackageElement) ? util.getParentPackage((PackageElement) element)
                : element.getEnclosingElement();
        if (parent != null && isRootPackage(parent)) {
            if (element instanceof PackageElement) {
                return (PackageElement) element;
            } else {
                return null;
            }
        } else {
            if (parent == null || (parent instanceof PackageElement && StringUtils.isBlank(parent.getSimpleName()))) {
                if (element instanceof PackageElement) {
                    return (PackageElement) element;
                } else {
                    return null;
                }
            } else {
                return getTopLevelPackage(parent);
            }
        }
    }

    /**
     * Finds the first (including itself) enclosing package annotated with @Root.
     */
    public PackageElement getFirstEnclosingRootPackage(PackageElement packageElement) {
        if (packageElement == null) {
            return null;
        }
        if (isRootPackage(packageElement)) {
            return packageElement;
        }
        return getFirstEnclosingRootPackage(util.getParentPackage(packageElement));
    }

    private void getRootRelativeJavaName(StringBuilder sb, Element element) {
        if (!isRootPackage(element)) {
            if (sb.length() > 0 && !"".equals(element.toString())) {
                sb.insert(0, ".");
            }

            String name = element.getSimpleName().toString();

            sb.insert(0, name);
            element = (element instanceof PackageElement) ? util.getParentPackage((PackageElement) element)
                    : element.getEnclosingElement();
            if (element != null) {
                getRootRelativeJavaName(sb, element);
            }
        }
    }

    /**
     * Gets the qualified name of a symbol relatively to the root package
     * (potentially annotated with <code>jsweet.lang.Root</code>).
     * 
     * @param nameMapping  a map to redirect names
     * @param symbol       the symbol to get the name of
     * @param useJavaNames if true uses plain Java names, if false uses
     *                     <code>jsweet.lang.Name</code> annotations
     * @return
     */
    public String getRootRelativeName(Map<Element, String> nameMapping, Element symbol, boolean useJavaNames) {
        if (useJavaNames) {
            return getRootRelativeJavaName(symbol);
        } else {
            return getRootRelativeName(nameMapping, symbol);
        }
    }

    /**
     * Gets the qualified name of a symbol relatively to the root package
     * (potentially annotated with <code>jsweet.lang.Root</code>). This function
     * takes into account potential <code>jsweet.lang.Name</code> annotations).
     */
    public String getRootRelativeName(Map<Element, String> nameMapping, Element symbol) {
        StringBuilder sb = new StringBuilder();
        getRootRelativeName(nameMapping, sb, symbol);
        if (sb.length() > 0 && sb.charAt(0) == '.') {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * Gets the qualified name of a symbol relatively to the root package
     * (potentially annotated with <code>jsweet.lang.Root</code>). This function
     * ignores <code>jsweet.lang.Name</code> annotations).
     */
    public String getRootRelativeJavaName(Element symbol) {
        StringBuilder sb = new StringBuilder();
        getRootRelativeJavaName(sb, symbol);
        return sb.toString();
    }

    /**
     * Gets the first value of the 'value' property for the given annotation type if
     * found on the given symbol.
     */
    public final <T> T getAnnotationValue(Element symbol, String annotationType, Class<T> propertyClass,
            T defaultValue) {
        return getAnnotationValue(symbol, annotationType, null, propertyClass, defaultValue);
    }

    /**
     * Gets the first value of the given property for the given annotation type if
     * found on the given symbol.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAnnotationValue(Element element, String annotationType, String propertyName, Class<T> propertyClass,
            T defaultValue) {
        for (AnnotationManager annotationIntrospector : annotationManagers) {
            T value = annotationIntrospector.getAnnotationValue(element, annotationType, propertyName, propertyClass,
                    defaultValue);
            if (value != null) {
                return value;
            }
        }
        if (hasAnnotationFilters()) {
            String signature = getElementSignatureForAnnotationFilters(element);
            Collection<AnnotationFilterDescriptor> filterDescriptors = annotationFilters.get(annotationType);
            if (filterDescriptors != null) {
                for (AnnotationFilterDescriptor filterDescriptor : filterDescriptors) {
                    for (Pattern include : filterDescriptor.inclusionPatterns) {
                        if (include.matcher(signature).matches()) {
                            boolean excluded = false;
                            Collection<Pattern> excludePatterns = filterDescriptor.exclusionPatterns;
                            if (excludePatterns != null) {
                                for (Pattern exclude : excludePatterns) {
                                    if (exclude.matcher(signature).matches()) {
                                        excluded = true;
                                        break;
                                    }
                                }
                            }
                            if (!excluded) {
                                if (filterDescriptor.parameter == null) {
                                    return defaultValue;
                                } else if (filterDescriptor.parameter.startsWith("'")) {
                                    return (T) filterDescriptor.parameter.substring(1,
                                            filterDescriptor.parameter.length() - 1);
                                } else if (filterDescriptor.parameter.endsWith(".class")) {
                                    return (T) filterDescriptor.parameter.substring(0,
                                            filterDescriptor.parameter.length() - 6);
                                } else {
                                    return (T) filterDescriptor.parameter;
                                }
                            }
                        }
                    }
                }
            }
        }

        AnnotationMirror anno = getAnnotation(element, annotationType);
        T val = defaultValue;
        if (anno != null) {
            T firstVal = getFirstAnnotationValue(anno, propertyName, propertyClass, null);
            if (firstVal != null) {
                val = firstVal;
            }
        }
        return val;
    }

    /**
     * Gets the first value of the 'value' property.
     */
    @SuppressWarnings("unchecked")
    private static <T> T getFirstAnnotationValue(AnnotationMirror annotation, String propertyName,
            Class<T> propertyClass, T defaultValue) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> valueEntry : annotation
                .getElementValues().entrySet()) {
            if (propertyName == null || propertyName.equals(valueEntry.getKey().getSimpleName().toString())) {
                if (propertyClass.isArray()) {
                    List<AnnotationValue> annotationValues = (List<AnnotationValue>) valueEntry.getValue().getValue();
                    Object array = Array.newInstance(propertyClass.getComponentType(), annotationValues.size());
                    for (int i = 0; i < annotationValues.size(); i++) {
                        Array.set(array, i, annotationValues.get(i).getValue());
                    }
                    return (T) array;
                } else {
                    return (T) valueEntry.getValue().getValue();
                }
            }
        }
        return defaultValue;
    }

    /**
     * Gets the symbol's annotation that correspond to the given annotation type
     * name if exists.
     */
    private AnnotationMirror getAnnotation(Element symbol, String annotationType) {
        for (AnnotationMirror annotation : symbol.getAnnotationMirrors()) {
            if (annotationType.equals(annotation.getAnnotationType().toString())) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Grabs the names of all the support interfaces in the class and interface
     * hierarchy.
     */
    public void grabSupportedInterfaceNames(Set<String> interfaces, TypeElement type, PrinterAdapter adapter) {
        if (type == null) {
            return;
        }
        if (isInterface(type) && !hasAnnotationType(type, JSweetConfig.ANNOTATION_ERASED)) {
            interfaces.add(type.getQualifiedName().toString());
        }
        if (type instanceof TypeElement) {
            for (TypeElement interfaceType : util.getInterfaces((TypeElement) type)) {
                if (interfaceType != null && !adapter.eraseSuperInterface((TypeElement)type, interfaceType)) {
                    grabSupportedInterfaceNames(interfaces, interfaceType, adapter);
                }
            }
            
            TypeElement superClassType = Util.getElement(((TypeElement) type).getSuperclass());
            if (superClassType != null && !adapter.eraseSuperInterface((TypeElement)type, (TypeElement)superClassType)) {
                grabSupportedInterfaceNames(interfaces, superClassType, adapter);
            }
        }
    }

    /**
     * Grabs the names of all the superclasses.
     */
    public void grabSuperClassNames(Set<String> superClasses, Element type) {
        if (type == null) {
            return;
        }
        if (type instanceof TypeElement) {
            superClasses.add(((TypeElement) type).getQualifiedName().toString());
            grabSuperClassNames(superClasses, util.getSuperclass((TypeElement) type));
        }
    }

    public void grabMethodsToBeImplemented(List<ExecutableElement> methods, TypeElement type) {
        if (type == null) {
            return;
        }
        if (isInterface(type)) {
            for (Element s : type.getEnclosedElements()) {
                if (s instanceof ExecutableElement) {
                    if (!s.getModifiers().contains(Modifier.STATIC)
                            && !util.isOverridingBuiltInJavaObjectMethod((ExecutableElement) s)) {
                        methods.add((ExecutableElement) s);
                    }
                }
            }
        }
        if (type instanceof TypeElement) {
            for (TypeElement interfaceType : util.getInterfaces((TypeElement) type)) {
                grabMethodsToBeImplemented(methods, interfaceType);
            }
            grabMethodsToBeImplemented(methods, util.getSuperclass((TypeElement) type));
        }
    }

    /**
     * Tells if the given symbol is annotated with one of the given annotation type
     * names.
     */
    private static boolean hasActualAnnotationType(Element symbol, String... annotationTypes) {
        for (AnnotationMirror annotation : symbol.getAnnotationMirrors()) {
            for (String annotationType : annotationTypes) {
                if (annotationType.equals(annotation.getAnnotationType().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if this new class expression defines an anonymous class.
     */
    public boolean isAnonymousClass(NewClassTree newClass, CompilationUnitTree compilationUnit) {
        if (newClass.getIdentifier() != null && newClass.getClassBody() != null) {
            TypeElement newClassTypeElement = Util.getTypeElement(newClass.getIdentifier());

            if (hasAnnotationType(newClassTypeElement, JSweetConfig.ANNOTATION_OBJECT_TYPE)
                    || hasAnnotationType(newClassTypeElement, JSweetConfig.ANNOTATION_INTERFACE)) {
                return false;
            }
            if (newClass.getClassBody().getMembers().size() > 2) {
                // a map has a constructor (implicit) and an initializer
                return true;
            }
            if (newClassTypeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                // maps cannot be abstract
                return true;
            }
            for (Tree memberTree : newClass.getClassBody().getMembers()) {
                if (memberTree instanceof VariableTree) {
                    // no variables in maps
                    return true;
                }
                if (memberTree instanceof MethodTree) {
                    MethodTree methodTree = (MethodTree) memberTree;
                    ExecutableElement methodElement = Util.getElement(methodTree);
                    if (!(methodElement.getKind() == ElementKind.CONSTRUCTOR
                            && methodElement.getParameters().isEmpty())) {
                        // no regular methods or non-empty constructors in maps
                        return true;
                    }
                }
                if (memberTree instanceof BlockTree) {
                    for (StatementTree statementTree : ((BlockTree) memberTree).getStatements()) {
                        if (!isAllowedStatementInMap(statementTree)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }

    /**
     * Returns true if given statement can be used in a Java object instantation
     * block to be transformed to a JS Object Literal
     */
    private static boolean isAllowedStatementInMap(StatementTree statement) {
        if (statement instanceof ExpressionStatementTree) {
            ExpressionStatementTree exprStat = (ExpressionStatementTree) statement;
            if (exprStat.getExpression() instanceof AssignmentTree) {
                return true;
            }
            if (exprStat.getExpression() instanceof MethodInvocationTree) {
                MethodInvocationTree inv = (MethodInvocationTree) exprStat.getExpression();
                String methodName;
                if (inv.getMethodSelect() instanceof MemberSelectTree) {
                    methodName = ((MemberSelectTree) inv.getMethodSelect()).getIdentifier().toString();
                } else {
                    methodName = inv.getMethodSelect().toString();
                }
                if (JSweetConfig.INDEXED_GET_FUCTION_NAME.equals(methodName)
                        || JSweetConfig.INDEXED_SET_FUCTION_NAME.equals(methodName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the number of arguments declared by a function interface.
     */
    public int getFunctionalTypeParameterCount(TypeMirror type) {

        Element typeElement = types.asElement(type);
        String name = typeElement.getSimpleName().toString();
        String fullName = type.toString();
        if (Runnable.class.getName().equals(fullName)) {
            return 0;
        } else if (type.toString().startsWith(JSweetConfig.FUNCTION_CLASSES_PACKAGE + ".")) {
            if (name.equals("TriFunction")) {
                return 3;
            } else if (name.equals("TriConsumer")) {
                return 3;
            } else if (name.equals("Consumer")) {
                return 1;
            } else if (name.startsWith("Function") || name.startsWith("Consumer")) {
                return Integer.parseInt(name.substring(8));
            } else {
                return -1;
            }
        } else if (type.toString().startsWith("java.util.function.")) {
            if (name.endsWith("Consumer")) {
                if (name.startsWith("Bi")) {
                    return 2;
                } else {
                    return 1;
                }
            } else if (name.endsWith("Function")) {
                if (name.startsWith("Bi")) {
                    return 2;
                } else {
                    return 1;
                }
            } else if (name.endsWith("UnaryOperator")) {
                return 1;
            } else if (name.endsWith("BinaryOperator")) {
                return 2;
            } else if (name.endsWith("Supplier")) {
                return 0;
            } else if (name.endsWith("Predicate")) {
                if (name.startsWith("Bi")) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        } else {
            if (hasAnnotationType(typeElement, JSweetConfig.ANNOTATION_FUNCTIONAL_INTERFACE)) {
                for (Element e : typeElement.getEnclosedElements()) {
                    if (e instanceof ExecutableElement) {
                        return ((ExecutableElement) e).getParameters().size();
                    }
                }
                return -1;
            } else {
                return -1;
            }
        }

    }

    /**
     * Returns true if the given type symbol corresponds to a functional type (in
     * the TypeScript way).
     */
    public boolean isFunctionalType(Element typeElement) {
        if (typeElement == null) {
            return false;
        }
        String name = util.getQualifiedName(typeElement);
        return name.startsWith("java.util.function.") //
                || name.equals(Runnable.class.getName()) //
                || name.startsWith(JSweetConfig.FUNCTION_CLASSES_PACKAGE + ".") //
                || (util.isInterface(typeElement)
                        && (hasAnnotationType(typeElement, FunctionalInterface.class.getName())
                                || hasAnonymousFunction(typeElement)));
    }

    /**
     * Returns true if the given type symbol corresponds to a core functional type.
     */
    public boolean isCoreFunctionalType(TypeElement type) {
        String name = type.getQualifiedName().toString();
        return name.startsWith("java.util.function.") //
                || name.equals(Runnable.class.getName()) //
                || name.startsWith(JSweetConfig.FUNCTION_CLASSES_PACKAGE + ".") //
                || (util.isInterface(type) && hasAnonymousFunction(type));
    }

    /**
     * Tells if the given type has a anonymous function (instances can be used as
     * lambdas).
     */
    public boolean hasAnonymousFunction(Element type) {
        for (Element s : type.getEnclosedElements()) {
            if (s instanceof ExecutableElement) {
                String methodName = s.getSimpleName().toString();
                if (JSweetConfig.ANONYMOUS_FUNCTION_NAME.equals(methodName) //
                        || (deprecatedApply && JSweetConfig.ANONYMOUS_DEPRECATED_FUNCTION_NAME.equals(methodName))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if this class declaration is not to be output by the transpiler.
     */
    public boolean isIgnored(ClassTree classdecl, CompilationUnitTree compilationUnit) {
        Element classElement = Util.getElement(classdecl);

        if (hasAnnotationType(classElement, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
            // object types are ignored
            return true;
        }
        if (hasAnnotationType(classElement, JSweetConfig.ANNOTATION_ERASED)) {
            // erased types are ignored
            return true;
        }
        if (classElement.getKind() == ElementKind.ANNOTATION_TYPE
                && !hasAnnotationType(classElement, JSweetConfig.ANNOTATION_DECORATOR)) {
            // annotation types are ignored
            return true;
        }
        return false;
    }

    /**
     * Returns true if the given package is erased (it could be erased because a
     * parent package is erased).
     */
    public boolean isPackageErased(PackageElement pkg) {
        if (pkg == null) {
            return false;
        }
        if (hasAnnotationType(pkg, JSweetConfig.ANNOTATION_ERASED)) {
            return true;
        } else {
            return isPackageErased(util.getParentPackage(pkg));
        }
    }

    /**
     * Tells if the transpiler is using J4TS Java runtime. If yes, it will use the
     * adapter that tries to delegate to the Java emulation layer for the Java API.
     */
    public boolean isUsingJavaRuntime() {
        return usingJavaRuntime;
    }

    /**
     * Sets the transpiler to use the J4TS Java runtime.
     */
    public void setUsingJavaRuntime(boolean usingJavaRuntime) {
        this.usingJavaRuntime = usingJavaRuntime;
    }

    public final Map<String, String> getLangTypeMappings() {
        return langTypesMapping;
    }

    public final Set<String> getLangTypesSimpleNames() {
        return langTypesSimpleNames;
    }

    public final Set<String> getBaseThrowables() {
        return baseThrowables;
    }

    public Map<Element, String> docComments = new HashMap<>();

    private boolean isAmbientAnnotatedDeclaration(Element symbol) {
        if (symbol == null) {
            return false;
        }
        if (hasAnnotationType(symbol, JSweetConfig.ANNOTATION_AMBIENT)) {
            return true;
        } else {
            return isAmbientAnnotatedDeclaration(symbol.getEnclosingElement());
        }
    }

    /**
     * Tells if the given symbol is ambient (part of a def.* package or within an
     * <code>@Ambient</code>-annotated scope).
     */
    public boolean isAmbientDeclaration(Element element) {
        if (util.getQualifiedName(element).startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
            return true;
        } else {
            return isAmbientAnnotatedDeclaration(element);
        }
    }

    private final Map<Tree, CompilationUnitTree> compilationUnitsMapping = new HashMap<>();

    public void registerTreeCompilationUnit(Tree tree, CompilationUnitTree compilationUnit) {
        compilationUnitsMapping.put(tree, compilationUnit);
    }

    public void registerMethodTreeCompilationUnit(MethodTree methodTree, CompilationUnitTree compilationUnit) {
        registerTreeCompilationUnit(methodTree, compilationUnit);
        registerTreeCompilationUnit(methodTree.getReturnType(), compilationUnit);
        for (VariableTree parameterTree : methodTree.getParameters()) {
            registerTreeCompilationUnit(parameterTree, compilationUnit);
        }
    }

    /**
     * Returns compilation unit for given tree if a specific mapping has been
     * defined, or null otherwise
     */
//    public CompilationUnitTree getCompilationUnitForTree(Tree tree) {
//        CompilationUnitTree compilationUnit = compilationUnitsMapping.get(tree);
//        return compilationUnit;
//    }

    private Set<MethodInvocationTree> awaitInvocations;

    public void addAwaitInvocation(MethodInvocationTree invocation) {
        if (this.awaitInvocations == null) {
            this.awaitInvocations = new HashSet<>();
        }
        this.awaitInvocations.add(invocation);
    }

    public boolean isAwaitInvocation(MethodInvocationTree invocation) {
        if (this.awaitInvocations != null) {
            return this.awaitInvocations.contains(invocation);
        } else {
            return false;
        }
    }
}