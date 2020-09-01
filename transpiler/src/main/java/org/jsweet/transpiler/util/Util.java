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
package org.jsweet.transpiler.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetContext.DefaultMethodEntry;
import org.jsweet.transpiler.SourcePosition;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

/**
 * Various utilities.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class Util {

    public static final String CONSTRUCTOR_METHOD_NAME = "<init>";

    private final static Logger logger = Logger.getLogger(Util.class);

    protected JSweetContext context;

    public Util(JSweetContext context) {
        this.context = context;
    }

    /**
     * If the given type is a capture and has an upper bound, returns it, else
     * return the given type unchanged.
     */
    public TypeMirror getUpperBound(TypeMirror type) {
        if (type instanceof TypeVariable && ((TypeVariable) type).getUpperBound() != null) {
            return ((TypeVariable) type).getUpperBound();
        } else {
            return type;
        }
    }

    /**
     * Gets the type arguments of a given type (if any).
     */
    public List<? extends TypeMirror> getTypeArguments(TypeMirror type) {
        if (type instanceof DeclaredType) {
            return ((DeclaredType) type).getTypeArguments();
        } else {
            return null;
        }
    }

    /**
     * Gets the qualified name for the given type.
     */
    public String getQualifiedName(TypeMirror type) {
        if (type instanceof DeclaredType) {
            Element e = ((DeclaredType) type).asElement();
            if (e instanceof TypeElement) {
                return ((TypeElement) e).getQualifiedName().toString();
            }
        }
        return type.toString();
    }

    /**
     * Gets the type from an existing runtime class when possible (return null when
     * the type cannot be found in the compiler's symbol table).
     */
    public TypeMirror getType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return types().getPrimitiveType(TypeKind.valueOf(clazz.getName().toUpperCase()));
        }
        TypeElement typeElement = getTypeElementByName(context, clazz.getName());
        return typeElement == null ? null : typeElement.asType();
    }

    /**
     * Gets the source type from the fully qualified name when possible (return null
     * when the type cannot be found).
     * <p/>
     * This method looks up well-known Java types and all the types that are in the
     * complied source files.
     */
    public TypeMirror getType(String fullyQualifiedName) {
        TypeMirror result = null;
        try {
            Class<?> clazz = Class.forName(fullyQualifiedName);
            result = getType(clazz);
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            // swallow
        }
        TypeElement typeElement = context.elements.getTypeElement(fullyQualifiedName);
        if (typeElement != null) {
            return typeElement.asType();
        }
        return null;
    }

    private static long id = 121;

    /**
     * Returns a unique id (incremental).
     */
    public long getId() {
        return id++;
    }

    /**
     * Tells if the given element is within the Java sources being compiled.
     */
    public boolean isSourceElement(Element element) {
        if (element == null || element instanceof PackageElement) {
            return false;
        }
        if (element instanceof TypeElement) {
            TypeElement clazz = (TypeElement) element;
            // hack to know if it is a source file or a class file
            JavaFileObject sourceFile = javacInternals().getSourceFileObjectFromElement(clazz);
            if (sourceFile != null
                    && (sourceFile.getClass().getName().equals("com.sun.tools.javac.file.RegularFileObject")
                            || sourceFile.getClass().getName()
                                    .equals("com.sun.tools.javac.file.PathFileObject$SimpleFileObject"))) {
                return true;
            }
        }
        return isSourceElement(element.getEnclosingElement());
    }

    /**
     * Gets the source file path of the given element.
     */
    public String getSourceFilePath(Element element) {
        if (element == null || element instanceof PackageElement) {
            return null;
        }
        if (element instanceof TypeElement) {
            TypeElement clazz = (TypeElement) element;
            if (isSourceElement(clazz)) {
                return javacInternals().getSourceFileObjectFromElement(clazz).getName();
            }
        }
        return getSourceFilePath(element.getEnclosingElement());
    }

    public boolean isInSameSourceFile(CompilationUnitTree compilationUnitTree, Element element) {
        if (compilationUnitTree == null || element == null) {
            return false;
        }
        assert isSourceElement(element) : "unsupported element type (from a class file and not a source file)";

        return getSourceFilePath(element).equals(compilationUnitTree.getSourceFile().getName());
    }

    public static <T> T firstOrDefault(Iterable<T> iterable) {
        return iterable.iterator().next();
    }

    /**
     * Gets the tree that corresponds to the given element (this is a slow
     * implementation - do not use intensively).
     * 
     * @param context the transpiler's context
     * @param element the element to lookup
     * @return the javac AST that corresponds to that element
     */
    public Tree lookupTree(JSweetContext context, Element element) {
        if (element == null || element instanceof PackageElement) {
            return null;
        }
        Element rootClass = getRootClassElement(element);
        if (rootClass instanceof TypeElement) {
            TypeElement clazz = (TypeElement) rootClass;
            if (isSourceElement(clazz)) {
                Tree[] result = { null };
                for (int i = 0; i < context.sourceFiles.length; i++) {
                    if (new File(javacInternals().getSourceFileObjectFromElement(clazz).getName())
                            .equals(context.sourceFiles[i].getJavaFile())) {
                        CompilationUnitTree cu = context.compilationUnits.get(i);
                        new TreePathScanner<Void, Trees>() {

                            @Override
                            public Void visitClass(ClassTree tree, Trees trees) {
                                if (trees.getElement(getCurrentPath()) == element) {
                                    result[0] = tree;
                                } else {
                                    super.visitClass(tree, trees);
                                }
                                return null;
                            }

                            @Override
                            public Void visitMethod(MethodTree tree, Trees trees) {
                                if (trees.getElement(getCurrentPath()) == element) {
                                    result[0] = tree;
                                }
                                return null;
                            }

                            @Override
                            public Void visitVariable(VariableTree tree, Trees trees) {
                                if (trees.getElement(getCurrentPath()) == element) {
                                    result[0] = tree;
                                } else {
                                    super.visitVariable(tree, trees);
                                }
                                return null;
                            }
                        }.scan(cu, trees());
                        return result[0];
                    }
                }
            }
        }
        return null;
    }

    private static Element getRootClassElement(Element element) {
        if (element == null) {
            return null;
        }
        if (element instanceof TypeElement
                && (element.getEnclosingElement() == null || element.getEnclosingElement() instanceof PackageElement)) {
            return element;
        } else {
            return getRootClassElement(element.getEnclosingElement());
        }
    }

    public static class Static {

        /**
         * Recursively adds files to the given list.
         * 
         * @param extension the extension to filter with
         * @param file      the root file/directory to look into recursively
         * @param files     the list to add the files matching the extension
         */
        public static void addFiles(String extension, File file, Collection<File> files) {
            addFiles(f -> f.getName().endsWith(extension), file, files);
        }

        /**
         * Recursively adds files to the given list.
         * 
         * @param filter the filter predicate to apply (only files matching the
         *               predicate will be added)
         * @param file   the root file/directory to look into recursively
         * @param files  the list to add the files matching the extension
         */
        public static void addFiles(Predicate<File> filter, File file, Collection<File> files) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    addFiles(filter, f, files);
                }
            } else if (filter.test(file)) {
                files.add(file);
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public static <T> T newInstance(String fullClassName) {
            String errorMessage = "cannot find or instantiate class: " + fullClassName
                    + " (make sure the class is in the plugin's classpath and that it defines an empty public constructor)";

            Class<T> clazz = null;
            try {
                clazz = (Class) Thread.currentThread().getContextClassLoader().loadClass(fullClassName);
            } catch (Exception e) {
                try {
                    // try forName just in case
                    clazz = (Class) Class.forName(fullClassName);
                } catch (Exception e2) {
                    throw new RuntimeException(errorMessage, e2);
                }
            }

            try {
                Constructor<T> constructor = clazz.getConstructor();
                return constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(errorMessage, e);
            }
        }
    }

    /**
     * Recursively adds files to the given list.
     * 
     * @param extension the extension to filter with
     * @param file      the root file/directory to look into recursively
     * @param files     the list to add the files matching the extension
     */
    public void addFiles(String extension, File file, Collection<File> files) {
        Static.addFiles(extension, file, files);
    }

    /**
     * Recursively adds files to the given list.
     * 
     * @param filter the filter predicate to apply (only files matching the
     *               predicate will be added)
     * @param file   the root file/directory to look into recursively
     * @param files  the list to add the files matching the extension
     */
    public void addFiles(Predicate<File> filter, File file, Collection<File> files) {
        Static.addFiles(filter, file, files);
    }

    /**
     * Gets the full signature of the given method.
     */
    public String getFullMethodSignature(ExecutableElement method) {
        TypeElement enclosingType = getParentElement(method, TypeElement.class);
        return enclosingType.getQualifiedName() + "." + method.toString();
    }

    /**
     * Tells if the given type declaration contains some method declarations.
     */
    public boolean containsMethods(ClassTree classDeclaration, CompilationUnitTree compilationUnit) {
        for (Tree member : classDeclaration.getMembers()) {
            if (member instanceof MethodTree) {
                MethodTree method = (MethodTree) member;

                long methodPos = sourcePositions().getStartPosition(compilationUnit, method);
                long classDeclarationPos = sourcePositions().getStartPosition(compilationUnit, classDeclaration);
                if (methodPos == classDeclarationPos) {
                    continue;
                }
                return true;
            } else if (member instanceof VariableTree) {
                if (((VariableTree) member).getModifiers().getFlags().contains(Modifier.STATIC)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void putVar(Map<String, VariableElement> vars, VariableElement varSymbol) {
        vars.put(varSymbol.getSimpleName().toString(), varSymbol);
    }

    /**
     * Finds all the variables accessible within the current scanning scope.
     */
    public void fillAllVariablesInScope(Map<String, VariableElement> vars, Stack<Tree> scanningStack, Tree from,
            Tree to, CompilationUnitTree compilationUnit) {
        if (from == to) {
            return;
        }
        int i = scanningStack.indexOf(from);
        if (i == -1 || i == 0) {
            return;
        }
        Tree parent = scanningStack.get(i - 1);
        List<? extends StatementTree> statements = null;
        switch (parent.getKind()) {
        case BLOCK:
            statements = ((BlockTree) parent).getStatements();
            break;
        case CASE:
            statements = ((CaseTree) parent).getStatements();
            break;
        case CATCH:
            putVar(vars, getElementForTree(((CatchTree) parent).getParameter(), compilationUnit));
            break;
        case FOR_LOOP:
            if (((ForLoopTree) parent).getInitializer() != null) {
                for (StatementTree s : ((ForLoopTree) parent).getInitializer()) {
                    if (s instanceof VariableTree) {
                        putVar(vars, getElementForTree(s, compilationUnit));
                    }
                }
            }
            break;
        case ENHANCED_FOR_LOOP:
            putVar(vars, getElementForTree(((EnhancedForLoopTree) parent).getVariable(), compilationUnit));
            break;
        case METHOD:
            for (VariableTree var : ((MethodTree) parent).getParameters()) {
                putVar(vars, getElementForTree(var, compilationUnit));
            }
            break;
        default:

        }
        if (statements != null) {
            for (StatementTree s : statements) {
                if (s == from) {
                    break;
                } else if (s instanceof VariableTree) {
                    putVar(vars, getElementForTree(s, compilationUnit));
                }
            }
        }
        fillAllVariablesInScope(vars, scanningStack, parent, to, compilationUnit);
    }

    public List<ClassTree> findTypeDeclarationsInCompilationUnits(List<CompilationUnitTree> compilationUnits) {
        List<ClassTree> symbols = new LinkedList<>();
        for (CompilationUnitTree compilationUnit : compilationUnits) {
            for (Tree definition : compilationUnit.getTypeDecls()) {
                if (definition instanceof ClassTree) {
                    symbols.add((ClassTree) definition);
                }
            }
        }

        return symbols;
    }

    public List<MethodTree> findMethodDeclarations(ClassTree typeDeclaration) {
        List<MethodTree> methods = new LinkedList<>();
        for (Tree definition : typeDeclaration.getMembers()) {
            if (definition instanceof MethodTree) {
                methods.add((MethodTree) definition);
            }
        }

        return methods;
    }

    public MethodTree findFirstMethodDeclaration(ClassTree typeDeclaration, String methodName) {
        return findMethodDeclarations(typeDeclaration).stream() //
                .filter(methodDecl -> methodDecl.getName().toString().equals(methodName)) //
                .findFirst().orElse(null);
    }

    /**
     * Fills the given map with all the variables beeing accessed within the given
     * code tree.
     */
    public void fillAllVariableAccesses(final Map<String, VariableElement> vars, final Tree tree,
            final CompilationUnitTree compilationUnit) {
        new TreeScanner<Void, Trees>() {
            @Override
            public Void visitIdentifier(IdentifierTree identifierTree, Trees trees) {
                Element element = getElementForTree(identifierTree, compilationUnit);
                if (element.getKind() == ElementKind.LOCAL_VARIABLE) {
                    putVar(vars, (VariableElement) element);
                }
                return null;
            }

            public Void visitLambdaExpression(LambdaExpressionTree lambdaTree, Trees trees) {
                if (lambdaTree == tree) {
                    super.visitLambdaExpression(lambdaTree, trees);
                }
                return null;
            }

        }.scan(tree, trees());
    }

    /**
     * Finds the method declaration within the given type, for the given invocation.
     */
    public ExecutableElement findMethodDeclarationInType(TypeElement typeSymbol, MethodInvocationTree invocation) {
        ExpressionTree method = invocation.getMethodSelect();
        String methName = method.toString().substring(method.toString().lastIndexOf('.') + 1);

        TypeMirror methodType = getTypeForTree(method, trees().getPath(typeSymbol).getCompilationUnit());

        return findMethodDeclarationInType(typeSymbol, methName, (ExecutableType) methodType);
    }

    /**
     * Finds the method in the given type that matches the given name and signature.
     */
    public ExecutableElement findMethodDeclarationInType(TypeElement typeSymbol, String methodName,
            ExecutableType methodType) {
        return findMethodDeclarationInType(typeSymbol, methodName, methodType, false);
    }

    /**
     * Finds the method in the given type that matches the given name and signature.
     */
    public ExecutableElement findMethodDeclarationInType(TypeElement typeSymbol, String methodName,
            ExecutableType methodType, boolean overrides) {

        // gathers all the potential method matches
        List<ExecutableElement> candidates = new LinkedList<>();
        collectMatchingMethodDeclarationsInType(typeSymbol, methodName, methodType, overrides, candidates);

        // score them
        ExecutableElement bestMatch = null;
        int lastScore = Integer.MIN_VALUE;
        for (ExecutableElement candidate : candidates) {
            int currentScore = getCandidateMethodMatchScore(candidate, methodType);
            if (bestMatch == null || currentScore > lastScore) {
                bestMatch = candidate;
                lastScore = currentScore;
            }
        }

        // return the best match
        if (logger.isTraceEnabled()) {
            logger.trace("method declaration match for " + typeSymbol + "." + methodName + " - " + methodType + " : "
                    + bestMatch + " score=" + lastScore);
        }
        return bestMatch;
    }

    private static int getCandidateMethodMatchScore(ExecutableElement candidate, ExecutableType methodType) {

        if (methodType == null || candidate.getParameters().size() != methodType.getParameterTypes().size()) {
            return -50;
        }

        int score = 0;

        boolean isAbstract = candidate.getModifiers().contains(Modifier.ABSTRACT);
        if (isAbstract) {
            score -= 30;
        }
        for (int i = 0; i < candidate.getParameters().size(); i++) {
            TypeMirror candidateParamType = candidate.getParameters().get(i).asType();
            TypeMirror paramType = methodType.getParameterTypes().get(i);

            if (!candidateParamType.equals(paramType)) {
                score--;
            }
        }

        return score;
    }

    private void collectMatchingMethodDeclarationsInType(TypeElement typeSymbol, String methodName,
            ExecutableType methodType, boolean overrides, List<ExecutableElement> collector) {
        if (typeSymbol == null) {
            return;
        }
        if (typeSymbol.getEnclosedElements() != null) {
            for (Element element : typeSymbol.getEnclosedElements()) {
                if ((element instanceof ExecutableElement) && (methodName.equals(element.getSimpleName().toString())
                        || ((ExecutableElement) element).getKind() == ElementKind.CONSTRUCTOR
                                && ("this".equals(methodName) /* || "super".equals(methodName) */))) {
                    ExecutableElement methodSymbol = (ExecutableElement) element;
                    if (methodType == null) {
                        collector.add(methodSymbol);
                    } else if (overrides ? isInvocable((ExecutableType) methodSymbol.asType(), methodType)
                            : isInvocable(methodType, (ExecutableType) methodSymbol.asType())) {
                        collector.add(methodSymbol);
                    }
                }
            }
        }
        if (typeSymbol.getSuperclass() != null) {
            if (!overrides || !Object.class.getName().equals(typeSymbol.getSuperclass().toString())) {

                collectMatchingMethodDeclarationsInType((TypeElement) types().asElement(typeSymbol.getSuperclass()),
                        methodName, methodType, overrides, collector);
            }
        }
        if (typeSymbol.getInterfaces() != null) {
            for (TypeElement interfaceElement : getInterfaces(typeSymbol)) {
                collectMatchingMethodDeclarationsInType(interfaceElement, methodName, methodType, overrides, collector);
            }
        }
    }

    /**
     * Finds methods by name.
     */
    public void findMethodDeclarationsInType(TypeElement typeSymbol, Collection<String> methodNames,
            Set<String> ignoredTypeNames, List<ExecutableElement> result) {
        if (typeSymbol == null) {
            return;
        }
        if (ignoredTypeNames.contains(typeSymbol.getQualifiedName().toString())) {
            return;
        }
        if (typeSymbol.getEnclosedElements() != null) {
            for (Element element : typeSymbol.getEnclosedElements()) {
                if ((element instanceof ExecutableElement)
                        && (methodNames.contains(element.getSimpleName().toString()))) {
                    result.add((ExecutableElement) element);
                }
            }
        }
        if (typeSymbol instanceof TypeElement && ((TypeElement) typeSymbol).getSuperclass() != null) {
            findMethodDeclarationsInType(getSuperclass((TypeElement) typeSymbol), methodNames, ignoredTypeNames,
                    result);
        }
        if (result == null) {
            if (typeSymbol instanceof TypeElement && ((TypeElement) typeSymbol).getInterfaces() != null) {
                for (TypeElement interfaceElement : getInterfaces(typeSymbol)) {
                    findMethodDeclarationsInType(interfaceElement, methodNames, ignoredTypeNames, result);
                }
            }
        }
    }

    public TypeElement getSuperclass(TypeElement element) {
        return (TypeElement) types().asElement(element.getSuperclass());
    }

    public List<TypeElement> getInterfaces(TypeElement element) {
        return element.getInterfaces().stream().map(interfaceType -> (TypeElement) types().asElement(interfaceType))
                .collect(toList());
    }

    /**
     * Finds first method matching name (no super types lookup).
     */
    public ExecutableElement findFirstMethodDeclarationInType(Element typeSymbol, String methodName) {
        if (typeSymbol == null) {
            return null;
        }
        if (typeSymbol.getEnclosedElements() != null) {
            for (Element element : typeSymbol.getEnclosedElements()) {
                if ((element instanceof ExecutableElement) && (methodName.equals(element.getSimpleName().toString()))) {
                    return (ExecutableElement) element;
                }
            }
        }
        return null;
    }

    /**
     * Tells if the given element is deprecated.
     */
    public boolean isDeprecated(Element element) {
        return element.getAnnotation(Deprecated.class) != null;
    }

    /**
     * Find first declaration (of any kind) matching the given name.
     */
    public Element findFirstDeclarationInType(Element typeSymbol, String name) {
        if (typeSymbol == null) {
            return null;
        }
        if (typeSymbol.getEnclosedElements() != null) {
            for (Element element : typeSymbol.getEnclosedElements()) {
                if (name.equals(element.getSimpleName().toString())) {
                    return (Element) element;
                }
            }
        }
        return null;
    }

    /**
     * @see #findFirstDeclarationInClassAndSuperClasses(TypeElement, String,
     *      ElementKind, Integer)
     */
    public Element findFirstDeclarationInClassAndSuperClasses(TypeElement typeSymbol, String name, ElementKind kind) {
        return findFirstDeclarationInClassAndSuperClasses(typeSymbol, name, kind, null);
    }

    /**
     * Find first declaration (of any kind) matching the given name (and optionally
     * the given number of arguments for methods)
     */
    public Element findFirstDeclarationInClassAndSuperClasses(TypeElement typeSymbol, String name, ElementKind kind,
            Integer methodArgsCount) {
        if (typeSymbol == null) {
            return null;
        }
        if (typeSymbol.getEnclosedElements() != null) {
            for (Element element : typeSymbol.getEnclosedElements()) {
                if (name.equals(element.getSimpleName().toString()) && element.getKind() == kind
                        && (methodArgsCount == null
                                || methodArgsCount.equals(((ExecutableElement) element).getParameters().size()))) {
                    return (Element) element;
                }
            }
        }
        if (typeSymbol instanceof TypeElement) {
            Element s = findFirstDeclarationInClassAndSuperClasses(getSuperclass((TypeElement) typeSymbol), name, kind,
                    methodArgsCount);
            if (s == null && kind == ElementKind.METHOD) {
                // also looks up in interfaces for methods
                for (TypeElement interfaceElement : getInterfaces((TypeElement) typeSymbol)) {
                    s = findFirstDeclarationInClassAndSuperClasses(interfaceElement, name, kind, methodArgsCount);
                    if (s != null) {
                        break;
                    }
                }
            }
            return s;
        } else {
            return null;
        }
    }

    /**
     * Scans member declarations in type hierachy.
     */
    public boolean scanMemberDeclarationsInType(TypeElement typeSymbol, Set<String> ignoredTypeNames,
            Function<Element, Boolean> scanner) {
        if (typeSymbol == null) {
            return true;
        }
        if (ignoredTypeNames.contains(typeSymbol.getQualifiedName().toString())) {
            return true;
        }
        if (typeSymbol.getEnclosedElements() != null) {
            for (Element element : typeSymbol.getEnclosedElements()) {
                if (!scanner.apply(element)) {
                    return false;
                }
            }
        }
        if (typeSymbol instanceof TypeElement && ((TypeElement) typeSymbol).getSuperclass() != null) {
            if (!scanMemberDeclarationsInType(getSuperclass((TypeElement) typeSymbol), ignoredTypeNames, scanner)) {
                return false;
            }
        }
        if (typeSymbol instanceof TypeElement && ((TypeElement) typeSymbol).getInterfaces() != null) {
            for (TypeElement interfaceElement : getInterfaces((TypeElement) typeSymbol)) {
                if (!scanMemberDeclarationsInType(interfaceElement, ignoredTypeNames, scanner)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds and returns the parameter matching the given name if any.
     */
    public VariableTree findParameter(MethodTree method, String name) {
        for (VariableTree parameter : method.getParameters()) {
            if (name.equals(parameter.getName().toString())) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * Tells if a method can be invoked with some given parameter types.
     * 
     * @param types  a reference to the types in the compilation scope
     * @param from   the caller method signature to test (contains the parameter
     *               types)
     * @param target the callee method signature
     * @return true if the callee can be invoked by the caller
     */
    public boolean isInvocable(ExecutableType from, ExecutableType target) {
        if (from.getParameterTypes().size() != target.getParameterTypes().size()) {
            return false;
        }
        for (int i = 0; i < from.getParameterTypes().size(); i++) {
            if (!types().isAssignable(types().erasure(from.getParameterTypes().get(i)),
                    types().erasure(target.getParameterTypes().get(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the TypeScript initial default value for a type.
     */
    public String getTypeInitalValue(String typeName) {
        if (typeName == null) {
            return "null";
        }
        switch (typeName) {
        case "void":
            return null;
        case "boolean":
            return "false";
        case "number":
            return "0";
        default:
            return "null";
        }
    }

    /**
     * Fills the given set with all the default methods found in the current type
     * and super interfaces.
     */
    public void findDefaultMethodsInType(Set<DefaultMethodEntry> defaultMethods, JSweetContext context,
            TypeElement typeElement) {
        if (context.getDefaultMethods(typeElement) != null) {
            defaultMethods.addAll(context.getDefaultMethods(typeElement));
        }
        for (TypeElement interfaceElement : getInterfaces((TypeElement) typeElement)) {
            findDefaultMethodsInType(defaultMethods, context, interfaceElement);
        }
    }

    /**
     * Finds the field in the given type that matches the given name.
     */
    public VariableElement findFieldDeclaration(TypeElement classSymbol, Name name) {
        if (classSymbol == null) {
            return null;
        }

        for (Element member : classSymbol.getEnclosedElements()) {
            if (member instanceof VariableElement) {
                VariableElement field = (VariableElement) member;
                if (field.getSimpleName().toString().equals(name.toString())) {
                    return field;
                }
            }
        }

        if (classSymbol.getSuperclass() != null) {
            return findFieldDeclaration(getSuperclass(classSymbol), name);
        }

        return null;
    }

    /**
     * Tells if this qualified name denotes a JSweet globals class.
     */
    public boolean isGlobalsClassName(String qualifiedName) {
        return qualifiedName != null && (JSweetConfig.GLOBALS_CLASS_NAME.equals(qualifiedName)
                || qualifiedName.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME));
    }

    /**
     * Tells if this parameter declaration is varargs.
     */
    public boolean isVarargs(VariableTree varDecl, CompilationUnitTree compilationUnitTree) {
        VariableElement varElement = getElementForTree(varDecl, compilationUnitTree);
        return isVarargs(varElement);
    }

    /**
     * Tells if this variable is a varargs parameter element
     */
    public boolean isVarargs(VariableElement varElement) {
        Element varOwner = varElement.getEnclosingElement();
        if (!(varOwner instanceof ExecutableElement)) {
            return false;
        }
        ExecutableElement methodElement = (ExecutableElement) varOwner;
        return methodElement.isVarArgs() //
                && methodElement.getParameters().size() > 0 //
                && last(methodElement.getParameters()) == varElement;
    }

    /**
     * Gets the file from a Java file object.
     */
    public File toFile(JavaFileObject javaFileObject) {
        return new File(javaFileObject.getName());
    }

    /**
     * Transforms a list of source files to Java file objects (used by javac).
     */
    public List<JavaFileObject> toJavaFileObjects(StandardJavaFileManager fileManager, Collection<File> sourceFiles) {
        Iterable<? extends JavaFileObject> javaFileObjectsIterable = fileManager
                .getJavaFileObjectsFromFiles(sourceFiles);
        return iterableToList(javaFileObjectsIterable);
    }

    public <T> List<T> iterableToList(Iterable<? extends T> iterable) {
        List<T> result = new ArrayList<T>();
        iterable.forEach(result::add);
        return result;
    }

    /**
     * Transforms a source file to a Java file object (used by javac).
     */
    public JavaFileObject toJavaFileObject(StandardJavaFileManager fileManager, File sourceFile) throws IOException {
        List<JavaFileObject> javaFileObjects = toJavaFileObjects(fileManager, asList(sourceFile));
        return javaFileObjects.isEmpty() ? null : javaFileObjects.get(0);
    }

    private final static Pattern REGEX_CHARS = Pattern.compile("([\\\\*+\\[\\](){}\\$.?\\^|])");

    /**
     * This function will escape special characters within a string to ensure that
     * the string will not be parsed as a regular expression. This is helpful with
     * accepting using input that needs to be used in functions that take a regular
     * expression as an argument (such as String.replaceAll(), or String.split()).
     * 
     * @param regex - argument which we wish to escape.
     * @return - Resulting string with the following characters escaped:
     *         [](){}+*^?$.\
     */
    public String escapeRegex(final String regex) {
        Matcher match = REGEX_CHARS.matcher(regex);
        return match.replaceAll("\\\\$1");
    }

    /**
     * Varargs to mutable list.
     */
    @SafeVarargs
    public final <T> List<T> list(T... items) {
        ArrayList<T> list = new ArrayList<T>(items.length);
        for (T item : items) {
            list.add(item);
        }
        return list;
    }

    /**
     * Tells if two type symbols are assignable.
     */
    public boolean isAssignable(Types types, TypeElement to, TypeElement from) {
        if (to.equals(from)) {
            return true;
        } else {
            return types.isAssignable(from.asType(), to.asType());
        }
    }

    private final static List<TypeKind> IS_ASSIGABLE_FORBBIDEN_TYPES = asList(TypeKind.EXECUTABLE, TypeKind.PACKAGE,
            TypeKind.MODULE);

    /**
     * Tells if the given list contains an type which is assignable from type.
     */
    public boolean containsAssignableType(List<? extends TypeMirror> list, TypeMirror type) {
        for (TypeMirror t : list) {
            if (types().isSameType(t, type)) {
                return true;
            }

            if (!IS_ASSIGABLE_FORBBIDEN_TYPES.contains(t.getKind()) //
                    && !IS_ASSIGABLE_FORBBIDEN_TYPES.contains(type.getKind()) //
                    && types().isAssignable(t, type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the relative path to reach a symbol from another one.
     * 
     * @param fromSymbol the start path
     * @param toSymbol   the end path
     * @return the '/'-separated path
     * @see #getRelativePath(String, String)
     */
    public String getRelativePath(Element fromSymbol, Element toSymbol) {
        return getRelativePath("/" + getQualifiedName(fromSymbol).replace('.', '/'),
                "/" + getQualifiedName(toSymbol).replace('.', '/'));
    }

    /**
     * Gets the relative path that links the two given paths.
     * 
     * <pre>
     * assertEquals("../c", util.getRelativePath("/a/b", "/a/c"));
     * assertEquals("..", util.getRelativePath("/a/b", "/a"));
     * assertEquals("../e", util.getRelativePath("/a/b/c", "/a/b/e"));
     * assertEquals("d", util.getRelativePath("/a/b/c", "/a/b/c/d"));
     * assertEquals("d/e", util.getRelativePath("/a/b/c", "/a/b/c/d/e"));
     * assertEquals("../../../d/e/f", util.getRelativePath("/a/b/c", "/d/e/f"));
     * assertEquals("../..", util.getRelativePath("/a/b/c", "/a"));
     * assertEquals("..", util.getRelativePath("/a/b/c", "/a/b"));
     * </pre>
     * 
     * <p>
     * Thanks to:
     * http://mrpmorris.blogspot.com/2007/05/convert-absolute-path-to-relative-
     * path.html
     * 
     * <p>
     * Bug fix: Renaud Pawlak
     * 
     * @param fromPath the path to start from
     * @param toPath   the path to reach
     */
    public String getRelativePath(String fromPath, String toPath) {
        StringBuilder relativePath = null;

        fromPath = fromPath.replaceAll("\\\\", "/");
        toPath = toPath.replaceAll("\\\\", "/");

        if (!fromPath.equals(toPath)) {
            String[] fromSegments = fromPath.split("/");
            String[] toSegments = toPath.split("/");

            // Get the shortest of the two paths
            int length = fromSegments.length < toSegments.length ? fromSegments.length : toSegments.length;

            // Use to determine where in the loop we exited
            int lastCommonRoot = -1;
            int index;

            // Find common root
            for (index = 0; index < length; index++) {
                if (fromSegments[index].equals(toSegments[index])) {
                    lastCommonRoot = index;
                } else {
                    break;
                    // If we didn't find a common prefix then throw
                }
            }
            if (lastCommonRoot != -1) {
                // Build up the relative path
                relativePath = new StringBuilder();
                // Add on the ..
                for (index = lastCommonRoot + 1; index < fromSegments.length; index++) {
                    if (fromSegments[index].length() > 0) {
                        relativePath.append("../");
                    }
                }
                for (index = lastCommonRoot + 1; index < toSegments.length - 1; index++) {
                    relativePath.append(toSegments[index] + "/");
                }
                if (!fromPath.startsWith(toPath)) {
                    relativePath.append(toSegments[toSegments.length - 1]);
                } else {
                    if (relativePath.length() > 0) {
                        relativePath.deleteCharAt(relativePath.length() - 1);
                    }
                }
            }
        }
        return relativePath == null ? null : relativePath.toString();
    }

    /**
     * Removes the extensions of the given file name.
     * 
     * @param fileName the given file name (can contain path)
     * @return the file name without the extension
     */
    public String removeExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    /**
     * Tells if the given directory or any of its sub-directory contains one of the
     * given files.
     * 
     * @param dir   the directory to look into
     * @param files the files to be found
     * @return true if one of the given files is found
     */
    public boolean containsFile(File dir, File[] files) {
        for (File child : dir.listFiles()) {
            if (child.isDirectory()) {
                if (containsFile(child, files)) {
                    return true;
                }
            } else {
                for (File file : files) {
                    if (child.getAbsolutePath().equals(file.getAbsolutePath())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true is the type is an integral numeric value.
     */
    public boolean isIntegral(TypeMirror type) {
        if (type == null) {
            return false;
        }
        switch (type.getKind()) {
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true is the type is a number.
     */
    public boolean isNumber(TypeMirror type) {
        if (type == null) {
            return false;
        }
        switch (type.getKind()) {
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
        case DOUBLE:
        case FLOAT:
            return true;
        default:
            return false;
        }
    }

    public boolean isStringType(TypeMirror type) {
        return isType(type, String.class);
    }

    public boolean isVoidType(TypeMirror type) {
        return isType(type, Void.class) || type.getKind() == TypeKind.VOID;
    }

    /**
     * Returns true if given type is the type of given class instance
     */
    public boolean isType(TypeMirror type, Class<?> potentielTypeClass) {
        return type != null && potentielTypeClass != null && types().isSameType(getType(potentielTypeClass), type);
    }

    /**
     * Returns true is the type is a core (String or primitive)
     */
    public boolean isCoreType(TypeMirror type) {
        if (type == null) {
            return false;
        }
        if (isStringType(type)) {
            return true;
        }
        switch (type.getKind()) {
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
        case DOUBLE:
        case FLOAT:
        case BOOLEAN:
        case CHAR:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns operator equivalent of given tree's kind. For instance, PLUS would
     * return "+"
     */
    public String toOperator(Kind kind) {
        switch (kind) {
        case MINUS:
        case UNARY_MINUS:
            return "-";
        case PLUS:
        case UNARY_PLUS:
            return "+";
        case MULTIPLY:
            return "*";
        case DIVIDE:
            return "/";
        case AND:
            return "&";
        case AND_ASSIGNMENT:
            return "&=";
        case OR_ASSIGNMENT:
            return "|=";
        case DIVIDE_ASSIGNMENT:
            return "/=";
        case REMAINDER_ASSIGNMENT:
            return "%=";
        case LEFT_SHIFT_ASSIGNMENT:
            return "<<=";
        case RIGHT_SHIFT_ASSIGNMENT:
            return ">>=";
        case MINUS_ASSIGNMENT:
            return "-=";
        case MULTIPLY_ASSIGNMENT:
            return "*=";
        case PLUS_ASSIGNMENT:
            return "+=";
        case XOR_ASSIGNMENT:
            return "^=";
        case LEFT_SHIFT:
            return "<<";
        case RIGHT_SHIFT:
            return ">>";
        case OR:
            return "|";
        case XOR:
            return "^";
        case ASSIGNMENT:
            return "=";
        case BITWISE_COMPLEMENT:
            return "~";
        case CONDITIONAL_AND:
            return "&&";
        case CONDITIONAL_OR:
            return "||";
        case EQUAL_TO:
            return "==";
        case GREATER_THAN:
            return ">";
        case GREATER_THAN_EQUAL:
            return ">=";
        case LESS_THAN:
            return "<";
        case LESS_THAN_EQUAL:
            return "<=";
        case LOGICAL_COMPLEMENT:
            return "!";
        case NOT_EQUAL_TO:
            return "!=";
        case POSTFIX_DECREMENT:
        case PREFIX_DECREMENT:
            return "--";
        case POSTFIX_INCREMENT:
        case PREFIX_INCREMENT:
            return "++";
        case REMAINDER:
            return "%";
        case UNSIGNED_RIGHT_SHIFT:
            return ">>>";
        case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
            return ">>>=";
        default:
            return null;
        }

    }

    /**
     * Returns true is an arithmetic operator.
     */
    public boolean isArithmeticOrLogicalOperator(Kind kind) {
        switch (kind) {
        case MINUS:
        case PLUS:
        case MULTIPLY:
        case DIVIDE:
        case AND:
        case AND_ASSIGNMENT:
        case OR_ASSIGNMENT:
        case DIVIDE_ASSIGNMENT:
        case REMAINDER_ASSIGNMENT:
        case LEFT_SHIFT_ASSIGNMENT:
        case RIGHT_SHIFT_ASSIGNMENT:
        case MINUS_ASSIGNMENT:
        case MULTIPLY_ASSIGNMENT:
        case PLUS_ASSIGNMENT:
        case XOR_ASSIGNMENT:
        case LEFT_SHIFT:
        case RIGHT_SHIFT:
        case UNSIGNED_RIGHT_SHIFT:
        case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
        case OR:
        case XOR:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true is an arithmetic operator.
     */
    public boolean isArithmeticOperator(Kind kind) {
        switch (kind) {
        case MINUS:
        case PLUS:
        case MULTIPLY:
        case DIVIDE:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true is an comparison operator.
     */
    public boolean isComparisonOperator(Kind kind) {
        switch (kind) {
        case GREATER_THAN:
        case GREATER_THAN_EQUAL:
        case LESS_THAN:
        case LESS_THAN_EQUAL:
        case EQUAL_TO:
        case NOT_EQUAL_TO:
            return true;
        default:
            return false;
        }
    }

    /**
     * Looks up a class in the given class hierarchy and returns true if found.
     */
    public boolean isParent(TypeElement type, TypeElement toFind) {
        if (!(type instanceof TypeElement)) {
            return false;
        }
        TypeElement clazz = (TypeElement) type;
        if (clazz.equals(toFind)) {
            return true;
        }
        if (isParent(getSuperclass(clazz), toFind)) {
            return true;
        }
        for (TypeElement interfaceElement : getInterfaces(clazz)) {
            if (isParent(interfaceElement, toFind)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recursively looks up one of the given types in the type hierachy of the given
     * class.
     * 
     * @return true if one of the given names is found as a superclass or a
     *         superinterface
     */
    public boolean hasParent(TypeElement clazz, String... qualifiedNamesToFind) {
        if (clazz == null) {
            return false;
        }
        if (ArrayUtils.contains(qualifiedNamesToFind, clazz.getQualifiedName().toString())) {
            return true;
        }
        if (hasParent(getSuperclass(clazz), qualifiedNamesToFind)) {
            return true;
        }
        for (TypeElement interfaceElement : getInterfaces(clazz)) {
            if (hasParent(interfaceElement, qualifiedNamesToFind)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks up a package element from its qualified name.
     * 
     * @return null if not found
     */
    public PackageElement getPackageByName(String qualifiedName) {
        Iterator<? extends PackageElement> matchingPackagesIterator = elements().getAllPackageElements(qualifiedName)
                .iterator();
        return matchingPackagesIterator.hasNext() ? matchingPackagesIterator.next() : null;
    }

    /**
     * Returns given compilation unit's package full name
     */
    public String getPackageFullNameForCompilationUnit(CompilationUnitTree compilationUnitTree) {
        PackageTree compilUnitPackage = compilationUnitTree.getPackage();
        if (compilUnitPackage == null) {
            return "";
        }

        return compilUnitPackage.getPackageName().toString();
    }

    /**
     * Looks up a type element from its qualified name.
     * 
     * @return null if not found
     */
    public TypeElement getTypeElementByName(JSweetContext context, String qualifiedName) {
        return elements().getTypeElement(qualifiedName);
    }

    /**
     * Tells if the given method has varargs.
     */
    public boolean hasVarargs(ExecutableElement methodSymbol) {
        return methodSymbol != null && methodSymbol.getParameters().size() > 0 && methodSymbol.isVarArgs();
    }

    /**
     * Tells if the method uses a type parameter.
     */
    public boolean hasTypeParameters(ExecutableElement methodSymbol) {
        if (methodSymbol != null && methodSymbol.getParameters().size() > 0) {
            for (VariableElement p : methodSymbol.getParameters()) {
                TypeMirror paramType = p.asType();
                if (p.getKind() == ElementKind.TYPE_PARAMETER || paramType.getKind() == TypeKind.TYPEVAR) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tells if the given type is imported in the given compilation unit.
     */
    public boolean isImported(CompilationUnitTree compilationUnit, TypeElement type) {
        for (ImportTree importTree : compilationUnit.getImports()) {
            if (importTree.isStatic()) {
                continue;
            }
            TypeElement importedTypeElement = getImportedTypeElement(importTree, compilationUnit);
            if (importedTypeElement == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tells if the given symbol is statically imported in the given compilation
     * unit.
     */
    public TypeElement getStaticImportTarget(CompilationUnitTree compilationUnit, String name) {
        if (compilationUnit == null) {
            return null;
        }
        for (ImportTree importTree : compilationUnit.getImports()) {
            if (!importTree.isStatic()) {
                continue;
            }
            if (!importTree.getQualifiedIdentifier().toString().endsWith("." + name)) {
                continue;
            }
            return getImportedTypeElement(importTree, compilationUnit);
        }
        return null;
    }

    /**
     * Gets the imported type (wether statically imported or not).
     */
    public TypeElement getImportedTypeElement(ImportTree importTree, CompilationUnitTree compilationUnit) {
        if (!importTree.isStatic()) {
            TypeMirror importedType = getTypeForTree(importTree.getQualifiedIdentifier(), compilationUnit);
            return importedType == null ? null : (TypeElement) types().asElement(importedType);
        } else {
            if (importTree.getQualifiedIdentifier() instanceof MemberSelectTree) {
                MemberSelectTree qualified = (MemberSelectTree) importTree.getQualifiedIdentifier();
                if (qualified.getExpression() instanceof MemberSelectTree) {
                    qualified = (MemberSelectTree) qualified.getExpression();
                }

                Element importedElement = getElementForTree(qualified, compilationUnit);
                if (importedElement instanceof TypeElement) {
                    return (TypeElement) importedElement;
                }
            }
        }
        return null;
    }

    /**
     * Tells if the given expression is a constant.
     */
    public boolean isConstant(ExpressionTree expr, CompilationUnitTree compilationUnit) {
        Element element = getElementForTree(expr, compilationUnit);

        boolean constant = false;
        if (expr instanceof LiteralTree) {
            constant = true;
        } else if (expr instanceof MemberSelectTree || expr instanceof IdentifierTree) {
            if (element.getModifiers().containsAll(asList(Modifier.STATIC, Modifier.FINAL))) {
                constant = true;
            }
        }
        return constant;
    }

    /**
     * Tells if that tree is the null literal.
     */
    public boolean isNullLiteral(Tree tree) {
        return tree instanceof LiteralTree && ((LiteralTree) tree).getValue() == null;
    }

    /**
     * Tells if that variable is a non-static final field initialized with a literal
     * value.
     */
    public boolean isConstantOrNullField(VariableTree var) {
        return !var.getModifiers().getFlags().contains(Modifier.STATIC)
                && (var.getInitializer() == null || var.getModifiers().getFlags().contains(Modifier.FINAL)
                        && var.getInitializer() instanceof LiteralTree);
    }

    /**
     * Returns the literal for a given type inital value.
     */
    public String getTypeInitialValue(TypeMirror type) {
        if (type == null) {
            return "null";
        }
        if (isNumber(type)) {
            return "0";
        } else if (type.getKind() == TypeKind.BOOLEAN) {
            return "false";
        } else if (type.getKind() == TypeKind.VOID) {
            return null;
        } else {
            return "null";
        }
    }

    public Element getFirstTypeArgumentAsElement(DeclaredType type) {
        if (type == null || type.getTypeArguments().isEmpty()) {
            return null;
        }
        TypeMirror firstTypeArg = type.getTypeArguments().get(0);
        return types().asElement(firstTypeArg);
    }

    public TypeMirror getFirstTypeArgument(DeclaredType type) {
        if (type == null || type.getTypeArguments().isEmpty()) {
            return null;
        }
        return type.getTypeArguments().get(0);
    }

    /**
     * Returns true if the given class declares an abstract method.
     */
    public boolean hasAbstractMethod(TypeElement classSymbol) {
        for (Element member : classSymbol.getEnclosedElements()) {
            if (member instanceof ExecutableElement
                    && ((ExecutableElement) member).getModifiers().contains(Modifier.ABSTRACT)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOverridingBuiltInJavaObjectMethod(ExecutableElement method) {
        switch (method.toString()) {
        case "toString()":
        case "hashCode()":
        case "equals(java.lang.Object)":
            return true;
        }
        return false;
    }

    /**
     * Gets the inheritance-based sorted class declarations.
     * 
     * <p>
     * This method aims at overcoming TypeScrit limitation that forces a parent
     * class to be declared before its child class (it is not the case in Java). So
     * far this is a partial implementation that should cover most cases... for a
     * 100% coverage we would need a much more complicated implementation that is
     * probably not worth it.
     */
    public List<ClassTree> getSortedClassDeclarations(List<? extends Tree> decls, CompilationUnitTree compilationUnit) {
        // return (List<ClassTree>)(Object)decls;
        List<ClassTree> classDecls = decls.stream().filter(d -> d instanceof ClassTree).map(d -> (ClassTree) d)
                .collect(Collectors.toList());

        DirectedGraph<ClassTree> defs = new DirectedGraph<>();
        List<TypeElement> typeElements = classDecls.stream()
                .map(d -> (TypeElement) getElementForTree(d, compilationUnit)).collect(toList());
        defs.add(classDecls.toArray(new ClassTree[0]));
        for (int i = 0; i < typeElements.size(); i++) {
            int superClassIndex = indexOfSuperclass(typeElements, typeElements.get(i));
            if (superClassIndex >= 0) {
                defs.addEdge(classDecls.get(superClassIndex), classDecls.get(i));
            }
        }
        // we assume no cycles are possible
        return defs.topologicalSort(null);
    }

    private int indexOfSuperclass(List<TypeElement> symbols, TypeElement clazz) {
        int superClassIndex = symbols.indexOf(types().asElement(clazz.getSuperclass()));
        // looks up also if any inner class extends a class in the list
        if (superClassIndex < 0) {
            for (Element s : clazz.getEnclosedElements()) {
                if (s instanceof TypeElement) {
                    return indexOfSuperclass(symbols, ((TypeElement) s));
                }
            }
        }
        return superClassIndex;
    }

    /**
     * Finds the first inner class declaration of the given name in the given class
     * hierarchy.
     */
    public TypeElement findInnerClassDeclaration(TypeElement clazz, String name) {
        if (clazz == null) {
            return null;
        }
        for (Element s : clazz.getEnclosedElements()) {
            if (s instanceof TypeElement && s.getSimpleName().toString().equals(name)) {
                return (TypeElement) s;
            }
        }
        if (clazz.getSuperclass() != null) {
            return findInnerClassDeclaration((TypeElement) types().asElement(clazz.getSuperclass()), name);
        }
        return null;
    }

    /**
     * Returns resulting type for this binary operation
     */
    public TypeMirror getOperatorType(BinaryTree binaryTree) {
        if (binaryTree == null) {
            return null;
        }
        return getOperatorElement(binaryTree).getReturnType();
    }

    /**
     * Returns resulting type for this operation
     */
    public TypeMirror getOperatorType(CompoundAssignmentTree compoundAssignmentTree) {
        if (compoundAssignmentTree == null) {
            return null;
        }
        return getOperatorElement(compoundAssignmentTree).getReturnType();
    }

    public ExecutableElement getOperatorElement(BinaryTree binaryTree) {
        if (binaryTree == null) {
            return null;
        }
        try {
            return (ExecutableElement) javacInternals().binaryTreeOperatorField.get(binaryTree);
        } catch (Exception e) {
            throw new RuntimeException("Cannot call internal Javac API :( please adapt this code if API changed", e);
        }
    }

    public ExecutableElement getOperatorElement(CompoundAssignmentTree assignmentTree) {
        if (assignmentTree == null) {
            return null;
        }
        try {
            return (ExecutableElement) javacInternals().assignOpOperatorField.get(assignmentTree);
        } catch (Exception e) {
            throw new RuntimeException("Cannot call internal Javac API :( please adapt this code if API changed", e);
        }
    }

    public boolean isLiteralExpression(ExpressionTree expression) {
        if (expression == null) {
            return false;
        }

        if (expression instanceof LiteralTree) {
            return true;
        }

        if (expression instanceof BinaryTree) {
            return isLiteralExpression(((BinaryTree) expression).getLeftOperand())
                    && isLiteralExpression(((BinaryTree) expression).getRightOperand());
        }

        if (expression instanceof UnaryTree) {
            return isLiteralExpression(((UnaryTree) expression).getExpression());
        }

        return false;
    }

    public List<List<Tree>> getExecutionPaths(MethodTree methodDeclaration) {

        List<List<Tree>> executionPaths = new LinkedList<>();
        executionPaths.add(new LinkedList<>());
        List<List<Tree>> currentPaths = new LinkedList<>(executionPaths);
        List<BreakTree> activeBreaks = new LinkedList<>();
        for (StatementTree statement : methodDeclaration.getBody().getStatements()) {
            collectExecutionPaths(statement, executionPaths, currentPaths, activeBreaks);
        }

        return executionPaths;
    }

    private static void collectExecutionPaths(Tree currentNode, List<List<Tree>> allExecutionPaths,
            List<List<Tree>> currentPaths, List<BreakTree> activeBreaks) {

        for (List<Tree> currentPath : new ArrayList<>(currentPaths)) {
            Tree lastStatement = currentPath.isEmpty() ? null : currentPath.get(currentPath.size() - 1);
            if (lastStatement instanceof ReturnTree
                    || (lastStatement instanceof BreakTree && activeBreaks.contains(lastStatement))) {
                continue;
            }

            if (allExecutionPaths.size() > 20000) {
                throw new RuntimeException("too many execution paths, aborting");
            }

            currentPath.add(currentNode);

            List<List<Tree>> currentPathForksList = pathsList(currentPath);
            if (currentNode instanceof IfTree) {
                IfTree ifNode = (IfTree) currentNode;

                boolean lastIsCurrentPath = true;
                StatementTree[] forks = { ifNode.getThenStatement(), ifNode.getElseStatement() };

                evaluateForksExecutionPaths(allExecutionPaths, currentPathForksList, currentPath, lastIsCurrentPath,
                        activeBreaks, forks);

            } else if (currentNode instanceof BlockTree) {
                for (StatementTree statement : ((BlockTree) currentNode).getStatements()) {
                    collectExecutionPaths(statement, allExecutionPaths, currentPathForksList, activeBreaks);
                }
            } else if (currentNode instanceof LabeledStatementTree) {
                collectExecutionPaths(((LabeledStatementTree) currentNode).getStatement(), allExecutionPaths,
                        currentPaths, activeBreaks);
            } else if (currentNode instanceof ForLoopTree) {
                collectExecutionPaths(((ForLoopTree) currentNode).getStatement(), allExecutionPaths, currentPaths,
                        activeBreaks);

                activeBreaks.clear();
            } else if (currentNode instanceof EnhancedForLoopTree) {
                collectExecutionPaths(((EnhancedForLoopTree) currentNode).getStatement(), allExecutionPaths,
                        currentPaths, activeBreaks);

                activeBreaks.clear();
            } else if (currentNode instanceof WhileLoopTree) {
                collectExecutionPaths(((WhileLoopTree) currentNode).getStatement(), allExecutionPaths, currentPaths,
                        activeBreaks);

                activeBreaks.clear();
            } else if (currentNode instanceof DoWhileLoopTree) {
                collectExecutionPaths(((DoWhileLoopTree) currentNode).getStatement(), allExecutionPaths, currentPaths,
                        activeBreaks);

                activeBreaks.clear();
            } else if (currentNode instanceof SynchronizedTree) {
                collectExecutionPaths(((SynchronizedTree) currentNode).getBlock(), allExecutionPaths, currentPaths,
                        activeBreaks);
            } else if (currentNode instanceof TryTree) {
                TryTree tryNode = (TryTree) currentNode;
                collectExecutionPaths(tryNode.getBlock(), allExecutionPaths, currentPaths, activeBreaks);

                StatementTree[] catchForks = (StatementTree[]) tryNode.getCatches().stream()
                        .map(catchExp -> catchExp.getBlock()).collect(toList()).toArray(new StatementTree[0]);

                evaluateForksExecutionPaths(allExecutionPaths, currentPathForksList, currentPath, false, activeBreaks,
                        catchForks);
                if (tryNode.getFinallyBlock() != null) {
                    collectExecutionPaths(tryNode.getFinallyBlock(), allExecutionPaths, currentPathForksList,
                            activeBreaks);
                }
            } else if (currentNode instanceof SwitchTree) {
                SwitchTree switchNode = (SwitchTree) currentNode;

                evaluateForksExecutionPaths(allExecutionPaths, currentPathForksList, currentPath, true, activeBreaks,
                        switchNode.getCases().toArray(new CaseTree[0]));

                activeBreaks.clear();
            } else if (currentNode instanceof CaseTree) {
                for (StatementTree statement : ((CaseTree) currentNode).getStatements()) {
                    collectExecutionPaths(statement, allExecutionPaths, currentPathForksList, activeBreaks);
                }
            }

            addAllWithoutDuplicates(currentPaths, currentPathForksList);
        }
    }

    /**
     * @return generated paths
     */
    private static void evaluateForksExecutionPaths( //
            List<List<Tree>> allExecutionPaths, //
            List<List<Tree>> currentPaths, //
            List<Tree> currentPath, //
            boolean lastIsCurrentPath, //
            List<BreakTree> activeBreaks, //
            Tree[] forks) {
        int i = 0;
        List<List<Tree>> generatedExecutionPaths = new LinkedList<>();
        for (Tree fork : forks) {
            if (fork != null) {
                List<List<Tree>> currentPathsForFork;
                if (lastIsCurrentPath && ++i == forks.length) {
                    currentPathsForFork = pathsList(currentPath);
                } else {
                    List<Tree> forkedPath = new LinkedList<>(currentPath);
                    allExecutionPaths.add(forkedPath);
                    currentPathsForFork = pathsList(forkedPath);
                }
                collectExecutionPaths(fork, allExecutionPaths, currentPathsForFork, activeBreaks);

                generatedExecutionPaths.addAll(currentPathsForFork);
            }
        }

        // we need to merge with paths created by fork
        addAllWithoutDuplicates(currentPaths, generatedExecutionPaths);
    }

    private static List<List<Tree>> addAllWithoutDuplicates(List<List<Tree>> pathsListUnique,
            List<List<Tree>> listToBeAdded) {
        for (List<Tree> path : listToBeAdded) {
            boolean pathAlreadyAdded = false;
            for (List<Tree> pathFromUnique : pathsListUnique) {
                if (path == pathFromUnique) {
                    pathAlreadyAdded = true;
                }
            }
            if (!pathAlreadyAdded) {
                pathsListUnique.add(path);
            }
        }
        return pathsListUnique;
    }

    @SafeVarargs
    private static List<List<Tree>> pathsList(List<Tree>... executionPaths) {
        List<List<Tree>> pathsList = new LinkedList<>();
        for (List<Tree> path : executionPaths) {
            pathsList.add(path);
        }
        return pathsList;
    }

    public boolean isDeclarationOrSubClassDeclaration(TypeMirror classType, String searchedClassName) {

        while (classType != null) {
            TypeElement typeElement = (TypeElement) types().asElement(classType);
            if (typeElement.getQualifiedName().toString().equals(searchedClassName)) {
                return true;
            }
            List<? extends TypeMirror> superTypes = types().directSupertypes(classType);
            classType = superTypes == null || superTypes.isEmpty() ? null : superTypes.get(0);
        }

        return false;
    }

    public boolean isDeclarationOrSubClassDeclarationBySimpleName(TypeMirror classType,
            String searchedClassSimpleName) {

        while (classType != null) {
            TypeElement typeElement = (TypeElement) types().asElement(classType);
            if (typeElement.getSimpleName().toString().equals(searchedClassSimpleName)) {
                return true;
            }
            List<? extends TypeMirror> superTypes = types().directSupertypes(classType);
            classType = superTypes == null || superTypes.isEmpty() ? null : superTypes.get(0);
        }

        return false;
    }

    public long getStartPosition(Tree tree, CompilationUnitTree compilationUnit) {
        return sourcePositions().getStartPosition(compilationUnit, tree);
    }

    public SourcePosition getSourcePosition(Tree tree, CompilationUnitTree compilationUnit) {
        return getSourcePosition(tree, null, compilationUnit);
    }

    public SourcePosition getSourcePosition(Tree tree, Name nameOffsetForEndPosition,
            CompilationUnitTree compilationUnit) {
        // map offsets to line numbers in source file
        LineMap lineMap = compilationUnit.getLineMap();
        if (lineMap == null) {
            return null;
        }
        // find offset of the specified AST node
        long startPosition = getStartPosition(tree, compilationUnit);
        long endPosition = sourcePositions().getEndPosition(compilationUnit, tree);
        if (endPosition == -1) {
            endPosition = startPosition;
        }
        if (nameOffsetForEndPosition != null) {
            endPosition += nameOffsetForEndPosition.length();
        }

        return new SourcePosition( //
                new File(compilationUnit.getSourceFile().getName()), //
                tree, //
                (int) lineMap.getLineNumber(startPosition), (int) lineMap.getColumnNumber(startPosition), //
                (int) lineMap.getLineNumber(endPosition), (int) lineMap.getColumnNumber(endPosition));
    }

    private SourcePositions sourcePositions() {
        return trees().getSourcePositions();
    }

    private Trees trees() {
        return context.trees;
    }

    private Types types() {
        return context.types;
    }

    private Elements elements() {
        return context.elements;
    }

    /**
     * Returns an Element corresponding to given tree (for given compilationUnit).
     * Will return null if no element matches tree.
     * 
     * For instance:
     * <ul>
     * <li>getElementForTree(VariableTree, ...) would likely return a
     * VariableElement.
     * <li>getElementForTree(NewClassTree, ...) would, on the other hand, return a
     * TypeElement.
     * </ul>
     */
    public <T extends Element> T getElementForTree(Tree tree, CompilationUnitTree compilationUnit) {
        if (tree == null) {
            return null;
        }
        TreePath treePath = trees().getPath(compilationUnit, tree);
        if (treePath == null) {
            return getElementFromLegacySymbolField(tree);
        }
        return getElementForTreePath(treePath);
    }

    /**
     * Returns the element corresponding to given treePath or null if treePath is
     * null or matches no element
     */
    @SuppressWarnings("unchecked")
    public <T extends Element> T getElementForTreePath(TreePath treePath) {
        return (T) trees().getElement(treePath);
    }

    /**
     * If given tree has a corresponding element (getElementForTree(tree,
     * compilationUnit) doesn't return null), it will return its type
     * (ExecutableType for an ExecutableElement, DeclaredType for a TypeElement,
     * ...)
     */
    @SuppressWarnings("unchecked")
    public <T extends TypeMirror> T getElementTypeForTree(Tree tree, CompilationUnitTree compilationUnit) {
        Element element = getElementForTree(tree, compilationUnit);
        return element == null ? null : (T) element.asType();
    }

    /**
     * If given tree has a corresponding element (getElementForTree(tree,
     * compilationUnit) doesn't return null), it will return its type
     * (ExecutableType for an ExecutableElement, DeclaredType for a TypeElement,
     * ...)
     */
    @SuppressWarnings("unchecked")
    public <T extends TypeMirror> T getElementTypeForTreePath(TreePath treePath) {
        Element element = getElementForTreePath(treePath);
        return element == null ? null : (T) element.asType();
    }

    /**
     * If this tree has a type (getTypeForTree(tree, compilationUnit) doesn't return
     * null), returns the corresponding TypeElement, if any, otherwise null.
     * 
     * Can return TypeElement or TypeParameterElement
     */
    public <T extends Element> T getTypeElementForTree(Tree tree, CompilationUnitTree compilationUnit) {
        TreePath treePath = TreePath.getPath(compilationUnit, tree);
        if (treePath == null) {
            TypeMirror treeType = getTypeFromLegacyTypeField(tree);
            if (treeType != null) {
                return getTypeElementFromType(treeType);
            }
        }

        return getTypeElementForTreePath(treePath);
    }

    /**
     * @see #getTypeElementForTreePath(TreePath)
     */
    public <T extends Element> T getTypeElementForTreePath(TreePath treePath) {
        TypeMirror treeType = getTypeForTreePath(treePath);
        return getTypeElementFromType(treeType);
    }

    @SuppressWarnings("unchecked")
    private <T extends Element> T getTypeElementFromType(TypeMirror treeType) {
        if (treeType != null && treeType.getKind() == TypeKind.PACKAGE) {
            return (T) getPackageByName(treeType.toString());
        }

        return treeType == null ? null : (T) types().asElement(treeType);
    }

    /**
     * Returns type associated with given tree
     * 
     * @see #getTypeForTreePath(TreePath)
     */
    public <T extends TypeMirror> T getTypeForTree(Tree tree, CompilationUnitTree compilationUnit) {
        if (tree == null) {
            return null;
        }

        TreePath treePath = trees().getPath(compilationUnit, tree);
        if (treePath == null) {
            return getTypeFromLegacyTypeField(tree);
        }

        return getTypeForTreePath(treePath);
    }

    /**
     * @see #getTypeForTreePath(TreePath)
     */
    public <T extends TypeMirror> T getTypeForTree(Tree tree, TreePath parentTreePath) {
        if (tree == null || parentTreePath == null) {
            return null;
        }

        TreePath treePath = TreePath.getPath(parentTreePath, tree);
        if (treePath == null) {
            return getTypeFromLegacyTypeField(tree);
        }

        return getTypeForTreePath(treePath);
    }

    /**
     * Returns type associated with given tree
     * 
     * @see Trees#getTypeMirror(com.sun.source.util.TreePath)
     */
    @SuppressWarnings("unchecked")
    public <T extends TypeMirror> T getTypeForTreePath(TreePath treePath) {
        if (treePath == null) {
            return null;
        }
        TypeMirror typeMirror = trees().getTypeMirror(treePath);
        return (T) typeMirror;
    }

    @SuppressWarnings("unchecked")
    public <T extends Element> T getParentElement(Element element, Class<T> parentElementClass) {
        Element parent = element;
        while ((parent = parent.getEnclosingElement()) != null) {
            if (parentElementClass.isAssignableFrom(parent.getClass())) {
                return (T) parent;
            }
        }
        return null;
    }

    public CompilationUnitTree getCompilationUnit(Element element) {
        TreePath treePath = trees().getPath(element);
        if (treePath == null) {
            return null;
        }
        return treePath.getCompilationUnit();
    }

    /**
     * Backward compatibility method delegating to
     * com.sun.tools.javac.code.Types.erasureRecursive(Type) behind the scene
     */
    public TypeMirror erasureRecursive(TypeMirror type) {
        try {
            Method erasureRecursiveMethod = javacInternals().typesErasureRecursiveMethod;
            return (TypeMirror) erasureRecursiveMethod.invoke(javacInternals().typesInstance, type);
        } catch (Exception e) {
            throw new RuntimeException("Cannot call internal Javac API :( please adapt this code if API changed", e);
        }
    }

    public boolean isPrimitiveOrVoid(TypeMirror type) {
        return type != null && (type.getKind() == TypeKind.VOID || type instanceof PrimitiveType);
    }

    /**
     * Returns parent (enclosing) package, for instance it will return
     * <code>com.foo</code> if <code>com.foo.bar</code> is given as packageElement.
     * <br />
     * Will return null if packageElement is null or topmost package
     */
    public PackageElement getParentPackage(PackageElement packageElement) {
        if (packageElement == null) {
            return null;
        }

        String packageFullName = packageElement.getQualifiedName().toString();
        int lastDotIndex = packageFullName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return null;
        }

        return getPackageByName(packageFullName.substring(0, lastDotIndex));
    }

    /**
     * Returns qualified name (simple name including parent name with full package
     * name). For instance, a method parameter's could have for qualified name:
     * my.awesome.package.MyClass.myMethod.myParam
     */
    public String getQualifiedName(Element element) {
        if (element == null) {
            return null;
        }

        if (element instanceof PackageElement || element instanceof TypeElement) {
            return element.toString();
        }

        String simpleName = element.getSimpleName().toString();
        if (element.getEnclosingElement() != null) {
            return getQualifiedName(element.getEnclosingElement()) + "." + simpleName;
        } else {
            return simpleName;
        }
    }

    public <T> T last(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    /**
     * Returns true if given element is an interface TypeElement. <br />
     * WARNING: does not include JSweet @Interface
     */
    public boolean isInterface(Element typeElement) {
        return typeElement != null && typeElement.getKind() == ElementKind.INTERFACE;
    }

    /**
     * Returns true if given element is part of an enum: either an Enum element, or
     * an enum constant
     */
    public boolean isPartOfAnEnum(Element element) {
        if (element == null) {
            return false;
        }

        return element.getKind() == ElementKind.ENUM || element.getKind() == ElementKind.ENUM_CONSTANT;
    }

    public Element getMethodOwner(ExecutableElement methodElement) {
        Element targetType = methodElement.getEnclosingElement();
        if (targetType != null) {
            return targetType;
        }

        TypeMirror ownerType = ((ExecutableType) methodElement.asType()).getReceiverType();

        return ownerType == null ? null : types().asElement(ownerType);
    }

    public boolean isGeneratedConstructor(MethodTree methodTree, ClassTree parentClassTree,
            ExecutableElement methodElement) {
        if (!methodTree.getName().toString().equals(CONSTRUCTOR_METHOD_NAME)) {
            return false;
        }
        if (methodElement.getAnnotationMirrors().size() > 0) {
            return false;
        }

        try {
            int methodPos = (int) javacInternals().treePosField.get(methodTree);
            int parentClassPos = (int) javacInternals().treePosField.get(parentClassTree);

            return methodPos == parentClassPos;
        } catch (Exception e) {
            throw new RuntimeException("Cannot call internal Javac API :( please adapt this code if API changed", e);
        }

    }

    public TypeMirror unboxedTypeOrType(TypeMirror type) {
        if (isPrimitiveOrVoid(type)) {
            return type;
        }

        try {
            return types().unboxedType(type);
        } catch (Exception e) {
            return type;
        }
    }

    public boolean isNullType(TypeMirror type) {
        return type == null || type.getKind() == TypeKind.NULL;
    }

    @SuppressWarnings("unchecked")
    private <T extends Element> T getElementFromLegacySymbolField(Tree tree) {
        if (tree == null) {
            return null;
        }

        Field symbolField = javacInternals().getSymbolField(tree);
        if (symbolField == null) {
            return null;
        }

        try {
            return (T) symbolField.get(tree);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends TypeMirror> T getTypeFromLegacyTypeField(Tree tree) {
        if (tree == null) {
            return null;
        }

        try {
            return (T) javacInternals().treeTypeField.get(tree);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * should not be used, backward compatibility purpose only
     */
    private JavacInternals javacInternals() {
        return JavacInternals.instance(types());
    }

    /**
     * should not be used, backward compatibility purpose only
     */
    private static class JavacInternals {

        private static JavacInternals instance;

        final Class<?> typesClass;
        final Class<?> typeClass;
        final Method typesErasureRecursiveMethod;
        final Object typesInstance;

        final Field binaryTreeOperatorField;
        final Field assignOpOperatorField;
        final Field treePosField;
        final Field treeTypeField;

        @SuppressWarnings("rawtypes")
        private final Map<Class, Field> treeClassToSymbolField = new HashMap<>();

        private JavacInternals(Types types) {
            try {
                typesClass = Class.forName("com.sun.tools.javac.code.Types");
                typeClass = Class.forName("com.sun.tools.javac.code.Type");
                typesErasureRecursiveMethod = typesClass.getMethod("erasureRecursive", typeClass);

                Class<?> JCTreeClass = Class.forName("com.sun.tools.javac.tree.JCTree");

                binaryTreeOperatorField = Stream.of(JCTreeClass.getDeclaredClasses())
                        .filter(innerClass -> innerClass.getSimpleName().equals("JCBinary")) //
                        .findFirst().get() //
                        .getField("operator");

                assignOpOperatorField = Stream.of(JCTreeClass.getDeclaredClasses())
                        .filter(innerClass -> innerClass.getSimpleName().equals("JCAssignOp")) //
                        .findFirst().get() //
                        .getField("operator");

                Field typesField = types.getClass().getDeclaredField("types");
                typesField.trySetAccessible();
                typesInstance = typesField.get(types);

                treePosField = JCTreeClass.getDeclaredField("pos");
                treeTypeField = JCTreeClass.getDeclaredField("type");
            } catch (Exception e) {
                throw new RuntimeException("Cannot call internal Javac API :( please adapt this code if API changed",
                        e);
            }
        }

        Field getSymbolField(Tree tree) {
            Class<? extends Tree> treeClass = tree.getClass();
            if (!treeClassToSymbolField.containsKey(treeClass)) {
                Field field;
                try {
                    field = treeClass.getField("sym");
                } catch (NoSuchFieldException | SecurityException e) {
                    field = null;
                }
                treeClassToSymbolField.put(treeClass, field);
            }

            return treeClassToSymbolField.get(treeClass);
        }

        static JavacInternals instance(Types types) {
            if (instance == null) {
                instance = new JavacInternals(types);
            }
            return instance;
        }

        JavaFileObject getSourceFileObjectFromElement(TypeElement element) {
            try {
                if (element == null) {
                    return null;
                }
                Field internalSourceFileField = element.getClass().getDeclaredField("sourcefile");
                return (JavaFileObject) internalSourceFileField.get(element);
            } catch (Exception e) {
                throw new RuntimeException("Cannot call internal Javac API :( please adapt this code if API changed",
                        e);
            }
        }

    }
}
