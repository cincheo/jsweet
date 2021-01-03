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

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jsweet.JSweetConfig.ANNOTATION_ERASED;
import static org.jsweet.JSweetConfig.ANNOTATION_STRING_ENUM;
import static org.jsweet.JSweetConfig.ANNOTATION_FUNCTIONAL_INTERFACE;
import static org.jsweet.JSweetConfig.ANNOTATION_OBJECT_TYPE;
import static org.jsweet.JSweetConfig.ANNOTATION_STRING_TYPE;
import static org.jsweet.JSweetConfig.GLOBALS_CLASS_NAME;
import static org.jsweet.JSweetConfig.GLOBALS_PACKAGE_NAME;
import static org.jsweet.JSweetConfig.TS_IDENTIFIER_FORBIDDEN_CHARS;
import static org.jsweet.JSweetConfig.TUPLE_CLASSES_PACKAGE;
import static org.jsweet.JSweetConfig.UNION_CLASS_NAME;
import static org.jsweet.JSweetConfig.UTIL_CLASSNAME;
import static org.jsweet.JSweetConfig.UTIL_PACKAGE;
import static org.jsweet.transpiler.util.Util.CONSTRUCTOR_METHOD_NAME;
import static org.jsweet.transpiler.util.Util.firstOrDefault;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext.DefaultMethodEntry;
import org.jsweet.transpiler.JSweetContext.GlobalMethodInfos;
import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.OverloadScanner.OverloadMethodEntry;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import org.jsweet.transpiler.util.JSDoc;
import org.jsweet.transpiler.util.Util;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

/**
 * This is a TypeScript printer for translating the Java AST to a TypeScript
 * program.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class Java2TypeScriptTranslator extends AbstractTreePrinter {

    /**
     * The name of the field where the parent class is stored in the generated
     * TypeScript code.
     */
    public static final String PARENT_CLASS_FIELD_NAME = "__parent";
    /**
     * The name of the field where the implemented interface names are stored in the
     * generated TypeScript code (for <code>instanceof</code> operator).
     */
    public static final String INTERFACES_FIELD_NAME = "__interfaces";
    /**
     * The suffix added to static field initialization methods (for Java semantics).
     */
    public static final String STATIC_INITIALIZATION_SUFFIX = "_$LI$";
    /**
     * The name of the field where the class name is stored in the class
     * constructor.
     */
    public static final String CLASS_NAME_IN_CONSTRUCTOR = "__class";
    /**
     * A prefix/separator for anonymous classes.
     */
    public static final String ANONYMOUS_PREFIX = "$";
    /**
     * A suffix for name of classes that wrap regular TypeScript enums.
     */
    public static final String ENUM_WRAPPER_CLASS_SUFFIX = "_$WRAPPER";
    /**
     * The name of the variable that contains the enum wrapper instances.
     */
    public static final String ENUM_WRAPPER_CLASS_WRAPPERS = "_$wrappers";
    /**
     * The field name for storing the enum's name.
     */
    public static final String ENUM_WRAPPER_CLASS_NAME = "_$name";
    /**
     * The field name for storing the enum's ordinal.
     */
    public static final String ENUM_WRAPPER_CLASS_ORDINAL = "_$ordinal";
    /**
     * The default keyword for declaring variables.
     */
    public static final String VAR_DECL_KEYWORD = "let";
    /**
     * A regular expression for matching body markers in <code>@Replace</code>
     * expression.
     */
    public static final Pattern BODY_MARKER = Pattern.compile("\\{\\{\\s*body\\s*\\}\\}");
    /**
     * A regular expression for matching base indent markers in
     * <code>@Replace</code> expression.
     */
    public static final Pattern BASE_INDENT_MARKER = Pattern.compile("\\{\\{\\s*baseIndent\\s*\\}\\}");
    /**
     * A regular expression for matching indent markers in <code>@Replace</code>
     * expression.
     */
    public static final Pattern INDENT_MARKER = Pattern.compile("\\{\\{\\s*indent\\s*\\}\\}");
    /**
     * A regular expression for matching method name markers in
     * <code>@Replace</code> expression.
     */
    public static final Pattern METHOD_NAME_MARKER = Pattern.compile("\\{\\{\\s*methodName\\s*\\}\\}");
    /**
     * A regular expression for matching class name markers in <code>@Replace</code>
     * expression.
     */
    public static final Pattern CLASS_NAME_MARKER = Pattern.compile("\\{\\{\\s*className\\s*\\}\\}");
    /**
     * A prefix for generators.
     */
    public static final String GENERATOR_PREFIX = "__generator_";

    /**
     * A logger for internal messages.
     */
    protected static Logger logger = Logger.getLogger(Java2TypeScriptTranslator.class);

    /**
     * A state flag indicating the comparison mode to be used by this printer for
     * printing comparison operators.
     * 
     * @author Renaud Pawlak
     */
    public static enum ComparisonMode {
        /**
         * Forces the strict comparison operators (===, >==, <==), even for null
         * literals.
         */
        FORCE_STRICT,
        /**
         * Uses the strict comparison operators (===, >==, <==), except for null
         * literals, where loose operators are used to match better the Java semantics.
         * This is the default behavior.
         */
        STRICT,
        /**
         * Uses the loose comparison operators (==, >=, <=).
         */
        LOOSE;
    }

    private final Stack<ComparisonMode> comparisonModeStack = new Stack<>();

    /**
     * Selects a comparison mode for subsequently printed comparison operators.
     * 
     * @see #exitComparisonMode()
     */
    public void enterComparisonMode(ComparisonMode comparisonMode) {
        comparisonModeStack.push(comparisonMode);
    }

    /**
     * Exits a comparison mode and go back to the previous one.
     * 
     * @see #enterComparisonMode(ComparisonMode)
     */
    public void exitComparisonMode() {
        comparisonModeStack.pop();
    }

    private ComparisonMode getComparisonMode() {
        if (comparisonModeStack.isEmpty()) {
            return ComparisonMode.STRICT;
        } else {
            if (comparisonModeStack.peek() == ComparisonMode.STRICT) {
                return ComparisonMode.FORCE_STRICT;
            } else {
                return ComparisonMode.LOOSE;
            }
        }
    }

    public static class ClassScope {
        private String name;

        private MethodTree mainMethod;

        private boolean interfaceScope = false;

        private boolean enumScope = false;

        private boolean isComplexEnum = false;

        private boolean enumWrapperClassScope = false;

        private boolean stringEnumScope = false;

        private boolean removedSuperclass = false;

        private boolean declareClassScope;

        private boolean skipTypeAnnotations = false;

        private boolean defaultMethodScope = false;

        private boolean eraseVariableTypes = false;

        private boolean hasDeclaredConstructor = false;

        private boolean innerClass = false;

        private boolean innerClassNotStatic = false;

        private boolean hasInnerClass = false;

        private List<ClassTree> anonymousClasses = new ArrayList<>();

        private List<NewClassTree> anonymousClassesConstructors = new ArrayList<>();

        private List<LinkedHashSet<VariableElement>> finalVariables = new ArrayList<>();

        private boolean hasConstructorOverloadWithSuperClass;

        private List<VariableTree> fieldsWithInitializers = new ArrayList<>();

        private List<String> inlinedConstructorArgs = null;

        private List<ClassTree> localClasses = new ArrayList<>();

        private List<String> generatedMethodNames = new ArrayList<>();

        // to be accessed in the parent scope
        private boolean isAnonymousClass = false;
        // to be accessed in the parent scope
        private boolean isInnerClass = false;
        // to be accessed in the parent scope
        private boolean isLocalClass = false;

        private boolean constructor = false;

        private boolean decoratorScope = false;

        private final JSweetContext context;
        private final CompilationUnitTree compilationUnit;

        private ClassScope(JSweetContext context, CompilationUnitTree compilationUnit) {
            this.context = context;
            this.compilationUnit = compilationUnit;
        }

        public String getName() {
            return name;
        }

        public MethodTree getMainMethod() {
            return mainMethod;
        }

        public ExecutableElement getMainMethodElement() {
            return Util.getElement(mainMethod);
        }

        public List<DeclaredType> getLocalClassesTypes() {
            return getLocalClasses().stream() //
                    .map((ClassTree localClassTree) -> (DeclaredType) context.util.getType(localClassTree))
                    .collect(toList());
        }

        public boolean isLocalClassType(TypeMirror type) {
            return getLocalClassesTypes().stream().anyMatch(localClassType -> localClassType.equals(type));
        }

        public boolean isInterfaceScope() {
            return interfaceScope;
        }

        public boolean isEnumScope() {
            return enumScope;
        }

        public boolean isComplexEnum() {
            return isComplexEnum;
        }

        public boolean isEnumWrapperClassScope() {
            return enumWrapperClassScope;
        }

        public boolean isRemovedSuperclass() {
            return removedSuperclass;
        }

        public boolean isDeclareClassScope() {
            return declareClassScope;
        }

        public boolean isSkipTypeAnnotations() {
            return skipTypeAnnotations;
        }

        public boolean isDefaultMethodScope() {
            return defaultMethodScope;
        }

        public boolean isEraseVariableTypes() {
            return eraseVariableTypes;
        }

        public void setEraseVariableTypes(boolean eraseVariableTypes) {
            this.eraseVariableTypes = eraseVariableTypes;
        }

        public boolean isHasDeclaredConstructor() {
            return hasDeclaredConstructor;
        }

        public boolean getInnerClass() {
            return innerClass;
        }

        public boolean isInnerClassNotStatic() {
            return innerClassNotStatic;
        }

        public boolean isHasInnerClass() {
            return hasInnerClass;
        }

        public List<ClassTree> getAnonymousClasses() {
            return anonymousClasses;
        }

        public List<NewClassTree> getAnonymousClassesConstructors() {
            return anonymousClassesConstructors;
        }

        public NewClassTree getAnonymousClassConstructorFromClassTree(ClassTree classTree) {
            int anonymousClassIndex = anonymousClasses.indexOf(classTree);
            if (anonymousClassIndex == -1) {
                return null;
            }
            return anonymousClassesConstructors.get(anonymousClassIndex);
        }

        public List<LinkedHashSet<VariableElement>> getFinalVariables() {
            return finalVariables;
        }

        public boolean isHasConstructorOverloadWithSuperClass() {
            return hasConstructorOverloadWithSuperClass;
        }

        public List<VariableTree> getFieldsWithInitializers() {
            return fieldsWithInitializers;
        }

        public List<String> getInlinedConstructorArgs() {
            return inlinedConstructorArgs;
        }

        public List<ClassTree> getLocalClasses() {
            return localClasses;
        }

        public List<String> getGeneratedMethodNames() {
            return generatedMethodNames;
        }

        public boolean isAnonymousClass() {
            return isAnonymousClass;
        }

        public boolean isInnerClass() {
            return isInnerClass;
        }

        public boolean isLocalClass() {
            return isLocalClass;
        }

        public boolean isConstructor() {
            return constructor;
        }

        public boolean isDecoratorScope() {
            return decoratorScope;
        }

    }

    private Stack<ClassScope> scope = new Stack<>();

    private boolean isAnnotationScope = false;

    private boolean isDefinitionScope = false;

    protected final boolean isTopLevelScope() {
        return getIndent() == 0;
    }

    protected final ClassScope getScope() {
        return scope.peek();
    }

    protected final ClassScope getScope(int i) {
        return scope.get(scope.size() - 1 - i);
    }

    /**
     * Enters a new class scope.
     * 
     * @see #exitScope()
     */
    public void enterScope() {
        scope.push(new ClassScope(context, getCompilationUnit()));
    }

    /**
     * Exits a class scope.
     * 
     * @see #enterScope()
     */
    public void exitScope() {
        scope.pop();
    }

    /**
     * Creates a new TypeScript translator.
     * 
     * @param adapter         an object that can tune various aspects of the
     *                        TypeScript code generation
     * @param logHandler      the handler for logging and error reporting
     * @param context         the AST scanning context
     * @param compilationUnit the compilation unit to be translated
     * @param fillSourceMap   if true, the printer generates the source maps, for
     *                        debugging purpose
     */
    public Java2TypeScriptTranslator(PrinterAdapter adapter, TranspilationHandler logHandler, JSweetContext context,
            CompilationUnitTree compilationUnit, boolean fillSourceMap) {
        super(logHandler, context, compilationUnit, adapter, fillSourceMap);
    }

    private static String mapConstructorType(String typeName) {
        if (CONSTRUCTOR_TYPE_MAPPING.containsKey(typeName)) {
            return CONSTRUCTOR_TYPE_MAPPING.get(typeName);
        } else {
            return typeName;
        }
    }

    public static final Map<String, String> TYPE_MAPPING;
    static {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("java.lang.String", "String");
        mapping.put("java.lang.Number", "Number");
        mapping.put("java.lang.Integer", "Number");
        mapping.put("java.lang.Float", "Number");
        mapping.put("java.lang.Double", "Number");
        mapping.put("java.lang.Short", "Number");
        mapping.put("java.lang.Character", "String");
        mapping.put("java.lang.Byte", "Number");
        mapping.put("java.lang.Boolean", "Boolean");
        mapping.put("java.lang.Long", "Number");
        mapping.put("int", "Number");
        mapping.put("float", "Number");
        mapping.put("double", "Number");
        mapping.put("short", "Number");
        mapping.put("char", "String");
        mapping.put("boolean", "Boolean");
        mapping.put("byte", "Number");
        mapping.put("long", "Number");
        TYPE_MAPPING = Collections.unmodifiableMap(mapping);
    }

    private static final Map<String, String> CONSTRUCTOR_TYPE_MAPPING;
    static {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("string", "String");
        mapping.put("number", "Number");
        mapping.put("boolean", "Boolean");
        mapping.put("any", "Object");
        CONSTRUCTOR_TYPE_MAPPING = Collections.unmodifiableMap(mapping);
    }

    private PackageElement topLevelPackage;

    public void useModule(ModuleImportDescriptor moduleImport) {
        if (moduleImport != null) {
            useModule(false, moduleImport.isDirect(), moduleImport.getTargetPackage(), null,
                    moduleImport.getImportedName(), moduleImport.getPathToImportedClass(), null);
        }
    }

    private void useModule(boolean require, boolean direct, PackageElement targetPackage, Tree sourceTree,
            String targetName, String moduleName, Element sourceElement) {
        if (context.useModules) {
            context.packageDependencies.add((PackageElement) targetPackage);
            PackageElement packageElement = Util.getElement(compilationUnit.getPackage());
            context.packageDependencies.add(packageElement);
            context.packageDependencies.addEdge(packageElement, (PackageElement) targetPackage);
        }
        context.registerUsedModule(moduleName);
        Set<String> importedNames = context.getImportedNames(compilationUnit.getSourceFile().getName());
        if (!importedNames.contains(targetName)) {
            if (context.useModules) {
                // TODO: when using several qualified Globals classes, we need
                // to disambiguate (Globals__1, Globals__2, ....)
                // TODO: IDEA FOR MODULES AND FULLY QUALIFIED NAMES
                // when using a fully qualified name in the code, we need an
                // import, which can be named after something like
                // qualName.replace(".", "_")... this would work in the general
                // case...

                if (context.isExcludedSourcePath(util().getSourceFilePath(sourceElement))) {
                    // ignore excluded source files
                    return;
                }

                if (sourceElement instanceof TypeElement
                        && context.hasAnnotationType(sourceElement, JSweetConfig.ANNOTATION_DECORATOR)) {
                    context.forceTopImports();
                }

                if (!context.moduleBundleMode && sourceElement instanceof TypeElement
                        && util().isSourceElement(sourceElement)
                        && !(sourceElement instanceof TypeElement && context.referenceAnalyzer != null
                                && context.referenceAnalyzer.isDependent(compilationUnit,
                                        (TypeElement) sourceElement))) {

                    // import as footer statements to avoid cyclic dependencies
                    // as much as possible
                    // note that the better way to avoid cyclic dependency
                    // issues is to create bundles
                    context.addTopFooterStatement("import." + targetName,
                            "import { " + targetName + " } from '" + moduleName + "';\n");

                } else {
                    if (direct) {
                        context.addHeader("import." + targetName, "import " + targetName + " = " + moduleName + ";\n");
                    } else {

                        boolean fullImport = require || GLOBALS_CLASS_NAME.equals(targetName);
                        if (fullImport) {
                            if (context.useRequireForModules) {
                                context.addHeader("import." + targetName, "import " + targetName + " = require("
                                        + getStringLiteralQuote() + moduleName + getStringLiteralQuote() + ");\n");
                            } else {
                                context.addHeader("import." + targetName,
                                        "import * as " + targetName + " from '" + moduleName + "';\n");
                            }
                        } else {
                            context.addHeader("import." + targetName,
                                    "import { " + targetName + " } from '" + moduleName + "';\n");
                        }
                    }
                }
            }
            context.registerImportedName(compilationUnit.getSourceFile().getName(), sourceElement, targetName);
        }
    }

    private boolean checkRootPackageParent(CompilationUnitTree topLevel, PackageElement rootPackage,
            PackageElement parentPackage) {
        if (parentPackage == null) {
            return true;
        }
        if (!context.options.isNoRootDirectories() || context.options.isBundle()) {
            return true;
        }
        if (context.isRootPackage(parentPackage)) {
            report(topLevel.getPackageName(), JSweetProblem.ENCLOSED_ROOT_PACKAGES,
                    rootPackage.getQualifiedName().toString(), parentPackage.getQualifiedName().toString());
            return false;
        }
        for (Element s : parentPackage.getEnclosedElements()) {
            if (s instanceof TypeElement) {
                if (util().isSourceElement(s)) {
                    report(topLevel.getPackageName(), JSweetProblem.CLASS_OUT_OF_ROOT_PACKAGE_SCOPE,
                            util().getQualifiedName(s), rootPackage.getQualifiedName().toString());
                    return false;
                }
            }
        }
        return checkRootPackageParent(topLevel, rootPackage, util().getParentPackage(parentPackage));
    }

    private ModuleImportDescriptor getModuleImportDescriptor(String importedName, TypeElement importedClass) {
        return getAdapter().getModuleImportDescriptor(createExtendedElement(compilationUnit), importedName,
                importedClass);
    }

    private boolean isMappedOrErasedType(Element typeElement) {
        return context.isMappedType(util().getQualifiedName(typeElement))
                || context.hasAnnotationType(typeElement, JSweetConfig.ANNOTATION_ERASED);
    }

    /**
     * Ensures that the module that corresponds to the given element is used
     * (imported).
     */
    public void ensureModuleIsUsed(Element element) {
        if (context.useModules) {
            if (element instanceof TypeElement) {
                ModuleImportDescriptor moduleImport = getAdapter().getModuleImportDescriptor(
                        getCompilationUnitElement(), element.getSimpleName().toString(), (TypeElement) element);
                if (moduleImport != null) {
                    useModule(moduleImport);
                }
            }
        }

    }

    /**
     * Prints a compilation unit tree.
     */
    @Override
    public Void visitCompilationUnit(final CompilationUnitTree compilationUnit, final Trees trees) {

        PackageElement packageElement = Util.getElement(compilationUnit.getPackage());
        if (context.isPackageErased(packageElement)) {
            return returnNothing();
        }

        String packageFullName = util().getPackageFullNameForCompilationUnit(compilationUnit);
        isDefinitionScope = packageFullName.startsWith(JSweetConfig.LIBS_PACKAGE + ".");

        if (packageElement != null && context.hasAnnotationType(packageElement, JSweetConfig.ANNOTATION_MODULE)) {
            context.addExportedElement(
                    context.getAnnotationValue(packageElement, JSweetConfig.ANNOTATION_MODULE, String.class, null),
                    packageElement, getCompilationUnit());
        }

        PackageElement rootPackage = context.getFirstEnclosingRootPackage(packageElement);
        if (rootPackage != null) {
            if (!checkRootPackageParent(compilationUnit, rootPackage,
                    util().getParentPackage((PackageElement) rootPackage))) {
                return returnNothing();
            }
        }
        context.importedTopPackages.clear();
        context.rootPackages.add(rootPackage);

        topLevelPackage = context.getTopLevelPackage(packageElement);
        if (topLevelPackage != null) {
            context.topLevelPackageNames.add(topLevelPackage.getQualifiedName().toString());
        }

        footer.delete(0, footer.length());

        setCompilationUnit(compilationUnit);

        getAdapter().beforeCompilationUnit();

        boolean globalModule = JSweetConfig.GLOBALS_PACKAGE_NAME.equals(packageFullName)
                || packageFullName.endsWith("." + JSweetConfig.GLOBALS_PACKAGE_NAME);
        String rootRelativePackageName = "";
        if (!globalModule) {
            rootRelativePackageName = getRootRelativeName(packageElement);
            if (rootRelativePackageName.length() == 0) {
                globalModule = true;
            }
        }

        List<String> packageSegments = new ArrayList<String>(Arrays.asList(rootRelativePackageName.split("\\.")));
        packageSegments.retainAll(JSweetConfig.TS_TOP_LEVEL_KEYWORDS);
        if (!packageSegments.isEmpty()) {
            report(compilationUnit.getPackageName(), JSweetProblem.PACKAGE_NAME_CONTAINS_KEYWORD, packageSegments);
        }

        detectAndUseImportedModules(compilationUnit);

        detectAndUseModulesFromReferencedTypes(compilationUnit);

        // require root modules when using fully qualified names or reserved
        // keywords
        new TreeScanner<Void, Trees>() {
            Stack<Tree> stack = new Stack<>();

            @Override
            public Void scan(Tree tree, Trees trees) {
                if (tree != null) {
                    stack.push(tree);
                    try {
                        super.scan(tree, trees);
                    } finally {
                        stack.pop();
                    }
                }

                return returnNothing();
            }

            @SuppressWarnings("unchecked")
            public <T extends Tree> T getParent(Class<T> type) {
                for (int i = this.stack.size() - 2; i >= 0; i--) {
                    if (type.isAssignableFrom(this.stack.get(i).getClass())) {
                        return (T) this.stack.get(i);
                    }
                }
                return null;
            }

            @Override
            public Void visitIdentifier(final IdentifierTree identifier, final Trees trees) {
                PackageElement compilationUnitPackageElement = Util.getElement(compilationUnit.getPackage());
                Element identifierElement = Util.getElement(identifier);
                if (identifierElement instanceof PackageElement) {
                    // ignore packages in imports
                    if (getParent(ImportTree.class) != null) {
                        return returnNothing();
                    }
                    boolean isSourceType = false;
                    for (int i = stack.size() - 2; i >= 0; i--) {
                        Tree tree = stack.get(i);
                        if (!(tree instanceof MemberSelectTree)) {
                            break;
                        } else {
                            MemberSelectTree selectTree = (MemberSelectTree) tree;
                            Element selectElement = Util.getElement(selectTree);
                            if ((selectElement instanceof TypeElement) && util().isSourceElement(selectElement)) {
                                isSourceType = true;
                                break;
                            }
                        }
                    }
                    if (!isSourceType) {
                        return returnNothing();
                    }
                    PackageElement identifierPackage = (PackageElement) identifierElement;
                    String pathToModulePackage = util().getRelativePath(compilationUnitPackageElement,
                            identifierPackage);
                    if (pathToModulePackage == null) {
                        return returnNothing();
                    }
                    File moduleFile = new File(new File(pathToModulePackage), JSweetConfig.MODULE_FILE_NAME);
                    if (!identifierPackage.getSimpleName().toString()
                            .equals(compilationUnitPackageElement.getSimpleName().toString())) {
                        useModule(false, false, identifierPackage, identifier,
                                identifierPackage.getSimpleName().toString(), moduleFile.getPath().replace('\\', '/'),
                                null);
                    }
                } else if (identifierElement instanceof TypeElement) {
                    if (JSweetConfig.GLOBALS_PACKAGE_NAME
                            .equals(identifierElement.getEnclosingElement().getSimpleName().toString())) {
                        String pathToModulePackage = util().getRelativePath(compilationUnitPackageElement,
                                identifierElement.getEnclosingElement());
                        if (pathToModulePackage == null) {
                            return returnNothing();
                        }
                        File moduleFile = new File(new File(pathToModulePackage), JSweetConfig.MODULE_FILE_NAME);
                        if (!identifierElement.getEnclosingElement().getSimpleName().toString()
                                .equals(compilationUnitPackageElement.getSimpleName().toString())) {
                            useModule(false, false, (PackageElement) identifierElement.getEnclosingElement(),
                                    identifier, JSweetConfig.GLOBALS_PACKAGE_NAME,
                                    moduleFile.getPath().replace('\\', '/'), null);
                        }
                    }
                }

                return returnNothing();
            }

            @Override
            public Void visitMethodInvocation(final MethodInvocationTree invocation, final Trees trees) {
                PackageElement compilationUnitPackageElement = Util.getElement(compilationUnit.getPackage());

                // TODO: same for static variables
                if (invocation.getMethodSelect() instanceof IdentifierTree && JSweetConfig.TS_STRICT_MODE_KEYWORDS
                        .contains(invocation.getMethodSelect().toString().toLowerCase())) {

                    Element invokedMethodElement = Util.getElement((IdentifierTree) invocation.getMethodSelect());
                    PackageElement invocationPackage = util().getParentElement(invokedMethodElement,
                            PackageElement.class);
                    String rootRelativeInvocationPackageName = getRootRelativeName(invocationPackage);
                    if (rootRelativeInvocationPackageName.indexOf('.') == -1) {
                        return super.visitMethodInvocation(invocation, trees);
                    }
                    String targetRootPackageName = rootRelativeInvocationPackageName.substring(0,
                            rootRelativeInvocationPackageName.indexOf('.'));
                    String pathToReachRootPackage = util().getRelativePath(
                            "/" + compilationUnitPackageElement.getQualifiedName().toString().replace('.', '/'),
                            "/" + targetRootPackageName);
                    if (pathToReachRootPackage == null) {
                        return super.visitMethodInvocation(invocation, trees);
                    }
                    File moduleFile = new File(new File(pathToReachRootPackage), JSweetConfig.MODULE_FILE_NAME);
                    if (!invocationPackage.toString()
                            .equals(compilationUnitPackageElement.getSimpleName().toString())) {
                        useModule(false, false, invocationPackage, invocation, targetRootPackageName,
                                moduleFile.getPath().replace('\\', '/'), null);
                    }
                }
                return super.visitMethodInvocation(invocation, trees);
            }

        };
        // TODO: change the way qualified names are handled (because of new
        // module organization)
        // inlinedModuleScanner.scan(compilationUnit);

        if (!globalModule && !context.useModules) {
            printIndent();
            if (isDefinitionScope) {
                print("declare ");
            } else if (context.moduleBundleMode) {
                print("export ");
            }
            print("namespace ").print(rootRelativePackageName).print(" {").startIndent().println();
        }

        for (ImportTree def : compilationUnit.getImports()) {
            print(def);
        }

        for (ClassTree def : util().getSortedClassDeclarations(compilationUnit.getTypeDecls(), compilationUnit)) {
            printIndent();
            int pos = getCurrentPosition();
            print(def);
            if (getCurrentPosition() == pos) {
                removeLastIndent();
                continue;
            }
            println().println();
        }
        if (!globalModule && !context.useModules) {
            removeLastChar().endIndent().printIndent().print("}").println();
        }

        if (footer.length() > 0) {
            println().print(footer.toString());
        }

        globalModule = false;

        return returnNothing();
    }

    private void detectAndUseModulesFromReferencedTypes(CompilationUnitTree compilationUnit) {
        if (context.useModules) {

            new UsedTypesScanner().scan(compilationUnit, context.trees);
        }
    }

    private void detectAndUseImportedModules(CompilationUnitTree compilationUnit) {
        // generate requires by looking up imported external modules
        for (ImportTree importDecl : compilationUnit.getImports()) {

            TreeScanner<Void, Trees> importedModulesScanner = new TreeScanner<>() {
                @Override
                public Void scan(Tree tree, Trees trees) {
                    if (tree instanceof MemberSelectTree) {
                        MemberSelectTree qualified = (MemberSelectTree) tree;
                        Element memberSelectElement = Util.getElement(tree);
                        if (memberSelectElement != null) {
                            // regular import case (qualified.sym is a package)
                            if (context.hasAnnotationType(memberSelectElement, JSweetConfig.ANNOTATION_MODULE)) {
                                String targetName = createImportAliasFromFieldAccess(qualified);
                                String actualName = context.getAnnotationValue(memberSelectElement,
                                        JSweetConfig.ANNOTATION_MODULE, String.class, null);
                                useModule(true, false, null, importDecl, targetName, actualName, memberSelectElement);
                            }
                        } else {
                            // static import case (imported fields and methods)
                            if (qualified.getExpression() instanceof MemberSelectTree) {
                                MemberSelectTree qualifier = (MemberSelectTree) qualified.getExpression();
                                Element subMemberSelectElement = Util.getElement(qualifier);
                                if (subMemberSelectElement != null) {
                                    try {
                                        for (Element importedMember : subMemberSelectElement.getEnclosedElements()) {
                                            if (qualified.getIdentifier().equals(importedMember.getSimpleName())) {
                                                if (context.hasAnnotationType(importedMember,
                                                        JSweetConfig.ANNOTATION_MODULE)) {
                                                    String targetName = createImportAliasFromSymbol(importedMember);
                                                    String actualName = context.getAnnotationValue(importedMember,
                                                            JSweetConfig.ANNOTATION_MODULE, String.class, null);
                                                    useModule(true, false, null, importDecl, targetName, actualName,
                                                            importedMember);
                                                    break;
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        logger.error("error occurred collecting modules from static imports", e);
                                    }
                                }
                            }
                        }
                    }
                    return super.scan(tree, trees);
                }
            };
            importedModulesScanner.scan(importDecl.getQualifiedIdentifier(), context.trees);

            for (ImportTree importTree : compilationUnit.getImports()) {
                if (importTree.getQualifiedIdentifier() instanceof MemberSelectTree) {
                    MemberSelectTree qualified = (MemberSelectTree) importTree.getQualifiedIdentifier();
                    String importedName = qualified.getIdentifier().toString();
                    if (importTree.isStatic() && (qualified.getExpression() instanceof MemberSelectTree)) {
                        qualified = (MemberSelectTree) qualified.getExpression();
                    }
                    Element qualifiedElement = Util.getElement(qualified);
                    if (qualifiedElement instanceof TypeElement) {
                        boolean globals = JSweetConfig.GLOBALS_CLASS_NAME
                                .equals(qualifiedElement.getSimpleName().toString());
                        if (!globals) {
                            importedName = qualified.getIdentifier().toString();
                        }
                        TypeElement importedClass = (TypeElement) qualifiedElement;
                        String qualId = importTree.getQualifiedIdentifier().toString();
                        String adaptedQualId = getAdapter().needsImport(createExtendedElement(importTree), qualId);
                        if (globals || adaptedQualId != null) {
                            ModuleImportDescriptor moduleImport = getModuleImportDescriptor(importedName,
                                    importedClass);
                            if (moduleImport != null) {
                                useModule(false, moduleImport.isDirect(), moduleImport.getTargetPackage(), importTree,
                                        moduleImport.getImportedName(), moduleImport.getPathToImportedClass(), null);
                            }
                        }
                    }
                }
            }
        }
    }

    private String createImportAliasFromFieldAccess(MemberSelectTree access) {
        String name = extractNameFromAnnotatedSymbol(Util.getElement(access));
        if (name != null) {
            return name;
        } else {
            return access.getIdentifier().toString();
        }
    }

    private String createImportAliasFromSymbol(Element symbol) {
        String name = extractNameFromAnnotatedSymbol(symbol);
        if (name != null) {
            return name;
        } else {
            return symbol.getSimpleName().toString();
        }
    }

    private String extractNameFromAnnotatedSymbol(Element symbol) {
        if (context.hasAnnotationType(symbol, JSweetConfig.ANNOTATION_NAME)) {
            return context.getAnnotationValue(symbol, JSweetConfig.ANNOTATION_NAME, String.class, null);
        } else {
            return null;
        }
    }

    private void printDocComment(Tree element) {
        printDocComment(element, false);
    }

    private void printDocComment(Tree tree, boolean newline) {
        if (compilationUnit != null) {
            TreePath treePath = TreePath.getPath(compilationUnit, tree);
            Element element = Util.getElement(tree);
            if (treePath == null && element != null) {
                treePath = trees().getPath(element);
            }

            String docComment = trees().getDocComment(treePath);
            String commentText = JSDoc.adaptDocComment(context, treePath, tree, docComment);

            if (element != null) {
                commentText = getAdapter().adaptDocComment(element, commentText);
            }

            List<String> lines = new ArrayList<>();
            if (commentText != null) {
                lines.addAll(Arrays.asList(commentText.split("\n")));
            }
            if (!lines.isEmpty()) {
                if (newline) {
                    println().printIndent();
                }
                print("/**").println();
                for (String line : lines) {
                    printIndent().print(" * ").print(line.trim()).println();
                }
                removeLastChar();
                println().printIndent().print(" ").print("*/").println();
                printIndent();
            }
        }
    }

    private void printAnonymousClassTypeArgs(NewClassTree newClass) {
        if ((newClass.getIdentifier() instanceof ParameterizedTypeTree)) {
            ParameterizedTypeTree tapply = (ParameterizedTypeTree) newClass.getIdentifier();
            if (tapply.getTypeArguments() != null && !tapply.getTypeArguments().isEmpty()) {
                boolean printed = false;
                print("<");
                for (Tree typeArg : tapply.getTypeArguments()) {
                    TypeMirror typeArgType = Util.getType(typeArg);
                    if (typeArgType instanceof TypeVariable) {
                        printed = true;
                        print(typeArg).print(", ");
                    }
                }
                if (printed) {
                    removeLastChars(2);
                    print(">");
                } else {
                    removeLastChar();
                }
            }
        }
    }

    protected boolean isAnonymousClass() {
        return scope.size() > 1 && getScope(1).isAnonymousClass;
    }

    private boolean isInnerClass() {
        return scope.size() > 1 && getScope(1).isInnerClass;
    }

    private boolean isLocalClass() {
        return scope.size() > 1 && getScope(1).isLocalClass;
    }

    /**
     * A flags that indicates that this adapter is printing type parameters.
     */
    private boolean inTypeParameters = false;

    /**
     * A flags that indicates that this adapter is not substituting types.
     */
    private boolean disableTypeSubstitution = false;

    public final AbstractTreePrinter substituteAndPrintType(Tree typeTree) {
        return substituteAndPrintType(typeTree, false, inTypeParameters, true, disableTypeSubstitution);
    }

    private AbstractTreePrinter printArguments(List<? extends Tree> arguments) {
        int i = 1;
        for (Tree argument : arguments) {
            printArgument(argument, i++).print(", ");
        }
        if (arguments.size() > 0) {
            removeLastChars(2);
        }
        return this;
    }

    private AbstractTreePrinter printArgument(Tree argument, int i) {
        print("p" + i + ": ");
        substituteAndPrintType(argument, false, false, true, false);
        return this;
    }

    private AbstractTreePrinter substituteAndPrintType(Tree typeTree, boolean arrayComponent, boolean inTypeParameters,
            boolean completeRawTypes, boolean disableSubstitution) {

        if (typeTree instanceof IntersectionTypeTree) {
            for (Tree t : ((IntersectionTypeTree) typeTree).getBounds()) {
                substituteAndPrintType(t, arrayComponent, inTypeParameters, completeRawTypes, disableSubstitution);
                print(" & ");
            }
            removeLastChars(3);
            return this;
        }

        Element typeElement = Util.getTypeElement(typeTree);
        TypeMirror typeType = Util.getType(typeTree);
        
        if (typeElement instanceof TypeParameterElement) {
            if (getAdapter().typeVariablesToErase.contains(typeElement)) {
                return print("any");
            }
        }
        if (!disableSubstitution) {
            if (context.hasAnnotationType(typeElement, ANNOTATION_ERASED)) {
                return print("any");
            }
            if (context.hasAnnotationType(typeElement, ANNOTATION_OBJECT_TYPE)) {
                // TODO: in case of object types, we should replace with the org
                // object type...
                return print("any");
            }

            String typeFullName = util().getQualifiedName(typeType);
            if (Runnable.class.getName().equals(typeFullName)) {
                if (arrayComponent) {
                    print("(");
                }
                print("() => void");
                if (arrayComponent) {
                    print(")");
                }
                return this;
            }
            if (typeTree instanceof ParameterizedTypeTree) {
                ParameterizedTypeTree typeApply = ((ParameterizedTypeTree) typeTree);
                TypeElement parametrizedTypeElement = Util.getTypeElement(typeApply.getType());
                String typeName = parametrizedTypeElement.getSimpleName().toString();
                String mappedTypeName = context.getTypeMappingTarget(typeName);
                if (mappedTypeName != null && mappedTypeName.endsWith("<>")) {
                    print(typeName.substring(0, mappedTypeName.length() - 2));
                    return this;
                }

                if (typeFullName.startsWith(TUPLE_CLASSES_PACKAGE + ".")) {
                    print("[");
                    for (Tree argument : typeApply.getTypeArguments()) {
                        substituteAndPrintType(argument, arrayComponent, inTypeParameters, completeRawTypes, false)
                                .print(",");
                    }
                    if (typeApply.getTypeArguments().size() > 0) {
                        removeLastChar();
                    }
                    print("]");
                    return this;
                }
                if (typeFullName.startsWith(UNION_CLASS_NAME)) {
                    print("(");
                    for (Tree argument : typeApply.getTypeArguments()) {
                        print("(");
                        substituteAndPrintType(argument, arrayComponent, inTypeParameters, completeRawTypes, false);
                        print(")");
                        print("|");
                    }
                    if (typeApply.getTypeArguments().size() > 0) {
                        removeLastChar();
                    }
                    print(")");
                    return this;
                }
                if (typeFullName.startsWith(UTIL_PACKAGE + ".") || typeFullName.startsWith("java.util.function.")) {
                    if (typeName.endsWith("Consumer") || typeName.startsWith("Consumer")) {
                        if (arrayComponent) {
                            print("(");
                        }
                        print("(");
                        if (typeName.startsWith("Int") || typeName.startsWith("Long")
                                || typeName.startsWith("Double")) {
                            print("p0: number");
                        } else {
                            printArguments(typeApply.getTypeArguments());
                        }
                        print(") => void");
                        if (arrayComponent) {
                            print(")");
                        }
                        return this;
                    } else if (typeName.endsWith("Function") || typeName.startsWith("Function")) {
                        if (arrayComponent) {
                            print("(");
                        }
                        print("(");
                        if (typeName.startsWith("Int") || typeName.startsWith("Long")
                                || typeName.startsWith("Double")) {
                            print("p0: number");
                        } else {
                            printArguments(
                                    typeApply.getTypeArguments().subList(0, typeApply.getTypeArguments().size() - 1));
                        }
                        print(") => ");
                        substituteAndPrintType(util().last(typeApply.getTypeArguments()), arrayComponent,
                                inTypeParameters, completeRawTypes, false);
                        if (arrayComponent) {
                            print(")");
                        }
                        return this;
                    } else if (typeName.endsWith("Supplier") || typeName.startsWith("Supplier")) {
                        if (arrayComponent) {
                            print("(");
                        }
                        print("(");
                        print(") => ");
                        if (typeName.startsWith("Int") || typeName.startsWith("Long")
                                || typeName.startsWith("Double")) {
                            print("number");
                        } else {
                            substituteAndPrintType(typeApply.getTypeArguments().get(0), arrayComponent,
                                    inTypeParameters, completeRawTypes, false);
                        }
                        if (arrayComponent) {
                            print(")");
                        }
                        return this;
                    } else if (typeName.endsWith("Predicate")) {
                        if (arrayComponent) {
                            print("(");
                        }
                        print("(");
                        if (typeName.startsWith("Int") || typeName.startsWith("Long")
                                || typeName.startsWith("Double")) {
                            print("p0: number");
                        } else {
                            printArguments(typeApply.getTypeArguments());
                        }
                        print(") => boolean");
                        if (arrayComponent) {
                            print(")");
                        }
                        return this;
                    } else if (typeName.endsWith("Operator")) {
                        if (arrayComponent) {
                            print("(");
                        }
                        print("(");
                        printArgument(typeApply.getTypeArguments().get(0), 1);
                        if (typeName.startsWith("Binary")) {
                            print(", ");
                            printArgument(typeApply.getTypeArguments().get(0), 2);
                        }
                        print(") => ");
                        substituteAndPrintType(typeApply.getTypeArguments().get(0), arrayComponent, inTypeParameters,
                                completeRawTypes, false);
                        if (arrayComponent) {
                            print(")");
                        }
                        return this;
                    }
                }
                if (typeType.toString().startsWith(Class.class.getName() + "<")) {
                    return print("any");
                }
            } else {
                if (!(typeTree instanceof ArrayTypeTree) && typeFullName.startsWith("java.util.function.")) {
                    // case of a raw functional type (programmer's mistake)
                    return print("any");
                }
                String mappedType = context.getTypeMappingTarget(typeFullName);
                if (mappedType != null) {
                    if (mappedType.endsWith("<>")) {
                        print(mappedType.substring(0, mappedType.length() - 2));
                    } else {
                        print(mappedType);
                        if (completeRawTypes && typeElement != null
                                && !((TypeElement) typeElement).getTypeParameters().isEmpty()
                                && !context.getTypeMappingTarget(typeFullName).equals("any")) {
                            printAnyTypeArguments(((TypeElement) typeElement).getTypeParameters().size());
                        }
                    }
                    return this;
                }
            }
            for (BiFunction<ExtendedElement, String, Object> mapping : context.getFunctionalTypeMappings()) {
                Object mapped = mapping.apply(createExtendedElement(typeTree), typeFullName);
                if (mapped instanceof String) {
                    print((String) mapped);
                    return this;
                } else if (mapped instanceof Tree) {
                    substituteAndPrintType((Tree) mapped);
                    return this;
                } else if (mapped instanceof TypeMirror) {
                    print(getAdapter().getMappedType((TypeMirror) mapped));
                    return this;
                }
            }
            for (Function<TypeMirror, String> mapping : context.getFunctionalTypeMirrorMappings()) {
                String mapped = mapping.apply(typeType);
                if (mapped != null) {
                    print(mapped);
                    return this;
                }
            }
        }

        if (typeTree instanceof ParameterizedTypeTree) {
            ParameterizedTypeTree typeApply = ((ParameterizedTypeTree) typeTree);
            substituteAndPrintType(typeApply.getType(), arrayComponent, inTypeParameters, false, disableSubstitution);
            if (!typeApply.getTypeArguments().isEmpty() && !"any".equals(getLastPrintedString(3))
                    && !"Object".equals(getLastPrintedString(6))) {
                print("<");
                for (Tree argument : typeApply.getTypeArguments()) {
                    substituteAndPrintType(argument, arrayComponent, false, completeRawTypes, false).print(", ");
                }
                if (typeApply.getTypeArguments().size() > 0) {
                    removeLastChars(2);
                }
                print(">");
            }
            return this;
        } else if (typeTree instanceof WildcardTree) {
            WildcardTree wildcard = ((WildcardTree) typeTree);
            String name = context.getWildcardName(wildcard);
            if (name == null) {
                return print("any");
            } else {
                print(name);
                if (inTypeParameters) {
                    print(" extends ");
                    return substituteAndPrintType(wildcard.getBound(), arrayComponent, false, completeRawTypes,
                            disableSubstitution);
                } else {
                    return this;
                }
            }
        } else {
            if (typeTree instanceof ArrayTypeTree) {
                return substituteAndPrintType(((ArrayTypeTree) typeTree).getType(), true, inTypeParameters,
                        completeRawTypes, disableSubstitution).print("[]");
            }
            if (completeRawTypes && (typeElement instanceof TypeElement)
                    && ((TypeElement) typeElement).getTypeParameters() != null
                    && !((TypeElement) typeElement).getTypeParameters().isEmpty()) {
                // raw type case (Java warning)
                print(typeTree);
                print("<");
                for (int i = 0; i < ((TypeElement) typeElement).getTypeParameters().size(); i++) {
                    print("any, ");
                }
                removeLastChars(2);
                print(">");
                return this;
            } else {
                return print(typeTree);
            }
        }

    }

    private String getClassName(TypeElement clazz) {
        String name;
        if (context.hasClassNameMapping(clazz)) {
            name = context.getClassNameMapping(clazz);
        } else {
            name = clazz.getSimpleName().toString();
        }
        if (clazz.getKind() == ElementKind.ENUM) {
            name += ENUM_WRAPPER_CLASS_SUFFIX;
        }
        return name;
    }

    @Override
    public Void visitClass(final ClassTree classTree, final Trees trees) {
        TypeElement classTypeElement = Util.getElement(classTree);
        if (getAdapter().substituteType(classTypeElement)) {
            getAdapter().afterType(classTypeElement);
            return null;
        }

        if (context.isIgnored(classTree, compilationUnit)) {
            getAdapter().afterType(classTypeElement);
            return returnNothing();
        }
        String name = classTree.getSimpleName().toString();
        if (context.hasClassNameMapping(classTypeElement)) {
            name = context.getClassNameMapping(classTypeElement);
        }

        if (!scope.isEmpty() && getScope().anonymousClasses.contains(classTree)) {
            name = getScope().name + ANONYMOUS_PREFIX + getScope().anonymousClasses.indexOf(classTree);
        }

        Tree testParent = getFirstParent(ClassTree.class, MethodTree.class);
        if (testParent != null && testParent instanceof MethodTree) {
            if (!isLocalClass()) {
                getScope().localClasses.add(classTree);
                return returnNothing();
            }
        }

        enterScope();
        getScope().name = name;

        ClassTree parent = getParent(ClassTree.class);
        List<TypeParameterElement> parentTypeVars = new ArrayList<>();
        if (parent != null) {
            getScope().innerClass = true;
            if (!classTree.getModifiers().getFlags().contains(Modifier.STATIC)
                    && (getScope(1).getAnonymousClassConstructorFromClassTree(classTree) == null
                            || !isStaticAnonymousClass(getScope(1).getAnonymousClassConstructorFromClassTree(classTree),
                                    getCompilationUnit()))) {
                getScope().innerClassNotStatic = true;
                if (parent.getTypeParameters() != null) {
                    parentTypeVars.addAll(parent.getTypeParameters().stream()
                            .map(t -> (TypeParameterElement) Util.getTypeElement(t)).collect(Collectors.toList()));
                    getAdapter().typeVariablesToErase.addAll(parentTypeVars);
                }
            }
        }
        getScope().declareClassScope = context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_AMBIENT)
                || isDefinitionScope;
        getScope().interfaceScope = false;
        getScope().removedSuperclass = false;
        getScope().enumScope = false;
        getScope().enumWrapperClassScope = false;

        if (getScope().declareClassScope) {
            if (context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_DECORATOR)) {
                print("declare function ").print(name).print("(...args: any[]);").println();
                exitScope();
                return returnNothing();
            }
        } else {
            if (context.lookupDecoratorAnnotation(classTypeElement.getQualifiedName().toString()) != null) {

                boolean requiresDecoratorFunction = context.getAnnotationValue(classTypeElement,
                        JSweetConfig.ANNOTATION_DECORATOR, Boolean.class, true);
                if (requiresDecoratorFunction) {

                    GlobalMethodInfos globalDecoratorFunction = context
                            .lookupGlobalMethod(classTypeElement.getQualifiedName().toString());
                    if (globalDecoratorFunction == null) {
                        report(classTree, JSweetProblem.CANNOT_FIND_GLOBAL_DECORATOR_FUNCTION,
                                classTypeElement.getQualifiedName());
                    } else {
                        CompilationUnitTree previousCompilUnit = getCompilationUnit();
                        getScope().decoratorScope = true;
                        compilationUnit = globalDecoratorFunction.compilationUnitTree;
                        try {
                            enter(globalDecoratorFunction.classTree);
                            print(globalDecoratorFunction.methodTree);
                            exit();
                        } finally {
                            getScope().decoratorScope = false;
                            compilationUnit = previousCompilUnit;
                        }
                    }
                    exitScope();
                    return returnNothing();

                }
            }
        }

        HashSet<DefaultMethodEntry> defaultMethods = null;
        boolean globals = JSweetConfig.GLOBALS_CLASS_NAME.equals(classTree.getSimpleName().toString());
        if (globals && classTree.getExtendsClause() != null) {
            report(classTree, JSweetProblem.GLOBALS_CLASS_CANNOT_HAVE_SUPERCLASS);
        }
        List<TypeMirror> implementedInterfaces = new ArrayList<>();

        if (!globals) {
            if (classTree.getExtendsClause() != null && JSweetConfig.GLOBALS_CLASS_NAME
                    .equals(Util.getTypeElement(classTree.getExtendsClause()).getSimpleName().toString())) {
                report(classTree, JSweetProblem.GLOBALS_CLASS_CANNOT_BE_SUBCLASSED);
                return returnNothing();
            }
            if (!(classTree.getKind() == Kind.ENUM && scope.size() > 1 && getScope(1).isComplexEnum)) {
                printDocComment(classTree);
            } else {
                print("/** @ignore */").println().printIndent();
            }
            print(classTree.getModifiers());

            if (!isTopLevelScope() || context.useModules || context.moduleBundleMode || isAnonymousClass()
                    || isInnerClass() || isLocalClass()) {
                print("export ");
            }
            if (context.isInterface(classTypeElement)) {
                print("interface ");
                getScope().interfaceScope = true;
            } else {
                if (classTree.getKind() == Kind.ENUM) {
                    if (getScope().declareClassScope && !(getIndent() != 0 && isDefinitionScope)) {
                        print("declare ");
                    }
                    if (scope.size() > 1 && getScope(1).isComplexEnum) {
                        if (util().hasAbstractMethod(classTypeElement)) {
                            print("abstract ");
                        }
                        print("class ");
                        getScope().enumWrapperClassScope = true;
                    } else {
                        print("enum ");
                        getScope().enumScope = true;
                        if (context.hasAnnotationType(classTypeElement, ANNOTATION_STRING_ENUM)) {
                            getScope().stringEnumScope = true;
                            getScope().enumWrapperClassScope = false;
                            getScope().isComplexEnum = false;
                        }
                    }
                } else {
                    if (getScope().declareClassScope && !(getIndent() != 0 && isDefinitionScope)) {
                        print("declare ");
                    }
                    defaultMethods = new HashSet<>();
                    util().findDefaultMethodsInType(defaultMethods, context, classTypeElement);
                    if (classTree.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
                        print("abstract ");
                    }
                    print("class ");
                }
            }

            print(name + (getScope().enumWrapperClassScope ? ENUM_WRAPPER_CLASS_SUFFIX : ""));

            if (classTree.getTypeParameters() != null && classTree.getTypeParameters().size() > 0) {
                print("<").printArgList(null, classTree.getTypeParameters()).print(">");
            } else if (isAnonymousClass()) {
                NewClassTree newClass = getScope(1).anonymousClassesConstructors
                        .get(getScope(1).anonymousClasses.indexOf(classTree));

                if (isStaticAnonymousClass(newClass, getCompilationUnit())) {
                    printAnonymousClassTypeArgs(newClass);
                }
            }
            TypeMirror mixin = null;
            if (context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_MIXIN)) {
                mixin = context.getAnnotationValue(classTypeElement, JSweetConfig.ANNOTATION_MIXIN, TypeMirror.class,
                        null);
                for (AnnotationMirror c : classTypeElement.getAnnotationMirrors()) {
                    if (JSweetConfig.ANNOTATION_MIXIN.equals(c.getAnnotationType().toString())) {

                        Entry<? extends ExecutableElement, ? extends AnnotationValue> annotationValuesEntry = firstOrDefault(
                                c.getElementValues().entrySet());

                        AnnotationValue mixinClassAnnotationValue = annotationValuesEntry.getValue();
                        TypeMirror mixinClassType = (TypeMirror) mixinClassAnnotationValue.getValue();
                        TypeElement valueTypeElement = (TypeElement) types().asElement(mixinClassType);
                        String targetName = getRootRelativeName(valueTypeElement);
                        String mixinName = getRootRelativeName(classTypeElement);
                        if (!mixinName.equals(targetName)) {
                            report(classTree, JSweetProblem.WRONG_MIXIN_NAME, mixinName, targetName);
                        } else {
                            if (valueTypeElement.equals(classTypeElement)) {
                                report(classTree, JSweetProblem.SELF_MIXIN_TARGET, mixinName);
                            }
                        }
                    }
                }
            }

            // print EXTENDS
            boolean extendsInterface = false;
            if (classTree.getExtendsClause() != null) {
                TypeElement superTypeElement = Util.getTypeElement(classTree.getExtendsClause());
                TypeMirror superType = superTypeElement.asType();

                boolean removeIterable = false;
                if (context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_SYNTACTIC_ITERABLE)
                        && superTypeElement.getQualifiedName().toString().equals(Iterable.class.getName())) {
                    removeIterable = true;
                }

                if (!getAdapter().substituteExtends(classTypeElement)) {
                    if (!removeIterable && !JSweetConfig.isJDKReplacementMode()
                            && !(JSweetConfig.OBJECT_CLASSNAME.equals(superType.toString())
                                    || Object.class.getName().equals(superType.toString()))
                            && !(mixin != null && types().isSameType(mixin, superType))
                            && !(getAdapter().eraseSuperClass(classTypeElement, superTypeElement))) {

                        if (!getScope().interfaceScope && context.isInterface(superTypeElement)) {
                            extendsInterface = true;
                            print(" implements ");
                            implementedInterfaces.add(superType);
                        } else {
                            print(" extends ");
                        }
                        if (getScope().enumWrapperClassScope && getScope(1).anonymousClasses.contains(classTree)) {
                            print(classTree.getExtendsClause().toString() + ENUM_WRAPPER_CLASS_SUFFIX);
                        } else {
                            disableTypeSubstitution = !getAdapter().isSubstituteSuperTypes();
                            substituteAndPrintType(classTree.getExtendsClause());
                            disableTypeSubstitution = false;
                        }
                        if (context.classesWithWrongConstructorOverload.contains(classTypeElement)) {
                            getScope().hasConstructorOverloadWithSuperClass = true;
                        }
                    } else {
                        getScope().removedSuperclass = true;
                    }
                }
            }

            // print IMPLEMENTS
            if (!getAdapter().substituteImplements(classTypeElement)) {
                if (classTree.getImplementsClause() != null && !classTree.getImplementsClause().isEmpty()
                        && !getScope().enumScope) {
                    List<Tree> implementing = new ArrayList<>(classTree.getImplementsClause());

                    if (context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_SYNTACTIC_ITERABLE)) {
                        for (Tree implementsTree : classTree.getImplementsClause()) {
                            TypeElement implementsElement = Util.getTypeElement(implementsTree);
                            if (implementsElement.getQualifiedName().toString().equals(Iterable.class.getName())) {
                                implementing.remove(implementsTree);
                            }
                        }
                    }

                    for (Tree implementsTree : classTree.getImplementsClause()) {
                        TypeElement implementsElement = Util.getTypeElement(implementsTree);
                        // erase Java interfaces
                        if (context.isFunctionalType(implementsElement) //
                                || getAdapter().eraseSuperInterface(classTypeElement, implementsElement)) {
                            implementing.remove(implementsTree);
                        }
                    }

                    if (!implementing.isEmpty()) {
                        if (!extendsInterface) {
                            if (getScope().interfaceScope) {
                                print(" extends ");
                            } else {
                                print(" implements ");
                            }
                        } else {
                            print(", ");
                        }
                        for (Tree implementsTree : implementing) {
                            TypeMirror implementsType = Util.getType(implementsTree);
                            disableTypeSubstitution = !getAdapter().isSubstituteSuperTypes();
                            substituteAndPrintType(implementsTree);
                            disableTypeSubstitution = false;
                            implementedInterfaces.add(implementsType);
                            print(", ");
                        }
                        removeLastChars(2);
                    }
                }
            }
            print(" {").println().startIndent();
        }

        getAdapter().beforeTypeBody(classTypeElement);

        if (getScope().innerClassNotStatic && !getScope().interfaceScope && !getScope().enumScope
                && !getScope().enumWrapperClassScope) {
            printIndent().print("public " + PARENT_CLASS_FIELD_NAME + ": any;").println();
        }

        Set<MethodTree> injectedDefaultMethods = new HashSet<>();
        Map<MethodTree, DefaultMethodEntry> injectedDefaultMethodMap = new HashMap<>();
        if (defaultMethods != null && !defaultMethods.isEmpty()) {

            for (DefaultMethodEntry entry : defaultMethods) {

                ExecutableElement methodElementMatch = util().findMethodDeclarationInType2(classTypeElement,
                        entry.methodTree.getName().toString(), entry.methodType);
                if (methodElementMatch == null || methodElementMatch == entry.methodElement) {
                    injectedDefaultMethods.add(entry.methodTree);
                    injectedDefaultMethodMap.put(entry.methodTree, entry);
                }
            }
        }

        if (getScope().enumScope) {
            printIndent();
        }
        if (globals) {
            removeLastIndent();
        }

        for (Tree def : classTree.getMembers()) {
            if (def instanceof ClassTree) {
                getScope().hasInnerClass = true;
            }
            if (def instanceof VariableTree) {
                VariableTree var = (VariableTree) def;
                VariableElement varElement = Util.getElement(var);
                if (!varElement.getModifiers().contains(Modifier.STATIC) && var.getInitializer() != null) {
                    getScope().fieldsWithInitializers.add(var);
                }
            }
        }

        if (!globals && !getScope().enumScope && !context.isInterface(classTypeElement)
                && context.getStaticInitializerCount(classTypeElement) > 0) {
            printIndent().print("static __static_initialized: boolean = false;").println();
            int liCount = context.getStaticInitializerCount(classTypeElement);
            String prefix = getClassName(classTypeElement) + ".";
            printIndent().print("static __static_initialize() { ");
            print("if (!" + prefix + "__static_initialized) { ");
            print(prefix + "__static_initialized = true; ");
            for (int i = 0; i < liCount; i++) {
                print(prefix + "__static_initializer_" + i + "(); ");
            }
            print("} }").println().println();
            String qualifiedClassName = getQualifiedTypeName(classTypeElement, globals, true);
            if (util().isPartOfAnEnum(classTypeElement)) {
                qualifiedClassName += ENUM_WRAPPER_CLASS_SUFFIX;
            }
            context.addTopFooterStatement(
                    (isBlank(qualifiedClassName) ? "" : qualifiedClassName + ".__static_initialize();"));
        }

        boolean hasUninitializedFields = false;

        List<Tree> classMembers = new ArrayList<>();
        classMembers.addAll(injectedDefaultMethods);
        classMembers.addAll(classTree.getMembers());
        if (context.options.isSortClassMembers()) {
            List<Tree> memberDefs = classMembers.stream()
                    .filter(t -> (t instanceof MethodTree || t instanceof VariableTree)).sorted((t1, t2) -> getAdapter()
                            .getClassMemberComparator().compare(createExtendedElement(t1), createExtendedElement(t2)))
                    .collect(Collectors.toList());
            classMembers = new ArrayList<>();
            classMembers.addAll(memberDefs);
            for (Tree def : classTree.getMembers()) {
                if (!classMembers.contains(def)) {
                    classMembers.add(def);
                }
            }
        }

        for (Tree def : classMembers) {
            if (injectedDefaultMethods.contains(def)) {
                MethodTree defaultMethod = (MethodTree) def;
                DefaultMethodEntry entry = injectedDefaultMethodMap.get(defaultMethod);

                getScope().defaultMethodScope = true;
                getAdapter().typeVariablesToErase
                        .addAll(((TypeElement) entry.methodElement.getEnclosingElement()).getTypeParameters());
                // scan for used types to generate imports
                if (context.useModules) {
                    UsedTypesScanner scanner = new UsedTypesScanner();
                    if (!context.hasAnnotationType(entry.methodElement, JSweetConfig.ANNOTATION_ERASED)) {
                        if (context.hasAnnotationType(entry.methodElement, JSweetConfig.ANNOTATION_REPLACE)) {
                            // do not scan the method body
                            scanner.scan(entry.methodTree.getParameters(), trees);
                            scanner.scan(entry.methodTree.getReturnType(), trees);
                            scanner.scan(entry.methodTree.getThrows(), trees);
                            scanner.scan(entry.methodTree.getTypeParameters(), trees);
                            scanner.scan(entry.methodTree.getReceiverParameter(), trees);
                        } else {
                            scanner.scan(entry.methodTree, trees);
                        }
                    }
                }
                if (!context.hasAnnotationType(entry.methodElement, JSweetConfig.ANNOTATION_ERASED)) {
                    printIndent().print(
                            "/* Default method injected from " + entry.enclosingClassElement.getQualifiedName() + " */")
                            .println();
                }

                printIndent().print(defaultMethod).println();

                getAdapter().typeVariablesToErase
                        .removeAll(((TypeElement) entry.methodElement.getEnclosingElement()).getTypeParameters());

                getScope().defaultMethodScope = false;
                continue;
            }

            if (getScope().interfaceScope) {
                // static interface members are printed in a namespace
                Element memberElement = Util.getElementNoErrors(def);
                if (memberElement != null && (def instanceof MethodTree || def instanceof VariableTree)
                        && memberElement.getModifiers().contains(Modifier.STATIC)) {
                    continue;
                }
            }
            if (getScope().interfaceScope && def instanceof MethodTree) {
                // object method should not be defined otherwise they will have
                // to be implemented
                if (util().isOverridingBuiltInJavaObjectMethod(Util.getElement(def))) {
                    continue;
                }
            }
            if (def instanceof ClassTree) {
                // inner types are be printed in a namespace
                continue;
            }
            if (def instanceof VariableTree) {
                if (getScope().enumScope && Util.getElement(def).getKind() != ElementKind.ENUM_CONSTANT) {
                    getScope().isComplexEnum = true;
                    continue;
                }
                if (shouldPrintFieldInitializationInConstructor((VariableTree) def)) {
                    hasUninitializedFields = true;
                }
            }
            if (def instanceof BlockTree && !((BlockTree) def).isStatic()) {
                hasUninitializedFields = true;
            }
            if (!getScope().enumScope) {
                printIndent();
            }
            int pos = getCurrentPosition();
            print(def);

            if (getScope().enumScope && def.getKind() == Kind.VARIABLE && getScope().stringEnumScope) {
                print(" =  " + getStringLiteralQuote());
                print(def);
                print(getStringLiteralQuote());
            }

            if (getCurrentPosition() == pos) {
                if (!getScope().enumScope) {
                    removeLastIndent();
                }
                continue;
            }
            if (def instanceof VariableTree) {
                if (getScope().enumScope) {
                    print(", ");
                } else {
                    if (getLastPrintedChar() != '}') {
                        print(";");
                    }
                    println().println();
                }
            } else {
                println().println();
            }
        }

        // generate missing abstract methods if abstract class
        if (!getScope().interfaceScope && classTree.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
            List<ExecutableElement> methods = new ArrayList<>();
            for (TypeMirror t : implementedInterfaces) {
                context.grabMethodsToBeImplemented(methods, (TypeElement) types().asElement(t));
            }

            methods.sort( //
                    (ExecutableElement m1, ExecutableElement m2) //
                    -> m1.getSimpleName().toString().compareTo(m2.getSimpleName().toString()));

            Map<String, String> signatures = new HashMap<>();
            for (ExecutableElement meth : methods) {
                if (meth.getKind() == ElementKind.METHOD
                        && !context.hasAnnotationType(meth, JSweetConfig.ANNOTATION_ERASED)
                        && !isMappedOrErasedType(util().getParentElement(meth, TypeElement.class))) {

                    // do not generate default abstract method for already
                    // generated methods
                    if (getScope().generatedMethodNames.contains(meth.getSimpleName().toString())) {
                        continue;
                    }
                    ExecutableType methodType = (ExecutableType) meth.asType();
                    ExecutableElement s = util().findMethodDeclarationInType(classTypeElement,
                            meth.getSimpleName().toString(), methodType, true);
                    if (Object.class.getName().equals(s.getEnclosingElement().toString())) {
                        s = null;
                    }
                    boolean printAbstractDeclaration = false;
                    if (s != null) {
                        if (!s.getEnclosingElement().equals(classTypeElement)) {
                            if (!(s.isDefault() || (!context.isInterface((TypeElement) s.getEnclosingElement())
                                    && !s.getModifiers().contains(Modifier.ABSTRACT)))) {
                                printAbstractDeclaration = true;
                            }
                        }
                    }

                    if (printAbstractDeclaration) {
                        Overload o = context.getOverload(classTypeElement, meth);
                        if (o != null && o.getMethodsCount() > 1 && !o.isValid) {
                            if (!methodType.equals(o.getCoreEntry().methodType)) {
                                printAbstractDeclaration = false;
                            }
                        }
                    }
                    if (s == null || printAbstractDeclaration) {
                        String signature = getContext().types.erasure(methodType).toString();
                        String methodName = meth.getSimpleName().toString();
                        if (!(signatures.containsKey(methodName) && signatures.get(methodName).equals(signature))) {
                            printAbstractMethodDeclaration(meth);
                            signatures.put(methodName, signature);
                        }
                    }
                }
            }
        }

        // automated constructor generation (for inner class for instance)
        if (!getScope().hasDeclaredConstructor
                && !(getScope().interfaceScope || getScope().enumScope || getScope().declareClassScope)) {
            Set<String> interfaces = new HashSet<>();
            context.grabSupportedInterfaceNames(interfaces, classTypeElement);
            if (!interfaces.isEmpty() || getScope().innerClass || getScope().innerClassNotStatic
                    || hasUninitializedFields) {
                printIndent().print("constructor(");
                boolean hasArgs = false;
                if (getScope().innerClassNotStatic) {
                    print(PARENT_CLASS_FIELD_NAME + ": any");
                    hasArgs = true;
                }
                int anonymousClassIndex = scope.size() > 1 ? getScope(1).anonymousClasses.indexOf(classTree) : -1;
                if (anonymousClassIndex != -1) {
                    for (int i = 0; i < getScope(1).anonymousClassesConstructors.get(anonymousClassIndex).getArguments()
                            .size(); i++) {
                        if (!hasArgs) {
                            hasArgs = true;
                        } else {
                            print(", ");
                        }
                        print("__arg" + i + ": any");
                    }
                    for (VariableElement v : getScope(1).finalVariables.get(anonymousClassIndex)) {
                        if (!hasArgs) {
                            hasArgs = true;
                        } else {
                            print(", ");
                        }
                        print("private " + v.getSimpleName() + ": any");
                    }
                }

                print(") {").startIndent().println();
                if (classTree.getExtendsClause() != null && !getScope().removedSuperclass) {
                    TypeElement superTypeElement = Util.getTypeElement(classTree.getExtendsClause());
                    if (!context.isInterface(superTypeElement)) {
                        printIndent().print("super(");
                        boolean hasArg = false;
                        if (getScope().innerClassNotStatic) {
                            if (superTypeElement.getEnclosingElement() instanceof TypeElement //
                                    && !superTypeElement.getModifiers().contains(Modifier.STATIC)) {
                                print(PARENT_CLASS_FIELD_NAME);
                                hasArg = true;
                            }
                        }
                        if (anonymousClassIndex != -1) {
                            for (int i = 0; i < getScope(1).anonymousClassesConstructors.get(anonymousClassIndex)
                                    .getArguments().size(); i++) {
                                if (hasArg) {
                                    print(", ");
                                } else {
                                    hasArg = true;
                                }
                                print("__arg" + i);
                            }
                        }
                        print(");").println();
                    }

                }
                printInstanceInitialization(classTree, null);
                endIndent().printIndent().print("}").println().println();
            }
        }

        removeLastChar();

        if (getScope().enumWrapperClassScope && !getScope(1).anonymousClasses.contains(classTree)) {
            printIndent().print("public name(): string { return this." + ENUM_WRAPPER_CLASS_NAME + "; }").println();
            printIndent().print("public ordinal(): number { return this." + ENUM_WRAPPER_CLASS_ORDINAL + "; }")
                    .println();
            printIndent().print("public compareTo(other: any): number { return this." + ENUM_WRAPPER_CLASS_ORDINAL
                    + " - (isNaN(other)?other." + ENUM_WRAPPER_CLASS_ORDINAL + ":other); }").println();
        }

        if (getScope().enumScope) {
            removeLastChar().println();
        }

        getAdapter().afterTypeBody(classTypeElement);

        if (!globals) {
            endIndent().printIndent().print("}");

            if (!getScope().interfaceScope && !getScope().declareClassScope && !getScope().enumScope
                    && !(getScope().enumWrapperClassScope
                            && classTypeElement.getNestingKind() == NestingKind.ANONYMOUS)) {
                if (classTypeElement.getNestingKind() != NestingKind.ANONYMOUS) {
                    println().printIndent()
                            .print(getScope().enumWrapperClassScope ? classTypeElement.getSimpleName().toString()
                                    : name)
                            .print("[" + getStringLiteralQuote() + CLASS_NAME_IN_CONSTRUCTOR + getStringLiteralQuote()
                                    + "] = ")
                            .print(getStringLiteralQuote() + classTypeElement.getQualifiedName().toString()
                                    + getStringLiteralQuote() + ";");
                }
                Set<String> interfaces = new HashSet<>();
                context.grabSupportedInterfaceNames(interfaces, classTypeElement);
                if (!interfaces.isEmpty()) {
                    println().printIndent()
                            .print(getScope().enumWrapperClassScope ? classTypeElement.getSimpleName().toString()
                                    : name)
                            .print("[" + getStringLiteralQuote() + INTERFACES_FIELD_NAME + getStringLiteralQuote()
                                    + "] = ");
                    print("[");
                    for (String itf : interfaces) {
                        print(getStringLiteralQuote()).print(itf).print(getStringLiteralQuote() + ",");
                    }
                    removeLastChar();
                    print("];").println();
                }
                if (!getScope().enumWrapperClassScope) {
                    println();
                }
            }
        }

        // enum class for complex enum
        if (getScope().isComplexEnum) {
            println().println().printIndent();
            visitClass(classTree, trees);
        }

        boolean nameSpace = false;

        if (getScope().interfaceScope) {
            // print static members of interfaces
            for (Tree def : classTree.getMembers()) {
                Element memberElement = Util.getElementNoErrors(def);
                if ((def instanceof MethodTree || def instanceof VariableTree)
                        && memberElement.getModifiers().contains(Modifier.STATIC)) {

                    if (def instanceof VariableTree && context.hasAnnotationType(memberElement, ANNOTATION_STRING_TYPE,
                            JSweetConfig.ANNOTATION_ERASED)) {
                        continue;
                    }
                    if (!nameSpace) {
                        nameSpace = true;
                        println().println().printIndent();

                        if (getIndent() != 0 || context.useModules || context.moduleBundleMode) {
                            print("export ");
                        } else {
                            if (isDefinitionScope) {
                                print("declare ");
                            }
                        }

                        print("namespace ").print(classTree.getSimpleName().toString()).print(" {").startIndent();
                    }
                    println().println().printIndent().print(def);
                    if (def instanceof VariableTree) {
                        print(";");
                    }
                }
            }
            if (nameSpace) {
                println().endIndent().printIndent().print("}").println();
            }
        }

        nameSpace = false;
        // inner, anonymous and local classes in a namespace
        // ======================
        // print valid inner classes
        for (Tree def : util().getSortedClassDeclarations(classTree.getMembers(), compilationUnit)) {
            if (def instanceof ClassTree) {
                ClassTree cdef = (ClassTree) def;
                if (context.isIgnored(cdef, compilationUnit)) {
                    continue;
                }
                if (!nameSpace) {
                    nameSpace = true;
                    println().println().printIndent();
                    if (!isTopLevelScope() || context.useModules || context.moduleBundleMode) {
                        print("export ");
                    } else {
                        if (isDefinitionScope) {
                            print("declare ");
                        }
                    }
                    print("namespace ").print(name).print(" {").startIndent();
                }
                getScope().isInnerClass = true;
                println().println().printIndent().print(cdef);
                getScope().isInnerClass = false;
            }
        }
        // print anonymous classes
        for (ClassTree cdef : getScope().anonymousClasses) {
            if (!nameSpace) {
                nameSpace = true;
                println().println().printIndent();
                if (!isTopLevelScope() || context.useModules || context.moduleBundleMode) {
                    print("export ");
                }
                print("namespace ").print(name).print(" {").startIndent();
            }
            getScope().isAnonymousClass = true;
            println().println().printIndent().print(cdef);
            getScope().isAnonymousClass = false;
        }
        // print local classes
        for (ClassTree cdef : getScope().localClasses) {
            if (!nameSpace) {
                nameSpace = true;
                println().println().printIndent();
                if (!isTopLevelScope() || context.useModules) {
                    print("export ");
                }
                print("namespace ").print(name).print(" {").startIndent();
            }
            getScope().isLocalClass = true;
            println().println().printIndent().print(cdef);
            getScope().isLocalClass = false;
        }
        if (nameSpace) {
            println().endIndent().printIndent().print("}").println();
        }
        // end of namespace =================================================

        if (getScope().enumScope && getScope().isComplexEnum && !getScope().anonymousClasses.contains(classTree)) {
            println().printIndent().print(classTypeElement.getSimpleName().toString()).print(
                    "[" + getStringLiteralQuote() + ENUM_WRAPPER_CLASS_WRAPPERS + getStringLiteralQuote() + "] = {");
            int index = 0;
            for (Tree tree : classTree.getMembers()) {
                Element memberElement = Util.getElementNoErrors(tree);

                if (tree instanceof VariableTree && memberElement.getKind() == ElementKind.ENUM_CONSTANT) {
                    VariableTree varDecl = (VariableTree) tree;
                    // enum fields are not part of the enum auxiliary class but
                    // will initialize the enum values
                    NewClassTree newClass = (NewClassTree) varDecl.getInitializer();
                    ClassTree clazz = classTree;
                    try {

                        if (getScope().stringEnumScope) {
                            print(getStringLiteralQuote());
                            print(memberElement.getSimpleName().toString());
                            print(getStringLiteralQuote());
                        } else {
                            print("" + index);
                        }
                        print(": ");

                        int anonymousClassIndex = getScope().anonymousClasses.indexOf(newClass.getClassBody());
                        if (anonymousClassIndex >= 0) {
                            print("new ")
                                    .print(clazz.getSimpleName().toString() + "." + clazz.getSimpleName().toString()
                                            + ANONYMOUS_PREFIX + anonymousClassIndex + ENUM_WRAPPER_CLASS_SUFFIX)
                                    .print("(");
                        } else {
                            print("new ").print(clazz.getSimpleName().toString() + ENUM_WRAPPER_CLASS_SUFFIX)
                                    .print("(");
                        }
                        print("" + (index++) + ", ");
                        print(getStringLiteralQuote() + memberElement.getSimpleName().toString()
                                + getStringLiteralQuote());
                        if (!newClass.getArguments().isEmpty()) {
                            print(", ");
                        }
                        printArgList(null, newClass.getArguments()).print(")");
                        print(", ");
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            removeLastChars(2);
            print("};").println();
        }

        if (getScope().mainMethod != null && getScope().mainMethod.getParameters().size() < 2
                && getScope().getMainMethodElement().getEnclosingElement().equals(classTypeElement)) {
            String mainClassName = getQualifiedTypeName(classTypeElement, globals, true);
            if (util().isPartOfAnEnum(classTypeElement)) {
                mainClassName += ENUM_WRAPPER_CLASS_SUFFIX;
            }

            String mainMethodQualifier = mainClassName;
            if (!isBlank(mainClassName)) {
                mainMethodQualifier = mainClassName + ".";
            }
            context.entryFiles.add(new File(compilationUnit.getSourceFile().getName()));
            context.addFooterStatement(mainMethodQualifier + JSweetConfig.MAIN_FUNCTION_NAME + "("
                    + (getScope().mainMethod.getParameters().isEmpty() ? "" : "null") + ");");
        }

        getAdapter().typeVariablesToErase.removeAll(parentTypeVars);
        exitScope();

        getAdapter().afterType(classTypeElement);

        return returnNothing();
    }

    private void printAbstractMethodDeclaration(ExecutableElement method) {
        printIndent().print("public abstract ").print(method.getSimpleName().toString());
        print("(");
        if (method.getParameters() != null && !method.getParameters().isEmpty()) {
            for (VariableElement var : method.getParameters()) {
                print(var.getSimpleName().toString()).print("?: any");
                print(", ");
            }
            removeLastChars(2);
        }
        print(")");
        print(": any;").println();
    }

    private String getTSMethodName(MethodTree methodDecl) {
        ExecutableElement methodElement = Util.getElement(methodDecl);
        String name = context.getActualName(methodElement);
        switch (name) {
        case CONSTRUCTOR_METHOD_NAME:
            return "constructor";
        case JSweetConfig.ANONYMOUS_FUNCTION_NAME:
        case JSweetConfig.ANONYMOUS_STATIC_FUNCTION_NAME:
            return "";
        case JSweetConfig.ANONYMOUS_DEPRECATED_FUNCTION_NAME:
        case JSweetConfig.ANONYMOUS_DEPRECATED_STATIC_FUNCTION_NAME:
            if (context.deprecatedApply) {
                return "";
            } else {
                return name;
            }
        case JSweetConfig.NEW_FUNCTION_NAME:
            return "new";
        default:
            if (context.hasMethodNameMapping(methodElement)) {
                return context.getMethodNameMapping(methodElement);
            } else {
                return name;
            }
        }
    }

    private boolean printCoreMethodDelegate = false;

    protected boolean isDebugMode(MethodTree methodDecl) {
        ExecutableElement methodElement = Util.getElement(methodDecl);
        return methodDecl != null && !getScope().constructor && context.options.isDebugMode()
                && !(context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_NO_DEBUG) || context
                        .hasAnnotationType(methodElement.getEnclosingElement(), JSweetConfig.ANNOTATION_NO_DEBUG));
    }

    private boolean isInterfaceMethod(ClassTree parent, MethodTree method) {
        ExecutableElement methodElement = Util.getElement(method);
        TypeElement parentElement = Util.getElement(parent);
        return context.isInterface(parentElement) && !methodElement.getModifiers().contains(Modifier.STATIC);
    }

    @Override
    public Void visitMethod(final MethodTree methodTree, final Trees trees) {
        ExecutableElement methodElement = Util.getElement(methodTree);

        if (getAdapter().substituteExecutable(methodElement)) {
            return returnNothing();
        }

        if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_ERASED)) {
            // erased elements are ignored
            return returnNothing();
        }

        final ClassTree parent = (ClassTree) getParent();
        TypeElement parentElement = Util.getElement(parent);

        if (!getScope().enumWrapperClassScope //
                && parent != null //
                && util().isGeneratedConstructor(methodTree, parent, methodElement) //
        ) {
            return returnNothing();
        }

        if (JSweetConfig.INDEXED_GET_FUCTION_NAME.equals(methodTree.getName().toString())
                && methodTree.getParameters().size() == 1) {
            print("[").print(methodTree.getParameters().get(0)).print("]: ");
            substituteAndPrintType(methodTree.getReturnType()).print(";");
            return returnNothing();
        }

        getScope().constructor = methodElement.getKind() == ElementKind.CONSTRUCTOR;
        if (getScope().enumScope) {
            if (getScope().constructor) {
                if (parent != null && util().getStartPosition(methodTree, getCompilationUnit()) != util()
                        .getStartPosition(parent, getCompilationUnit())) {
                    getScope().isComplexEnum = true;
                }
            } else {
                getScope().isComplexEnum = true;
            }
            return returnNothing();
        }

        // do not generate definition if parent class already declares method to avoid
        // wrong override error with overloads ({ scale(number); scale(number, number);
        // } cannot be overriden with { scale(number) } only)
        if (getScope().isDeclareClassScope() && parent.getExtendsClause() != null && !getScope().constructor) {

            TypeElement superTypeElement = Util.getTypeElement(parent.getExtendsClause());

            ExecutableElement superMethod = util().findMethodDeclarationInType(superTypeElement,
                    methodTree.getName().toString(), Util.getType(methodTree));
            if (superMethod != null && util().isSourceElement(superMethod)) {
                return returnNothing();
            }
        }

        Overload overload = null;
        boolean inOverload = false;
        boolean inCoreWrongOverload = false;
        if (parent != null) {
            overload = context.getOverload(parentElement, methodElement);
            inOverload = overload != null && overload.getMethodsCount() > 1;
            if (inOverload) {
                if (!overload.isValid) {
                    if (!printCoreMethodDelegate) {
                        if (overload.getCoreMethod().equals(methodTree)) {
                            inCoreWrongOverload = true;
                            if (!isInterfaceMethod(parent, methodTree)
                                    && methodElement.getKind() != ElementKind.CONSTRUCTOR
                                    && parentElement.equals(overload.getCoreMethodElement().getEnclosingElement())) {
                                printCoreMethodDelegate = true;
                                visitMethod(overload.getCoreMethod(), trees);
                                println().println().printIndent();
                                printCoreMethodDelegate = false;
                            }
                        } else {
                            if (methodElement.getKind() == ElementKind.CONSTRUCTOR) {
                                return returnNothing();
                            }
                            boolean addCoreMethod = false;
                            addCoreMethod = !overload.printed
                                    && overload.getCoreMethodElement().getEnclosingElement() != parentElement
                                    && !overload.getCoreMethodElement().getModifiers().contains(Modifier.DEFAULT)
                                    && (!overload.getCoreMethodElement().getModifiers().contains(Modifier.ABSTRACT)
                                            || isInterfaceMethod(parent, methodTree)
                                            || !types().isSubtype(parentElement.asType(),
                                                    overload.getCoreMethodElement().getEnclosingElement().asType()));
                            if (!overload.printed && !addCoreMethod
                                    && overload.getCoreMethodElement().getKind() == ElementKind.METHOD) {
                                addCoreMethod = util().findMethodDeclarationInType(parentElement,
                                        methodTree.getName().toString(),
                                        (ExecutableType) overload.getCoreMethodElement().asType()) == null;
                            }
                            if (addCoreMethod) {
                                visitMethod(overload.getCoreMethod(), trees);
                                overload.printed = true;
                                if (!isInterfaceMethod(parent, methodTree)) {
                                    println().println().printIndent();
                                }
                            }
                            if (isInterfaceMethod(parent, methodTree)) {
                                return returnNothing();
                            }
                        }
                    }
                } else {
                    if (!overload.getCoreMethod().equals(methodTree)) {
                        return returnNothing();
                    }
                }
            }
        }

        boolean ambient = context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_AMBIENT);

        if (inOverload && !inCoreWrongOverload && (ambient || isDefinitionScope)) {
            // do not generate method stubs for definitions
            return returnNothing();
        }

        if (isDebugMode(methodTree)) {
            printMethodModifiers(methodTree, parent, getScope().constructor, inOverload, overload);
            print(getTSMethodName(methodTree)).print("(");
            printArgList(null, methodTree.getParameters());
            print("): ");
            substituteAndPrintType(methodTree.getReturnType());
            print(" {").println();
            startIndent().printIndent();

            if (!util().isVoidType(methodElement.getReturnType())) {
                print("return ");
            }
            print("__debug_exec('" + (parentElement == null ? "null" : parentElement.getQualifiedName()) + "', '"
                    + methodTree.getName() + "', ");
            if (!methodTree.getParameters().isEmpty()) {
                print("[");
                for (VariableTree param : methodTree.getParameters()) {
                    print("'" + param.getName() + "', ");
                }
                removeLastChars(2);
                print("]");
            } else {
                print("undefined");
            }
            print(", this, arguments, ");
            if (methodElement.getModifiers().contains(Modifier.STATIC)) {
                print(methodElement.getEnclosingElement().getSimpleName().toString());
            } else {
                print("this");
            }
            print("." + GENERATOR_PREFIX + getTSMethodName(methodTree) + "(");
            for (VariableTree param : methodTree.getParameters()) {
                VariableElement paramElement = Util.getElement(param);
                print(context.getActualName(paramElement) + ", ");
            }
            if (!methodTree.getParameters().isEmpty()) {
                removeLastChars(2);
            }
            print("));");
            println().endIndent().printIndent();
            print("}").println().println().printIndent();
        }

        if (inOverload && !overload.isValid) {
            // spread all annotations of the overload to the methods
            for (MethodTree m : overload.getMethods()) {
                if (m != methodTree) {
                    for (AnnotationTree anno : m.getModifiers().getAnnotations()) {
                        if (!methodTree.getModifiers().getAnnotations().stream().anyMatch(
                                a -> a.getAnnotationType().toString().equals(anno.getAnnotationType().toString()))) {
                            util().addAnnotation(methodTree.getModifiers(), anno);
                        }
                    }
                }
            }
        }

        print(methodTree.getModifiers());

        if (methodTree.getModifiers().getFlags().contains(Modifier.NATIVE)) {
            if (!getScope().declareClassScope && !ambient && !getScope().interfaceScope) {
                report(methodTree, methodTree.getName(), JSweetProblem.NATIVE_MODIFIER_IS_NOT_ALLOWED,
                        methodTree.getName());
            }
        } else {
            if (getScope().declareClassScope && !getScope().constructor && !getScope().interfaceScope
                    && !methodTree.getModifiers().getFlags().contains(Modifier.DEFAULT)) {
                report(methodTree, methodTree.getName(), JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE,
                        methodTree.getName(), parent == null ? "<no class>" : parent.getSimpleName());
            }
        }

        if (methodTree.getName().toString().equals("constructor")) {
            report(methodTree, methodTree.getName(), JSweetProblem.CONSTRUCTOR_MEMBER);
        }
        if (parent != null) {
            VariableElement v = util().findFieldDeclaration(parentElement, methodTree.getName());
            if (v != null && context.getFieldNameMapping(v) == null) {
                if (isDefinitionScope) {
                    return returnNothing();
                } else {
                    if (methodTree.getName().toString().equals(context.getActualName(v))) {
                        report(methodTree, methodTree.getName(), JSweetProblem.METHOD_CONFLICTS_FIELD,
                                methodTree.getName(), parentElement);
                    }
                }
            }
        }
        if (JSweetConfig.MAIN_FUNCTION_NAME.equals(methodTree.getName().toString())
                && methodTree.getModifiers().getFlags().contains(Modifier.STATIC)
                && !context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_DISABLED)) {
            // ignore main methods in inner classes
            if (scope.size() == 1 || (scope.size() == 2 && getScope().enumWrapperClassScope)) {
                getScope().mainMethod = methodTree;
            }
        }

        boolean globals = parent == null ? false
                : JSweetConfig.GLOBALS_CLASS_NAME.equals(parent.getSimpleName().toString());
        globals = globals
                || (getScope().interfaceScope && methodTree.getModifiers().getFlags().contains(Modifier.STATIC));
        if (!(inOverload && !inCoreWrongOverload)) {
            printDocComment(methodTree);
        }

        if (parent == null) {
            printAsyncKeyword(methodTree);

            print("function ");
        } else if (globals) {
            if (getScope().constructor && methodElement.getModifiers().contains(Modifier.PRIVATE)
                    && methodTree.getParameters().isEmpty()) {
                return returnNothing();
            }
            if (getScope().constructor) {
                report(methodTree, methodTree.getName(), JSweetProblem.GLOBAL_CONSTRUCTOR_DEF);
                return returnNothing();
            }
            if (context.lookupDecoratorAnnotation((parentElement.getQualifiedName() + "." + methodTree.getName())
                    .replace(JSweetConfig.GLOBALS_CLASS_NAME + ".", "")) != null) {
                if (!getScope().decoratorScope) {
                    return returnNothing();
                }
            }

            if (!methodTree.getModifiers().getFlags().contains(Modifier.STATIC)) {
                report(methodTree, methodTree.getName(), JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
                return returnNothing();
            }

            if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_MODULE)) {
                getContext().addExportedElement(
                        context.getAnnotationValue(methodElement, JSweetConfig.ANNOTATION_MODULE, String.class, null),
                        methodElement, getCompilationUnit());
            }

            if (context.useModules || context.moduleBundleMode) {
                if (!methodTree.getModifiers().getFlags().contains(Modifier.PRIVATE)) {
                    print("export ");
                }
            } else {
                if (!isTopLevelScope()) {
                    print("export ");
                }
            }
            if (ambient || (getIndent() == 0 && isDefinitionScope)) {
                print("declare ");
            }

            printAsyncKeyword(methodTree);

            print("function ");
        } else {
            printMethodModifiers(methodTree, parent, getScope().constructor, inOverload, overload);

            printAsyncKeyword(methodTree);

            if (ambient) {
                report(methodTree, methodTree.getName(), JSweetProblem.WRONG_USE_OF_AMBIENT, methodTree.getName());
            }
        }
        if (parent == null || !context.isFunctionalType(Util.getTypeElement(parent))) {
            if (isDebugMode(methodTree)) {
                print("*").print(GENERATOR_PREFIX);
            }
            if (inOverload && !overload.isValid && !inCoreWrongOverload) {
                print(getOverloadMethodName(methodElement));
            } else {
                String tsMethodName = getTSMethodName(methodTree);
                getScope().generatedMethodNames.add(tsMethodName);
                if (doesMemberNameRequireQuotes(tsMethodName)) {
                    print("'" + tsMethodName + "'");
                } else {
                    print(tsMethodName);
                }
            }
        }
        if ((methodTree.getTypeParameters() != null && !methodTree.getTypeParameters().isEmpty())
                || (getContext().getWildcards(methodElement) != null)) {
            inTypeParameters = true;
            print("<");
            if (methodTree.getTypeParameters() != null && !methodTree.getTypeParameters().isEmpty()) {
                printArgList(null, methodTree.getTypeParameters());
                if (getContext().getWildcards(methodElement) != null) {
                    print(", ");
                }
            }
            if (getContext().getWildcards(methodElement) != null) {
                printArgList(null, getContext().getWildcards(methodElement), this::substituteAndPrintType);
            }
            print(">");
            inTypeParameters = false;
        }
        print("(");
        printMethodArgs(methodTree, overload, inOverload, inCoreWrongOverload, getScope());
        print(")");
        printMethodReturnDeclaration(methodTree, inCoreWrongOverload);
        if (inCoreWrongOverload && isInterfaceMethod(parent, methodTree)) {
            print(";");
            return returnNothing();
        }
        if (methodTree.getBody() == null && !(inCoreWrongOverload && !getScope().declareClassScope)
                || (methodTree.getModifiers().getFlags().contains(Modifier.DEFAULT)
                        && !getScope().defaultMethodScope)) {
            if (!getScope().interfaceScope && methodTree.getModifiers().getFlags().contains(Modifier.ABSTRACT)
                    && inOverload && !overload.isValid) {
                print(" {");
                // runtime error if we go there...
                print(" throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); ");
                print("}");
            } else {
                print(";");
            }
        } else {
            if (!getScope().declareClassScope && getScope().interfaceScope) {
                if (!methodTree.getModifiers().getFlags().contains(Modifier.STATIC)) {
                    report(methodTree, methodTree.getName(), JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE,
                            methodTree.getName(), parent == null ? "<no class>" : parent.getSimpleName());
                }
            }
            if (getScope().declareClassScope) {
                if (!getScope().constructor
                        || (methodTree.getBody() != null && methodTree.getBody().getStatements().isEmpty())) {
                    report(methodTree, methodTree.getName(), JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE,
                            methodTree.getName(), parent == null ? "<no class>" : parent.getSimpleName());
                }
                print(";");
            } else {
                if (inCoreWrongOverload) {
                    print(" {").println().startIndent().printIndent();

                    printCoreOverloadMethod(methodTree, parent, overload);

                    print(" else throw new Error('invalid overload');");
                    endIndent().println().printIndent().print("}");
                } else {
                    print(" ").print("{").println().startIndent();

                    if (!getAdapter().substituteMethodBody(parentElement, methodElement)) {

                        String replacedBody = null;
                        if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_REPLACE)) {
                            replacedBody = (String) context.getAnnotationValue(methodElement,
                                    JSweetConfig.ANNOTATION_REPLACE, String.class, null);
                        }

                        boolean fieldsInitPrinted = false;
                        int position = getCurrentPosition();
                        if (replacedBody == null || BODY_MARKER.matcher(replacedBody).find()) {
                            enter(methodTree.getBody());

                            List<? extends StatementTree> statements = methodTree.getBody().getStatements();
                            if (!statements.isEmpty() && statements.get(0).toString().startsWith("super(")) {
                                printBlockStatement(statements.get(0));
                                if (parent != null) {
                                    printInstanceInitialization(parent, methodElement);
                                    fieldsInitPrinted = true;
                                }
                                printBlockStatements(statements.subList(1, statements.size()));
                            } else {
                                if (parent != null) {
                                    printInstanceInitialization(parent, methodElement);
                                    fieldsInitPrinted = true;
                                }
                                printBlockStatements(statements);
                            }
                            exit();
                            if (replacedBody != null) {
                                String orgBody = getOutput().substring(position);
                                removeLastChars(getCurrentPosition() - position);
                                replacedBody = BODY_MARKER.matcher(replacedBody).replaceAll(orgBody);
                                replacedBody = BASE_INDENT_MARKER.matcher(replacedBody).replaceAll(getIndentString());
                                replacedBody = INDENT_MARKER.matcher(replacedBody).replaceAll(INDENT);
                                replacedBody = METHOD_NAME_MARKER.matcher(replacedBody)
                                        .replaceAll(methodTree.getName().toString());
                                TypeElement parentTypeElement = Util.getElement(parent);
                                replacedBody = CLASS_NAME_MARKER.matcher(replacedBody)
                                        .replaceAll(parentTypeElement.getQualifiedName().toString());
                            }
                        }

                        if (replacedBody != null && replacedBody.trim().startsWith("super(")) {
                            String superCall = replacedBody.substring(0, replacedBody.indexOf(';') + 1);
                            replacedBody = replacedBody.substring(replacedBody.indexOf(';') + 1);
                            println(superCall);
                        }

                        if (!fieldsInitPrinted && parent != null) {
                            printInstanceInitialization(parent, methodElement);
                            fieldsInitPrinted = true;
                        }

                        if (replacedBody != null) {
                            if (methodElement.getKind() == ElementKind.CONSTRUCTOR) {
                                getScope().hasDeclaredConstructor = true;
                            }
                            printIndent().print(replacedBody).println();
                        }

                    }
                    endIndent().printIndent().print("}");
                }
            }
        }

        return returnNothing();
    }

    private void printCoreOverloadMethod(MethodTree methodDecl, ClassTree parent, Overload overload) {
        if (getAdapter().substituteOverloadMethodBody(Util.getElement(parent), overload)) {
            return;
        }

        boolean wasPrinted = false;
        for (OverloadMethodEntry overloadMethodEntry : overload.getEntries()) {
            MethodTree method = overloadMethodEntry.methodTree;

            ExecutableElement methodElement = overloadMethodEntry.methodElement;
            if (context.isInterface((TypeElement) methodElement.getEnclosingElement())
                    && !method.getModifiers().getFlags().contains(Modifier.DEFAULT)
                    && !method.getModifiers().getFlags().contains(Modifier.STATIC)) {
                continue;
            }
            TypeElement parentElement = Util.getElement(parent);
            if (!util().isParent(parentElement, (TypeElement) methodElement.getEnclosingElement())) {
                continue;
            }
            if (wasPrinted) {
                print(" else ");
            }
            wasPrinted = true;
            print("if (");
            printMethodParamsTest(overload, overloadMethodEntry);
            print(") ");
            if (methodElement.getKind() == ElementKind.CONSTRUCTOR
                    || (methodElement.getModifiers().contains(Modifier.DEFAULT)
                            && method.equals(overload.getCoreMethod()))) {
                printInlinedMethod(overload, method, methodDecl.getParameters());
            } else {
                if (parentElement != methodElement.getEnclosingElement()
                        && context.getOverload((TypeElement) methodElement.getEnclosingElement(), methodElement)
                                .getCoreMethod() == method) {
                    print("{").println().startIndent().printIndent();

                    if ((method.getBody() == null || (method.getModifiers().getFlags().contains(Modifier.DEFAULT)
                            && !getScope().defaultMethodScope)) && !getScope().interfaceScope
                            && method.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
                        print(" throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); ");
                    } else {
                        String tsMethodAccess = getTSMemberAccess(getTSMethodName(methodDecl), true);
                        if (!"void".equals(Util.getType(method.getReturnType()).toString())) {
                            print("return ");
                        }

                        print("super" + tsMethodAccess);
                        print("(");
                        for (int j = 0; j < method.getParameters().size(); j++) {
                            print(avoidJSKeyword(overload.getCoreMethod().getParameters().get(j).getName().toString()))
                                    .print(", ");
                        }
                        if (!method.getParameters().isEmpty()) {
                            removeLastChars(2);
                        }
                        print(");");
                    }
                    println().endIndent().printIndent().print("}");
                } else {
                    print("{").println().startIndent().printIndent();
                    // temporary cast to any because of Java generics bug
                    print("return <any>");
                    if (isAsyncMethod(method)) {
                        print("await ");
                    }
                    if (!util().isGlobalsClassName(parent.getSimpleName().toString())) {
                        if (methodElement.getModifiers().contains(Modifier.STATIC)) {
                            print(getQualifiedTypeName(parentElement, false, false).toString());
                            if (util().isPartOfAnEnum(parentElement)) {
                                print(ENUM_WRAPPER_CLASS_SUFFIX);
                            }
                        } else {
                            print("this");
                        }
                        print(".");
                    }
                    print(getOverloadMethodName(methodElement)).print("(");
                    for (int j = 0; j < method.getParameters().size(); j++) {
                        if (j == method.getParameters().size() - 1 && util().hasVarargs(methodElement)) {
                            print("...");
                        } else if (j == method.getParameters().size() - 1
                                && util().hasVarargs(overload.getCoreMethodElement())) {
                            print("<any>");
                        }
                        print(avoidJSKeyword(overload.getCoreMethod().getParameters().get(j).getName().toString()))
                                .print(", ");
                    }
                    if (!method.getParameters().isEmpty()) {
                        removeLastChars(2);
                    }
                    print(");");
                    println().endIndent().printIndent().print("}");
                }
            }
        }
    }

    protected void printMethodReturnDeclaration(MethodTree methodTree, boolean inCoreWrongOverload) {
        ExecutableElement methodElement = Util.getElement(methodTree);

        if (methodTree.getReturnType() != null) {
            Element returnTypeElement = Util.getTypeElement(methodTree.getReturnType());
            TypeMirror returnType = Util.getType(methodTree.getReturnType());
            if (returnType.getKind() != TypeKind.VOID) {

                print(": ");

                boolean promisify = isAsyncMethod(methodTree) && (returnTypeElement == null
                        || !util().getQualifiedName(returnTypeElement).endsWith(".Promise"));
                if (promisify) {
                    print("Promise<");
                }

                if (inCoreWrongOverload && methodElement.getKind() != ElementKind.CONSTRUCTOR) {
                    print("any");
                } else {
                    substituteAndPrintType(methodTree.getReturnType());
                }

                if (promisify) {
                    print(">");
                }
            }
        }
    }

    /**
     * Print async keyword for given method if relevant. Prints nothing if method
     * shouldn't be async
     * 
     */
    protected void printAsyncKeyword(MethodTree methodDecl) {
        if (getScope().declareClassScope || getScope().interfaceScope) {
            return;
        }

        if (isAsyncMethod(methodDecl)) {
            print("async ");
        }
    }

    protected boolean isAsyncMethod(MethodTree methodTree) {
        return context.hasAnnotationType(Util.getElement(methodTree), JSweetConfig.ANNOTATION_ASYNC);
    }

    protected void printMethodArgs(MethodTree methodTree, Overload overload, boolean inOverload,
            boolean inCoreWrongOverload, ClassScope scope) {

        ExecutableElement methodElement = Util.getElement(methodTree);
        boolean isWrapped = false;
        if (this.context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_WRAP_PARAMETERS)) {
            isWrapped = true;
        }
        if (isWrapped) {
            print("{");
        }

        if (inCoreWrongOverload) {
            scope.setEraseVariableTypes(true);
        }
        boolean paramPrinted = false;
        if (scope.isInnerClassNotStatic() && methodElement.getKind() == ElementKind.CONSTRUCTOR
                && !scope.isEnumWrapperClassScope()) {
            print(PARENT_CLASS_FIELD_NAME + ": any, ");
            paramPrinted = true;
        }
        if (scope.isConstructor() && scope.isEnumWrapperClassScope()) {
            print((isAnonymousClass() ? "" : "protected ") + ENUM_WRAPPER_CLASS_ORDINAL + ": number, ");
            print((isAnonymousClass() ? "" : "protected ") + ENUM_WRAPPER_CLASS_NAME + ": string, ");
            paramPrinted = true;
        }
        int i = 0;
        for (VariableTree param : methodTree.getParameters()) {
            if (isWrapped) {
                print(param.getName().toString());
            } else {
                print(param);
            }
            if (inOverload && overload.isValid && overload.defaultValues.get(i) != null) {
                print(" = ").print(overload.defaultValues.get(i));
            }
            print(", ");
            i++;
            paramPrinted = true;
        }
        if (inCoreWrongOverload) {
            scope.setEraseVariableTypes(false);
        }
        if (paramPrinted) {
            removeLastChars(2);
        }

        if (isWrapped) {
            print("}: {");
            for (VariableTree param : methodTree.getParameters()) {
                print(param).println(";");
            }
            print("}");
        }
    }

    protected void printMethodModifiers(MethodTree methodDecl, ClassTree parent, boolean constructor,
            boolean inOverload, Overload overload) {
        if (methodDecl.getModifiers().getFlags().contains(Modifier.PUBLIC)
                || (inOverload && overload.getCoreMethod().equals(methodDecl))) {
            if (!getScope().interfaceScope) {
                print("public ");
            }
        }
        if (methodDecl.getModifiers().getFlags().contains(Modifier.PRIVATE)) {
            if (!constructor) {
                if (!getScope().innerClass) {
                    if (!getScope().interfaceScope) {
                        if (!(inOverload && overload.getCoreMethod().equals(methodDecl) || getScope().hasInnerClass)) {
                            print("/*private*/ ");
                        }
                    } else {
                        if (!(inOverload && overload.getCoreMethod().equals(methodDecl))) {
                            report(methodDecl, methodDecl.getName(), JSweetProblem.INVALID_PRIVATE_IN_INTERFACE,
                                    methodDecl.getName(), parent == null ? "<no class>" : parent.getSimpleName());
                        }
                    }
                }
            }
        }
        if (methodDecl.getModifiers().getFlags().contains(Modifier.STATIC)) {
            if (!getScope().interfaceScope) {
                print("static ");
            }
        }
        if (methodDecl.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
            if (!getScope().interfaceScope && !inOverload) {
                print("abstract ");
            }
        }
    }

    private boolean shouldPrintFieldInitializationInConstructor(VariableTree var) {
        VariableElement varElement = Util.getElement(var);
        if (varElement.getModifiers().contains(Modifier.STATIC)) {
            return false;
        }

        return var.getInitializer() == null || (!getScope().hasConstructorOverloadWithSuperClass
                && getScope().getFieldsWithInitializers().contains(var));
    }

    protected void printVariableInitialization(ClassTree clazz, VariableTree var) {
        VariableElement varElement = Util.getElement(var);

        String name = var.getName().toString();
        if (context.getFieldNameMapping(varElement) != null) {
            name = context.getFieldNameMapping(varElement);
        } else {
            name = getIdentifier(varElement);
        }
        if (getScope().innerClassNotStatic && !util().isConstantOrNullField(var)
                // constructor fields init is handled in another method
                // (printFieldInitializations)
                || var.getInitializer() != null && shouldPrintFieldInitializationInConstructor(var)) {

            if (doesMemberNameRequireQuotes(name)) {
                printIndent().print("this['").print(name).print("'] = ");
            } else {
                printIndent().print("this.").print(name).print(" = ");
            }
            substituteAndPrintAssignedExpression(varElement.asType(), var.getInitializer());
            print(";").println();
        } else if (var.getInitializer() == null) {
            if (doesMemberNameRequireQuotes(name)) {
                printIndent();
                print("if (").print("this['").print(name).print("']").print(" === undefined) { ");
                if (context.options.isNonEnumerableTransients()
                        && varElement.getModifiers().contains(Modifier.TRANSIENT)) {
                    print("Object.defineProperty(this, " + getStringLiteralQuote() + name + getStringLiteralQuote()
                            + ", { value: ").print(getAdapter().getVariableInitialValue(varElement))
                                    .print(", enumerable: false }); } ").println();
                } else {
                    print("this['").print(name).print("'] = ").print(getAdapter().getVariableInitialValue(varElement))
                            .print("; } ").println();
                }

            } else {
                printIndent().print("if (").print("this.").print(name).print(" === undefined) { ");
                if (context.options.isNonEnumerableTransients()
                        && varElement.getModifiers().contains(Modifier.TRANSIENT)) {
                    print("Object.defineProperty(this, " + getStringLiteralQuote() + name + getStringLiteralQuote()
                            + ", { value: ").print(getAdapter().getVariableInitialValue(varElement))
                                    .print(", enumerable: false }); } ").println();
                } else {
                    print("this.").print(name).print(" = ").print(getAdapter().getVariableInitialValue(varElement))
                            .print("; }").println();
                }
            }
        }
    }

    protected void printInstanceInitialization(ClassTree clazz, ExecutableElement method) {
        if (method == null || method.getKind() == ElementKind.CONSTRUCTOR) {
            getScope().hasDeclaredConstructor = true;

            TypeElement typeElement = Util.getElement(clazz);
            // this workaround will not work on all browsers (see
            // https://github.com/Microsoft/TypeScript-wiki/blob/master/Breaking-Changes.md#extending-built-ins-like-error-array-and-map-may-no-longer-work)
            if (types().isAssignable(typeElement.asType(), util().getType(Throwable.class))) {
                printIndent().print("(<any>Object).setPrototypeOf(this, " + getClassName(typeElement) + ".prototype);")
                        .println();
            }
            if (getScope().innerClassNotStatic && !getScope().enumWrapperClassScope) {
                printIndent().print("this." + PARENT_CLASS_FIELD_NAME + " = " + PARENT_CLASS_FIELD_NAME + ";")
                        .println();
            }
            for (Tree member : clazz.getMembers()) {
                if (member instanceof VariableTree) {
                    VariableTree var = (VariableTree) member;
                    VariableElement varElement = Util.getElement(var);
                    if (!varElement.getModifiers().contains(Modifier.STATIC)
                            && !context.hasAnnotationType(varElement, JSweetConfig.ANNOTATION_ERASED)) {
                        printVariableInitialization(clazz, var);
                    }
                } else if (member instanceof BlockTree) {
                    BlockTree block = (BlockTree) member;
                    if (!block.isStatic()) {
                        printIndent().print("(() => {").startIndent().println();
                        stack.push(block);
                        printBlockStatements(block.getStatements());
                        stack.pop();
                        endIndent().printIndent().print("})();").println();
                    }
                }
            }
        }
    }

    private void printInlinedMethod(Overload overload, MethodTree method, List<? extends Tree> args) {
        print("{").println().startIndent();
        if (getScope().innerClassNotStatic && getScope().constructor) {
            // the __parent added parameter is not part of the actual arguments
            printIndent().print(VAR_DECL_KEYWORD + " __args = Array.prototype.slice.call(arguments, [1]);").println();
        } else {
            printIndent().print(VAR_DECL_KEYWORD + " __args = arguments;").println();
        }
        for (int j = 0; j < method.getParameters().size(); j++) {
            if (args.get(j) instanceof VariableTree) {
                if (method.getParameters().get(j).getName().equals(((VariableTree) args.get(j)).getName())) {
                    continue;
                } else {
                    printIndent().print(VAR_DECL_KEYWORD + " ")
                            .print(avoidJSKeyword(method.getParameters().get(j).getName().toString())).print(": ")
                            .print("any")
                            .print(util().isVarargs(method.getParameters().get(j)) ? "[]" : "")
                            .print(" = ").print("__args[" + j + "]").print(";").println();
                }
            } else {
                if (method.getParameters().get(j).getName().toString().equals(args.get(j).toString())) {
                    continue;
                } else {
                    getScope().inlinedConstructorArgs = method.getParameters().stream()
                            .map((VariableTree p) -> Util.getElement(p).getSimpleName().toString())
                            .collect(Collectors.toList());
                    printIndent().print(VAR_DECL_KEYWORD + " ")
                            .print(avoidJSKeyword(method.getParameters().get(j).getName().toString())).print(": ")
                            .print("any")
                            .print(util().isVarargs(method.getParameters().get(j)) ? "[]" : "")
                            .print(" = ").print(args.get(j)).print(";").println();
                    getScope().inlinedConstructorArgs = null;
                }
            }
        }
        ExecutableElement methodElement = Util.getElement(method);
        if (method.getBody() != null) {
            boolean skipFirst = false;
            List<? extends StatementTree> statements = method.getBody().getStatements();
            if (!statements.isEmpty() && statements.get(0).toString().startsWith("this(")) {
                skipFirst = true;
                MethodInvocationTree inv = (MethodInvocationTree) ((ExpressionStatementTree) statements.get(0))
                        .getExpression();
                ExecutableElement ms = util().findMethodDeclarationInType(
                        (TypeElement) overload.getCoreMethodElement().getEnclosingElement(), inv);
                for (OverloadMethodEntry overloadMethodEntry : overload.getEntries()) {
                    if (overloadMethodEntry.methodElement.equals(ms)) {
                        printIndent();
                        printInlinedMethod(overload, overloadMethodEntry.methodTree, inv.getArguments());
                        println();
                    }
                }

            }
            boolean isConstructor = methodElement.getKind() == ElementKind.CONSTRUCTOR;
            String replacedBody = null;
            if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_REPLACE)) {
                replacedBody = context.getAnnotationValue(methodElement, JSweetConfig.ANNOTATION_REPLACE, String.class,
                        null);
            }
            int position = getCurrentPosition();
            if (replacedBody == null || BODY_MARKER.matcher(replacedBody).find()) {
                enter(method.getBody());
                List<? extends StatementTree> actualStatements = skipFirst ? statements.subList(1, statements.size())
                        : statements;

                if (isConstructor) {
                    getScope().hasDeclaredConstructor = true;
                }

                if (!actualStatements.isEmpty() && actualStatements.get(0).toString().startsWith("super(")) {
                    printBlockStatement(actualStatements.get(0));
                    printFieldInitializations(actualStatements);
                    List<? extends StatementTree> nextStatements = actualStatements.subList(1, actualStatements.size());
                    if (!nextStatements.isEmpty()) {
                        printBlockStatements(nextStatements);
                    }
                } else {
                    printFieldInitializations(actualStatements);
                    if (!actualStatements.isEmpty() || !isConstructor) {
                        printIndent();
                    }
                    if (!isConstructor) {
                        print("return <any>");
                    }
                    if (!actualStatements.isEmpty() || !isConstructor) {
                        print("((").print(") => {").startIndent().println();
                        printBlockStatements(actualStatements);
                        endIndent().printIndent().print("})(").print(");").println();
                    }
                }
                exit();
                if (replacedBody != null) {
                    getIndent();
                    printIndent();
                    String orgBody = getOutput().substring(position);
                    removeLastChars(getCurrentPosition() - position);
                    replacedBody = BODY_MARKER.matcher(replacedBody).replaceAll(orgBody);
                    replacedBody = BASE_INDENT_MARKER.matcher(replacedBody).replaceAll(getIndentString());
                    replacedBody = INDENT_MARKER.matcher(replacedBody).replaceAll(INDENT);
                    replacedBody = METHOD_NAME_MARKER.matcher(replacedBody).replaceAll(method.getName().toString());
                    replacedBody = CLASS_NAME_MARKER.matcher(replacedBody)
                            .replaceAll(util().getQualifiedName(methodElement.getEnclosingElement()));
                }
            }
            if (replacedBody != null) {
                if (isConstructor) {
                    getScope().hasDeclaredConstructor = true;
                }
                printIndent().print(replacedBody).println();
            }
        } else {
            String returnValue = util().getTypeInitialValue(methodElement.getReturnType());
            if (returnValue != null) {
                print(" return ").print(returnValue).print("; ");
            }
        }
        endIndent().printIndent().print("}");
    }

    private void printFieldInitializations(List<? extends StatementTree> potentialInitializationStatements) {
        ClassTree clazz = getParent(ClassTree.class);
        for (Tree t : clazz.getMembers()) {
            if (t instanceof VariableTree && !getScope().fieldsWithInitializers.contains(t)) {
                VariableTree field = (VariableTree) t;
                VariableElement fieldElement = Util.getElement(field);
                if (!fieldElement.getModifiers().contains(Modifier.STATIC)
                        && !context.hasAnnotationType(fieldElement, JSweetConfig.ANNOTATION_ERASED)) {
                    String name = getIdentifier(fieldElement);
                    if (context.getFieldNameMapping(fieldElement) != null) {
                        name = context.getFieldNameMapping(fieldElement);
                    }

                    printIndent().print("if (").print("this.").print(name).print(" === undefined) { ");
                    if (context.options.isNonEnumerableTransients()
                            && fieldElement.getModifiers().contains(Modifier.TRANSIENT)) {
                        print("Object.defineProperty(this, " + getStringLiteralQuote() + name + getStringLiteralQuote()
                                + ", { value: ").print(getAdapter().getVariableInitialValue(fieldElement))
                                        .print(", enumerable: false }); } ");
                    } else {
                        print("this.").print(name).print(" = ")
                                .print(getAdapter().getVariableInitialValue(fieldElement)).print("; } ").println();
                    }
                }
            }
        }
        for (VariableTree field : getScope().fieldsWithInitializers) {
            VariableElement fieldElement = Util.getElement(field);
            if (context.hasAnnotationType(fieldElement, JSweetConfig.ANNOTATION_ERASED)) {
                continue;
            }
            String name = getIdentifier(fieldElement);
            if (context.getFieldNameMapping(fieldElement) != null) {
                name = context.getFieldNameMapping(fieldElement);
            }
            printIndent().print("this.").print(name).print(" = ");
            if (!substituteAssignedExpression(fieldElement.asType(), field.getInitializer())) {
                print(field.getInitializer());
            }
            print(";").println();
        }
    }

    protected void printBlockStatements(List<? extends StatementTree> statements) {
        for (StatementTree statement : statements) {
            if (context.options.isDebugMode()) {
                MethodTree methodDecl = getParent(MethodTree.class);
                if (isDebugMode(methodDecl)) {
                    SourcePosition statementPosition = util().getSourcePosition(statement, getCompilationUnit());
                    printIndent();
                    print("yield { row: ");
                    print("" + statementPosition.getStartLine());
                    print(", column: " + statementPosition.getStartColumn());
                    print(", statement: \"");
                    print(StringEscapeUtils.escapeJson(statement.toString())).print("\"");

                    final Stack<List<String>> locals = new Stack<>();
                    try {
                        new TreeScanner<Void, Trees>() {
                            @Override
                            public Void scan(Tree tree, Trees trees) {
                                if (tree == statement) {
                                    throw new RuntimeException();
                                }
                                boolean contextChange = false;
                                if (tree instanceof BlockTree || tree instanceof EnhancedForLoopTree
                                        || tree instanceof LambdaExpressionTree || tree instanceof ForLoopTree
                                        || tree instanceof DoWhileLoopTree) {
                                    locals.push(new ArrayList<>());
                                    contextChange = true;
                                }
                                if (tree instanceof VariableTree) {
                                    locals.peek().add(((VariableTree) tree).getName().toString());
                                }
                                super.scan(tree, trees);
                                if (contextChange) {
                                    locals.pop();
                                }

                                return returnNothing();
                            }

                        }.scan(methodDecl.getBody(), trees());
                    } catch (Exception end) {
                        // swallow
                    }
                    List<String> accessibleLocals = new ArrayList<>();
                    for (List<String> l : locals) {
                        accessibleLocals.addAll(l);
                    }
                    if (!accessibleLocals.isEmpty()) {
                        print(", locals: ");
                        print("{");
                        for (String local : accessibleLocals) {
                            print("" + local + ": " + local + ", ");
                        }
                        removeLastChars(2);
                        print("}");
                    }
                    print(" };").println();
                }
            }
            printBlockStatement(statement);
        }
    }

    private void printBlockStatement(StatementTree statement) {
        printIndent();
        int pos = getCurrentPosition();
        print(statement);
        if (getCurrentPosition() == pos) {
            removeLastIndent();
            return;
        }
        if (!isStatementWithNoSemiColon(statement)) {
            if (statement instanceof LabeledStatementTree) {
                if (!isStatementWithNoSemiColon(((LabeledStatementTree) statement).getStatement())) {
                    print(";");
                }
            } else {
                print(";");
            }
        }
        println();
    }

    private String getOverloadMethodName(ExecutableElement method) {
        if (method.getKind() == ElementKind.CONSTRUCTOR) {
            return "constructor";
        }
        StringBuilder nameBuilder = new StringBuilder(method.getSimpleName().toString());
        nameBuilder.append("$");
        for (VariableElement paramElement : method.getParameters()) {
            nameBuilder.append(types().erasure(paramElement.asType()).toString().replace('.', '_').replace("[]", "_A"));
            nameBuilder.append("$");
        }
        if (!method.getParameters().isEmpty()) {
            nameBuilder.deleteCharAt(nameBuilder.length() - 1);
        }
        return nameBuilder.toString();
    }

    private void checkType(Element type) {
        if (type instanceof TypeElement && !isMappedOrErasedType(type)) {
            String name = type.getSimpleName().toString();
            ModuleImportDescriptor moduleImport = getModuleImportDescriptor(name, (TypeElement) type);
            if (moduleImport != null) {
                useModule(false, moduleImport.isDirect(), moduleImport.getTargetPackage(), null,
                        moduleImport.getImportedName(), moduleImport.getPathToImportedClass(), null);
            }
        }
    }

    private void printMethodParamsTest(Overload overload, OverloadMethodEntry methodEntry) {

        MethodTree method = methodEntry.methodTree;

        int i = 0;
        for (; i < method.getParameters().size(); i++) {
            print("(");

            VariableTree parameter = method.getParameters().get(i);
            TypeMirror paramType = Util.getType(parameter);
            Element paramTypeElement = Util.getTypeElement(parameter);

            if (Class.class.getName().equals(context.types.erasure(paramType).toString())) {
                List<? extends TypeMirror> typeArguments = ((DeclaredType) paramType).getTypeArguments();
                if (typeArguments.size() > 0) {

                    Element firstArgTypeElement = types().asElement(typeArguments.get(0));
                    if (firstArgTypeElement != null && firstArgTypeElement.getKind() == ElementKind.INTERFACE) {
                        print(avoidJSKeyword(overload.getCoreMethod().getParameters().get(i).getName().toString()))
                                .print(" === ");
                        print(getStringLiteralQuote()).print(typeArguments.get(0).toString())
                                .print(getStringLiteralQuote());
                        print(" || ");
                    }
                }
            }

            printInstanceOf(avoidJSKeyword(overload.getCoreMethod().getParameters().get(i).getName().toString()), null,
                    paramType);
            checkType(paramTypeElement);
            print(" || ").print(
                    avoidJSKeyword(overload.getCoreMethod().getParameters().get(i).getName().toString()) + " === null")
                    .print(")");
            print(" && ");
        }
        for (; i < overload.getCoreMethod().getParameters().size(); i++) {
            print(avoidJSKeyword(overload.getCoreMethod().getParameters().get(i).getName().toString()))
                    .print(" === undefined");

            if (i == overload.getCoreMethod().getParameters().size() - 1
                    && overload.getCoreMethodElement().isVarArgs()) {
                print(" || ");
                print(avoidJSKeyword(overload.getCoreMethod().getParameters().get(i).getName().toString()))
                        .print(".length === 0");
            }
            print(" && ");
        }
        removeLastChars(4);
    }

    @Override
    public Void visitBlock(BlockTree block, Trees trees) {
        Tree parent = getParent();
        boolean globals = (parent instanceof ClassTree)
                && JSweetConfig.GLOBALS_CLASS_NAME.equals(((ClassTree) parent).getSimpleName().toString());
        boolean initializer = (parent instanceof ClassTree) && !globals;
        if (initializer) {
            if (getScope().interfaceScope) {
                report(block, JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, ((ClassTree) parent).getSimpleName());
            }
            if (!block.isStatic()) {
                // non-static blocks are initialized in the constructor
                return returnNothing();
            }

            printStaticInitializer(block);
            return returnNothing();
        }
        if (!globals) {
            print("{").println().startIndent();
        }
        printBlockStatements(block.getStatements());
        if (!globals) {
            endIndent().printIndent().print("}");
        }

        return returnNothing();
    }

    private void printStaticInitializer(BlockTree block) {
        if (getScope().isEnumScope()) {
            // static blocks are initialized in the enum wrapper class
            return;
        }

        if (getScope().isDeclareClassScope()) {
            // static init block are erased in declare class
            return;
        }

        int static_i = 0;
        for (Tree m : ((ClassTree) getParent()).getMembers()) {
            if (m instanceof BlockTree) {
                if (((BlockTree) m).isStatic()) {
                    if (block == m) {
                        String asyncKeyword = isAsyncStaticInitializer(block) ? "async" : "";
                        print("static " + asyncKeyword + " __static_initializer_" + static_i + "() ");

                        print("{");
                        println().startIndent();

                        printBlockStatements(block.getStatements());

                        endIndent().printIndent();
                        print("}");
                        break;
                    }
                    static_i++;
                }
            }
        }
    }

    protected boolean isAsyncStaticInitializer(BlockTree initializerBlock) {
        AsyncCallsFinder finder = new AsyncCallsFinder();
        initializerBlock.accept(finder, trees());
        return finder.found;
    }

    private class AsyncCallsFinder extends TreeScanner<Void, Trees> {

        protected boolean found = false;

        @Override
        public Void scan(Tree tree, Trees p) {
            if (found) {
                return returnNothing();
            }
            return super.scan(tree, p);
        }

        @Override
        public Void scan(Iterable<? extends Tree> nodes, Trees p) {
            if (found) {
                return returnNothing();
            }
            return super.scan(nodes, p);
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree methodInvocation, Trees trees) {
            Element methodSymbol = Util.getElement(methodInvocation.getMethodSelect());
            if (methodSymbol instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) methodSymbol;

                if (isAwait(method)) {
                    found = true;
                    return returnNothing();
                }
            }
            return super.visitMethodInvocation(methodInvocation, trees);
        }

        private boolean isAwait(ExecutableElement method) {
            return method.getEnclosingElement() instanceof TypeElement
                    && ((TypeElement) method.getEnclosingElement()).getQualifiedName().toString().equals(UTIL_CLASSNAME)
                    && method.getSimpleName().toString().equals("await");
        }
    }

    private String avoidJSKeyword(String name) {
        if (JSweetConfig.JS_KEYWORDS.contains(name)) {
            name = JSweetConfig.JS_KEYWORD_PREFIX + name;
        }
        return name;
    }

    private boolean isLazyInitialized(VariableElement var) {
        return context.options.isLazyInitializedStatics() && var.getModifiers().contains(Modifier.STATIC)
                && context.lazyInitializedStatics.contains(var)
                && /* enum fields are not lazy initialized */ !(util().isPartOfAnEnum(var)
                        && var.getEnclosingElement().equals(Util.getElement(var.asType())));
    }

    @Override
    public Void visitVariable(VariableTree varTree, Trees trees) {
        VariableElement varElement = Util.getElement(varTree);

        if (getAdapter().substituteVariable(varElement)) {
            return returnNothing();
        }

        if (context.hasAnnotationType(varElement, JSweetConfig.ANNOTATION_ERASED)) {
            // erased elements are ignored
            return returnNothing();
        }
        if (context.hasAnnotationType(varElement, JSweetConfig.ANNOTATION_STRING_TYPE)) {
            // string type fields are ignored
            return returnNothing();
        }

        if (getScope().enumScope) {
            // we print the doc comment for information, but
            // TypeScript-generated cannot be recognized by JSDoc... so this
            // comment will be ignored and shall be inserted in the enum
            // document with a @property annotation
            printDocComment(varTree, true);
            print(varTree.getName().toString());
            if (varTree.getInitializer() instanceof NewClassTree) {
                NewClassTree newClass = (NewClassTree) varTree.getInitializer();
                if (newClass.getClassBody() != null) {
                    initAnonymousClass(newClass);
                }
            }
        } else {
            Tree parent = getParent();

            if (getScope().enumWrapperClassScope && varElement.getKind() == ElementKind.ENUM_CONSTANT) {
                return returnNothing();
            }

            String name = getIdentifier(varElement);
            if (context.getFieldNameMapping(varElement) != null) {
                name = context.getFieldNameMapping(varElement);
            }

            boolean confictInDefinitionScope = false;

            if (parent instanceof ClassTree) {
                ExecutableElement parentMethod = util().findMethodDeclarationInType(Util.getElement((ClassTree) parent), name,
                        null);
                if (parentMethod != null) {
                    if (!isDefinitionScope) {
                        report(varTree, varTree.getName(), JSweetProblem.FIELD_CONFLICTS_METHOD, name,
                                parentMethod.getEnclosingElement());
                    } else {
                        confictInDefinitionScope = true;
                    }
                }
                if (!getScope().interfaceScope && name.equals("constructor")) {
                    report(varTree, varTree.getName(), JSweetProblem.CONSTRUCTOR_MEMBER);
                }
            } else {
                if (!context.useModules) {
                    if (context.importedTopPackages.contains(name)) {
                        name = "__var_" + name;
                    }
                }
                if (JSweetConfig.JS_KEYWORDS.contains(name)) {
                    report(varTree, varTree.getName(), JSweetProblem.JS_KEYWORD_CONFLICT, name, name);
                    name = JSweetConfig.JS_KEYWORD_PREFIX + name;
                }
            }

            boolean globals = (parent instanceof ClassTree)
                    && JSweetConfig.GLOBALS_CLASS_NAME.equals(((ClassTree) parent).getSimpleName().toString());

            if (globals && !varTree.getModifiers().getFlags().contains(Modifier.STATIC)) {
                report(varTree, varTree.getName(), JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
                return returnNothing();
            }

            globals = globals || (parent instanceof ClassTree && ((ClassTree) parent).getKind() == Kind.INTERFACE)
                    || (getScope().interfaceScope && varElement.getModifiers().contains(Modifier.STATIC));

            if (parent instanceof ClassTree) {
                printDocComment(varTree);
            }

            print(varTree.getModifiers());

            if (!globals && parent instanceof ClassTree) {
                if (varTree.getModifiers().getFlags().contains(Modifier.PUBLIC)) {
                    if (!getScope().interfaceScope) {
                        print("public ");
                    }
                }
                if (varTree.getModifiers().getFlags().contains(Modifier.PRIVATE)) {
                    if (!getScope().interfaceScope) {
                        if (!getScope().innerClass && !varTree.getModifiers().getFlags().contains(Modifier.STATIC)) {
                            // cannot keep private fields because they may be
                            // accessed in an inner class
                            print("/*private*/ ");
                        }
                    } else {
                        report(varTree, varTree.getName(), JSweetProblem.INVALID_PRIVATE_IN_INTERFACE,
                                varTree.getName(), ((ClassTree) parent).getSimpleName());
                    }
                }

                if (varTree.getModifiers().getFlags().contains(Modifier.STATIC)) {
                    if (!getScope().interfaceScope) {
                        print("static ");
                    }
                }
            }
            if (!getScope().interfaceScope && parent instanceof ClassTree) {
                if (context.hasAnnotationType(varElement, JSweetConfig.ANNOTATION_OPTIONAL)) {
                    report(varTree, varTree.getName(), JSweetProblem.USELESS_OPTIONAL_ANNOTATION, varTree.getName(),
                            ((ClassTree) parent).getSimpleName());
                }
            }
            boolean ambient = context.hasAnnotationType(varElement, JSweetConfig.ANNOTATION_AMBIENT);
            if (globals || !(parent instanceof ClassTree || parent instanceof MethodTree
                    || parent instanceof LambdaExpressionTree)) {
                if (globals) {
                    if (context.hasAnnotationType(varElement, JSweetConfig.ANNOTATION_MODULE)) {
                        getContext().addExportedElement(context.getAnnotationValue(varElement,
                                JSweetConfig.ANNOTATION_MODULE, String.class, null), varElement, getCompilationUnit());
                    }
                    if (context.useModules || context.moduleBundleMode) {
                        if (!varTree.getModifiers().getFlags().contains(Modifier.PRIVATE)) {
                            print("export ");
                        }
                    } else {
                        if (!isTopLevelScope()) {
                            print("export ");
                        }
                    }
                    if (ambient || (isTopLevelScope() && isDefinitionScope)) {
                        print("declare ");
                    }
                }
                if (!(inArgListTail && (parent instanceof ForLoopTree))) {
                    if (!getAdapter().substituteVariableDeclarationKeyword(varElement)) {
                        if (isDefinitionScope) {
                            print("var ");
                        } else {
                            if (!isLazyInitialized(varElement) && ((!globals && context.constAnalyzer != null
                                    && !context.constAnalyzer.getModifiedVariables().contains(varElement))
                                    || (globals && varElement.getModifiers().contains(Modifier.FINAL)
                                            && varTree.getInitializer() != null))) {
                                print("const ");
                            } else {
                                print(VAR_DECL_KEYWORD + " ");
                            }
                        }
                    }
                }
            } else {
                if (ambient) {
                    report(varTree, varTree.getName(), JSweetProblem.WRONG_USE_OF_AMBIENT, varTree.getName());
                }
            }

            if (util().isVarargs(varTree)) {
                print("...");
            }

            if (doesMemberNameRequireQuotes(name)) {
                print("'" + name + "'");
            } else {
                print(name);
            }

            if (!util().isVarargs(varTree)
                    && (getScope().isEraseVariableTypes() || (getScope().interfaceScope
                            && context.hasAnnotationType(varElement, JSweetConfig.ANNOTATION_OPTIONAL)))) {
                print("?");
            }
            if (!getScope().skipTypeAnnotations && !getScope().enumWrapperClassScope) {
                if (typeChecker.checkType(varTree, varTree.getName(), varTree.getType(),
                        compilationUnit)) {
                    print(": ");
                    if (confictInDefinitionScope) {
                        print("any");
                    } else {
                        if (getScope().isEraseVariableTypes()) {
                            print("any");
                            if (util().isVarargs(varTree)) {
                                print("[]");
                            }
                        } else {
                            Element varTypeElement = Util.getTypeElement(varTree.getType());
                            if (varTypeElement != null
                                    && context.hasAnnotationType(varTypeElement, ANNOTATION_STRING_TYPE)
                                    && !util().isPartOfAnEnum(varTypeElement)) {
                                print(getStringLiteralQuote());
                                print(context.getAnnotationValue(varTypeElement, ANNOTATION_STRING_TYPE, String.class,
                                        varTypeElement.getSimpleName().toString()).toString());
                                print(getStringLiteralQuote());
                            } else {
                                substituteAndPrintType(varTree.getType());
                            }
                        }
                    }
                }
            }
            if (isLazyInitialized(varElement)) {
                ClassTree clazz = (ClassTree) parent;
                TypeElement classTypeElement = Util.getElement(clazz);
                String prefix = getClassName(classTypeElement);
                if (GLOBALS_CLASS_NAME.equals(prefix)) {
                    prefix = "";
                } else {
                    prefix += ".";
                }
                print("; ");
                if (globals) {
                    if (!isTopLevelScope()) {
                        print("export ");
                    }
                    print("function ");
                } else {
                    print("public static ");
                }
                print(name).print(STATIC_INITIALIZATION_SUFFIX + "(): ");
                substituteAndPrintType(varTree.getType());
                print(" { ");
                int liCount = context.getStaticInitializerCount(classTypeElement);
                if (liCount > 0) {
                    if (!globals) {
                        print(prefix + "__static_initialize(); ");
                    }
                }
                if (varTree.getInitializer() != null && !isDefinitionScope) {
                    print("if (" + prefix).print(name).print(" == null) { ").print(prefix).print(name).print(" = ");
                    /*
                     * if (getScope().enumWrapperClassScope) { NewClassTree newClass =
                     * (NewClassTree) varDecl.init; print("new "
                     * ).print(clazz.getSimpleName().toString()).print("(") .printArgList(null,
                     * newClass.args).print(")"); } else {
                     */
                    if (!substituteAssignedExpression(Util.getType(varTree), varTree.getInitializer())) {
                        print(varTree.getInitializer());
                    }
                    // }
                    print("; } ");
                }
                print(" return ").print(prefix).print(name).print("; }");
                if (!globals && context.bundleMode) {
                    String qualifiedClassName = getQualifiedTypeName(classTypeElement, globals, true);

                    if (util().isPartOfAnEnum(classTypeElement)) {
                        qualifiedClassName += ENUM_WRAPPER_CLASS_SUFFIX;
                    }
                    context.addTopFooterStatement((isBlank(qualifiedClassName) ? "" : qualifiedClassName + ".") + name
                            + STATIC_INITIALIZATION_SUFFIX + "();");
                }
            } else {
                if (varTree.getInitializer() != null && !isDefinitionScope) {
                    if (!(parent instanceof ClassTree && getScope().innerClassNotStatic
                            && !varElement.getModifiers().contains(Modifier.STATIC)
                            && !util().isConstantOrNullField(varTree))) {
                        if (!globals && parent instanceof ClassTree && getScope().interfaceScope) {
                            report(varTree, varTree.getName(), JSweetProblem.INVALID_FIELD_INITIALIZER_IN_INTERFACE,
                                    varTree.getName(), ((ClassTree) parent).getSimpleName());
                        } else {
                            if (!getScope().fieldsWithInitializers.contains(varTree)) {
                                print(" = ");
                                if (!substituteAssignedExpression(Util.getType(varTree), varTree.getInitializer())) {
                                    print(varTree.getInitializer());
                                }
                            }
                        }
                    }
                }

                // var initialization is not allowed in definition
                if (!isDefinitionScope && !(ambient || (isTopLevelScope() && isDefinitionScope))
                        && varElement.getModifiers().contains(Modifier.STATIC) && varTree.getInitializer() == null) {
                    print(" = ").print(getAdapter().getVariableInitialValue(varElement));
                }
            }
        }

        return returnNothing();
    }

    private String getTSMemberAccess(String memberName, boolean hasSelector) {
        if (doesMemberNameRequireQuotes(memberName)) {
            // TODO : hasSelector should not be false by now for member with
            // special chars for now but we should handle node case (window
            // isn't something) => replace with global context
            return (hasSelector ? "" : "window") + "['" + memberName + "']";
        } else {
            return (hasSelector ? "." : "") + memberName;
        }
    }

    private boolean doesMemberNameRequireQuotes(String name) {
        for (char c : name.toCharArray()) {
            if (TS_IDENTIFIER_FORBIDDEN_CHARS.contains(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Void visitParenthesized(ParenthesizedTree parens, Trees trees) {
        print("(");
        super.visitParenthesized(parens, trees);
        print(")");

        return returnNothing();
    }

    @Override
    public Void visitImport(ImportTree importTree, Trees trees) {
        String qualId = importTree.getQualifiedIdentifier().toString();
        if (context.useModules && qualId.endsWith("*")
                && !(qualId.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME + ".*")
                        || qualId.equals(JSweetConfig.UTIL_CLASSNAME + ".*"))) {
            report(importTree, JSweetProblem.WILDCARD_IMPORT);
            return returnNothing();
        }
        String adaptedQualId = getAdapter().needsImport(createExtendedElement(importTree), qualId);
        if (adaptedQualId != null && adaptedQualId.contains(".")) {
            if (!(importTree.isStatic() && !qualId.contains("." + JSweetConfig.GLOBALS_CLASS_NAME + ".")
                    && !qualId.contains("." + JSweetConfig.STRING_TYPES_INTERFACE_NAME + "."))) {
                String[] namePath;
                if (context.useModules && importTree.isStatic()) {
                    namePath = qualId.split("\\.");
                } else {
                    namePath = adaptedQualId.split("\\.");
                }
                String name = namePath[namePath.length - 1];
                if (context.useModules) {
                    if (!adaptedQualId.startsWith(GLOBALS_PACKAGE_NAME)) {
                        if (!context.getImportedNames(compilationUnit.getSourceFile().getName()).contains(name)) {
                            print("import ").print(name).print(" = ").print(adaptedQualId).print(";").println();
                            context.registerImportedName(compilationUnit.getSourceFile().getName(), null, name);
                        }
                    }
                } else {
                    if (topLevelPackage == null) {
                        if (context.globalImports.contains(name)) {
                            // Tsc global package does allow multiple import
                            // with
                            // the same name in the global namespace (bug?)
                            return returnNothing();
                        }
                        context.globalImports.add(name);
                    }
                    if (!context.useModules) {
                        // in bundle mode, we do not use imports to minimize
                        // dependencies
                        // (imports create unavoidable dependencies!)
                        context.importedTopPackages.add(namePath[0]);
                    } else {
                        if (!context.getImportedNames(compilationUnit.getSourceFile().getName()).contains(name)) {
                            print("import ").print(name).print(" = ").print(adaptedQualId).print(";").println();
                            context.registerImportedName(compilationUnit.getSourceFile().getName(), null, name);
                        }
                    }
                }
            }
        }

        return returnNothing();
    }

    private void printInnerClassAccess(String accessedElementName, ElementKind kind) {
        printInnerClassAccess(accessedElementName, kind, null);
    }

    private void printInnerClassAccess(String accessedElementName, ElementKind kind, Integer methodArgsCount) {
        print("this.");
        ClassTree parent = getParent(ClassTree.class);
        TypeElement parentTypeElement = Util.getElement(parent);
        int level = 0;
        boolean foundInParent = util().findFirstDeclarationInClassAndSuperClasses(parentTypeElement,
                accessedElementName, kind, methodArgsCount) != null;
        if (!foundInParent) {
            while (getScope(level++).innerClassNotStatic) {
                parent = getParent(ClassTree.class, parent);
                parentTypeElement = Util.getElement(parent);
                if (parent != null && util().findFirstDeclarationInClassAndSuperClasses(parentTypeElement,
                        accessedElementName, kind, methodArgsCount) != null) {
                    foundInParent = true;
                    break;
                }
            }
        }
        if (foundInParent && level > 0) {
            if (getScope().constructor) {
                removeLastChars(5);
            }
            for (int i = 0; i < level; i++) {
                print(PARENT_CLASS_FIELD_NAME + ".");
            }
        }
    }
    
    public boolean printClass(TypeMirror type) {
        Element element = Util.getElement(type);
        TypeElement typeElement = element instanceof TypeElement ? (TypeElement) element : null;
        String className = typeElement == null ? type.toString() : typeElement.toString();
        
        if (context.isMappedType(className)) {
            String target = context.getTypeMappingTarget(className);
            if (CONSTRUCTOR_TYPE_MAPPING.containsKey(target)) {
                print(mapConstructorType(target));
                return true;
            } else if (type instanceof DeclaredType) {
                Element firstTypeArgumentAsElement = util()
                        .getFirstTypeArgumentAsElement((DeclaredType) type);
                print(getStringLiteralQuote())
                        .print(context.getRootRelativeJavaName(firstTypeArgumentAsElement))
                        .print(getStringLiteralQuote());
                return true;
            }
        } else {
            if (CONSTRUCTOR_TYPE_MAPPING.containsKey(className)) {
                print(mapConstructorType(className));
                return true;
            }
        }
        return false;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, Trees trees) {
        if (!getAdapter().substitute(createExtendedElement(memberSelectTree))) {
            Element selectedElement = null;
            if (memberSelectTree.getExpression() instanceof MemberSelectTree 
            		|| memberSelectTree.getExpression() instanceof IdentifierTree) {
            	selectedElement = Util.getElement(memberSelectTree.getExpression());
            }
            TypeMirror selectedType = Util.getType(memberSelectTree.getExpression());
            Element selectedTypeElement = Util.getTypeElement(memberSelectTree.getExpression());
            Element memberElement = Util.getElement(memberSelectTree);
            TypeMirror memberType = Util.getType(memberSelectTree);
            Element memberTypeElement = Util.getTypeElement(memberSelectTree);
            if (selectedElement instanceof PackageElement) {
                if (context.isRootPackage(selectedElement)) {
                    if (memberTypeElement != null) {
                        printIdentifier(memberTypeElement);
                    } else {
                        print(memberSelectTree.getIdentifier().toString());
                    }
                    return returnNothing();
                }
            }

            if ("class".equals(memberSelectTree.getIdentifier().toString())) {
                if (memberType.getKind() == TypeKind.DECLARED //
                        && util().getFirstTypeArgumentAsElement((DeclaredType) memberType) != null //
                        && context.isInterface(util().getFirstTypeArgumentAsElement((DeclaredType) memberType))) {

                    print(getStringLiteralQuote())
                            .print(context.getRootRelativeJavaName(
                                    util().getFirstTypeArgumentAsElement((DeclaredType) memberType)))
                            .print(getStringLiteralQuote());
                } else {
                    if (!printClass(selectedType)) {
                        print(memberSelectTree.getExpression());
                    }
                }
            } else if ("this".equals(memberSelectTree.getIdentifier().toString())) {
                print("this");

                if (getScope().innerClassNotStatic) {
                    ClassTree parent = getParent(ClassTree.class);
                    TypeElement parentTypeElement = Util.getElement(parent);

                    int level = 0;
                    boolean foundInParent = false;
                    while (getScope(level++).innerClassNotStatic) {
                        parent = getParent(ClassTree.class, parent);
                        parentTypeElement = Util.getElement(parent);
                        if (parent != null && parentTypeElement.equals(selectedTypeElement)) {
                            foundInParent = true;
                            break;
                        }
                    }
                    if (foundInParent && level > 0) {
                        print(".");
                        if (getScope().constructor) {
                            removeLastChars(5);
                        }
                        for (int i = 0; i < level; i++) {
                            print(PARENT_CLASS_FIELD_NAME + ".");
                        }
                        removeLastChar();
                    }
                }

            } else {
                String selected = memberSelectTree.getExpression().toString();
                if (!selected.equals(GLOBALS_CLASS_NAME)) {
                    if (selected.equals("super") && (memberElement instanceof VariableElement)) {
                        print("this.");
                    } else if (getScope().innerClassNotStatic
                            && ("this".equals(selected) || selected.endsWith(".this"))) {
                        printInnerClassAccess(memberSelectTree.getIdentifier().toString(), ElementKind.FIELD);
                    } else {
                        boolean accessSubstituted = false;
                        if (memberElement instanceof VariableElement) {
                            VariableElement varElement = (VariableElement) memberElement;
                            if (varElement.getModifiers().contains(Modifier.STATIC)
                                    && varElement.getEnclosingElement() != selectedElement) {
                                accessSubstituted = true;
                                print(getRootRelativeName(varElement.getEnclosingElement())).print(".");
                                ensureModuleIsUsed(varElement.getEnclosingElement());
                            }
                        } else if (selectedElement instanceof PackageElement && context.useModules
                                && !context.moduleBundleMode && memberElement instanceof TypeElement
                                && util().isSourceElement(memberElement)) {
                            accessSubstituted = true;
                            ModuleImportDescriptor moduleImport = getAdapter().getModuleImportDescriptor(
                                    getCompilationUnitElement(), memberElement.getSimpleName().toString(),
                                    (TypeElement) memberElement);
                            if (moduleImport != null) {
                                useModule(moduleImport);
                            }
                        }

                        if (!accessSubstituted) {
                            print(memberSelectTree.getExpression()).print(".");
                        }
                    }
                }

                String fieldName = null;
                if (memberElement instanceof VariableElement
                        && context.getFieldNameMapping((VariableElement) memberElement) != null) {
                    fieldName = context.getFieldNameMapping((VariableElement) memberElement);
                } else {
                    fieldName = getIdentifier(memberElement);
                }
                if (memberElement instanceof TypeElement) {
                    if (context.hasClassNameMapping((TypeElement) memberElement)) {
                        fieldName = context.getClassNameMapping((TypeElement) selectedElement);
                    }
                }
                if (doesMemberNameRequireQuotes(fieldName)) {
                    if (getLastPrintedChar() == '.') {
                        removeLastChar();
                        print("['").print(fieldName).print("']");
                    } else {
                        print("this['").print(fieldName).print("']");
                    }
                } else {
                    print(fieldName);
                }
                if (memberElement instanceof VariableElement && isLazyInitialized((VariableElement) memberElement)) {
                    if (!staticInitializedAssignment) {
                        print(STATIC_INITIALIZATION_SUFFIX + "()");
                    }
                }
            }
        }

        return returnNothing();
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree methodInvocationTree, Trees trees) {

        boolean debugMode = false;
        if (context.options.isDebugMode()) {
            Element methodElement = Util.getElement(methodInvocationTree.getMethodSelect());
            if (methodElement instanceof ExecutableElement) {
                if (methodElement.getKind() != ElementKind.CONSTRUCTOR && util().isSourceElement(methodElement)) {
                    debugMode = true;
                }
            }
        }
        if (debugMode) {
            print("__debug_result(yield ");
        }
        getAdapter().substituteMethodInvocation(createExtendedElement(methodInvocationTree));
        if (debugMode) {
            print(")");
        }

        return returnNothing();
    }

    /**
     * Prints a method invocation tree (default behavior).
     */
    public void printDefaultMethodInvocation(MethodInvocationTree methodInvocationTree) {
        String meth = methodInvocationTree.getMethodSelect().toString();
        String methName = meth.substring(meth.lastIndexOf('.') + 1);
        if (methName.equals("super") && getScope().removedSuperclass) {
            return;
        }

        boolean applyVarargs = true;
        if (JSweetConfig.NEW_FUNCTION_NAME.equals(methName)) {
            print("new ");
            applyVarargs = false;
        }

        if (context.isAwaitInvocation(methodInvocationTree)) {
            if (getParent() instanceof MethodInvocationTree) {
                print("(");
            }
            print("await ");
        }

        boolean anonymous = isAnonymousMethod(methName);
        boolean targetIsThis = meth.equals("this." + methName);
        boolean targetIsThisOrStaticImported = (meth.equals(methName) || targetIsThis);

        ExecutableType type = Util.getType(methodInvocationTree.getMethodSelect()) instanceof ExecutableType
                ? Util.getType(methodInvocationTree.getMethodSelect())
                : null;

        ExecutableElement methSym = null;
        String methodName = null;
        boolean keywordHandled = false;
        if (targetIsThisOrStaticImported) {
            ImportTree staticImport = getStaticGlobalImport(methName);
            if (staticImport == null || targetIsThis) {
                ClassTree p = getParent(ClassTree.class);
                methSym = p == null ? null : util().findMethodDeclarationInType(Util.getElement(p), methName, type);
                if (methSym != null) {
                    typeChecker.checkApply(methodInvocationTree, methSym);
                    if (!methSym.getModifiers().contains(Modifier.STATIC)) {
                        if (!meth.startsWith("this.")) {
                            print("this");
                            if (!anonymous) {
                                print(".");
                            }
                        }
                    } else {
                        if (meth.startsWith("this.") && methSym.getModifiers().contains(Modifier.STATIC)) {
                            report(methodInvocationTree, JSweetProblem.CANNOT_ACCESS_STATIC_MEMBER_ON_THIS,
                                    methSym.getSimpleName());
                        }
                        Element methodOwner = util().getParentElement(methSym, TypeElement.class);
                        if (!JSweetConfig.GLOBALS_CLASS_NAME.equals(methodOwner.getSimpleName().toString())) {
                            print("" + methodOwner.getSimpleName());
                            if (util().isPartOfAnEnum(methodOwner)) {
                                print(Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_SUFFIX);
                            }
                            if (!anonymous) {
                                print(".");
                            }
                        }
                    }
                } else {
                    if (getScope().defaultMethodScope) {
                        TypeElement target = util().getStaticImportTarget(
                                getContext().getDefaultMethodCompilationUnit(getParent(MethodTree.class)), methName);
                        if (target != null) {
                            print(getRootRelativeName(target) + ".");
                        }
                    } else {
                        TypeElement target = util().getStaticImportTarget(getCompilationUnit(), methName);
                        if (target != null) {
                            print(getRootRelativeName(target) + ".");
                        }
                    }

                    if (getScope().innerClass) {
                        ClassTree parent = getParent(ClassTree.class);
                        int level = 0;
                        ExecutableElement method = null;
                        if (parent != null) {
                            while (getScope(level++).innerClass) {
                                parent = getParent(ClassTree.class, parent);
                                if ((method = util().findMethodDeclarationInType(Util.getElement(parent), methName,
                                        type)) != null) {
                                    break;
                                }
                            }
                        }
                        if (method != null) {
                            if (method.getModifiers().contains(Modifier.STATIC)) {
                                print(method.getEnclosingElement().getSimpleName().toString() + ".");
                            } else {
                                if (level == 0 || !getScope().constructor) {
                                    print("this.");
                                }
                                for (int i = 0; i < level; i++) {
                                    print(Java2TypeScriptTranslator.PARENT_CLASS_FIELD_NAME + ".");
                                }
                                if (anonymous) {
                                    removeLastChar();
                                }
                            }
                        }
                    }

                }
            } else {
                MemberSelectTree staticFieldAccess = (MemberSelectTree) staticImport.getQualifiedIdentifier();
                methSym = util().findMethodDeclarationInType(Util.getTypeElement(staticFieldAccess.getExpression()), methName,
                        type);
                if (methSym != null) {
                    Map<String, VariableElement> vars = new HashMap<>();
                    util().fillAllVariablesInScope(vars, getStack(), methodInvocationTree, getParent(MethodTree.class),
                            getCompilationUnit());
                    if (vars.containsKey(methSym.getSimpleName().toString())) {
                        report(methodInvocationTree, JSweetProblem.HIDDEN_INVOCATION, methSym.getSimpleName());
                    }
                    Element methodOwner = util().getParentElement(methSym, TypeElement.class);
                    if (!context.useModules && methodOwner.getSimpleName().toString().equals(GLOBALS_CLASS_NAME)
                            && methodOwner.getEnclosingElement() != null && !methodOwner.getEnclosingElement()
                                    .getSimpleName().toString().equals(GLOBALS_PACKAGE_NAME)) {
                        String prefix = getRootRelativeName(methodOwner.getEnclosingElement());
                        if (!StringUtils.isEmpty(prefix)) {
                            print(getRootRelativeName(methodOwner.getEnclosingElement()) + ".");
                        }
                    }
                }
                if (JSweetConfig.TS_STRICT_MODE_KEYWORDS.contains(context.getActualName(methSym))) {
                    String targetClass = getStaticContainerFullName(staticImport);
                    if (!isBlank(targetClass)) {
                        print(targetClass);
                        print(".");
                        keywordHandled = true;
                    }
                    if (JSweetConfig.isLibPath(util().getQualifiedName(methSym.getEnclosingElement()))) {
                        methodName = methName.toLowerCase();
                    }
                }
            }
        } else {
            if (methodInvocationTree.getMethodSelect() instanceof MemberSelectTree) {
                ExpressionTree selected = ((MemberSelectTree) methodInvocationTree.getMethodSelect()).getExpression();
                Element selectedTypeElement = Util.getTypeElement(selected);
                if (context.isFunctionalType(selectedTypeElement)) {
                    anonymous = true;
                }
                if (selectedTypeElement instanceof TypeParameterElement) {
                    TypeParameterElement typeParameterElement = ((TypeParameterElement) selectedTypeElement);
                    for (TypeMirror genericBound : typeParameterElement.getBounds()) {
                        TypeElement genericBoundTypeElement = (TypeElement) types().asElement(genericBound);
                        methSym = util().findMethodDeclarationInType(genericBoundTypeElement, methName, type);
                        if (methSym != null) {
                            break;
                        }
                    }
                } else {
                    methSym = util().findMethodDeclarationInType((TypeElement) selectedTypeElement, methName, type);
                }
                if (methSym != null) {
                    typeChecker.checkApply(methodInvocationTree, methSym);
                }
            }
        }

        boolean isStatic = methSym == null || methSym.getModifiers().contains(Modifier.STATIC);
        List<? extends ExpressionTree> arguments = methodInvocationTree.getArguments();
        if (!util().hasVarargs(methSym) //
                || !arguments.isEmpty() && (Util.getType(util().last(arguments)).getKind() != TypeKind.ARRAY
                        // we dont use apply if var args type differ
                        || !types().erasure(((ArrayType) Util.getType(util().last(arguments))).getComponentType())
                                .equals(types().erasure(((ArrayType) util().last(methSym.getParameters()).asType())
                                        .getComponentType())))) {
            applyVarargs = false;
        }

        if (anonymous) {
            applyVarargs = false;
            if (methodInvocationTree.getMethodSelect() instanceof MemberSelectTree) {
                ExpressionTree selected = ((MemberSelectTree) methodInvocationTree.getMethodSelect()).getExpression();
                print(selected);
            }
        } else {
            // method with name
            if (methodInvocationTree.getMethodSelect() instanceof MemberSelectTree && applyVarargs
                    && !targetIsThisOrStaticImported && !isStatic) {
                print("(o => o");

                String accessedMemberName;
                if (keywordHandled) {
                    accessedMemberName = ((MemberSelectTree) methodInvocationTree.getMethodSelect()).getIdentifier()
                            .toString();
                } else {
                    if (methSym == null) {
                        methSym = Util.getElement((MemberSelectTree) methodInvocationTree.getMethodSelect());
                    }
                    if (methSym != null) {
                        accessedMemberName = context.getActualName(methSym);
                    } else {
                        accessedMemberName = ((MemberSelectTree) methodInvocationTree.getMethodSelect()).getIdentifier()
                                .toString();
                    }
                }
                print(getTSMemberAccess(accessedMemberName, true));
            } else if (methodName != null) {
                print(getTSMemberAccess(methodName, removeLastChar('.')));
            } else {
                if (keywordHandled) {
                    print(methodInvocationTree.getMethodSelect());
                } else {
                    if (methSym == null && methodInvocationTree.getMethodSelect() instanceof MemberSelectTree
                            && Util.getElement((MemberSelectTree) methodInvocationTree
                                    .getMethodSelect()) instanceof ExecutableElement) {
                        methSym = Util.getElement((MemberSelectTree) methodInvocationTree.getMethodSelect());
                    }
                    if (methSym != null && methodInvocationTree.getMethodSelect() instanceof MemberSelectTree) {
                        ExpressionTree selected = ((MemberSelectTree) methodInvocationTree.getMethodSelect())
                                .getExpression();
                        Element selectedTypeElement = Util.getTypeElement(selected);
                        if (selectedTypeElement == null
                                || !GLOBALS_CLASS_NAME.equals(selectedTypeElement.getSimpleName().toString())) {
                            if (getScope().innerClassNotStatic
                                    && ("this".equals(selected.toString()) || selected.toString().endsWith(".this"))) {
                                printInnerClassAccess(methSym.getSimpleName().toString(), methSym.getKind(),
                                        methSym.getParameters().size());
                            } else {
                                if (methSym.getModifiers().contains(Modifier.STATIC)
                                        && selected instanceof IdentifierTree
                                        && Util.getElement(selected) instanceof VariableElement) {
                                    // case of instance static access
                                    if (context.useModules && !context.moduleBundleMode) {
                                        print(getClassName((TypeElement) selectedTypeElement));
                                        ModuleImportDescriptor moduleImport = getAdapter().getModuleImportDescriptor(
                                                getCompilationUnitElement(),
                                                selectedTypeElement.getSimpleName().toString(),
                                                (TypeElement) selectedTypeElement);
                                        if (moduleImport != null) {
                                            useModule(moduleImport);
                                        }
                                    } else {
                                        print(getRootRelativeName(selectedTypeElement));
                                    }
                                } else {
                                    print(selected);
                                }
                                print(".");
                            }
                        } else {
                            if (context.useModules) {
                                if (selectedTypeElement == null || (util().isSourceElement(selectedTypeElement)
                                        && !util().isInSameSourceFile(getCompilationUnit(), selectedTypeElement))) {
                                    // TODO: when using several qualified
                                    // Globals classes, we
                                    // need to disambiguate (use qualified
                                    // name with
                                    // underscores)
                                    print(GLOBALS_CLASS_NAME).print(".");
                                }
                            }

                            Map<String, VariableElement> vars = new HashMap<>();
                            util().fillAllVariablesInScope(vars, getStack(), methodInvocationTree,
                                    getParent(MethodTree.class), getCompilationUnit());
                            if (vars.containsKey(methName)) {
                                report(methodInvocationTree, JSweetProblem.HIDDEN_INVOCATION, methName);
                            }
                        }
                    }
                    if (methSym != null) {
                        if (context.isInvalidOverload(methSym) && !util().hasTypeParameters(methSym)
                                && !methSym.isDefault() && getParent(MethodTree.class) != null
                                && !getParent(MethodTree.class).getModifiers().getFlags().contains(Modifier.DEFAULT)) {
                            if (context.isInterface((TypeElement) methSym.getEnclosingElement())) {
                                removeLastChar('.');
                                print("['" + getOverloadMethodName(methSym) + "']");
                            } else {
                                print(getOverloadMethodName(methSym));
                            }
                        } else {
                            print(getTSMemberAccess(context.getActualName(methSym), removeLastChar('.')));
                        }
                    } else {
                        print(methodInvocationTree.getMethodSelect());
                    }
                }
            }
        }

        if (applyVarargs) {
            print(".apply");
        } else {
            if (methodInvocationTree.getTypeArguments() != null && !methodInvocationTree.getTypeArguments().isEmpty()) {
                print("<");
                for (Tree argument : methodInvocationTree.getTypeArguments()) {
                    substituteAndPrintType(argument).print(",");
                }

                // with overloads, missing generic types need to be added because core method
                // has all generics
                if (methSym != null) {
                    TypeElement targetType = (TypeElement) methSym.getEnclosingElement();
                    Overload overload = context.getOverload(targetType, methSym);
                    if (overload != null && overload.getMethodsCount() > 1) {
                        int missingArgsCount = overload.getCoreMethodElement().getTypeParameters().size()
                                - methodInvocationTree.getTypeArguments().size();
                        print("any,".repeat(missingArgsCount));
                    }
                }

                removeLastChar(',');
                print(">");
            } else {
                // force type arguments to any because they are inferred to
                // {} by default
                if (methSym != null && !methSym.getTypeParameters().isEmpty()) {
                    TypeElement targetType = (TypeElement) methSym.getEnclosingElement();
                    if (!targetType.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
                        // invalid overload type parameters are erased
                        Overload overload = context.getOverload(targetType, methSym);
                        boolean inOverload = overload != null && overload.getMethodsCount() > 1;
                        if (!(inOverload && !overload.isValid)) {
                            printAnyTypeArguments(methSym.getTypeParameters().size());
                        }
                    }
                }
            }
        }

        print("(");

        if (applyVarargs) {
            String contextVar = "null";
            if (targetIsThisOrStaticImported) {
                contextVar = "this";
            } else if (methodInvocationTree.getMethodSelect() instanceof MemberSelectTree
                    && !targetIsThisOrStaticImported && !isStatic) {
                contextVar = "o";
            }

            print(contextVar + ", ");

            if (methodInvocationTree.getArguments().size() > 1) {
                print("[");
            }
        }

        int argsLength = applyVarargs ? methodInvocationTree.getArguments().size() - 1
                : methodInvocationTree.getArguments().size();

        if (getScope().innerClassNotStatic && "super".equals(methName)) {
            TypeElement s = Util.getTypeElement(getParent(ClassTree.class).getExtendsClause());
            if (s.getEnclosingElement() instanceof TypeElement && !s.getModifiers().contains(Modifier.STATIC)) {
                print(Java2TypeScriptTranslator.PARENT_CLASS_FIELD_NAME);
                if (argsLength > 0) {
                    print(", ");
                }
            }
        }

        if (getScope().enumWrapperClassScope && isAnonymousClass() && "super".equals(methName)) {
            print(Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_ORDINAL + ", "
                    + Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_NAME);
            if (argsLength > 0) {
                print(", ");
            }
        }

        if ("super".equals(methName)) {
            ClassTree p = getParent(ClassTree.class);
            methSym = p == null ? null : util().findMethodDeclarationInType(Util.getElement(p), "this", type);
        }

        TypeMirror methodType = Util.getType(methodInvocationTree.getMethodSelect());
        for (int i = 0; i < argsLength; i++) {
            ExpressionTree arg = methodInvocationTree.getArguments().get(i);
            if (methodType != null && methodType.getKind() == TypeKind.EXECUTABLE) {
                // varargs transmission with TS ... notation
                List<? extends TypeMirror> argTypes = ((ExecutableType) methodType).getParameterTypes();
                TypeMirror paramType = i < argTypes.size() ? argTypes.get(i) : util().last(argTypes);
                if (i == argsLength - 1 && !applyVarargs && methSym != null && methSym.isVarArgs()) {
                    if (arg instanceof IdentifierTree && Util.getElement((IdentifierTree) arg) instanceof VariableElement) {
                        VariableElement var = Util.getElement((IdentifierTree) arg);
                        if (var.getEnclosingElement() instanceof ExecutableElement) {
                            if (util().isVarargs(var)) {
                                print("...");
                            }
                        }
                    }
                }
                if (!substituteAssignedExpression(paramType, arg)) {
                    print(arg);
                }
            } else {
                // fall back when method type is wrongly resolved
                print(arg);
            }
            if (i < argsLength - 1) {
                print(", ");
            }
        }

        if (applyVarargs) {
            if (methodInvocationTree.getArguments().size() > 1) {
                // we cast array to any[] to avoid concat error on
                // different
                // types
                print("].concat(<any[]>");
            }

            print(util().last(methodInvocationTree.getArguments()));

            if (methodInvocationTree.getArguments().size() > 1) {
                print(")");
            }
            if (methodInvocationTree.getMethodSelect() instanceof MemberSelectTree && !targetIsThisOrStaticImported
                    && !isStatic) {
                print("))(").print(((MemberSelectTree) methodInvocationTree.getMethodSelect()).getExpression());
            }
        }

        print(")");

        if (context.isAwaitInvocation(methodInvocationTree) && getParent() instanceof MethodInvocationTree) {
            print(")");
        }
    }

    private boolean isAnonymousMethod(String methName) {
        boolean anonymous = JSweetConfig.ANONYMOUS_FUNCTION_NAME.equals(methName)
                || JSweetConfig.ANONYMOUS_STATIC_FUNCTION_NAME.equals(methName)
                || (context.deprecatedApply && JSweetConfig.ANONYMOUS_DEPRECATED_FUNCTION_NAME.equals(methName))
                || (context.deprecatedApply && JSweetConfig.ANONYMOUS_DEPRECATED_STATIC_FUNCTION_NAME.equals(methName))
                || JSweetConfig.NEW_FUNCTION_NAME.equals(methName);
        return anonymous;
    }

    private ImportTree getStaticGlobalImport(String methName) {
        if (compilationUnit == null) {
            return null;
        }
        for (ImportTree importTree : compilationUnit.getImports()) {
            if (importTree.isStatic()) {
                if (importTree.getQualifiedIdentifier().toString()
                        .endsWith(JSweetConfig.GLOBALS_CLASS_NAME + "." + methName)) {
                    return importTree;
                }
            }
        }
        return null;
    }

    private String getStaticContainerFullName(ImportTree importDecl) {
        if (importDecl.getQualifiedIdentifier() instanceof MemberSelectTree) {
            MemberSelectTree fa = (MemberSelectTree) importDecl.getQualifiedIdentifier();
            String name = context.getRootRelativeJavaName(Util.getTypeElement(fa.getExpression()));
            // function is a top-level global function (no need to import)
            if (JSweetConfig.GLOBALS_CLASS_NAME.equals(name)) {
                return null;
            }
            boolean globals = name.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME);
            if (globals) {
                name = name.substring(0, name.length() - JSweetConfig.GLOBALS_CLASS_NAME.length() - 1);
            }
            // function belong to the current package (no need to import)
            if (util().getPackageFullNameForCompilationUnit(compilationUnit).startsWith(name)) {
                return null;
            }
            return name;
        }

        return null;
    }

    @Override
    public Void visitIdentifier(IdentifierTree identifierTree, Trees trees) {
        Element identifierElement =Util.getElement(identifierTree);
        String name = identifierTree.toString();

        if (getScope().inlinedConstructorArgs != null) {
            if (identifierElement instanceof VariableElement && getScope().inlinedConstructorArgs.contains(name)) {
                print("__args[" + getScope().inlinedConstructorArgs.indexOf(name) + "]");
                return returnNothing();
            }
        }

        if (!getAdapter().substitute(createExtendedElement(identifierTree))) {
            boolean lazyInitializedStatic = false;
            // add this of class name if ident is a field
            if (identifierElement instanceof VariableElement
                    && !identifierElement.getSimpleName().toString().equals("this")
                    && !identifierElement.getSimpleName().toString().equals("super")) {
                VariableElement varElement = (VariableElement) identifierElement;
                if (varElement != null) {
                    Element varEnclosingElement = varElement.getEnclosingElement();
                    if (varEnclosingElement instanceof TypeElement) {
                        if (context.getFieldNameMapping(varElement) != null) {
                            name = context.getFieldNameMapping(varElement);
                        } else {
                            name = getIdentifier(varElement);
                        }
                        if (!varElement.getModifiers().contains(Modifier.STATIC)) {
                            printInnerClassAccess(varElement.getSimpleName().toString(), ElementKind.FIELD);
                        } else {
                            if (isLazyInitialized(varElement)) {
                                lazyInitializedStatic = true;
                            }
                            if (!util().getQualifiedName(varEnclosingElement).toString()
                                    .endsWith("." + GLOBALS_CLASS_NAME)) {
                                if (!context.useModules
                                        && !varEnclosingElement.equals(Util.getElement(getParent(ClassTree.class)))) {
                                    String prefix = context.getRootRelativeName(null, varEnclosingElement);
                                    if (!StringUtils.isEmpty(prefix)) {
                                        print(context.getRootRelativeName(null, varEnclosingElement));
                                        if (lazyInitializedStatic && util().isPartOfAnEnum(varEnclosingElement)) {
                                            print(ENUM_WRAPPER_CLASS_SUFFIX);
                                        }
                                        print(".");
                                    }
                                } else {
                                    if (!varEnclosingElement.getSimpleName().toString().equals(GLOBALS_PACKAGE_NAME)) {
                                        print(varEnclosingElement.getSimpleName().toString());

                                        ensureModuleIsUsed(varEnclosingElement);
                                        if (lazyInitializedStatic && util().isPartOfAnEnum(varEnclosingElement)) {
                                            print(ENUM_WRAPPER_CLASS_SUFFIX);
                                        }
                                        print(".");
                                    }
                                }
                            } else {
                                if (!context.useModules) {
                                    String prefix = context.getRootRelativeName(null, varEnclosingElement);
                                    prefix = prefix.substring(0, prefix.length() - GLOBALS_CLASS_NAME.length());
                                    if (!prefix.equals(GLOBALS_PACKAGE_NAME + ".")
                                            && !prefix.endsWith("." + GLOBALS_PACKAGE_NAME + ".")) {
                                        print(prefix);
                                    }
                                }
                            }
                        }
                    } else {
                        if (varEnclosingElement instanceof ExecutableElement && isAnonymousClass()
                                && getScope(1).finalVariables
                                        .get(getScope(1).anonymousClasses.indexOf(getParent(ClassTree.class)))
                                        .contains(varElement)) {
                            print("this.");
                        } else {
                            if (!context.useModules && varEnclosingElement instanceof ExecutableElement) {
                                if (context.importedTopPackages.contains(name)) {
                                    name = "__var_" + name;
                                }
                            }
                            if (JSweetConfig.JS_KEYWORDS.contains(name)) {
                                name = JSweetConfig.JS_KEYWORD_PREFIX + name;
                            }
                        }
                    }
                }
            }
            if (identifierElement instanceof TypeElement) {
                TypeElement classIdentifierTypeElement = (TypeElement) identifierElement;
                boolean prefixAdded = false;
                if (getScope().defaultMethodScope) {
                    if (util().isImported(getContext().getDefaultMethodCompilationUnit(getParent(MethodTree.class)),
                            classIdentifierTypeElement)) {
                        String rootRelativeName = getRootRelativeName(classIdentifierTypeElement.getEnclosingElement());
                        if (!rootRelativeName.isEmpty()) {
                            print(rootRelativeName + ".");
                            PackageElement identifierPackage = util().getParentElement(classIdentifierTypeElement,
                                    PackageElement.class);
                            String pathToModulePackage = util().getRelativePath(Util.getElement(compilationUnit.getPackage()),
                                    identifierPackage);
                            if (pathToModulePackage == null) {
                                pathToModulePackage = ".";
                            }
                            File moduleFile = new File(new File(pathToModulePackage),
                                    classIdentifierTypeElement.getEnclosingElement().getSimpleName().toString());
                            useModule(false, false, identifierPackage, identifierTree,
                                    classIdentifierTypeElement.getEnclosingElement().getSimpleName().toString(),
                                    moduleFile.getPath().replace('\\', '/'), null);
                        }
                        prefixAdded = true;
                    }
                }
                // add parent class name if ident is an inner class of the
                // current class
                if (!prefixAdded && classIdentifierTypeElement.getEnclosingElement() instanceof TypeElement) {
                    if (context.useModules) {
                        print(classIdentifierTypeElement.getEnclosingElement().getSimpleName() + ".");
                        prefixAdded = true;
                    } else {
                        // if the class has not been imported, we need to add
                        // the containing class prefix
                        if (!getCompilationUnit().getImports().stream()
                                .map(i -> Util.getTypeElement(i.getQualifiedIdentifier()))
                                .anyMatch(t -> t == classIdentifierTypeElement)) {
                            if (classIdentifierTypeElement.getEnclosingElement() instanceof TypeElement) {
                                print(getClassName((TypeElement) classIdentifierTypeElement.getEnclosingElement())
                                        + ".");
                            } else {
                                print(classIdentifierTypeElement.getEnclosingElement().getSimpleName() + ".");
                            }
                            prefixAdded = true;
                        }
                    }
                }
                if (!prefixAdded && !context.useModules
                        && !classIdentifierTypeElement.equals(Util.getElement(getParent(ClassTree.class)))) {
                    print(getRootRelativeName(classIdentifierTypeElement));
                } else {
                    if (context.hasClassNameMapping(classIdentifierTypeElement)) {
                        print(context.getClassNameMapping(classIdentifierTypeElement));
                    } else {
                        print(name);
                    }
                }
            } else {
                if (doesMemberNameRequireQuotes(name)) {
                    if (getLastPrintedChar() == '.') {
                        removeLastChar();
                        print("['").print(name).print("']");
                    } else {
                        print("this['").print(name).print("']");
                    }
                } else {
                    print(name);
                }
                if (lazyInitializedStatic) {
                    if (!staticInitializedAssignment) {
                        print(STATIC_INITIALIZATION_SUFFIX + "()");
                    }
                }
            }
        }

        return returnNothing();
    }

    /**
     * Prints a type apply (<code>T<P1,...PN></code>) tree.
     */
    @Override
    public Void visitParameterizedType(ParameterizedTypeTree typeApply, Trees trees) {
        substituteAndPrintType(typeApply);

        return returnNothing();
    }

    private int initAnonymousClass(NewClassTree newClass) {
        int anonymousClassIndex = getScope().anonymousClasses.indexOf(newClass.getClassBody());
        if (anonymousClassIndex == -1) {
            anonymousClassIndex = getScope().anonymousClasses.size();
            getScope().anonymousClasses.add(newClass.getClassBody());
            getScope().anonymousClassesConstructors.add(newClass);
            LinkedHashSet<VariableElement> finalVars = new LinkedHashSet<>();
            getScope().finalVariables.add(finalVars);

            Element newClassTypeElement = Util.getElement(newClass.getClassBody());
            new TreeScanner<Void, Trees>() {
                @Override
                public Void visitIdentifier(IdentifierTree var, Trees trees) {
                    if (Util.getElement(var) instanceof VariableElement) {
                        VariableElement varElement = Util.getElement(var);
                        if (varElement.getEnclosingElement() instanceof ExecutableElement) {
                            Element methodEnclosingTypeElement = varElement.getEnclosingElement().getEnclosingElement();

                            // search through method owners to match anonymous class to know if var (method
                            // param) is declared in anonymous class
                            // if so, we do not need it as an anonymous class param
                            boolean finalVariableFromThisClass = false;
                            Element wrappingTypeElement = methodEnclosingTypeElement;
                            do {
                                if (wrappingTypeElement == newClassTypeElement) {
                                    finalVariableFromThisClass = true;
                                    break;
                                }
                                wrappingTypeElement = wrappingTypeElement.getEnclosingElement();
                            } while (wrappingTypeElement != null);

                            if (!finalVariableFromThisClass) {
                                finalVars.add(varElement);
                            }
                        }
                    }

                    return returnNothing();
                }
            }.visitClass(newClass.getClassBody(), trees());

        }
        return anonymousClassIndex;
    }

    /**
     * Prints a new-class expression tree.
     */
    @Override
    public Void visitNewClass(NewClassTree newClass, Trees trees) {
        TypeElement classTypeElement = Util.getTypeElement(newClass.getIdentifier());
        if (classTypeElement.getSimpleName().toString().equals(JSweetConfig.GLOBALS_CLASS_NAME)) {
            report(newClass, JSweetProblem.GLOBAL_CANNOT_BE_INSTANTIATED);
            return returnNothing();
        }
        if (getScope().isLocalClassType(classTypeElement.asType())) {
            print("new ").print(getScope().getName() + ".").print(newClass.getIdentifier().toString());
            print("(").printConstructorArgList(newClass, true).print(")");
            return returnNothing();
        }
        boolean isInterface = context.isInterface(classTypeElement);
        if (newClass.getClassBody() != null || isInterface) {
            if (context.isAnonymousClass(newClass, getCompilationUnit())) {
                int anonymousClassIndex = initAnonymousClass(newClass);
                print("new ").print(
                        getScope().getName() + "." + getScope().getName() + ANONYMOUS_PREFIX + anonymousClassIndex);

                if (isStaticAnonymousClass(newClass, getCompilationUnit())) {
                    printAnonymousClassTypeArgs(newClass);
                }

                print("(");
                printConstructorArgList(newClass, false);
                print(")");
                return returnNothing();
            }

            if (isInterface || context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
                if (isInterface) {
                    print("<any>");
                }

                Set<String> interfaces = new HashSet<>();
                context.grabSupportedInterfaceNames(interfaces, classTypeElement);
                if (!interfaces.isEmpty()) {
                    print("Object.defineProperty(");
                }
                print("{").println().startIndent();
                boolean statementPrinted = false;
                boolean initializationBlockFound = false;
                if (newClass.getClassBody() != null) {
                    for (Tree m : newClass.getClassBody().getMembers()) {
                        if (m instanceof BlockTree) {
                            initializationBlockFound = true;
                            List<VariableElement> initializedVars = new ArrayList<>();
                            for (Tree s : ((BlockTree) m).getStatements()) {
                                boolean currentStatementPrinted = false;
                                if (s instanceof ExpressionStatementTree
                                        && ((ExpressionStatementTree) s).getExpression() instanceof AssignmentTree) {
                                    AssignmentTree assignment = (AssignmentTree) ((ExpressionStatementTree) s)
                                            .getExpression();
                                    VariableElement var = null;
                                    if (assignment.getVariable() instanceof MemberSelectTree) {
                                        var = util().findFieldDeclaration(classTypeElement,
                                                ((MemberSelectTree) assignment.getVariable()).getIdentifier());
                                        printIndent().print(var.getSimpleName().toString());
                                    } else if (assignment.getVariable() instanceof IdentifierTree) {
                                        var = util().findFieldDeclaration(classTypeElement,
                                                ((IdentifierTree) assignment.getVariable()).getName());
                                        printIndent().print(assignment.getVariable().toString());
                                    } else {
                                        continue;
                                    }
                                    initializedVars.add(var);
                                    print(": ").print(assignment.getExpression()).print(",").println();
                                    currentStatementPrinted = true;
                                    statementPrinted = true;
                                } else if (s instanceof ExpressionStatementTree && ((ExpressionStatementTree) s)
                                        .getExpression() instanceof MethodInvocationTree) {
                                    MethodInvocationTree invocation = (MethodInvocationTree) ((ExpressionStatementTree) s)
                                            .getExpression();
                                    MethodInvocationElement invocationElement = (MethodInvocationElement) createExtendedElement(
                                            invocation);
                                    if (invocationElement.getMethodName()
                                            .equals(JSweetConfig.INDEXED_SET_FUCTION_NAME)) {
                                        if (invocation.getArguments().size() == 3) {
                                            if ("this".equals(invocation.getArguments().get(0).toString())) {
                                                printIndent().print(invocation.getArguments().get(1)).print(": ")
                                                        .print(invocation.getArguments().get(2)).print(",").println();
                                            }
                                            currentStatementPrinted = true;
                                            statementPrinted = true;
                                        } else {
                                            printIndent().print(invocation.getArguments().get(0)).print(": ")
                                                    .print(invocation.getArguments().get(1)).print(",").println();
                                            currentStatementPrinted = true;
                                            statementPrinted = true;
                                        }
                                    }
                                }
                                if (!currentStatementPrinted) {
                                    report(s, JSweetProblem.INVALID_INITIALIZER_STATEMENT);
                                }
                            }
                            for (Element s : classTypeElement.getEnclosedElements()) {
                                if (s instanceof VariableElement) {
                                    if (!initializedVars.contains(s)) {
                                        if (!context.hasAnnotationType(s, JSweetConfig.ANNOTATION_OPTIONAL)) {
                                            report(m, JSweetProblem.UNINITIALIZED_FIELD, s);
                                        }
                                    }
                                }
                            }
                        }
                        if (m instanceof MethodTree) {
                            MethodTree method = (MethodTree) m;
                            if (Util.getElement(method).getKind() != ElementKind.CONSTRUCTOR) {
                                printIndent().print(method.getName() + ": (");
                                for (VariableTree param : method.getParameters()) {
                                    print(param.getName() + ", ");
                                }
                                if (!method.getParameters().isEmpty()) {
                                    removeLastChars(2);
                                }
                                print(") => ");
                                print(method.getBody());
                                print(",").println();
                                statementPrinted = true;
                            }
                        }
                    }
                    if (statementPrinted) {
                        removeLastChars(2);
                    }

                }
                if (!statementPrinted && !initializationBlockFound) {
                    for (Element s : classTypeElement.getEnclosedElements()) {
                        if (s instanceof VariableElement) {
                            if (!context.hasAnnotationType(s, JSweetConfig.ANNOTATION_OPTIONAL)) {
                                report(newClass, JSweetProblem.UNINITIALIZED_FIELD, s);
                            }
                        }
                    }
                }

                println().endIndent().printIndent().print("}");
                if (!interfaces.isEmpty()) {
                    print(", 'constructor', { configurable: true, value: { " + INTERFACES_FIELD_NAME + ": ");
                    print("[");
                    for (String i : interfaces) {
                        print(getStringLiteralQuote()).print(i).print(getStringLiteralQuote() + ",");
                    }
                    removeLastChar();
                    print("] }");
                    print(" })");
                }
            } else {

                print("((target:").print(newClass.getIdentifier()).print(") => {").println().startIndent();
                for (Tree m : newClass.getClassBody().getMembers()) {
                    if (m instanceof BlockTree) {
                        for (Tree s : ((BlockTree) m).getStatements()) {
                            boolean currentStatementPrinted = false;
                            if (s instanceof ExpressionStatementTree
                                    && ((ExpressionStatementTree) s).getExpression() instanceof AssignmentTree) {
                                AssignmentTree assignment = (AssignmentTree) ((ExpressionStatementTree) s)
                                        .getExpression();
                                VariableElement var = null;
                                if (assignment.getVariable() instanceof MemberSelectTree) {
                                    var = util().findFieldDeclaration(classTypeElement,
                                            ((MemberSelectTree) assignment.getVariable()).getIdentifier());
                                    printIndent().print("target['").print(var.getSimpleName().toString()).print("']");
                                } else if (assignment.getVariable() instanceof IdentifierTree) {
                                    printIndent().print("target['").print(assignment.getVariable().toString())
                                            .print("']");
                                } else {
                                    continue;
                                }
                                print(" = ").print(assignment.getExpression()).print(";").println();
                                currentStatementPrinted = true;
                            } else if (s instanceof ExpressionStatementTree
                                    && ((ExpressionStatementTree) s).getExpression() instanceof MethodInvocationTree) {
                                MethodInvocationTree invocation = (MethodInvocationTree) ((ExpressionStatementTree) s)
                                        .getExpression();
                                MethodInvocationElement invocationElement = (MethodInvocationElement) createExtendedElement(
                                        invocation);
                                if (invocationElement.getMethodName().equals(JSweetConfig.INDEXED_SET_FUCTION_NAME)) {
                                    if (invocation.getArguments().size() == 3) {
                                        if ("this".equals(invocation.getArguments().get(0).toString())) {
                                            printIndent().print("target[").print(invocation.getArguments().get(1))
                                                    .print("]").print(" = ").print(invocation.getArguments().get(2))
                                                    .print(";").println();
                                        }
                                        currentStatementPrinted = true;
                                    } else {
                                        printIndent().print("target[").print(invocation.getArguments().get(0))
                                                .print("]").print(" = ").print(invocation.getArguments().get(1))
                                                .print(";").println();
                                        currentStatementPrinted = true;
                                    }
                                }
                            }
                            if (!currentStatementPrinted) {
                                report(s, JSweetProblem.INVALID_INITIALIZER_STATEMENT);
                            }
                        }
                    }
                }
                printIndent().print("return target;").println();
                println().endIndent().printIndent().print("})(");
                print("new ").print(newClass.getIdentifier()).print("(").printArgList(null, newClass.getArguments())
                        .print("))");
            }
        } else {
            if (context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
                print("{}");
            } else {
                getAdapter().substituteNewClass(createExtendedElement(newClass));
            }
        }

        return returnNothing();
    }

    /**
     * Prints a new-class expression tree (default behavior).
     */
    public void printDefaultNewClass(NewClassTree newClass) {
        TypeMirror classType = Util.getType(newClass.getIdentifier());
        TypeElement classTypeElement = Util.getTypeElement(newClass.getIdentifier());
        String mappedType = context.getTypeMappingTarget(classType.toString());
        if (typeChecker.checkType(newClass, null, newClass.getIdentifier(), getCompilationUnit())) {

            boolean applyVarargs = true;

            Element constructorElement = Util.getElement(newClass);
            if (!(constructorElement instanceof ExecutableElement)) {
                // not in source path
                print("null /*cannot resolve " + newClass.getIdentifier() + "*/");
                return;
            }
            ExecutableElement constructorExecutableElement = (ExecutableElement) constructorElement;

            if (newClass.getArguments().size() == 0 || !util().hasVarargs(constructorExecutableElement) //
                    || Util.getType(util().last(newClass.getArguments())).getKind() != TypeKind.ARRAY
                    // we dont use apply if var args type differ
                    || !types().erasure(((ArrayType) Util.getType(util().last(newClass.getArguments()))).getComponentType())
                            .equals(types().erasure(
                                    ((ArrayType) util().last(constructorExecutableElement.getParameters()).asType())
                                            .getComponentType()))) {
                applyVarargs = false;
            }
            if (applyVarargs) {
                // this is necessary in case the user defines a
                // Function class that hides the global Function
                // class
                context.addGlobalsMapping("Function", "__Function");
                print("<any>new (__Function.prototype.bind.apply(");
                if (mappedType != null) {
                    print(Java2TypeScriptTranslator.mapConstructorType(mappedType));
                } else {
                    print(newClass.getIdentifier());
                }
                print(", [null");
                for (int i = 0; i < newClass.getArguments().size() - 1; i++) {
                    print(", ").print(newClass.getArguments().get(i));
                }
                print("].concat(<any[]>").print(util().last(newClass.getArguments())).print(")))");
            } else {
                if (newClass.getIdentifier() instanceof ParameterizedTypeTree) {
                    ParameterizedTypeTree typeApply = (ParameterizedTypeTree) newClass.getIdentifier();
                    mappedType = context.getTypeMappingTarget(Util.getType(typeApply.getType()).toString());
                    print("new ");
                    if (mappedType != null) {
                        print(Java2TypeScriptTranslator.mapConstructorType(mappedType));
                    } else {
                        print(typeApply.getType());
                    }
                    if (!typeApply.getTypeArguments().isEmpty()) {
                        print("<").printTypeArgList(typeApply.getTypeArguments()).print(">");
                    } else {
                        // erase types since the diamond (<>)
                        // operator
                        // does not exists in TypeScript
                        printAnyTypeArguments(classTypeElement.getTypeParameters().size());
                    }
                    print("(").printConstructorArgList(newClass, false).print(")");
                } else {
                    if (constructorExecutableElement.asType().getKind() == TypeKind.ERROR) {
                        print("null /*cannot resolve " + newClass.getIdentifier() + "*/");
                        return;
                    }
                    print("new ");
                    if (mappedType != null) {
                        print(Java2TypeScriptTranslator.mapConstructorType(mappedType));
                    } else {
                        print(newClass.getIdentifier());
                    }
                    print("(").printConstructorArgList(newClass, false).print(")");
                }
            }
        }
    }

    public void printAnyTypeArguments(int count) {
        print("<");
        for (int i = 0; i < count; i++) {
            print("any, ");
        }
        if (count > 0) {
            removeLastChars(2);
        }
        print(">");
    }

    @Override
    public AbstractTreePrinter printConstructorArgList(NewClassTree newClass, boolean localClass) {
        TypeElement classTypeElement = Util.getTypeElement(newClass.getIdentifier());

        boolean printed = false;
        if (localClass || (getScope().anonymousClasses.contains(newClass.getClassBody())
                && !isStaticAnonymousClass(newClass, getCompilationUnit()))) {
            print("this");
            if (!newClass.getArguments().isEmpty()) {
                print(", ");
            }
            printed = true;
        } else if ((classTypeElement.getEnclosingElement() instanceof TypeElement
                && !classTypeElement.getModifiers().contains(Modifier.STATIC)
                && !isStaticAnonymousClass(newClass, getCompilationUnit()))) {
            print("this");
            ClassTree parent = getParent(ClassTree.class);
            TypeElement parentSymbol = parent == null ? null : Util.getElement(parent);
            if (classTypeElement.getEnclosingElement() != parentSymbol) {
                print("." + PARENT_CLASS_FIELD_NAME);
            }
            if (!newClass.getArguments().isEmpty()) {
                print(", ");
            }
            printed = true;
        }

        ExecutableType methodType = (ExecutableType)Util.getElement(newClass).asType();

        printArgList(methodType == null ? null : methodType.getParameterTypes(), newClass.getArguments());
        int index = getScope().anonymousClasses.indexOf(newClass.getClassBody());
        if (index >= 0 && !getScope().finalVariables.get(index).isEmpty()) {
            if (printed || !newClass.getArguments().isEmpty()) {
                print(", ");
            }
            for (VariableElement paramToBeTransmittedToAnonymous : getScope().finalVariables.get(index)) {

                Element paramEnclosingTypeElement = paramToBeTransmittedToAnonymous.getEnclosingElement()
                        .getEnclosingElement();
                Element anonymousClassEnclosingTypeElement = Util.getElement(getParent(ClassTree.class));
                if (anonymousClassEnclosingTypeElement != paramEnclosingTypeElement) {
                    print("this.");
                }
                print(paramToBeTransmittedToAnonymous.getSimpleName().toString());
                print(", ");
            }
            removeLastChars(2);
        }
        return this;
    }

    @Override
    public Void visitLiteral(LiteralTree literal, Trees trees) {
        String s = literal.toString();
        switch (Util.getType(literal).getKind()) {
        case FLOAT:
            if (s.endsWith("F")) {
                s = s.substring(0, s.length() - 1);
            }
            break;
        case LONG:
            if (s.endsWith("L")) {
                s = s.substring(0, s.length() - 1);
            }
            break;
        default:
        }
        if (s.startsWith("\"") && context.options.isUseSingleQuotesForStringLiterals()) {
            s = "'" + s.substring(1, s.length() - 1).replace("'", "\'") + "'";
        }
        print(s);

        return returnNothing();
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree arrayAccess, Trees trees) {
        if (!getAdapter().substituteArrayAccess(createExtendedElement(arrayAccess))) {
            print(arrayAccess.getExpression()).print("[")
                    .substituteAndPrintAssignedExpression(util().getType(int.class), arrayAccess.getIndex()).print("]");
        }
        return returnNothing();
    }

    /**
     * Prints a foreach loop tree.
     */
    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree foreachLoop, Trees trees) {
        String indexVarName = "index" + util().getId();
        boolean[] hasLength = { false };
        TypeElement targetType = Util.getTypeElement(foreachLoop.getExpression());
        TypeMirror collectionType = Util.getType(foreachLoop.getExpression());

        if (collectionType.getKind() == TypeKind.ARRAY) {
            hasLength[0] = true;
        } else {
            util().scanMemberDeclarationsInType(targetType, getAdapter().getErasedTypes(), element -> {
                if (element instanceof VariableElement) {
                    if ("length".equals(element.getSimpleName().toString())
                            && util().isNumber(((VariableElement) element).asType())) {
                        hasLength[0] = true;
                        return false;
                    }
                }
                return true;
            });
        }
        if (!getAdapter().substituteForEachLoop(createExtendedElement(foreachLoop), hasLength[0], indexVarName)) {
            boolean noVariable = foreachLoop.getExpression() instanceof IdentifierTree
                    || foreachLoop.getExpression() instanceof MemberSelectTree;
            if (noVariable) {
                print("for(" + VAR_DECL_KEYWORD + " " + indexVarName + "=0; " + indexVarName + " < ")
                        .print(foreachLoop.getExpression()).print("." + "length" + "; " + indexVarName + "++) {")
                        .println().startIndent().printIndent();
                print(VAR_DECL_KEYWORD + " " + avoidJSKeyword(foreachLoop.getVariable().getName().toString()) + " = ")
                        .print(foreachLoop.getExpression()).print("[" + indexVarName + "];").println();
            } else {
                String arrayVarName = "array" + util().getId();
                print("{").println().startIndent().printIndent();
                print(VAR_DECL_KEYWORD + " " + arrayVarName + " = ").print(foreachLoop.getExpression()).print(";")
                        .println().printIndent();
                print("for(" + VAR_DECL_KEYWORD + " " + indexVarName + "=0; " + indexVarName + " < " + arrayVarName
                        + ".length; " + indexVarName + "++) {").println().startIndent().printIndent();
                print(VAR_DECL_KEYWORD + " " + avoidJSKeyword(foreachLoop.getVariable().getName().toString()) + " = "
                        + arrayVarName + "[" + indexVarName + "];").println();
            }
            visitBeforeForEachBody(foreachLoop);
            printIndent().print(foreachLoop.getStatement());
            endIndent().println().printIndent().print("}");
            if (!noVariable) {
                endIndent().println().printIndent().print("}");
            }
        }

        return returnNothing();
    }

    protected void visitBeforeForEachBody(EnhancedForLoopTree foreachLoop) {
    }

    /**
     * Prints a type identifier tree.
     */
    @Override
    public Void visitPrimitiveType(PrimitiveTypeTree type, Trees trees) {
        switch (Util.getType(type).getKind()) {
        case BYTE:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case SHORT:
            print("number");
            break;
        default:
            print(type.toString());
        }

        return returnNothing();
    }

    private boolean singlePrecisionFloats() {
        return !context.options.isDisableSinglePrecisionFloats()
                && context.options.getEcmaTargetVersion().higherThan(EcmaScriptComplianceLevel.ES3);
    }

    @Override
    public Void visitBinary(BinaryTree binary, Trees trees) {
        if (!getAdapter().substituteBinaryOperator(createExtendedElement(binary))) {
            String op = util().toOperator(binary.getKind());
            boolean forceParens = false;
            boolean booleanOp = false;
            if (types().isSameType(util().getType(boolean.class),
                    util().unboxedTypeOrType(Util.getType(binary.getLeftOperand())))) {
                booleanOp = true;
                if ("^".equals(op)) {
                    forceParens = true;
                }
                if ("|".equals(op) || "&".equals(op)) {
                    print("((lhs, rhs) => lhs " + op + op + " rhs)(").print(binary.getLeftOperand()).print(", ")
                            .print(binary.getRightOperand()).print(")");
                    return returnNothing();
                }
            }

            TypeMirror binaryType = Util.getType(binary);
            TypeMirror leftType = Util.getType(binary.getLeftOperand());
            TypeMirror rightType = Util.getType(binary.getRightOperand());
            TypeMirror stringType = util().getType(String.class);

            boolean closeParen = false;
            boolean truncate = false;
            if (util().isIntegral(binaryType) && binary.getKind() == Kind.DIVIDE) {
                if (binaryType.getKind() == TypeKind.LONG) {
                    print("(n => n<0?Math.ceil(n):Math.floor(n))(");
                    closeParen = true;
                } else {
                    print("(");
                    truncate = true;
                }
            }
            if (singlePrecisionFloats() && binaryType.getKind() == TypeKind.FLOAT) {
                print("(<any>Math).fround(");
                closeParen = true;
            }
            boolean charWrapping = util().isArithmeticOrLogicalOperator(binary.getKind())
                    || util().isComparisonOperator(binary.getKind());
            boolean actualCharWrapping = false;
            if (charWrapping
                    && types().isSameType(util().getType(char.class),
                            util().unboxedTypeOrType(Util.getType(binary.getLeftOperand())))
                    && !types().isSameType(rightType, stringType)) {
                actualCharWrapping = true;
                if (binary.getLeftOperand() instanceof LiteralTree) {
                    printBinaryLeftOperand(binary);
                    print(".charCodeAt(0)");
                } else {
                    print("(c => c.charCodeAt==null?<any>c:c.charCodeAt(0))(").print(binary.getLeftOperand())
                            .print(")");
                }
            } else {
                if (forceParens) {
                    print("(");
                }
                printBinaryLeftOperand(binary);
                if (forceParens) {
                    print(")");
                }
            }
            if (booleanOp) {
                if ("|".equals(op)) {
                    op = "||";
                } else if ("&".equals(op)) {
                    op = "&&";
                } else if ("^".equals(op)) {
                    op = "!==";
                }
            }
            if ("==".equals(op) || "!=".equals(op)) {
                if (charWrapping && types().isSameType(util().getType(char.class), util().unboxedTypeOrType(rightType))
                        && !types().isSameType(leftType, stringType)) {
                    actualCharWrapping = true;
                }
            }

            if (!actualCharWrapping && ("==".equals(op) || "!=".equals(op))) {
                switch (getComparisonMode()) {
                case FORCE_STRICT:
                    op += "=";
                    break;
                case STRICT:
                    if (!(util().isNullLiteral(binary.getLeftOperand())
                            || util().isNullLiteral(binary.getRightOperand()))) {
                        op += "=";
                    }
                    break;
                default:
                    break;
                }
            }

            space().print(op).space();
            if (charWrapping && types().isSameType(util().getType(char.class), util().unboxedTypeOrType(rightType))
                    && !types().isSameType(leftType, stringType)) {
                if (binary.getRightOperand() instanceof LiteralTree) {
                    printBinaryRightOperand(binary);
                    print(".charCodeAt(0)");
                } else {
                    print("(c => c.charCodeAt==null?<any>c:c.charCodeAt(0))(");
                    printBinaryRightOperand(binary);
                    print(")");
                }
            } else {
                if (forceParens) {
                    print("(");
                }
                printBinaryRightOperand(binary);
                if (forceParens) {
                    print(")");
                }
            }
            if (closeParen) {
                print(")");
            }
            if (truncate) {
                print("|0)");
            }
        }

        return returnNothing();
    }

    protected void printBinaryRightOperand(BinaryTree binary) {
        addInlinedExpression(binary.getRightOperand());
        print(binary.getRightOperand());
    }

    protected void printBinaryLeftOperand(BinaryTree binary) {
        addInlinedExpression(binary.getLeftOperand());
        print(binary.getLeftOperand());
    }

    /**
     * Prints an <code>if</code> tree.
     */
    @Override
    public Void visitIf(IfTree ifStatement, Trees trees) {
        print("if ");
        if (ifStatement.getCondition() instanceof ParenthesizedTree) {
            print(ifStatement.getCondition());
        } else {
            print("(");
            print(ifStatement.getCondition());
            print(") ");
        }
        print(ifStatement.getThenStatement());
        if (!(ifStatement.getThenStatement() instanceof BlockTree)) {
            if (!isStatementWithNoSemiColon(ifStatement.getThenStatement())) {
                print(";");
            }
        }
        if (ifStatement.getElseStatement() != null) {
            print(" else ");
            print(ifStatement.getElseStatement());
            if (!(ifStatement.getElseStatement() instanceof BlockTree)) {
                if (!isStatementWithNoSemiColon(ifStatement.getElseStatement())) {
                    print(";");
                }
            }
        }

        return returnNothing();
    }

    /**
     * Prints a <code>return</code> tree.
     */
    @Override
    public Void visitReturn(ReturnTree returnStatement, Trees trees) {
        print("return");
        if (returnStatement.getExpression() != null) {
            Tree parentFunction = getFirstParent(MethodTree.class, LambdaExpressionTree.class);

            if (Util.getType(returnStatement.getExpression()) == null) {
                report(returnStatement, JSweetProblem.CANNOT_ACCESS_THIS,
                        parentFunction == null ? returnStatement.toString() : parentFunction.toString());
                return returnNothing();
            }
            print(" ");
            TypeMirror returnType = null;
            if (parentFunction != null) {
                if (parentFunction instanceof MethodTree) {
                    returnType = Util.getType(((MethodTree) parentFunction).getReturnType());
                } else {
                    // TODO: this cannot work! Calculate the return type of the
                    // lambda
                    // either from the functional type type arguments, of from
                    // the method defining the lambda's signature
                    // returnType = ((LambdaExpressionTree) parentFunction).type;
                }
            }
            if (!substituteAssignedExpression(returnType, returnStatement.getExpression())) {
                print(returnStatement.getExpression());
            }
        }

        return returnNothing();
    }

    private boolean staticInitializedAssignment = false;

    private VariableElement getStaticInitializedField(Tree expr) {
    	if (expr instanceof ArrayAccessTree) {
    		return null;
    	}
        Element element = Util.getElement(expr);
        if (element instanceof VariableElement && context.lazyInitializedStatics.contains(element)) {
            return (VariableElement) element;
        }

        return null;
    }

    /**
     * Prints an assignment operator tree (<code>+=, -=, *=, ...</code>).
     */
    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree assignOpTree, Trees trees) {
        if (!getAdapter().substituteAssignmentWithOperator(createExtendedElement(assignOpTree))) {
            boolean expand = staticInitializedAssignment = (getStaticInitializedField(
                    assignOpTree.getVariable()) != null);

            TypeMirror variableType = Util.getType(assignOpTree.getVariable());
            TypeMirror expressionType = Util.getType(assignOpTree.getExpression());

            boolean expandChar = types().isSameType(util().getType(char.class), util().unboxedTypeOrType(variableType));
            print(assignOpTree.getVariable());
            staticInitializedAssignment = false;

            String assignmentOperator = util().toOperator(assignOpTree.getKind());
            String operator = assignmentOperator.replace("=", "");

            if (types().isSameType(util().getType(boolean.class), util().unboxedTypeOrType(variableType))) {
                if ("|".equals(operator)) {
                    print(" = ").print(assignOpTree.getExpression()).print(" || ").print(assignOpTree.getVariable());
                    return returnNothing();
                } else if ("&".equals(operator)) {
                    print(" = ").print(assignOpTree.getExpression()).print(" && ").print(assignOpTree.getVariable());
                    return returnNothing();
                }
            }

            boolean castToIntegral = "/".equals(operator) //
                    && util().isIntegral(variableType) //
                    && util().isIntegral(expressionType);

            if (expandChar) {
                print(" = String.fromCharCode(")
                        .substituteAndPrintAssignedExpression(util().getType(int.class), assignOpTree.getVariable())
                        .print(" " + operator + " ")
                        .substituteAndPrintAssignedExpression(util().getType(int.class), assignOpTree.getExpression())
                        .print(")");
                return returnNothing();
            }

            if (expand || castToIntegral) {
                print(" = ");

                if (castToIntegral) {
                    print("(n => n<0?Math.ceil(n):Math.floor(n))(");
                }

                print(assignOpTree.getVariable());
                print(" " + operator + " ");
                if (types().isSameType(util().getType(char.class), util().unboxedTypeOrType(expressionType))) {
                    substituteAndPrintAssignedExpression(util().getType(int.class), assignOpTree.getExpression());
                } else {
                    printAssignWithOperatorRightOperand(assignOpTree);
                }

                if (castToIntegral) {
                    print(")");
                }

                return returnNothing();
            }

            print(" " + operator + "= ");
            if (types().isSameType(util().getType(char.class), util().unboxedTypeOrType(expressionType))) {
                // TypeMirror lhsType = assignOp.getVariable().type;
                boolean isLeftOperandString = (types().asElement(variableType) == types()
                        .asElement(util().getType(String.class)));
                TypeMirror rightPromotedType = isLeftOperandString ? util().getType(char.class)
                        : util().getType(int.class);
                substituteAndPrintAssignedExpression(rightPromotedType, assignOpTree.getExpression());
            } else {
                printAssignWithOperatorRightOperand(assignOpTree);
            }
        }

        return returnNothing();
    }

    protected void printAssignWithOperatorRightOperand(CompoundAssignmentTree assignOp) {
        print(assignOp.getExpression());
    }

    /**
     * Prints a <code>condition?trueExpr:falseExpr</code> tree.
     */
    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree conditional, Trees trees) {
        print(conditional.getCondition());
        print(" ? ");
        if (!substituteAssignedExpression(
                rootConditionalAssignedTypes.isEmpty() ? null : rootConditionalAssignedTypes.peek(),
                conditional.getTrueExpression())) {
            print(conditional.getTrueExpression());
        }
        print(" : ");
        if (!substituteAssignedExpression(
                rootConditionalAssignedTypes.isEmpty() ? null : rootConditionalAssignedTypes.peek(),
                conditional.getFalseExpression())) {
            print(conditional.getFalseExpression());
        }
        if (!rootConditionalAssignedTypes.isEmpty()) {
            rootConditionalAssignedTypes.pop();
        }

        return returnNothing();
    }

    /**
     * Prints a <code>for</code> loop tree.
     */
    @Override
    public Void visitForLoop(ForLoopTree forLoopTree, Trees trees) {
        print("for(").printArgList(null, forLoopTree.getInitializer()).print("; ").print(forLoopTree.getCondition())
                .print("; ").printArgList(null, forLoopTree.getUpdate()).print(") ");
        print("{");
        visitBeforeForBody(forLoopTree);
        print(forLoopTree.getStatement()).print(";");
        print("}");

        return returnNothing();
    }

    protected void visitBeforeForBody(ForLoopTree forLoop) {
    }

    /**
     * Prints a <code>continue</code> tree.
     */
    @Override
    public Void visitContinue(ContinueTree continueStatement, Trees trees) {
        print("continue");
        if (continueStatement.getLabel() != null) {
            print(" ").print(continueStatement.getLabel().toString());
        }

        return returnNothing();
    }

    /**
     * Prints a <code>break</code> tree.
     */
    @Override
    public Void visitBreak(BreakTree breakStatement, Trees trees) {
        print("break");
        if (breakStatement.getLabel() != null) {
            print(" ").print(breakStatement.getLabel().toString());
        }
        return returnNothing();
    }

    /**
     * Prints a labeled statement tree.
     */
    @Override
    public Void visitLabeledStatement(LabeledStatementTree labelledStatement, Trees trees) {
        Tree parent = getParent(MethodTree.class);
        if (parent == null) {
            parent = getParent(BlockTree.class);
            while (parent != null && getParent(BlockTree.class, parent) != null) {
                parent = getParent(BlockTree.class, parent);
            }
        }
        boolean[] used = { false };
        new TreeScanner<Void, Trees>() {
            @Override
            public Void visitBreak(BreakTree b, Trees trees) {
                if (b.getLabel() != null && labelledStatement.getLabel().equals(b.getLabel())) {
                    used[0] = true;
                }
                return returnNothing();
            }

            @Override
            public Void visitContinue(ContinueTree c, Trees trees) {
                if (c.getLabel() != null && labelledStatement.getLabel().equals(c.getLabel())) {
                    used[0] = true;
                }
                return returnNothing();
            }
        }.scan(parent, trees);
        if (!used[0]) {
            print("/*");
        }
        print(labelledStatement.getLabel().toString()).print(":");
        if (!used[0]) {
            print("*/");
        }
        print(" ");
        print(labelledStatement.getStatement());

        return returnNothing();
    }

    /**
     * Prints an array type tree.
     */
    @Override
    public Void visitArrayType(ArrayTypeTree arrayType, Trees trees) {
        print(arrayType.getType()).print("[]");
        return returnNothing();
    }

    /**
     * Prints a new array tree.
     */
    @Override
    public Void visitNewArray(NewArrayTree newArray, Trees trees) {
        if (newArray.getType() != null) {
            typeChecker.checkType(newArray, null, newArray.getType(), getCompilationUnit());
        }
        if (newArray.getDimensions() != null && !newArray.getDimensions().isEmpty()) {
            TypeMirror newArrayElementType = Util.getType(newArray.getType());
            if (newArray.getDimensions().size() == 1) {
                if (newArray.getDimensions().get(0) instanceof LiteralTree
                        && ((int) ((LiteralTree) newArray.getDimensions().get(0)).getValue()) <= 10) {
                    boolean hasElements = false;
                    print("[");
                    for (int i = 0; i < (int) ((LiteralTree) newArray.getDimensions().get(0)).getValue(); i++) {
                        print(util().getTypeInitialValue(newArrayElementType) + ", ");
                        hasElements = true;
                    }
                    if (hasElements) {
                        removeLastChars(2);
                    }
                    print("]");
                } else {
                    print("(s => { let a=[]; while(s-->0) a.push(" + util().getTypeInitialValue(newArrayElementType)
                            + "); return a; })(").print(newArray.getDimensions().get(0)).print(")");
                }
            } else {
                print("<any> (function(dims) { " + VAR_DECL_KEYWORD
                        + " allocate = function(dims) { if (dims.length === 0) { return "
                        + util().getTypeInitialValue(newArrayElementType) + "; } else { " + VAR_DECL_KEYWORD
                        + " array = []; for(" + VAR_DECL_KEYWORD
                        + " i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})");
                print("([");
                printArgList(null, newArray.getDimensions());
                print("])");
            }
        } else {
            print("[");
            if (newArray.getInitializers() != null && !newArray.getInitializers().isEmpty()) {
                for (ExpressionTree e : newArray.getInitializers()) {
                    if (!rootArrayAssignedTypes.isEmpty()) {
                        if (!substituteAssignedExpression(rootArrayAssignedTypes.peek(), e)) {
                            print(e);
                        }
                    } else {
                        print(e);
                    }
                    print(", ");
                }
                removeLastChars(2);
                if (!rootArrayAssignedTypes.isEmpty()) {
                    rootArrayAssignedTypes.pop();
                }
            }
            print("]");
        }

        return returnNothing();
    }

    protected boolean inRollback = false;

    /**
     * Prints a unary operator tree.
     */
    @Override
    public Void visitUnary(UnaryTree unary, Trees trees) {
        if (!getAdapter().substituteUnaryOperator(createExtendedElement(unary))) {
            addInlinedExpression(unary.getExpression());

            String operatorAsString = util().toOperator(unary.getKind());
            if (!inRollback) {
                StatementTree statement = null;
                VariableElement[] staticInitializedField = { null };
                switch (operatorAsString) {
                case "--":
                case "++":
                    staticInitializedAssignment = (staticInitializedField[0] = getStaticInitializedField(
                            unary.getExpression())) != null;
                    if (staticInitializedAssignment) {
                        statement = getParent(StatementTree.class);
                    }
                default:
                }
                if (statement != null) {
                    rollback(statement, tree -> {
                        print(context.getRootRelativeName(null, staticInitializedField[0].getEnclosingElement()))
                                .print(".").print(staticInitializedField[0].getSimpleName().toString()
                                        + STATIC_INITIALIZATION_SUFFIX + "();")
                                .println().printIndent();
                        inRollback = true;
                        scan(tree, trees);
                    });
                }
            } else {
                inRollback = false;
            }
            switch (unary.getKind()) {
            case POSTFIX_DECREMENT:
            case POSTFIX_INCREMENT:
                print(unary.getExpression());
                print(operatorAsString);
                break;
            default:
                print(operatorAsString);
                print(unary.getExpression());
                break;
            }
        }
        return returnNothing();
    }

    /**
     * Prints a <code>switch</code> tree.
     */
    @Override
    public Void visitSwitch(SwitchTree switchStatement, Trees trees) {
        print("switch(");
        if (!getAdapter().substituteSwitchStatementSelector(createExtendedElement(switchStatement.getExpression()))) {
            print(switchStatement.getExpression());
            if (types().isSameType(util().getType(char.class),
                    util().unboxedTypeOrType(Util.getType(switchStatement.getExpression())))) {
                print(".charCodeAt(0)");
            }
        }
        print(") {").println();
        for (CaseTree caseStatement : switchStatement.getCases()) {
            printIndent();
            print(caseStatement);
        }
        printIndent().print("}");

        return returnNothing();
    }

    protected void printCaseStatementPattern(ExpressionTree pattern) {
    }

    /**
     * Prints a <code>case</code> tree.
     */
    @Override
    public Void visitCase(CaseTree caseTree, Trees trees) {
        if (caseTree.getExpression() != null) {
            TypeMirror expressionType = Util.getType(caseTree.getExpression());

            print("case ");
            if (!getAdapter().substituteCaseStatementPattern(createExtendedElement(caseTree),
                    createExtendedElement(caseTree.getExpression()))) {
                if (util().isPrimitiveOrVoid(expressionType)
                        || types().isSameType(util().getType(String.class), expressionType)) {
                    if (caseTree.getExpression() instanceof IdentifierTree) {
                        VariableElement varElement = Util.getElement((IdentifierTree) caseTree.getExpression());
                        Object value = varElement.getConstantValue();
                        if (types().isSameType(util().getType(String.class), expressionType)) {
                            print(getStringLiteralQuote() + value + getStringLiteralQuote() + " /* "
                                    + caseTree.getExpression() + " */");
                        } else {
                            if (value != null && value.getClass() == Character.class) {
                                value = (int) ((Character) value);
                            }

                            print("" + value + " /* " + caseTree.getExpression() + " */");
                        }
                    } else {
                        if (types().isSameType(util().getType(char.class), expressionType)) {
                            ExpressionTree caseExpression = caseTree.getExpression();
                            if (caseExpression instanceof TypeCastTree) {
                                caseExpression = ((TypeCastTree) caseExpression).getExpression();
                            }

                            if (caseExpression instanceof LiteralTree) {
                                Object value = ((LiteralTree) caseExpression).getValue();
                                if (value instanceof Character) {
                                    int charCodePoint = Character.codePointAt(value.toString(), 0);
                                    value = charCodePoint;
                                }
                                print(value + " /* " + caseTree.getExpression() + " */");
                            } else {
                                print(caseExpression);
                            }

                        } else {
                            print(caseTree.getExpression());
                        }
                    }
                } else {
                    Element expressionTypeElement = types().asElement(expressionType);
                    print(getRootRelativeName(expressionTypeElement) + "." + caseTree.getExpression());
                    ensureModuleIsUsed(expressionTypeElement);
                }
            }
        } else {
            print("default");
        }
        print(":");
        println().startIndent();
        for (StatementTree statement : caseTree.getStatements()) {
            printIndent();
            print(statement);
            if (!isStatementWithNoSemiColon(statement)) {
                print(";");
            }
            println();
        }
        endIndent();

        return returnNothing();
    }

    /**
     * Prints a type cast tree.
     */
    @Override
    public Void visitTypeCast(TypeCastTree cast, Trees trees) {
        TypeMirror fromType = Util.getType(cast.getExpression());
        Element fromTypeElement = types().asElement(fromType);
        TypeMirror toType = Util.getType(cast.getType());
        Element toTypeElement = types().asElement(toType);

        if (substituteAssignedExpression(toType, cast.getExpression())) {
            return returnNothing();
        }
        if (getAdapter().substituteTypeCast(createExtendedElement(cast))) {
            return returnNothing();
        }
        if (util().isIntegral(toType)) {
            if (toType.getKind() == TypeKind.LONG) {
                print("(n => n<0?Math.ceil(n):Math.floor(n))(");
            } else {
                print("(");
            }
        }
        if (!context.hasAnnotationType(toTypeElement, ANNOTATION_ERASED, ANNOTATION_OBJECT_TYPE,
                ANNOTATION_FUNCTIONAL_INTERFACE)) {
            // Java is more permissive than TypeScript when casting type
            // variables
            if (fromType.getKind() == TypeKind.TYPEVAR) {
                print("<any>");
            } else {
                print("<");
                substituteAndPrintType(cast.getType()).print(">");
                // Java always allows casting when an interface or a type param
                // is involved
                // (that's weak!!)
                if (util().isInterface(fromTypeElement) || util().isInterface(toTypeElement)
                        || toType.getKind() == TypeKind.TYPEVAR) {
                    print("<any>");
                }
            }
        }
        print(cast.getExpression());
        if (util().isIntegral(toType)) {
            if (toType.getKind() == TypeKind.LONG) {
                print(")");
            } else {
                print("|0)");
            }
        }
        return returnNothing();
    }

    /**
     * Prints a <code>do - while</code> loop tree.
     */
    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree doWhileLoop, Trees trees) {
        print("do ");
        print("{");
        visitBeforeDoWhileBody(doWhileLoop);
        if (doWhileLoop.getStatement() instanceof BlockTree) {
            print(doWhileLoop.getStatement());
        } else {
            print(doWhileLoop.getStatement()).print(";");
        }
        print("}");
        print(" while(").print(doWhileLoop.getCondition()).print(")");

        return returnNothing();
    }

    protected void visitBeforeDoWhileBody(DoWhileLoopTree doWhileLoop) {
    }

    /**
     * Prints a <code>while</code> loop tree.
     */
    @Override
    public Void visitWhileLoop(WhileLoopTree whileLoop, Trees trees) {
        print("while(").print(whileLoop.getCondition()).print(") ");
        print("{");
        visitBeforeWhileBody(whileLoop);
        print(whileLoop.getStatement());
        print("}");

        return returnNothing();
    }

    protected void visitBeforeWhileBody(WhileLoopTree whileLoop) {
    }

    /**
     * Prints a variable assignment tree.
     */
    @Override
    public Void visitAssignment(AssignmentTree assign, Trees trees) {
        if (!getAdapter().substituteAssignment(createExtendedElement(assign))) {
            staticInitializedAssignment = getStaticInitializedField(assign.getVariable()) != null;
            print(assign.getVariable()).print(isAnnotationScope ? ": " : " = ");
            if (!substituteAssignedExpression(Util.getType(assign.getVariable()), assign.getExpression())) {
                print(assign.getExpression());
            }
            staticInitializedAssignment = false;
        }

        return returnNothing();
    }

    /**
     * Prints a <code>try</code> tree.
     */
    @Override
    public Void visitTry(TryTree tryStatement, Trees trees) {
        boolean resourced = tryStatement.getResources() != null && !tryStatement.getResources().isEmpty();
        if (resourced) {
            for (Tree resource : tryStatement.getResources()) {
                print(resource).println(";").printIndent();
            }
        } else if (tryStatement.getCatches().isEmpty() && tryStatement.getFinallyBlock() == null) {
            report(tryStatement, JSweetProblem.TRY_WITHOUT_CATCH_OR_FINALLY);
        }
        print("try ").print(tryStatement.getBlock());
        if (tryStatement.getCatches().size() > 1) {
            print(" catch(__e) {").startIndent();
            for (CatchTree catcher : tryStatement.getCatches()) {
                println().printIndent().print("if");
                printInstanceOf("__e", null, Util.getType(catcher.getParameter()));
                print(" {").startIndent().println().printIndent();
                // if (!context.options.isUseJavaApis() &&
                // catcher.param.type.toString().startsWith("java.")) {
                // print(catcher.param).print(" = ").print("__e;").println();
                // } else {
                print(catcher.getParameter()).print(" = <");
                substituteAndPrintType(catcher.getParameter().getType());
                print(">__e;").println();
                // }
                printBlockStatements(catcher.getBlock().getStatements());
                endIndent().println().printIndent().print("}");
            }
            endIndent().println().printIndent().print("}");
        } else if (tryStatement.getCatches().size() == 1) {
            print(tryStatement.getCatches().get(0));
        }
        if (resourced || tryStatement.getFinallyBlock() != null) {
            print(" finally {");
            if (resourced) {
                // resources are closed in reverse order, before finally block is executed
                startIndent();

                List<? extends Tree> reversedResources = new ArrayList<>(tryStatement.getResources());
                Collections.reverse(reversedResources);
                for (Tree resource : reversedResources) {
                    if (resource instanceof VariableTree) {
                        println().printIndent().print(((VariableTree) resource).getName() + ".close();");
                    }
                }
                endIndent();
            }
            if (tryStatement.getFinallyBlock() != null) {
                startIndent();// .printIndent();
                for (StatementTree statement : tryStatement.getFinallyBlock().getStatements()) {
                    println().printIndent().print(statement).print(";");
                }
                endIndent();
            }
            println().printIndent().print("}"); // closes finally block
        }

        return returnNothing();
    }

    /**
     * Prints a <code>catch</code> tree.
     */
    @Override
    public Void visitCatch(CatchTree catcher, Trees trees) {
        print(" catch(").print(catcher.getParameter().getName().toString()).print(") ");
        print(catcher.getBlock());

        return returnNothing();
    }

    /**
     * Prints a lambda expression tree.
     */
    @Override
    public Void visitLambdaExpression(LambdaExpressionTree lamba, Trees trees) {
        boolean regularFunction = false;
        if (getParent() instanceof MethodInvocationTree
                && ((MethodInvocationTree) getParent()).getMethodSelect().toString().endsWith("function")
                && getParentOfParent() instanceof MethodInvocationTree
                && ((MethodInvocationTree) getParentOfParent()).getMethodSelect().toString().endsWith("$noarrow")) {
            MethodInvocationElement invocation = (MethodInvocationElement) createExtendedElement(getParent());
            if (JSweetConfig.UTIL_CLASSNAME.equals(invocation.getMethod().getEnclosingElement().toString())) {
                regularFunction = true;
            }
        }
        Map<String, VariableElement> varAccesses = new HashMap<>();
        util().fillAllVariableAccesses(varAccesses, lamba, getCompilationUnit());
        Collection<VariableElement> finalVars = new ArrayList<>(varAccesses.values());
        if (!varAccesses.isEmpty()) {
            Map<String, VariableElement> varDefs = new HashMap<>();
            int parentIndex = getStack().size() - 2;
            int i = parentIndex;
            StatementTree statement = null;
            while (i > 0 && getStack().get(i).getKind() != Kind.LAMBDA_EXPRESSION
                    && getStack().get(i).getKind() != Kind.METHOD) {
                if (statement == null && getStack().get(i) instanceof StatementTree) {
                    statement = (StatementTree) getStack().get(i);
                }
                i--;
            }
            if (i >= 0 && getStack().get(i).getKind() != Kind.LAMBDA_EXPRESSION && statement != null) {
                util().fillAllVariablesInScope(varDefs, getStack(), lamba, getStack().get(i), getCompilationUnit());
            }
            finalVars.retainAll(varDefs.values());
        }
        if (!finalVars.isEmpty()) {
            print("((");
            for (VariableElement var : finalVars) {
                print(var.getSimpleName().toString()).print(",");
            }
            removeLastChar();
            print(") => {").println().startIndent().printIndent().print("return ");
        }
        getScope().skipTypeAnnotations = true;
        if (regularFunction) {
            print("function(").printArgList(null, lamba.getParameters()).print(") ");
        } else {
            print("(").printArgList(null, lamba.getParameters()).print(") => ");
        }
        getScope().skipTypeAnnotations = false;
        print(lamba.getBody());

        if (!finalVars.isEmpty()) {
            endIndent().println().printIndent().print("})(");
            for (VariableElement var : finalVars) {
                print(var.getSimpleName().toString()).print(",");
            }
            removeLastChar();
            print(")");
        }

        return returnNothing();
    }

    @Override
    public Void visitMemberReference(MemberReferenceTree memberReference, Trees trees) {
        Element element = Util.getElement(memberReference);
        String memberReferenceSimpleName;
        if (memberReference.getQualifierExpression() instanceof ArrayTypeTree) {
            memberReferenceSimpleName = "Array";
        } else {
            memberReferenceSimpleName = Util.getTypeElement(memberReference.getQualifierExpression()).getSimpleName()
                    .toString();
        }
        boolean printAsInstanceMethod = !element.getModifiers().contains(Modifier.STATIC)
                && !CONSTRUCTOR_METHOD_NAME.equals(memberReference.getName().toString())
                && !JSweetConfig.GLOBALS_CLASS_NAME.equals(memberReferenceSimpleName);
        boolean exprIsInstance = memberReference.getQualifierExpression().toString().equals("this")
                || memberReference.getQualifierExpression().toString().equals("super")
                || (memberReference.getQualifierExpression() instanceof IdentifierTree && Util.getElement(
                        (IdentifierTree) memberReference.getQualifierExpression()) instanceof VariableElement)
                || (memberReference.getQualifierExpression() instanceof MemberSelectTree && Util.getElement(
                        (MemberSelectTree) memberReference.getQualifierExpression()) instanceof VariableElement);

        if (element instanceof ExecutableElement) {
            ExecutableElement method = (ExecutableElement) element;
            if (getParent() instanceof TypeCastTree) {
                print("(");
            }
            print("(");
            int argumentsPrinted = 0;
            if (printAsInstanceMethod && !exprIsInstance) {
                print("instance$").print(memberReferenceSimpleName);
                print(",");
                argumentsPrinted++;
            }
            if (method.getParameters() != null) {
                for (VariableElement var : method.getParameters()) {
                    print(var.getSimpleName().toString());
                    print(",");
                    argumentsPrinted++;
                }
            }
            if (argumentsPrinted > 0) {
                removeLastChar();
            }
            print(")");
            print(" => { return ");
        }

        if (JSweetConfig.GLOBALS_CLASS_NAME.equals(memberReferenceSimpleName)) {
            print(memberReference.getName().toString());
        } else {
            if (CONSTRUCTOR_METHOD_NAME.equals(memberReference.getName().toString())) {
                if (Util.getType(memberReference.getQualifierExpression()).getKind() == TypeKind.ARRAY) {
                    print("new Array<");
                    substituteAndPrintType(((ArrayTypeTree) memberReference.getQualifierExpression()).getType());
                    print(">");
                } else {
                    print("new ").print(memberReference.getQualifierExpression());
                }
            } else {
                if (printAsInstanceMethod && !exprIsInstance) {
                    print("instance$").print(memberReferenceSimpleName);
                } else {
                    print(memberReference.getQualifierExpression());
                }
                print(".").print(memberReference.getName().toString());
            }
        }

        if (element instanceof ExecutableElement) {
            ExecutableElement method = (ExecutableElement) element;

            print("(");
            if (method.getParameters() != null) {
                for (VariableElement var : method.getParameters()) {
                    print(var.getSimpleName().toString());
                    print(",");
                }
                if (!method.getParameters().isEmpty()) {
                    removeLastChar();
                }
            }
            print(")");
            print(" }");
            if (getParent() instanceof TypeCastTree) {
                print(")");
            }
        }

        return returnNothing();
    }

    /**
     * Prints a type parameter tree.
     */
    @Override
    public Void visitTypeParameter(TypeParameterTree typeParameter, Trees trees) {
        print(typeParameter.getName().toString());
        if (typeParameter.getBounds() != null && !typeParameter.getBounds().isEmpty()) {
            print(" extends ");
            for (Tree e : typeParameter.getBounds()) {
                substituteAndPrintType(e).print(" & ");
            }
            removeLastChars(3);
        }

        return returnNothing();
    }

    /** Prints a <code>synchronized</code> tree. */
    @Override
    public Void visitSynchronized(SynchronizedTree sync, Trees trees) {
        report(sync, JSweetProblem.SYNCHRONIZATION);
        if (sync.getBlock() != null) {
            print(sync.getBlock());
        }

        return returnNothing();
    }

    /**
     * Prints either a string, or the tree if the the string is null.
     * 
     * @param exprStr a string to be printed as is if not null
     * @param expr    a tree to be printed if exprStr is null
     */
    public void print(String exprStr, Tree expr) {
        if (exprStr == null) {
            print(expr);
        } else {
            print(exprStr);
        }
    }

    private void printInstanceOf(String exprStr, Tree expr, TypeMirror type) {
        printInstanceOf(exprStr, expr, type, false);
    }

    private void printInstanceOf(String exprStr, Tree expr, TypeMirror type, boolean checkFirstArrayElement) {
        if (!(getParent() instanceof ParenthesizedTree)) {
            print("(");
        }
        if (checkFirstArrayElement || !getAdapter().substituteInstanceof(exprStr, createExtendedElement(expr), type)) {
            Element typeElement = types().asElement(type);
            if (TYPE_MAPPING.containsKey(type.toString())) {
                print("typeof ");
                print(exprStr, expr);
                if (checkFirstArrayElement)
                    print("[0]");
                print(" === ").print("'" + TYPE_MAPPING.get(type.toString()).toLowerCase() + "'");
            } else if (util().isPartOfAnEnum(typeElement)) {
                print("typeof ");
                print(exprStr, expr);
                if (checkFirstArrayElement)
                    print("[0]");

                boolean isStringEnum = context.hasAnnotationType(typeElement, ANNOTATION_STRING_ENUM);
                if (isStringEnum) {
                    print(" === 'string'");
                } else {
                    print(" === 'number'");
                }
            } else if (type.toString().startsWith(JSweetConfig.FUNCTION_CLASSES_PACKAGE + ".")
                    || type.toString().startsWith("java.util.function.")
                    || Runnable.class.getName().equals(type.toString())
                    || context.hasAnnotationType(typeElement, JSweetConfig.ANNOTATION_FUNCTIONAL_INTERFACE)) {
                print("typeof ");
                print(exprStr, expr);
                if (checkFirstArrayElement)
                    print("[0]");
                print(" === 'function'");
                int parameterCount = context.getFunctionalTypeParameterCount(type);
                if (parameterCount != -1) {
                    print(" && (<any>");
                    print(exprStr, expr);
                    if (checkFirstArrayElement)
                        print("[0]");
                    print(").length === " + context.getFunctionalTypeParameterCount(type));
                }
            } else {
                print(exprStr, expr);
                if (checkFirstArrayElement)
                    print("[0]");
                if (context.isInterface(typeElement)) {
                    String interfaceQualifiedName = util().getQualifiedName(typeElement);

                    print(" != null && ");
                    print("(");

                    // comment
                    print(exprStr, expr);
                    if (checkFirstArrayElement)
                        print("[0]");
                    print(".constructor != null && ");
                    print(exprStr, expr);
                    if (checkFirstArrayElement)
                        print("[0]");
                    print(".constructor[" + getStringLiteralQuote() + INTERFACES_FIELD_NAME + getStringLiteralQuote()
                            + "]").print(" != null && ");
                    print(exprStr, expr);
                    if (checkFirstArrayElement)
                        print("[0]");

                    print(".constructor[" + getStringLiteralQuote() + INTERFACES_FIELD_NAME + getStringLiteralQuote()
                            + "].indexOf(" + getStringLiteralQuote()).print(interfaceQualifiedName)
                                    .print(getStringLiteralQuote() + ") >= 0");

                    if (CharSequence.class.getName().equals(interfaceQualifiedName)) {
                        print(" || typeof ");
                        print(exprStr, expr);
                        if (checkFirstArrayElement)
                            print("[0]");
                        print(" === " + getStringLiteralQuote() + "string" + getStringLiteralQuote());
                    }
                    print(")");
                } else if (typeElement instanceof TypeElement
                        && Class.class.getName().equals(((TypeElement) typeElement).getQualifiedName().toString())) {
                    print(" != null && ");
                    print("(");
                    print(exprStr, expr);
                    if (checkFirstArrayElement)
                        print("[0]");
                    print("[" + getStringLiteralQuote() + CLASS_NAME_IN_CONSTRUCTOR + getStringLiteralQuote() + "]")
                            .print(" != null");
                    print(" || ((t) => { try { new t; return true; } catch { return false; } })(");
                    print(exprStr, expr);
                    if (checkFirstArrayElement)
                        print("[0]");
                    print(")");
                    print(")");
                } else {
                    if (typeElement instanceof TypeParameterElement
                            || Object.class.getName().equals(util().getQualifiedName(typeElement))) {
                        print(" != null");
                    } else {

                        String qualifiedName;
                        if (typeElement == null) {
                            if (type.getKind() == TypeKind.ARRAY) {
                                qualifiedName = "Array";
                            } else {
                                qualifiedName = getAdapter().getMappedType(type);
                            }
                        } else {
                            qualifiedName = getQualifiedTypeName(typeElement, false, false);

                            if (util().isPartOfAnEnum(typeElement)) {
                                qualifiedName += ENUM_WRAPPER_CLASS_SUFFIX;
                            }
                        }

                        if (qualifiedName.startsWith("{")) {
                            qualifiedName = "Object";
                        }
                        print(" != null");
                        if (!"any".equals(qualifiedName)) {
                            print(" && ");
                            print(exprStr, expr);
                            if (checkFirstArrayElement)
                                print("[0]");
                            if (qualifiedName.startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
                                print(" instanceof ").print(qualifiedName);
                            } else {
                                print(" instanceof <any>").print(qualifiedName);
                            }
                            if (type instanceof ArrayType) {
                                ArrayType t = (ArrayType) type;
                                print(" && (");
                                print(exprStr, expr);
                                if (checkFirstArrayElement)
                                    print("[0]");
                                print(".length == 0 || ");
                                print(exprStr, expr);
                                print("[0] == null ||");
                                if (t.getComponentType() instanceof ArrayType) {
                                    print(exprStr, expr);
                                    print("[0] instanceof Array");
                                } else {
                                    printInstanceOf(exprStr, expr, t.getComponentType(), true);
                                }
                                print(")");
                            }
                        }
                    }
                }
            }
        }
        if (!(getParent() instanceof ParenthesizedTree)) {
            print(")");
        }
    }

    /**
     * Prints an <code>instanceof</code> tree.
     */
    @Override
    public Void visitInstanceOf(InstanceOfTree instanceOf, Trees trees) {
        printInstanceOf(null, instanceOf.getExpression(), Util.getType(instanceOf.getType()));
        return returnNothing();
    }

    @Override
    public Void visitThrow(ThrowTree throwTree, Trees trees) {
        print("throw ").print(throwTree.getExpression());
        return returnNothing();
    }

    /**
     * Prints an assert tree.
     */
    @Override
    public Void visitAssert(AssertTree assertion, Trees trees) {
        if (!context.options.isIgnoreAssertions()) {
            String assertCode = assertion.toString().replace("\"", "'");
            print("if (!(").print(assertion.getCondition()).print(
                    ")) { throw new Error(\"Assertion error line " + getCurrentLine() + ": " + assertCode + "\"); }");
        }
        return returnNothing();
    }

    @Override
    public Void visitAnnotation(AnnotationTree annotation, Trees trees) {
        if (!context.hasAnnotationType(Util.getTypeElement(annotation), JSweetConfig.ANNOTATION_DECORATOR)) {
            return returnNothing();
        }
        if (getScope().isInterfaceScope()) {
            return returnNothing();
        }
        print("@").print(annotation.getAnnotationType());
        if (annotation.getArguments() != null && !annotation.getArguments().isEmpty()) {
            print("(");
            isAnnotationScope = true;
            print(" { ");
            for (ExpressionTree e : annotation.getArguments()) {
                print(e);
                print(", ");
            }
            removeLastChars(2);
            print(" } ");
            isAnnotationScope = false;
            print(")");
        } else {
            boolean parens = true;
            if (annotation.getAnnotationType() instanceof IdentifierTree) {
                GlobalMethodInfos globalDecoratorFunction = context
                        .lookupGlobalMethod(Util.getElement(annotation.getAnnotationType()).toString());
                if (globalDecoratorFunction != null) {
                    if (!globalDecoratorFunction.methodTree.getParameters().isEmpty()) {
                        parens = false;
                    }
                }
            }
            if (parens) {
                print("()");
            }
        }
        println().printIndent();

        return returnNothing();
    }

    Stack<TypeMirror> rootConditionalAssignedTypes = new Stack<>();
    Stack<TypeMirror> rootArrayAssignedTypes = new Stack<>();

    @Override
    protected boolean substituteAssignedExpression(TypeMirror assignedType, ExpressionTree expression) {
        if (assignedType == null) {
            return false;
        }
        if (getAdapter().substituteAssignedExpression(assignedType, createExtendedElement(expression))) {
            return true;
        }
        Element expressionTypeElement = Util.getTypeElement(expression);
        TypeMirror expressionType = Util.getType(expression);
        Element assignedTypeElement = types().asElement(assignedType);
        if (util().isInterface(assignedTypeElement) && expressionTypeElement != null
                && util().isPartOfAnEnum(expressionTypeElement)) {
            String relTarget = getRootRelativeName(expressionTypeElement);

            TypeElement targetTypeElement = (TypeElement) expressionTypeElement;
            useModule(getAdapter().getModuleImportDescriptor(getAdapter().getCompilationUnit(),
                    context.getActualName(targetTypeElement), (TypeElement) targetTypeElement));
            print("((wrappers, value) => wrappers===undefined?value:wrappers[value])(")
                    .print(relTarget).print("[" + getStringLiteralQuote()
                            + Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_WRAPPERS + getStringLiteralQuote() + "], ")
                    .print(expression).print(")");
            return true;
        }
        if (expression instanceof ConditionalExpressionTree) {
            rootConditionalAssignedTypes.push(assignedType);
            return false;
        }
        if (expression instanceof NewArrayTree && assignedType instanceof ArrayType) {
            rootArrayAssignedTypes.push(((ArrayType) assignedType).getComponentType());
            return false;
        }
        if (assignedType.getKind() == TypeKind.CHAR && expressionType.getKind() != TypeKind.CHAR) {
            print("String.fromCharCode(").print(expression).print(")");
            return true;
        } else if (util().isNumber(assignedType) && expressionType.getKind() == TypeKind.CHAR) {
            print("(").print(expression).print(").charCodeAt(0)");
            return true;
        } else if (singlePrecisionFloats() && assignedType.getKind() == TypeKind.FLOAT
                && expressionType.getKind() == TypeKind.DOUBLE) {
            print("(<any>Math).fround(").print(expression).print(")");
            return true;
        } else {

            if (expression instanceof LambdaExpressionTree) {
                if (util().isInterface(assignedTypeElement)
                        && !context.isFunctionalType((TypeElement) assignedTypeElement)) {
                    LambdaExpressionTree lambda = (LambdaExpressionTree) expression;
                    ExecutableElement method = (ExecutableElement) assignedTypeElement.getEnclosedElements().get(0);
                    print("{ " + method.getSimpleName() + ": ").print(lambda).print(" }");
                    return true;
                }
            } else if (expression instanceof NewClassTree) {
                NewClassTree newClass = (NewClassTree) expression;
                if (newClass.getClassBody() != null && context.isFunctionalType((TypeElement) assignedTypeElement)) {
                    List<? extends Tree> defs = newClass.getClassBody().getMembers();
                    boolean printed = false;
                    for (Tree def : defs) {
                        if (def instanceof MethodTree) {
                            if (printed) {
                                // should never happen... report error?
                            }
                            MethodTree method = (MethodTree) def;
                            if (Util.getElement(method).getKind() == ElementKind.CONSTRUCTOR) {
                                continue;
                            }
                            getStack().push(method);
                            print("(").printArgList(null, method.getParameters()).print(") => ")
                                    .print(method.getBody());
                            getStack().pop();
                            printed = true;
                        }
                    }
                    if (printed) {
                        return true;
                    }
                } else {
                    // object assignment to functional type
                    if ((newClass.getClassBody() == null
                            && context.isFunctionalType((TypeElement) assignedTypeElement))) {

                        boolean lambdaPrinted = printFunctionalTypeAsLambda((TypeElement) assignedTypeElement, () -> {
                            print("new ").print(newClass.getIdentifier()).print("(");
                            printArgList(null, newClass.getArguments());
                            print(")");
                        });
                        if (lambdaPrinted) {
                            return true;
                        }

                    }
                    // raw generic type
                    TypeElement newClassElement = Util.getTypeElement(newClass);
                    if (!newClassElement.getTypeParameters().isEmpty() && newClass.getTypeArguments().isEmpty()) {
                        print("<any>(").print(expression).print(")");
                        return true;
                    }
                }
            } else if (!(expression instanceof LambdaExpressionTree || expression instanceof MemberReferenceTree)
                    && context.isFunctionalType(assignedTypeElement)) {
                // disallow typing to force objects to be passed as function
                // (may require runtime checks later on)
                print("<any>(");
                printFunctionalTypeAsLambda((TypeElement) assignedTypeElement, () -> print(expression));
                print(")");
                return true;
            } else if (expression instanceof MethodInvocationTree) {
                // disable type checking when the method returns a type variable
                // because it may to be correctly set in the invocation
                ExpressionTree methodSelectTree = ((MethodInvocationTree) expression).getMethodSelect();
                Element m = Util.getElement(methodSelectTree);
                if (m instanceof ExecutableElement) {
                    ExecutableElement methodElement = (ExecutableElement) m;
                    if (methodElement.getReturnType().getKind() == TypeKind.TYPEVAR
                            && types().asElement(methodElement.getReturnType()).getEnclosingElement() == m) {
                        print("<any>(").print(expression).print(")");
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private boolean printFunctionalTypeAsLambda(TypeElement assignedTypeElement, Runnable printInstance) {
        if (context.isFunctionalType(assignedTypeElement)) {
            ExecutableElement method;
            for (Element s : assignedTypeElement.getEnclosedElements()) {
                if (s instanceof ExecutableElement) {
                    // TODO also check that the method is compatible
                    // (here we just apply to the first found
                    // method)
                    method = (ExecutableElement) s;
                    String functionalMethodName = method.getSimpleName().toString();

                    print("((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } ");
                    print("return (");
                    for (VariableElement p : method.getParameters()) {
                        print(p.getSimpleName().toString()).print(", ");
                    }
                    if (!method.getParameters().isEmpty()) {
                        removeLastChars(2);
                    }
                    print(") =>  (funcInst['" + functionalMethodName + "'] ? funcInst['" + functionalMethodName
                            + "'] : funcInst) ");
                    print(".call(funcInst, ");
                    for (VariableElement p : method.getParameters()) {
                        print(p.getSimpleName().toString()).print(", ");
                    }
                    removeLastChar(' ');
                    removeLastChar(',');
                    print(")");
                    print("})(");
                    printInstance.run();
                    print(")");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the qualified name for a given type symbol.
     */
    @Override
    public String getQualifiedTypeName(Element type, boolean globals, boolean ignoreLangTypes) {
        String qualifiedName = super.getQualifiedTypeName(type, globals, ignoreLangTypes);
        String typeName = util().getQualifiedName(type);
        if (!ignoreLangTypes && context.getLangTypeMappings().containsKey(typeName)) {
            qualifiedName = context.getLangTypeMappings().get(typeName);
        } else if (context.isMappedType(typeName)) {
            qualifiedName = context.getTypeMappingTarget(typeName);
            if (qualifiedName.endsWith("<>")) {
                qualifiedName = qualifiedName.substring(0, qualifiedName.length() - 2);
            }
        } else {
            if (context.useModules) {
                String[] namePath = qualifiedName.split("\\.");
                int i = namePath.length - 1;
                Element s = type;
                qualifiedName = "";
                while (i >= 0 && !(s instanceof PackageElement)) {
                    qualifiedName = namePath[i--] + ("".equals(qualifiedName) ? "" : "." + qualifiedName);
                    s = s.getEnclosingElement();
                }
            }
            if (globals) {
                int dotIndex = qualifiedName.lastIndexOf(".");
                if (dotIndex == -1) {
                    qualifiedName = "";
                } else {
                    qualifiedName = qualifiedName.substring(0, dotIndex);
                }
            }
        }
        return qualifiedName;
    }

    private static final Void returnNothing() {
        return null;
    }

    private boolean isStatementWithNoSemiColon(Tree tree) {
        return (tree instanceof IfTree || tree instanceof ForLoopTree || tree instanceof EnhancedForLoopTree
                || tree instanceof SwitchTree || tree instanceof TryTree);
    }

    /**
     * Returns true if this new class expression defines an anonymous class which is
     * contained in a static method.
     */
    private boolean isStaticAnonymousClass(NewClassTree newClass, CompilationUnitTree compilationUnit) {
        if (!context.isAnonymousClass(newClass, compilationUnit)) {
            return false;
        }

        BlockTree parentBlock = getParent(BlockTree.class);
        if (parentBlock != null && parentBlock.isStatic()) {
            return true;
        }

        ExecutableElement parentMethodElement = null;
        Element newClassElement = Util.getElement(newClass);
        do {
            if (newClassElement.getModifiers().contains(Modifier.STATIC)) {
                return false;
            }
            parentMethodElement = util().getParentElement(newClassElement, ExecutableElement.class);
            if (parentMethodElement == null) {
                return false;
            }
            if (parentMethodElement.getModifiers().contains(Modifier.STATIC)) {
                return true;
            }
        } while ((newClassElement = util().getParentElement(parentMethodElement, TypeElement.class)) != null);
        return false;
    }

    class UsedTypesScanner extends TreeScanner<Void, Trees> {

        private HashSet<String> names = new HashSet<>();

        private void checkType(TypeElement type) {
            if (type instanceof TypeElement) {
                String name = type.getSimpleName().toString();
                if (!names.contains(name)) {
                    names.add(name);
                    ModuleImportDescriptor moduleImport = getModuleImportDescriptor(name, (TypeElement) type);
                    if (moduleImport != null) {
                        useModule(false, moduleImport.isDirect(), moduleImport.getTargetPackage(), null,
                                moduleImport.getImportedName(), moduleImport.getPathToImportedClass(),
                                moduleImport.getImportedClass());
                    }
                }
            }
        }

        @Override
        public Void scan(Tree tree, Trees trees) {
            if (tree instanceof ImportTree) {
                return returnNothing();
            }
            if (tree != null) {
                Element treeElement = Util.getElementNoErrors(tree);
                if (!(treeElement instanceof TypeElement)) {
                    treeElement = Util.getTypeElement(tree);
                }
                if (treeElement instanceof TypeElement) {
                    if (!(tree instanceof ParameterizedTypeTree)
                            // hack to include only explicit type declarations in AST
                            && (tree instanceof IdentifierTree
                                    && tree.toString().equals(treeElement.getSimpleName().toString()))) {
                        checkType((TypeElement) treeElement);
                    }
                }
            }
            return super.scan(tree, trees);
        }

    }
}
