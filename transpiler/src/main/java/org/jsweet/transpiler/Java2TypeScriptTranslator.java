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

import static com.google.common.collect.Iterables.getFirst;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jsweet.JSweetConfig.ANNOTATION_ERASED;
import static org.jsweet.JSweetConfig.ANNOTATION_FUNCTIONAL_INTERFACE;
import static org.jsweet.JSweetConfig.ANNOTATION_OBJECT_TYPE;
import static org.jsweet.JSweetConfig.ANNOTATION_STRING_TYPE;
import static org.jsweet.JSweetConfig.GLOBALS_CLASS_NAME;
import static org.jsweet.JSweetConfig.GLOBALS_PACKAGE_NAME;
import static org.jsweet.JSweetConfig.TS_IDENTIFIER_FORBIDDEN_CHARS;
import static org.jsweet.JSweetConfig.TUPLE_CLASSES_PACKAGE;
import static org.jsweet.JSweetConfig.UNION_CLASS_NAME;
import static org.jsweet.JSweetConfig.UTIL_PACKAGE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
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
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.support.ArrayAccessElementSupport;
import org.jsweet.transpiler.model.support.AssignmentElementSupport;
import org.jsweet.transpiler.model.support.AssignmentWithOperatorElementSupport;
import org.jsweet.transpiler.model.support.BinaryOperatorElementSupport;
import org.jsweet.transpiler.model.support.CaseElementSupport;
import org.jsweet.transpiler.model.support.CompilationUnitElementSupport;
import org.jsweet.transpiler.model.support.ExtendedElementSupport;
import org.jsweet.transpiler.model.support.ForeachLoopElementSupport;
import org.jsweet.transpiler.model.support.ImportElementSupport;
import org.jsweet.transpiler.model.support.MethodInvocationElementSupport;
import org.jsweet.transpiler.model.support.NewClassElementSupport;
import org.jsweet.transpiler.model.support.UnaryOperatorElementSupport;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import org.jsweet.transpiler.util.JSDoc;
import org.jsweet.transpiler.util.Util;

import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WildcardTree;

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
			return context.util.getElementForTree(mainMethod, compilationUnit);
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

	private static java.util.List<Class<?>> statementsWithNoSemis = Arrays
			.asList(new Class<?>[] { IfTree.class, ForLoopTree.class, EnhancedForLoopTree.class, SwitchTree.class });

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

	private void useModule(boolean require, PackageElement targetPackage, Tree sourceTree, String targetName,
			String moduleName, Element sourceElement) {
		if (context.useModules) {
			context.packageDependencies.add((PackageElement) targetPackage);
			PackageElement packageElement = toElement(compilationUnit.getPackage());
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

				boolean fullImport = require || GLOBALS_CLASS_NAME.equals(targetName);
				if (fullImport) {
					if (context.useRequireForModules) {
						context.addHeader("import." + targetName,
								"import " + targetName + " = require(\"" + moduleName + "\");\n");
					} else {
						context.addHeader("import." + targetName,
								"import * as " + targetName + " from '" + moduleName + "';\n");
					}
				} else {
					context.addHeader("import." + targetName,
							"import { " + targetName + " } from '" + moduleName + "';\n");
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
		return getAdapter().getModuleImportDescriptor(new CompilationUnitElementSupport(compilationUnit, context),
				importedName, importedClass);
	}

	private boolean isMappedOrErasedType(TypeElement typeElement) {
		return context.isMappedType(typeElement.getQualifiedName().toString())
				|| context.hasAnnotationType(typeElement, JSweetConfig.ANNOTATION_ERASED);
	}

	/**
	 * Prints a compilation unit tree.
	 */
	@Override
	public Void visitCompilationUnit(final CompilationUnitTree compilationUnit, final Trees trees) {

		PackageElement packageElement = toElement(compilationUnit.getPackage());
		if (context.isPackageErased(packageElement)) {
			return returnNothing();
		}

		isDefinitionScope = packageElement.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".");

		if (context.hasAnnotationType(packageElement, JSweetConfig.ANNOTATION_MODULE)) {
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

		String packge = packageElement.toString();

		boolean globalModule = JSweetConfig.GLOBALS_PACKAGE_NAME.equals(packge)
				|| packge.endsWith("." + JSweetConfig.GLOBALS_PACKAGE_NAME);
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

			public Void scan(Tree tree, Trees trees) {
				if (tree != null) {
					stack.push(tree);
					try {
						super.scan(tree, trees);
					} finally {
						stack.pop();
					}
				}
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
				PackageElement compilationUnitPackageElement = toElement(compilationUnit.getPackage());
				Element identifierElement = toElement(identifier);
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
							Element selectElement = toElement(selectTree);
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
						useModule(false, identifierPackage, identifier, identifierPackage.getSimpleName().toString(),
								moduleFile.getPath().replace('\\', '/'), null);
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
							useModule(false, (PackageElement) identifierElement.getEnclosingElement(), identifier,
									JSweetConfig.GLOBALS_PACKAGE_NAME, moduleFile.getPath().replace('\\', '/'), null);
						}
					}
				}
			}

			@Override
			public Void visitMethodInvocation(final MethodInvocationTree invocation, final Trees trees) {
				PackageElement compilationUnitPackageElement = toElement(compilationUnit.getPackage());

				// TODO: same for static variables
				if (invocation.getMethodSelect() instanceof IdentifierTree && JSweetConfig.TS_STRICT_MODE_KEYWORDS
						.contains(invocation.getMethodSelect().toString().toLowerCase())) {

					Element invokedMethodElement = toElement((IdentifierTree) invocation.getMethodSelect());
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
						useModule(false, invocationPackage, invocation, targetRootPackageName,
								moduleFile.getPath().replace('\\', '/'), null);
					}
				}
				super.visitMethodInvocation(invocation, trees);
			}

		};
		// TODO: change the way qualified names are handled (because of new
		// module organization)
		// inlinedModuleScanner.scan(compilationUnit);

		if (!globalModule && !context.useModules) {
			printIndent();
			if (isDefinitionScope) {
				print("declare ");
			}
			print("namespace ").print(rootRelativePackageName).print(" {").startIndent().println();
		}

		for (Tree def : compilationUnit.getTypeDecls()) {
			if (!(def instanceof ClassTree)) {
				print(def);
			}
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

	}

	private void detectAndUseModulesFromReferencedTypes(CompilationUnitTree compilationUnit) {
		if (context.useModules) {
			TreeScanner<Void, Trees> usedTypesScanner = new TreeScanner<>() {

				private HashSet<String> names = new HashSet<>();

				private void checkType(TypeElement type) {
					if (type instanceof TypeElement && !isMappedOrErasedType(type)) {
						String name = type.getSimpleName().toString();
						if (!names.contains(name)) {
							names.add(name);
							ModuleImportDescriptor moduleImport = getModuleImportDescriptor(name, (TypeElement) type);
							if (moduleImport != null) {
								useModule(false, moduleImport.getTargetPackage(), null, moduleImport.getImportedName(),
										moduleImport.getPathToImportedClass(), null);
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
						Element treeElement = toElement(tree);
						if (!(treeElement instanceof TypeElement)) {
							TypeMirror treeType = treeElement.asType();
							if (treeType != null) {
								treeElement = types().asElement(treeType);
							}
						}
						if (treeElement instanceof TypeElement) {
							if (!(tree instanceof ParameterizedTypeTree)) {
								checkType((TypeElement) treeElement);
							}
						}
					}
					super.scan(tree, trees);
				}

			};
			usedTypesScanner.scan(compilationUnit, context.trees);
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
						Element memberSelectElement = toElement(tree);
						if (memberSelectElement != null) {
							// regular import case (qualified.sym is a package)
							if (context.hasAnnotationType(memberSelectElement, JSweetConfig.ANNOTATION_MODULE)) {
								String targetName = createImportAliasFromFieldAccess(qualified);
								String actualName = context.getAnnotationValue(memberSelectElement,
										JSweetConfig.ANNOTATION_MODULE, String.class, null);
								useModule(true, null, importDecl, targetName, actualName,
										((PackageElement) memberSelectElement));
							}
						} else {
							// static import case (imported fields and methods)
							if (qualified.getExpression() instanceof MemberSelectTree) {
								MemberSelectTree qualifier = (MemberSelectTree) qualified.getExpression();
								Element subMemberSelectElement = toElement(qualifier);
								if (subMemberSelectElement != null) {
									try {
										for (Element importedMember : subMemberSelectElement.getEnclosedElements()) {
											if (qualified.getIdentifier().equals(importedMember.getSimpleName())) {
												if (context.hasAnnotationType(importedMember,
														JSweetConfig.ANNOTATION_MODULE)) {
													String targetName = createImportAliasFromSymbol(importedMember);
													String actualName = context.getAnnotationValue(importedMember,
															JSweetConfig.ANNOTATION_MODULE, String.class, null);
													useModule(true, null, importDecl, targetName, actualName,
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
					super.scan(tree, trees);
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
					Element qualifiedElement = toElement(qualified);
					if (qualifiedElement instanceof TypeElement) {
						boolean globals = JSweetConfig.GLOBALS_CLASS_NAME
								.equals(qualifiedElement.getSimpleName().toString());
						if (!globals) {
							importedName = qualified.getIdentifier().toString();
						}
						TypeElement importedClass = (TypeElement) qualifiedElement;
						String qualId = importTree.getQualifiedIdentifier().toString();
						String adaptedQualId = getAdapter()
								.needsImport(new ImportElementSupport(compilationUnit, importTree, context), qualId);
						if (globals || adaptedQualId != null) {
							ModuleImportDescriptor moduleImport = getModuleImportDescriptor(importedName,
									importedClass);
							if (moduleImport != null) {
								useModule(false, moduleImport.getTargetPackage(), importTree,
										moduleImport.getImportedName(), moduleImport.getPathToImportedClass(), null);
							}
						}
					}
				}
			}
		}
	}

	private String createImportAliasFromFieldAccess(MemberSelectTree access) {
		String name = extractNameFromAnnotatedSymbol(toElement(access));
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
			String docComment = trees().getDocComment(trees().getPath(compilationUnit, tree));
			String commentText = JSDoc.adaptDocComment(context, getCompilationUnit(), tree, docComment);

			Element element = toElement(tree);
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
					TypeMirror typeArgType = toType(typeArg);
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

	@SuppressWarnings("unlikely-arg-type")
	private AbstractTreePrinter substituteAndPrintType(Tree typeTree, boolean arrayComponent, boolean inTypeParameters,
			boolean completeRawTypes, boolean disableSubstitution) {

		Element typeElement = toElement(typeTree);
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
			String typeFullName = util().getQualifiedName(typeElement);
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
				TypeElement parametrizedTypeElement = toElement(typeApply.getType());
				String typeName = parametrizedTypeElement.toString();
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
							print("p0 : number");
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
							print("p0 : number");
						} else {
							printArguments(
									typeApply.getTypeArguments().subList(0, typeApply.getTypeArguments().size() - 1));
						}
						print(") => ");
						substituteAndPrintType(
								typeApply.getTypeArguments().get(typeApply.getTypeArguments().size() - 1),
								arrayComponent, inTypeParameters, completeRawTypes, false);
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
							print("p0 : number");
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
				if (typeFullName.startsWith(Class.class.getName() + "<")) {
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
						if (completeRawTypes && !((TypeElement) typeElement).getTypeParameters().isEmpty()
								&& !context.getTypeMappingTarget(typeFullName).equals("any")) {
							printAnyTypeArguments(((TypeElement) typeElement).getTypeParameters().size());
						}
					}
					return this;
				}
			}
			for (BiFunction<ExtendedElement, String, Object> mapping : context.getFunctionalTypeMappings()) {
				Object mapped = mapping.apply(new ExtendedElementSupport<Tree>(compilationUnit, typeTree, context),
						typeFullName);
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
			if (completeRawTypes && ((TypeElement) typeElement).getTypeParameters() != null
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
		TypeElement classTypeElement = toElement(classTree);
		if (context.isIgnored(classTree, compilationUnit)) {
			getAdapter().afterType(classTypeElement);
			return null;
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
				return null;
			}
		}

		enterScope();
		getScope().name = name;

		ClassTree parent = getParent(ClassTree.class);
		List<TypeParameterElement> parentTypeVars = new ArrayList<>();
		if (parent != null) {
			getScope().innerClass = true;
			if (!classTree.getModifiers().getFlags().contains(Modifier.STATIC)) {
				getScope().innerClassNotStatic = true;
				if (parent.getTypeParameters() != null) {
					parentTypeVars.addAll(parent.getTypeParameters().stream()
							.map(t -> (TypeParameterElement) toElement(t)).collect(Collectors.toList()));
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
				return null;
			}
		} else {
			if (context.lookupDecoratorAnnotation(classTypeElement.getQualifiedName().toString()) != null) {
				Tree[] globalDecoratorFunction = context
						.lookupGlobalMethod(classTypeElement.getQualifiedName().toString());
				if (globalDecoratorFunction == null) {
					report(classTree, JSweetProblem.CANNOT_FIND_GLOBAL_DECORATOR_FUNCTION,
							classTypeElement.getQualifiedName());
				} else {
					getScope().decoratorScope = true;
					enter(globalDecoratorFunction[0]);
					print(globalDecoratorFunction[1]);
					exit();
					getScope().decoratorScope = false;
				}
				exitScope();
				return null;
			}
		}

		HashSet<Entry<ClassTree, MethodTree>> defaultMethods = null;
		boolean globals = JSweetConfig.GLOBALS_CLASS_NAME.equals(classTree.getSimpleName().toString());
		if (globals && classTree.getExtendsClause() != null) {
			report(classTree, JSweetProblem.GLOBALS_CLASS_CANNOT_HAVE_SUPERCLASS);
		}
		List<TypeMirror> implementedInterfaces = new ArrayList<>();

		if (!globals) {
			if (classTree.getExtendsClause() != null && JSweetConfig.GLOBALS_CLASS_NAME
					.equals(toElement(classTree.getExtendsClause()).getSimpleName().toString())) {
				report(classTree, JSweetProblem.GLOBALS_CLASS_CANNOT_BE_SUBCLASSED);
				return null;
			}
			if (!(classTree.getKind() == Kind.ENUM && scope.size() > 1 && getScope(1).isComplexEnum)) {
				printDocComment(classTree);
			} else {
				print("/** @ignore */").println().printIndent();
			}
			print(classTree.getModifiers());

			if (!isTopLevelScope() || context.useModules || isAnonymousClass() || isInnerClass() || isLocalClass()) {
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
			} else if (isAnonymousClass() && classTree.getModifiers().getFlags().contains(Modifier.STATIC)) {
				NewClassTree newClass = getScope(1).anonymousClassesConstructors
						.get(getScope(1).anonymousClasses.indexOf(classTree));
				printAnonymousClassTypeArgs(newClass);
			}
			TypeMirror mixin = null;
			if (context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_MIXIN)) {
				mixin = context.getAnnotationValue(classTypeElement, JSweetConfig.ANNOTATION_MIXIN, TypeMirror.class,
						null);
				for (AnnotationMirror c : classTypeElement.getAnnotationMirrors()) {
					if (JSweetConfig.ANNOTATION_MIXIN.equals(c.getAnnotationType().toString())) {

						Entry<? extends ExecutableElement, ? extends AnnotationValue> annotationValuesEntry = getFirst(
								c.getElementValues().entrySet(), null);

						TypeElement valueTypeElement = (TypeElement) types()
								.asElement(annotationValuesEntry.getKey().asType());
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
				TypeElement superTypeElement = toElement(classTree.getExtendsClause());
				TypeMirror superType = superTypeElement.asType();

				boolean removeIterable = false;
				if (context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_SYNTACTIC_ITERABLE)
						&& superTypeElement.getQualifiedName().toString().equals(Iterable.class.getName())) {
					removeIterable = true;
				}

				if (!removeIterable && !JSweetConfig.isJDKReplacementMode()
						&& !(JSweetConfig.OBJECT_CLASSNAME.equals(superType.toString())
								|| Object.class.getName().equals(superType.toString()))
						&& !(mixin != null && context.types.isSameType(mixin, superType))
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

			// print IMPLEMENTS
			if (classTree.getImplementsClause() != null && !classTree.getImplementsClause().isEmpty()
					&& !getScope().enumScope) {
				List<Tree> implementing = new ArrayList<>(classTree.getImplementsClause());

				if (context.hasAnnotationType(classTypeElement, JSweetConfig.ANNOTATION_SYNTACTIC_ITERABLE)) {
					for (Tree implementsTree : classTree.getImplementsClause()) {
						TypeElement implementsElement = toElement(implementsTree);
						if (implementsElement.getQualifiedName().toString().equals(Iterable.class.getName())
								// erase Java interfaces
								|| context.isFunctionalType(implementsElement) //
								|| getAdapter().eraseSuperInterface(classTypeElement, implementsElement)) {
							implementing.remove(implementsTree);
						}
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
						TypeMirror implementsType = toType(implementsTree);
						disableTypeSubstitution = !getAdapter().isSubstituteSuperTypes();
						substituteAndPrintType(implementsTree);
						disableTypeSubstitution = false;
						implementedInterfaces.add(implementsType);
						print(", ");
					}
					removeLastChars(2);
				}
			}
			print(" {").println().startIndent();
		}

		if (getScope().innerClassNotStatic && !getScope().interfaceScope && !getScope().enumScope
				&& !getScope().enumWrapperClassScope) {
			printIndent().print("public " + PARENT_CLASS_FIELD_NAME + ": any;").println();
		}

		if (defaultMethods != null && !defaultMethods.isEmpty()) {
			getScope().defaultMethodScope = true;
			for (Entry<ClassTree, MethodTree> entry : defaultMethods) {

				ExecutableElement methodElement = toElement(entry.getValue());
				ExecutableType methodType = toType(entry.getValue());
				ExecutableElement methodElementMatch = util().findMethodDeclarationInType(classTypeElement,
						entry.getValue().getName().toString(), methodType);
				if (methodElementMatch == null || methodElementMatch == methodElement) {
					getAdapter().typeVariablesToErase
							.addAll(((TypeElement) methodElementMatch.getEnclosingElement()).getTypeParameters());
					printIndent().print(entry.getValue()).println();
					getAdapter().typeVariablesToErase
							.removeAll(((TypeElement) methodElementMatch.getEnclosingElement()).getTypeParameters());
				}
			}
			getScope().defaultMethodScope = false;
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
				VariableElement varElement = toElement(var);
				if (!varElement.getModifiers().contains(Modifier.STATIC) && var.getInitializer() != null) {
					getScope().fieldsWithInitializers.add(var);
				}
			}
		}

		if (!globals && !getScope().enumScope && !context.isInterface(classTypeElement)
				&& context.getStaticInitializerCount(classTypeElement) > 0) {
			printIndent().print("static __static_initialized : boolean = false;").println();
			int liCount = context.getStaticInitializerCount(classTypeElement);
			String prefix = getClassName(classTypeElement) + ".";
			printIndent().print("static __static_initialize() { ");
			print("if(!" + prefix + "__static_initialized) { ");
			print(prefix + "__static_initialized = true; ");
			for (int i = 0; i < liCount; i++) {
				print(prefix + "__static_initializer_" + i + "(); ");
			}
			print("} }").println().println();
			String qualifiedClassName = getQualifiedTypeName(classTypeElement, globals, true);
			context.addTopFooterStatement(
					(isBlank(qualifiedClassName) ? "" : qualifiedClassName + ".__static_initialize();"));
		}

		boolean hasUninitializedFields = false;

		for (Tree def : classTree.getMembers()) {
			if (getScope().interfaceScope) {
				// static interface members are printed in a namespace
				Element memberElement = toElement(def);
				if (memberElement != null && (def instanceof MethodTree || def instanceof VariableTree)
						&& memberElement.getModifiers().contains(Modifier.STATIC)) {
					continue;
				}
			}
			if (getScope().interfaceScope && def instanceof MethodTree) {
				// object method should not be defined otherwise they will have
				// to be implemented
				if (util().isOverridingBuiltInJavaObjectMethod(toElement(def))) {
					continue;
				}
			}
			if (def instanceof ClassTree) {
				// inner types are be printed in a namespace
				continue;
			}
			if (def instanceof VariableTree) {
				if (getScope().enumScope && toElement(def).getKind() != ElementKind.ENUM_CONSTANT) {
					getScope().isComplexEnum = true;
					continue;
				}
				if (!((VariableTree) def).getModifiers().getFlags().contains(Modifier.STATIC)
						&& ((VariableTree) def).getInitializer() == null) {
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
					print(";").println().println();
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
						if (o != null && o.methods.size() > 1 && !o.isValid) {
							if (!methodType.equals(toType(o.coreMethod))) {
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
					TypeElement superTypeElement = toElement(classTree.getExtendsClause());
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
			printIndent().print("public name() : string { return this." + ENUM_WRAPPER_CLASS_NAME + "; }").println();
			printIndent().print("public ordinal() : number { return this." + ENUM_WRAPPER_CLASS_ORDINAL + "; }")
					.println();
		}

		if (getScope().enumScope) {
			removeLastChar().println();
		}

		if (!globals) {
			endIndent().printIndent().print("}");

			if (!getScope().interfaceScope && !getScope().declareClassScope && !getScope().enumScope
					&& !(getScope().enumWrapperClassScope
							&& classTypeElement.getNestingKind() == NestingKind.ANONYMOUS)) {
				if (classTypeElement.getNestingKind() != NestingKind.ANONYMOUS) {
					println().printIndent()
							.print(getScope().enumWrapperClassScope ? classTypeElement.getSimpleName().toString()
									: name)
							.print("[\"" + CLASS_NAME_IN_CONSTRUCTOR + "\"] = ")
							.print("\"" + classTypeElement.getQualifiedName().toString() + "\";");
				}
				Set<String> interfaces = new HashSet<>();
				context.grabSupportedInterfaceNames(interfaces, classTypeElement);
				if (!interfaces.isEmpty()) {
					println().printIndent().print(
							getScope().enumWrapperClassScope ? classTypeElement.getSimpleName().toString() : name)
							.print("[\"" + INTERFACES_FIELD_NAME + "\"] = ");
					print("[");
					for (String itf : interfaces) {
						print("\"").print(itf).print("\",");
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
				Element memberElement = toElement(def);
				if ((def instanceof MethodTree || def instanceof VariableTree)
						&& memberElement.getModifiers().contains(Modifier.STATIC)) {

					if (def instanceof VariableTree && context.hasAnnotationType(memberElement, ANNOTATION_STRING_TYPE,
							JSweetConfig.ANNOTATION_ERASED)) {
						continue;
					}
					if (!nameSpace) {
						nameSpace = true;
						println().println().printIndent();

						if (getIndent() != 0 || context.useModules) {
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
					if (!isTopLevelScope() || context.useModules) {
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
				if (!isTopLevelScope() || context.useModules) {
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
			println().printIndent().print(classTypeElement.getSimpleName().toString())
					.print("[\"" + ENUM_WRAPPER_CLASS_WRAPPERS + "\"] = [");
			int index = 0;
			for (Tree tree : classTree.getMembers()) {
				Element memberElement = toElement(tree);

				if (tree instanceof VariableTree && memberElement.getKind() == ElementKind.ENUM_CONSTANT) {
					VariableTree varDecl = (VariableTree) tree;
					// enum fields are not part of the enum auxiliary class but
					// will initialize the enum values
					NewClassTree newClass = (NewClassTree) varDecl.getInitializer();
					ClassTree clazz = classTree;
					try {
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
						print("\"" + memberElement.getSimpleName().toString() + "\"");
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
			print("];").println();
		}

		if (getScope().mainMethod != null && getScope().mainMethod.getParameters().size() < 2
				&& getScope().getMainMethodElement().getEnclosingElement().equals(classTypeElement)) {
			String mainClassName = getQualifiedTypeName(classTypeElement, globals, true);
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
		ExecutableElement methodElement = toElement(methodDecl);
		String name = context.getActualName(methodElement);
		switch (name) {
		case "<init>":
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
			return name;
		}
	}

	private boolean printCoreMethodDelegate = false;

	protected boolean isDebugMode(MethodTree methodDecl) {
		ExecutableElement methodElement = toElement(methodDecl);
		return methodDecl != null && !getScope().constructor && context.options.isDebugMode()
				&& !(context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_NO_DEBUG) || context
						.hasAnnotationType(methodElement.getEnclosingElement(), JSweetConfig.ANNOTATION_NO_DEBUG));
	}

	private boolean isInterfaceMethod(ClassTree parent, MethodTree method) {
		ExecutableElement methodElement = toElement(method);
		TypeElement parentElement = toElement(parent);
		return context.isInterface(parentElement) && !methodElement.getModifiers().contains(Modifier.STATIC);
	}

	@Override
	public Void visitMethod(final MethodTree methodTree, final Trees trees) {
		ExecutableElement methodElement = toElement(methodTree);

		if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_ERASED)) {
			// erased elements are ignored
			return null;
		}

		ClassTree parent = (ClassTree) getParent();

		long methodStartPosition = util().getStartPosition(methodTree, getCompilationUnit());
		Long parentStartPosition = parent != null ? util().getStartPosition(parent, getCompilationUnit()) : null;

		if (parentStartPosition != null && parentStartPosition.equals(methodStartPosition)
				&& !getScope().enumWrapperClassScope) {
			return null;
		}

		if (JSweetConfig.INDEXED_GET_FUCTION_NAME.equals(methodTree.getName().toString())
				&& methodTree.getParameters().size() == 1) {
			print("[").print(methodTree.getParameters().get(0)).print("]: ");
			substituteAndPrintType(methodTree.getReturnType()).print(";");
			return null;
		}

		getScope().constructor = methodElement.getKind() == ElementKind.CONSTRUCTOR;
		if (getScope().enumScope) {
			if (getScope().constructor) {
				if (parentStartPosition != null && parent.pos != methodTree.pos) {
					getScope().isComplexEnum = true;
				}
			} else {
				getScope().isComplexEnum = true;
			}
			return null;
		}

		// do not generate definition if parent class already declares method to avoid
		// wrong override error with overloads ({ scale(number); scale(number, number);
		// } cannot be overriden with { scale(number) } only)
		if (getScope().isDeclareClassScope() && parent.getExtendsClause() != null
				&& parent.getExtendsClause().type instanceof ClassType) {
			ClassType superClassType = (ClassType) parent.getExtendsClause().type;
			ExecutableElement superMethod = Util.findMethodDeclarationInType(superClassType.tsym,
					methodTree.getName().toString(), (MethodType) methodTree.type);
			if (superMethod != null) {
				return null;
			}
		}

		Overload overload = null;
		boolean inOverload = false;
		boolean inCoreWrongOverload = false;
		if (parent != null) {
			overload = context.getOverload(parent.sym, methodElement);
			inOverload = overload != null && overload.methods.size() > 1;
			if (inOverload) {
				if (!overload.isValid) {
					if (!printCoreMethodDelegate) {
						if (overload.coreMethod.equals(methodTree)) {
							inCoreWrongOverload = true;
							if (!isInterfaceMethod(parent, methodTree) && !methodElement.isConstructor()
									&& parent.sym.equals(overload.coreMethod.sym.getEnclosingElement())) {
								printCoreMethodDelegate = true;
								visitMethodDef(overload.coreMethod);
								println().println().printIndent();
								printCoreMethodDelegate = false;
							}
						} else {
							if (methodElement.isConstructor()) {
								return null;
							}
							boolean addCoreMethod = false;
							addCoreMethod = !overload.printed
									&& overload.coreMethod.sym.getEnclosingElement() != parent.sym
									&& (!overload.coreMethod.sym.getModifiers().contains(Modifier.ABSTRACT)
											|| isInterfaceMethod(parent, methodTree)
											|| !context.types.isSubtype(parent.sym.type,
													overload.coreMethod.sym.getEnclosingElement().type));
							if (!overload.printed && !addCoreMethod && overload.coreMethod.type instanceof MethodType) {
								addCoreMethod = Util.findMethodDeclarationInType(parent.sym,
										methodTree.getName().toString(), (MethodType) overload.coreMethod.type) == null;
							}
							if (addCoreMethod) {
								visitMethodDef(overload.coreMethod);
								overload.printed = true;
								if (!isInterfaceMethod(parent, methodTree)) {
									println().println().printIndent();
								}
							}
							if (isInterfaceMethod(parent, methodTree)) {
								return null;
							}
						}
					}
				} else {
					if (!overload.coreMethod.equals(methodTree)) {
						return null;
					}
				}
			}
		}

		boolean ambient = context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_AMBIENT);

		if (inOverload && !inCoreWrongOverload && (ambient || isDefinitionScope)) {
			// do not generate method stubs for definitions
			return null;
		}

		if (isDebugMode(methodTree)) {
			printMethodModifiers(methodTree, parent, getScope().constructor, inOverload, overload);
			print(getTSMethodName(methodTree)).print("(");
			printArgList(null, methodTree.params);
			print(") : ");
			substituteAndPrintType(methodTree.getReturnType());
			print(" {").println();
			startIndent().printIndent();
			if (!context.types.isSameType(context.symtab.voidType, methodElement.getReturnType())) {
				print("return ");
			}
			print("__debug_exec('" + parent.sym.getQualifiedName() + "', '" + methodTree.getName() + "', ");
			if (!methodTree.params.isEmpty()) {
				print("[");
				for (VariableTree param : methodTree.params) {
					print("'" + param.getName() + "', ");
				}
				removeLastChars(2);
				print("]");
			} else {
				print("undefined");
			}
			print(", this, arguments, ");
			if (methodElement.isStatic()) {
				print(methodElement.getEnclosingElement().getSimpleName().toString());
			} else {
				print("this");
			}
			print("." + GENERATOR_PREFIX + getTSMethodName(methodTree) + "(");
			for (VariableTree param : methodTree.params) {
				print(context.getActualName(param.sym) + ", ");
			}
			if (!methodTree.params.isEmpty()) {
				removeLastChars(2);
			}
			print("));");
			println().endIndent().printIndent();
			print("}").println().println().printIndent();
		}

		print(methodTree.mods);

		if (methodTree.mods.getFlags().contains(Modifier.NATIVE)) {
			if (!getScope().declareClassScope && !ambient && !getScope().interfaceScope) {
				report(methodTree, methodTree.name, JSweetProblem.NATIVE_MODIFIER_IS_NOT_ALLOWED, methodTree.name);
			}
		} else {
			if (getScope().declareClassScope && !getScope().constructor && !getScope().interfaceScope
					&& !methodTree.mods.getFlags().contains(Modifier.DEFAULT)) {
				report(methodTree, methodTree.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodTree.name,
						parent == null ? "<no class>" : parent.name);
			}
		}

		if (methodTree.name.toString().equals("constructor")) {
			report(methodTree, methodTree.name, JSweetProblem.CONSTRUCTOR_MEMBER);
		}
		if (parent != null) {
			VariableElement v = Util.findFieldDeclaration(parent.sym, methodTree.name);
			if (v != null && context.getFieldNameMapping(v) == null) {
				if (isDefinitionScope) {
					return null;
				} else {
					report(methodTree, methodTree.name, JSweetProblem.METHOD_CONFLICTS_FIELD, methodTree.name, v.owner);
				}
			}
		}
		if (JSweetConfig.MAIN_FUNCTION_NAME.equals(methodTree.name.toString())
				&& methodTree.mods.getFlags().contains(Modifier.STATIC)
				&& !context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_DISABLED)) {
			// ignore main methods in inner classes
			if (scope.size() == 1 || (scope.size() == 2 && getScope().enumWrapperClassScope)) {
				getScope().mainMethod = methodTree;
			}
		}

		boolean globals = parent == null ? false : JSweetConfig.GLOBALS_CLASS_NAME.equals(parent.name.toString());
		globals = globals || (getScope().interfaceScope && methodTree.mods.getFlags().contains(Modifier.STATIC));
		if (!(inOverload && !inCoreWrongOverload)) {
			printDocComment(methodTree);
		}

		if (parent == null) {
			printAsyncKeyword(methodTree);

			print("function ");
		} else if (globals) {
			if (getScope().constructor && methodElement.isPrivate() && methodTree.getParameters().isEmpty()) {
				return null;
			}
			if (getScope().constructor) {
				report(methodTree, methodTree.name, JSweetProblem.GLOBAL_CONSTRUCTOR_DEF);
				return null;
			}
			if (context.lookupDecoratorAnnotation((parent.sym.getQualifiedName() + "." + methodTree.name)
					.replace(JSweetConfig.GLOBALS_CLASS_NAME + ".", "")) != null) {
				if (!getScope().decoratorScope) {
					return null;
				}
			}

			if (!methodTree.mods.getFlags().contains(Modifier.STATIC)) {
				report(methodTree, methodTree.name, JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
				return null;
			}

			if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_MODULE)) {
				getContext().addExportedElement(
						context.getAnnotationValue(methodElement, JSweetConfig.ANNOTATION_MODULE, String.class, null),
						methodElement, getCompilationUnit());
			}

			if (context.useModules) {
				if (!methodTree.mods.getFlags().contains(Modifier.PRIVATE)) {
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
				report(methodTree, methodTree.name, JSweetProblem.WRONG_USE_OF_AMBIENT, methodTree.name);
			}
		}
		if (parent == null || !context.isFunctionalType(parent.sym)) {
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
		if ((methodTree.typarams != null && !methodTree.typarams.isEmpty())
				|| (getContext().getWildcards(methodElement) != null)) {
			inTypeParameters = true;
			print("<");
			if (methodTree.typarams != null && !methodTree.typarams.isEmpty()) {
				printArgList(null, methodTree.typarams);
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
			return null;
		}
		if (methodTree.getBody() == null && !(inCoreWrongOverload && !getScope().declareClassScope)
				|| (methodTree.mods.getFlags().contains(Modifier.DEFAULT) && !getScope().defaultMethodScope)) {
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
				if (!methodTree.mods.getFlags().contains(Modifier.STATIC)) {
					report(methodTree, methodTree.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodTree.name,
							parent == null ? "<no class>" : parent.name);
				}
			}
			if (getScope().declareClassScope) {
				if (!getScope().constructor
						|| (methodTree.getBody() != null && methodTree.getBody().getStatements().isEmpty())) {
					report(methodTree, methodTree.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodTree.name,
							parent == null ? "<no class>" : parent.name);
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

					String replacedBody = null;
					if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_REPLACE)) {
						replacedBody = (String) context.getAnnotationValue(methodElement,
								JSweetConfig.ANNOTATION_REPLACE, String.class, null);
					}
					int position = getCurrentPosition();
					if (replacedBody == null || BODY_MARKER.matcher(replacedBody).find()) {
						enter(methodTree.getBody());
						if (!methodTree.getBody().stats.isEmpty()
								&& methodTree.getBody().stats.head.toString().startsWith("super(")) {
							printBlockStatement(methodTree.getBody().stats.head);
							if (parent != null) {
								printInstanceInitialization(parent, methodElement);
							}
							printBlockStatements(methodTree.getBody().stats.tail);
						} else {
							if (parent != null) {
								printInstanceInitialization(parent, methodElement);
							}
							printBlockStatements(methodTree.getBody().stats);
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
							replacedBody = CLASS_NAME_MARKER.matcher(replacedBody)
									.replaceAll(parent.sym.getQualifiedName().toString());
						}
					}
					if (replacedBody != null) {
						if (methodElement.isConstructor()) {
							getScope().hasDeclaredConstructor = true;
						}
						printIndent().print(replacedBody).println();
					}
					endIndent().printIndent().print("}");
				}
			}
		}
	}

	private void printCoreOverloadMethod(MethodTree methodDecl, ClassTree parent, Overload overload) {
		boolean wasPrinted = false;
		for (int i = 0; i < overload.methods.size(); i++) {
			MethodTree method = overload.methods.get(i);
			if (context.isInterface((TypeElement) method.sym.getEnclosingElement())
					&& !method.getModifiers().getFlags().contains(Modifier.DEFAULT)
					&& !method.getModifiers().getFlags().contains(Modifier.STATIC)) {
				continue;
			}
			if (!Util.isParent(parent.sym, (TypeElement) method.sym.getEnclosingElement())) {
				continue;
			}
			if (wasPrinted) {
				print(" else ");
			}
			wasPrinted = true;
			print("if(");
			printMethodParamsTest(overload, method);
			print(") ");
			if (method.sym.isConstructor()) {
				printInlinedMethod(overload, method, methodDecl.getParameters());
			} else {
				if (parent.sym != method.sym.getEnclosingElement() && context
						.getOverload((TypeElement) method.sym.getEnclosingElement(), method.sym).coreMethod == method) {
					print("{").println().startIndent().printIndent();

					if ((method.getBody() == null
							|| (method.mods.getFlags().contains(Modifier.DEFAULT) && !getScope().defaultMethodScope))
							&& !getScope().interfaceScope
							&& method.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
						print(" throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); ");
					} else {
						String tsMethodAccess = getTSMemberAccess(getTSMethodName(methodDecl), true);
						print("super" + tsMethodAccess);
						print("(");
						for (int j = 0; j < method.getParameters().size(); j++) {
							print(avoidJSKeyword(overload.coreMethod.getParameters().get(j).name.toString()))
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
					if (!isGlobalsClassName(parent.name.toString())) {
						if (method.sym.isStatic()) {
							print(getQualifiedTypeName(parent.sym, false, false).toString());
						} else {
							print("this");
						}
						print(".");
					}
					print(getOverloadMethodName(method.sym)).print("(");
					for (int j = 0; j < method.getParameters().size(); j++) {
						print(avoidJSKeyword(overload.coreMethod.getParameters().get(j).name.toString())).print(", ");
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

	protected void printMethodReturnDeclaration(MethodTree methodDecl, boolean inCoreWrongOverload) {
		if (inCoreWrongOverload && !methodDecl.sym.isConstructor()) {
			print(" : any");
		} else {
			if (methodDecl.restype != null && methodDecl.restype.type.getTag() != TypeTag.VOID) {
				print(" : ");

				boolean promisify = isAsyncMethod(methodDecl)
						&& !methodDecl.restype.type.tsym.getQualifiedName().toString().endsWith(".Promise");
				if (promisify) {
					print(" Promise< ");
				}

				substituteAndPrintType(methodDecl.restype);

				if (promisify) {
					print(" > ");
				}
			}
		}
	}

	@Override
	public void visitModifiers(JCModifiers modifiers) {

		// we don't want the abstract keyword in definition files
		if (getScope().isDeclareClassScope() && modifiers.getFlags().contains(Modifier.ABSTRACT)) {
			modifiers.flags ^= Flags.ABSTRACT;
		}

		super.visitModifiers(modifiers);
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
			print(" async ");
		}
	}

	protected boolean isAsyncMethod(MethodTree methodDecl) {
		return context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_ASYNC);
	}

	protected void printMethodArgs(MethodTree methodDecl, Overload overload, boolean inOverload,
			boolean inCoreWrongOverload, ClassScope scope) {
		boolean isWrapped = false;
		if (this.context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_WRAP_PARAMETERS)) {
			isWrapped = true;
		}
		if (isWrapped) {
			print("{");
		}

		if (inCoreWrongOverload) {
			scope.setEraseVariableTypes(true);
		}
		boolean paramPrinted = false;
		if (scope.isInnerClassNotStatic() && methodDecl.sym.isConstructor() && !scope.isEnumWrapperClassScope()) {
			print(PARENT_CLASS_FIELD_NAME + ": any, ");
			paramPrinted = true;
		}
		if (scope.isConstructor() && scope.isEnumWrapperClassScope()) {
			print((isAnonymousClass() ? "" : "protected ") + ENUM_WRAPPER_CLASS_ORDINAL + " : number, ");
			print((isAnonymousClass() ? "" : "protected ") + ENUM_WRAPPER_CLASS_NAME + " : string, ");
			paramPrinted = true;
		}
		int i = 0;
		for (VariableTree param : methodDecl.getParameters()) {
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
			for (VariableTree param : methodDecl.getParameters()) {
				print(param).println(";");
			}
			print("}");
		}
	}

	protected void printMethodModifiers(MethodTree methodDecl, ClassTree parent, boolean constructor,
			boolean inOverload, Overload overload) {
		if (methodDecl.mods.getFlags().contains(Modifier.PUBLIC)
				|| (inOverload && overload.coreMethod.equals(methodDecl))) {
			if (!getScope().interfaceScope) {
				print("public ");
			}
		}
		if (methodDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
			if (!constructor) {
				if (!getScope().innerClass) {
					if (!getScope().interfaceScope) {
						if (!(inOverload && overload.coreMethod.equals(methodDecl) || getScope().hasInnerClass)) {
							print("/*private*/ ");
						}
					} else {
						if (!(inOverload && overload.coreMethod.equals(methodDecl))) {
							report(methodDecl, methodDecl.name, JSweetProblem.INVALID_PRIVATE_IN_INTERFACE,
									methodDecl.name, parent == null ? "<no class>" : parent.name);
						}
					}
				}
			}
		}
		if (methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
			if (!getScope().interfaceScope) {
				print("static ");
			}
		}
		if (methodDecl.mods.getFlags().contains(Modifier.ABSTRACT)) {
			if (!getScope().interfaceScope && !inOverload) {
				print("abstract ");
			}
		}
	}

	protected void printVariableInitialization(ClassTree clazz, VariableTree var) {
		String name = var.getName().toString();
		if (context.getFieldNameMapping(var.sym) != null) {
			name = context.getFieldNameMapping(var.sym);
		} else {
			name = getIdentifier(var.sym);
		}
		if (getScope().innerClassNotStatic && !Util.isConstantOrNullField(var)) {
			if (doesMemberNameRequireQuotes(name)) {
				printIndent().print("this['").print(name).print("'] = ");
			} else {
				printIndent().print("this.").print(name).print(" = ");
			}
			substituteAndPrintAssignedExpression(var.type, var.init);
			print(";").println();
		} else if (var.init == null) {
			if (doesMemberNameRequireQuotes(name)) {
				printIndent().print("if(").print("this['").print(name).print("']").print("===undefined) ")
						.print("this['").print(name).print("'] = ").print(Util.getTypeInitialValue(var.type)).print(";")
						.println();
			} else {
				printIndent().print("if(").print("this.").print(name).print("===undefined) this.").print(name)
						.print(" = ").print(Util.getTypeInitialValue(var.type)).print(";").println();
			}
		}
	}

	protected void printInstanceInitialization(ClassTree clazz, ExecutableElement method) {
		if (method == null || method.isConstructor()) {
			getScope().hasDeclaredConstructor = true;
			// this workaround will not work on all browsers (see
			// https://github.com/Microsoft/TypeScript-wiki/blob/master/Breaking-Changes.md#extending-built-ins-like-error-array-and-map-may-no-longer-work)
			if (context.types.isAssignable(clazz.sym.type, context.symtab.throwableType)) {
				printIndent().print("(<any>Object).setPrototypeOf(this, " + getClassName(clazz.sym) + ".prototype);")
						.println();
			}
			if (getScope().innerClassNotStatic && !getScope().enumWrapperClassScope) {
				printIndent().print("this." + PARENT_CLASS_FIELD_NAME + " = " + PARENT_CLASS_FIELD_NAME + ";")
						.println();
			}
			for (Tree member : clazz.defs) {
				if (member instanceof VariableTree) {
					VariableTree var = (VariableTree) member;
					if (!var.sym.isStatic() && !context.hasAnnotationType(var.sym, JSweetConfig.ANNOTATION_ERASED)) {
						printVariableInitialization(clazz, var);
					}
				} else if (member instanceof BlockTree) {
					BlockTree block = (BlockTree) member;
					if (!block.isStatic()) {
						printIndent().print("(() => {").startIndent().println();
						stack.push(block);
						printBlockStatements(block.stats);
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
				if (method.getParameters().get(j).name.equals(((VariableTree) args.get(j)).name)) {
					continue;
				} else {
					printIndent().print(VAR_DECL_KEYWORD + " ")
							.print(avoidJSKeyword(method.getParameters().get(j).name.toString())).print(" : ")
							.print("any").print(Util.isVarargs(method.getParameters().get(j)) ? "[]" : "").print(" = ")
							.print("__args[" + j + "]").print(";").println();
				}
			} else {
				if (method.getParameters().get(j).name.toString().equals(args.get(j).toString())) {
					continue;
				} else {
					getScope().inlinedConstructorArgs = method.getParameters().stream().map(p -> p.sym.name.toString())
							.collect(Collectors.toList());
					printIndent().print(VAR_DECL_KEYWORD + " ")
							.print(avoidJSKeyword(method.getParameters().get(j).name.toString())).print(" : ")
							.print("any").print(Util.isVarargs(method.getParameters().get(j)) ? "[]" : "").print(" = ")
							.print(args.get(j)).print(";").println();
					getScope().inlinedConstructorArgs = null;
				}
			}
		}
		if (method.getBody() != null) {
			boolean skipFirst = false;
			boolean initialized = false;
			if (!method.getBody().stats.isEmpty() && method.getBody().stats.get(0).toString().startsWith("this(")) {
				skipFirst = true;
				MethodInvocationTree inv = (MethodInvocationTree) ((ExpressionStatementTree) method.getBody().stats
						.get(0)).expr;
				ExecutableElement ms = Util
						.findMethodDeclarationInType((TypeElement) overload.coreMethod.sym.getEnclosingElement(), inv);
				for (MethodTree md : overload.methods) {
					if (md.sym.equals(ms)) {
						printIndent();
						initialized = true;
						printInlinedMethod(overload, md, inv.args);
						println();
					}
				}

			}
			String replacedBody = null;
			if (context.hasAnnotationType(method.sym, JSweetConfig.ANNOTATION_REPLACE)) {
				replacedBody = context.getAnnotationValue(method.sym, JSweetConfig.ANNOTATION_REPLACE, String.class,
						null);
			}
			int position = getCurrentPosition();
			if (replacedBody == null || BODY_MARKER.matcher(replacedBody).find()) {
				enter(method.getBody());
				com.sun.tools.javac.util.List<StatementTree> stats = skipFirst ? method.getBody().stats.tail
						: method.getBody().stats;
				if (!stats.isEmpty() && stats.head.toString().startsWith("super(")) {
					printBlockStatement(stats.head);
					printFieldInitializations();
					if (!initialized) {
						printInstanceInitialization(getParent(ClassTree.class), method.sym);
					}
					if (!stats.tail.isEmpty()) {
						printIndent().print("((").print(") => {").startIndent().println();
						printBlockStatements(stats.tail);
						endIndent().printIndent().print("})(").print(");").println();
					}
				} else {
					if (!initialized) {
						printInstanceInitialization(getParent(ClassTree.class), method.sym);
					}
					if (!stats.isEmpty() || !method.sym.isConstructor()) {
						printIndent();
					}
					if (!method.sym.isConstructor()) {
						print("return <any>");
					}
					if (!stats.isEmpty() || !method.sym.isConstructor()) {
						print("((").print(") => {").startIndent().println();
						printBlockStatements(stats);
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
							.replaceAll(method.sym.getEnclosingElement().getQualifiedName().toString());
				}
			}
			if (replacedBody != null) {
				if (method.sym.isConstructor()) {
					getScope().hasDeclaredConstructor = true;
				}
				printIndent().print(replacedBody).println();
			}
		} else {
			String returnValue = Util.getTypeInitialValue(method.sym.getReturnType());
			if (returnValue != null) {
				print(" return ").print(returnValue).print("; ");
			}
		}
		endIndent().printIndent().print("}");
	}

	private void printFieldInitializations() {
		ClassTree clazz = getParent(ClassTree.class);
		for (Tree t : clazz.getMembers()) {
			if (t instanceof VariableTree && !getScope().fieldsWithInitializers.contains(t)) {
				VariableTree field = (VariableTree) t;
				if (!field.sym.isStatic() && !context.hasAnnotationType(field.sym, JSweetConfig.ANNOTATION_ERASED)) {
					String name = getIdentifier(field.sym);
					if (context.getFieldNameMapping(field.sym) != null) {
						name = context.getFieldNameMapping(field.sym);
					}
					printIndent().print("if(").print("this.").print(name).print("===undefined) ").print("this.")
							.print(name).print(" = ").print(Util.getTypeInitialValue(field.type)).print(";").println();
				}
			}
		}
		for (VariableTree field : getScope().fieldsWithInitializers) {
			if (context.hasAnnotationType(field.sym, JSweetConfig.ANNOTATION_ERASED)) {
				continue;
			}
			String name = getIdentifier(field.sym);
			if (context.getFieldNameMapping(field.sym) != null) {
				name = context.getFieldNameMapping(field.sym);
			}
			printIndent().print("this.").print(name).print(" = ");
			if (!substituteAssignedExpression(field.type, field.init)) {
				print(field.init);
			}
			print(";").println();
		}
	}

	protected void printBlockStatements(List<StatementTree> statements) {
		for (StatementTree statement : statements) {
			if (context.options.isDebugMode()) {
				MethodTree methodDecl = getParent(MethodTree.class);
				if (isDebugMode(methodDecl)) {
					int s = statement.getStartPosition();
					int e = statement.getEndPosition(diagnosticSource.getEndPosTable());
					if (e == -1) {
						e = s;
					}
					printIndent().print("yield { row: ").print("" + diagnosticSource.getLineNumber(s))
							.print(", column: " + diagnosticSource.getColumnNumber(s, false)).print(", statement: \"");
					print(StringEscapeUtils.escapeJson(statement.toString())).print("\"");

					final Stack<List<String>> locals = new Stack<>();
					try {
						new TreeScanner() {
							public void scan(Tree tree) {
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
									locals.peek().add(((VariableTree) tree).name.toString());
								}
								super.scan(tree);
								if (contextChange) {
									locals.pop();
								}
							}

						}.scan(methodDecl.body);
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
		if (!statementsWithNoSemis.contains(statement.getClass())) {
			if (statement instanceof LabeledStatementTree) {
				if (!statementsWithNoSemis.contains(((LabeledStatementTree) statement).body.getClass())) {
					print(";");
				}
			} else {
				print(";");
			}
		}
		println();
	}

	private String getOverloadMethodName(ExecutableElement method) {
		if (method.isConstructor()) {
			return "constructor";
		}
		StringBuilder sb = new StringBuilder(method.getSimpleName().toString());
		sb.append("$");
		for (VariableElement p : method.getParameters()) {
			sb.append(context.types.erasure(p.type).toString().replace('.', '_').replace("[]", "_A"));
			sb.append("$");
		}
		if (!method.getParameters().isEmpty()) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	private void checkType(TypeElement type) {
		if (type instanceof TypeElement && !isMappedOrErasedType(type)) {
			String name = type.getSimpleName().toString();
			ModuleImportDescriptor moduleImport = getModuleImportDescriptor(name, (TypeElement) type);
			if (moduleImport != null) {
				useModule(false, moduleImport.getTargetPackage(), null, moduleImport.getImportedName(),
						moduleImport.getPathToImportedClass(), null);
			}
		}
	}

	private void printMethodParamsTest(Overload overload, MethodTree m) {
		int i = 0;
		for (; i < m.getParameters().size(); i++) {
			print("(");
			printInstanceOf(avoidJSKeyword(overload.coreMethod.getParameters().get(i).name.toString()), null,
					m.getParameters().get(i).type);
			checkType(m.getParameters().get(i).type.tsym);
			print(" || ")
					.print(avoidJSKeyword(overload.coreMethod.getParameters().get(i).name.toString()) + " === null")
					.print(")");
			print(" && ");
		}
		for (; i < overload.coreMethod.getParameters().size(); i++) {
			print(avoidJSKeyword(overload.coreMethod.getParameters().get(i).name.toString())).print(" === undefined");
			print(" && ");
		}
		removeLastChars(4);
	}

	/**
	 * Prints a block tree.
	 */
	@Override
	public void visitBlock(BlockTree block) {
		Tree parent = getParent();
		boolean globals = (parent instanceof ClassTree)
				&& JSweetConfig.GLOBALS_CLASS_NAME.equals(((ClassTree) parent).name.toString());
		boolean initializer = (parent instanceof ClassTree) && !globals;
		if (initializer) {
			if (getScope().interfaceScope) {
				report(block, JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, ((ClassTree) parent).name);
			}
			if (!block.isStatic()) {
				// non-static blocks are initialized in the constructor
				return;
			}

			printStaticInitializer(block);
			return;
		}
		if (!globals) {
			print("{").println().startIndent();
		}
		printBlockStatements(block.stats);
		if (!globals) {
			endIndent().printIndent().print("}");
		}
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
						print("static __static_initializer_" + static_i + "() ");
						print("{");
						println().startIndent();

						printBlockStatements(block.stats);

						endIndent().printIndent();
						print("}");
						break;
					}
					static_i++;
				}
			}
		}
	}

	private String avoidJSKeyword(String name) {
		if (JSweetConfig.JS_KEYWORDS.contains(name)) {
			name = JSweetConfig.JS_KEYWORD_PREFIX + name;
		}
		return name;
	}

	private boolean isLazyInitialized(VariableElement var) {
		return var.isStatic() && context.lazyInitializedStatics.contains(var)
				&& /* enum fields are not lazy initialized */ !(var.isEnum()
						&& var.getEnclosingElement().equals(var.type.tsym));
	}

	/**
	 * Prints a variable declaration tree.
	 */
	@Override
	public void visitVarDef(VariableTree varDecl) {
		if (context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_ERASED)) {
			// erased elements are ignored
			return;
		}
		if (context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_STRING_TYPE)) {
			// string type fields are ignored
			return;
		}

		if (getScope().enumScope) {
			// we print the doc comment for information, but
			// TypeScript-generated cannot be recognized by JSDoc... so this
			// comment will be ignored and shall be inserted in the enum
			// document with a @property annotation
			printDocComment(varDecl, true);
			print(varDecl.name.toString());
			if (varDecl.init instanceof NewClassTree) {
				NewClassTree newClass = (NewClassTree) varDecl.init;
				if (newClass.def != null) {
					initAnonymousClass(newClass);
				}
			}
		} else {
			Tree parent = getParent();

			if (getScope().enumWrapperClassScope && varDecl.sym.getKind() == ElementKind.ENUM_CONSTANT) {
				return;
			}

			String name = getIdentifier(varDecl.sym);
			if (context.getFieldNameMapping(varDecl.sym) != null) {
				name = context.getFieldNameMapping(varDecl.sym);
			}

			boolean confictInDefinitionScope = false;

			if (parent instanceof ClassTree) {
				ExecutableElement m = Util.findMethodDeclarationInType(((ClassTree) parent).sym, name, null);
				if (m != null) {
					if (!isDefinitionScope) {
						report(varDecl, varDecl.name, JSweetProblem.FIELD_CONFLICTS_METHOD, name, m.owner);
					} else {
						confictInDefinitionScope = true;
					}
				}
				if (!getScope().interfaceScope && name.equals("constructor")) {
					report(varDecl, varDecl.name, JSweetProblem.CONSTRUCTOR_MEMBER);
				}
			} else {
				if (!context.useModules) {
					if (context.importedTopPackages.contains(name)) {
						name = "__var_" + name;
					}
				}
				if (JSweetConfig.JS_KEYWORDS.contains(name)) {
					report(varDecl, varDecl.name, JSweetProblem.JS_KEYWORD_CONFLICT, name, name);
					name = JSweetConfig.JS_KEYWORD_PREFIX + name;
				}
			}

			boolean globals = (parent instanceof ClassTree)
					&& JSweetConfig.GLOBALS_CLASS_NAME.equals(((ClassTree) parent).name.toString());

			if (globals && !varDecl.mods.getFlags().contains(Modifier.STATIC)) {
				report(varDecl, varDecl.name, JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
				return;
			}

			globals = globals || (parent instanceof ClassTree
					&& (((ClassTree) parent).sym.isInterface() || getScope().interfaceScope && varDecl.sym.isStatic()));

			if (parent instanceof ClassTree) {
				printDocComment(varDecl);
			}

			print(varDecl.mods);

			if (!globals && parent instanceof ClassTree) {
				if (varDecl.mods.getFlags().contains(Modifier.PUBLIC)) {
					if (!getScope().interfaceScope) {
						print("public ");
					}
				}
				if (varDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
					if (!getScope().interfaceScope) {
						if (!getScope().innerClass && !varDecl.mods.getFlags().contains(Modifier.STATIC)) {
							// cannot keep private fields because they may be
							// accessed in an inner class
							print("/*private*/ ");
						}
					} else {
						report(varDecl, varDecl.name, JSweetProblem.INVALID_PRIVATE_IN_INTERFACE, varDecl.name,
								((ClassTree) parent).name);
					}
				}

				if (varDecl.mods.getFlags().contains(Modifier.STATIC)) {
					if (!getScope().interfaceScope) {
						print("static ");
					}
				}
			}
			if (!getScope().interfaceScope && parent instanceof ClassTree) {
				if (context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_OPTIONAL)) {
					report(varDecl, varDecl.name, JSweetProblem.USELESS_OPTIONAL_ANNOTATION, varDecl.name,
							((ClassTree) parent).name);
				}
			}
			boolean ambient = context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_AMBIENT);
			if (globals || !(parent instanceof ClassTree || parent instanceof MethodTree
					|| parent instanceof LambdaExpressionTree)) {
				if (globals) {
					if (context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_MODULE)) {
						getContext().addExportedElement(context.getAnnotationValue(varDecl.sym,
								JSweetConfig.ANNOTATION_MODULE, String.class, null), varDecl.sym, getCompilationUnit());
					}
					if (context.useModules) {
						if (!varDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
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
					if (isDefinitionScope) {
						print("var ");
					} else {
						print(VAR_DECL_KEYWORD + " ");
					}
				}
			} else {
				if (ambient) {
					report(varDecl, varDecl.name, JSweetProblem.WRONG_USE_OF_AMBIENT, varDecl.name);
				}
			}

			if (Util.isVarargs(varDecl)) {
				print("...");
			}

			if (doesMemberNameRequireQuotes(name)) {
				print("'" + name + "'");
			} else {
				print(name);
			}

			if (!Util.isVarargs(varDecl) && (getScope().isEraseVariableTypes() || (getScope().interfaceScope
					&& context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_OPTIONAL)))) {
				print("?");
			}
			if (!getScope().skipTypeAnnotations && !getScope().enumWrapperClassScope) {
				if (typeChecker.checkType(varDecl, varDecl.name, varDecl.vartype)) {
					print(" : ");
					if (confictInDefinitionScope) {
						print("any");
					} else {
						if (getScope().isEraseVariableTypes()) {
							print("any");
							if (Util.isVarargs(varDecl)) {
								print("[]");
							}
						} else {
							if (context.hasAnnotationType(varDecl.vartype.type.tsym, ANNOTATION_STRING_TYPE)
									&& !varDecl.vartype.type.tsym.isEnum()) {
								print("\"");
								print(context.getAnnotationValue(varDecl.vartype.type.tsym, ANNOTATION_STRING_TYPE,
										String.class, varDecl.vartype.type.tsym.name.toString()).toString());
								print("\"");
							} else {
								substituteAndPrintType(varDecl.vartype);
							}
						}
					}
				}
			}
			if (isLazyInitialized(varDecl.sym)) {
				ClassTree clazz = (ClassTree) parent;
				String prefix = getClassName(clazz.sym);
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
				print(name).print(STATIC_INITIALIZATION_SUFFIX + "() : ");
				substituteAndPrintType(varDecl.vartype);
				print(" { ");
				int liCount = context.getStaticInitializerCount(clazz.sym);
				if (liCount > 0) {
					if (!globals) {
						print(prefix + "__static_initialize(); ");
					}
				}
				if (varDecl.init != null && !isDefinitionScope) {
					print("if(" + prefix).print(name).print(" == null) ").print(prefix).print(name).print(" = ");
					/*
					 * if (getScope().enumWrapperClassScope) { NewClassTree newClass =
					 * (NewClassTree) varDecl.init; print("new "
					 * ).print(clazz.getSimpleName().toString()).print("(") .printArgList(null,
					 * newClass.args).print(")"); } else {
					 */
					if (!substituteAssignedExpression(varDecl.type, varDecl.init)) {
						print(varDecl.init);
					}
					// }
					print("; ");
				}
				print("return ").print(prefix).print(name).print("; }");
				if (!globals) {
					String qualifiedClassName = getQualifiedTypeName(clazz.sym, globals, true);
					context.addTopFooterStatement((isBlank(qualifiedClassName) ? "" : qualifiedClassName + ".") + name
							+ STATIC_INITIALIZATION_SUFFIX + "();");
				}
			} else {
				if (varDecl.init != null && !isDefinitionScope) {
					if (!(parent instanceof ClassTree && getScope().innerClassNotStatic && !varDecl.sym.isStatic()
							&& !Util.isConstantOrNullField(varDecl))) {
						if (!globals && parent instanceof ClassTree && getScope().interfaceScope) {
							report(varDecl, varDecl.name, JSweetProblem.INVALID_FIELD_INITIALIZER_IN_INTERFACE,
									varDecl.name, ((ClassTree) parent).name);
						} else {
							if (!(getScope().hasConstructorOverloadWithSuperClass
									&& getScope().fieldsWithInitializers.contains(varDecl))) {
								print(" = ");
								if (!substituteAssignedExpression(varDecl.type, varDecl.init)) {
									print(varDecl.init);
								}
							}
						}
					}
				}

				// var initialization is not allowed in definition
				if (!isDefinitionScope && !(ambient || (isTopLevelScope() && isDefinitionScope))
						&& varDecl.sym.isStatic() && varDecl.init == null) {
					print(" = ").print(Util.getTypeInitialValue(varDecl.sym.type));
				}
			}
		}
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

	/**
	 * Prints a parenthesis tree.
	 */
	@Override
	public void visitParens(JCParens parens) {
		print("(");
		super.visitParens(parens);
		print(")");
	}

	/**
	 * Prints an <code>import</code> tree.
	 */
	@Override
	public void visitImport(ImportTree importDecl) {
		String qualId = importDecl.getQualifiedIdentifier().toString();
		if (context.useModules && qualId.endsWith("*")
				&& !(qualId.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME + ".*")
						|| qualId.equals(JSweetConfig.UTIL_CLASSNAME + ".*"))) {
			report(importDecl, JSweetProblem.WILDCARD_IMPORT);
			return;
		}
		String adaptedQualId = getAdapter().needsImport(new ImportElementSupport(importDecl), qualId);
		if (adaptedQualId != null && adaptedQualId.contains(".")) {
			if (!(importDecl.isStatic() && !qualId.contains("." + JSweetConfig.GLOBALS_CLASS_NAME + ".")
					&& !qualId.contains("." + JSweetConfig.STRING_TYPES_INTERFACE_NAME + "."))) {
				String[] namePath;
				if (context.useModules && importDecl.isStatic()) {
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
							return;
						}
						context.globalImports.add(name);
					}
					if (!context.useModules) {
						// in bundle mode, we do not use imports to minimize
						// dependencies
						// (imports create unavoidable dependencies!)
						context.importedTopPackages.add(namePath[0]);
					} else {
						print("import ").print(name).print(" = ").print(adaptedQualId).print(";").println();
					}
				}
			}
		}

	}

	private void printInnerClassAccess(String accessedElementName, ElementKind kind) {
		printInnerClassAccess(accessedElementName, kind, null);
	}

	private void printInnerClassAccess(String accessedElementName, ElementKind kind, Integer methodArgsCount) {
		print("this.");
		ClassTree parent = getParent(ClassTree.class);
		int level = 0;
		boolean foundInParent = Util.findFirstDeclarationInClassAndSuperClasses(parent.sym, accessedElementName, kind,
				methodArgsCount) != null;
		if (!foundInParent) {
			while (getScope(level++).innerClassNotStatic) {
				parent = getParent(ClassTree.class, parent);
				if (parent != null && Util.findFirstDeclarationInClassAndSuperClasses(parent.sym, accessedElementName,
						kind, methodArgsCount) != null) {
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

	/**
	 * Prints a field access tree.
	 */
	@Override
	public void visitSelect(MemberSelectTree fieldAccess) {
		if (!getAdapter().substitute(ExtendedElementFactory.INSTANCE.create(fieldAccess))) {
			if (fieldAccess.selected.type.tsym instanceof PackageElement) {
				if (context.isRootPackage(fieldAccess.selected.type.tsym)) {
					if (fieldAccess.type != null && fieldAccess.type.tsym != null) {
						printIdentifier(fieldAccess.type.tsym);
					} else {
						// TODO: see if it breaks something
						print(fieldAccess.name.toString());
					}
					return;
				}
			}

			if ("class".equals(fieldAccess.name.toString())) {
				if (fieldAccess.type instanceof TypeMirror.ClassType
						&& context.isInterface(((TypeMirror.ClassType) fieldAccess.type).typarams_field.head.tsym)) {
					print("\"").print(context.getRootRelativeJavaName(
							((TypeMirror.ClassType) fieldAccess.type).typarams_field.head.tsym)).print("\"");
				} else {
					String name = fieldAccess.selected.type.tsym.toString();
					if (context.isMappedType(name)) {
						String target = context.getTypeMappingTarget(name);
						if (CONSTRUCTOR_TYPE_MAPPING.containsKey(target)) {
							print(mapConstructorType(target));
						} else {
							print("\"")
									.print(context.getRootRelativeJavaName(
											((TypeMirror.ClassType) fieldAccess.type).typarams_field.head.tsym))
									.print("\"");
						}
					} else {
						if (CONSTRUCTOR_TYPE_MAPPING.containsKey(name)) {
							print(mapConstructorType(name));
						} else {
							print(fieldAccess.selected);
						}
					}
				}
			} else if ("this".equals(fieldAccess.name.toString())) {
				print("this");

				if (getScope().innerClassNotStatic) {
					ClassTree parent = getParent(ClassTree.class);
					int level = 0;
					boolean foundInParent = false;
					while (getScope(level++).innerClassNotStatic) {
						parent = getParent(ClassTree.class, parent);
						if (parent != null && parent.sym.equals(fieldAccess.selected.type.tsym)) {
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
				String selected = fieldAccess.selected.toString();
				if (!selected.equals(GLOBALS_CLASS_NAME)) {
					if (selected.equals("super") && (fieldAccess.sym instanceof VariableElement)) {
						print("this.");
					} else if (getScope().innerClassNotStatic
							&& ("this".equals(selected) || selected.endsWith(".this"))) {
						printInnerClassAccess(fieldAccess.name.toString(), ElementKind.FIELD);
					} else {
						boolean accessSubstituted = false;
						if (fieldAccess.sym instanceof VariableElement) {
							VariableElement varSym = (VariableElement) fieldAccess.sym;
							if (varSym.isStatic() && varSym.owner != Util.getAccessedSymbol(fieldAccess.selected)) {
								accessSubstituted = true;
								print(getRootRelativeName(varSym.owner)).print(".");
							}
						}
						if (!accessSubstituted) {
							print(fieldAccess.selected).print(".");
						}
					}
				}

				String fieldName = null;
				if (fieldAccess.sym instanceof VariableElement
						&& context.getFieldNameMapping(fieldAccess.sym) != null) {
					fieldName = context.getFieldNameMapping(fieldAccess.sym);
				} else {
					fieldName = getIdentifier(fieldAccess.sym);
				}
				if (fieldAccess.sym instanceof TypeElement) {
					if (context.hasClassNameMapping(fieldAccess.sym)) {
						fieldName = context.getClassNameMapping(fieldAccess.sym);
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
				if (fieldAccess.sym instanceof VariableElement
						&& isLazyInitialized((VariableElement) fieldAccess.sym)) {
					if (!staticInitializedAssignment) {
						print(STATIC_INITIALIZATION_SUFFIX + "()");
					}
				}
			}
		}
	}

	/**
	 * Prints an invocation tree.
	 */
	@Override
	public void visitApply(MethodInvocationTree inv) {

		boolean debugMode = false;
		if (context.options.isDebugMode()) {
			if (Util.getAccessedSymbol(inv.meth) instanceof ExecutableElement) {
				ExecutableElement methodSymbol = (ExecutableElement) Util.getAccessedSymbol(inv.meth);
				if (!methodSymbol.isConstructor() && Util.isSourceElement(methodSymbol)) {
					debugMode = true;
				}
			}
		}
		if (debugMode) {
			print("__debug_result(yield ");
		}
		getAdapter().substituteMethodInvocation(new MethodInvocationElementSupport(inv));
		if (debugMode) {
			print(")");
		}

	}

	/**
	 * Prints a method invocation tree (default behavior).
	 */
	public void printDefaultMethodInvocation(MethodInvocationTree inv) {
		String meth = inv.meth.toString();
		String methName = meth.substring(meth.lastIndexOf('.') + 1);
		if (methName.equals("super") && getScope().removedSuperclass) {
			return;
		}

		boolean applyVarargs = true;
		if (JSweetConfig.NEW_FUNCTION_NAME.equals(methName)) {
			print("new ");
			applyVarargs = false;
		}

		boolean anonymous = isAnonymousMethod(methName);
		boolean targetIsThisOrStaticImported = /*
												 * !"super".equals(methName) &&
												 */ (meth.equals(methName) || meth.equals("this." + methName));

		MethodType type = inv.meth.type instanceof MethodType ? (MethodType) inv.meth.type : null;
		ExecutableElement methSym = null;
		String methodName = null;
		boolean keywordHandled = false;
		if (targetIsThisOrStaticImported) {
			ImportTree staticImport = getStaticGlobalImport(methName);
			if (staticImport == null) {
				ClassTree p = getParent(ClassTree.class);
				methSym = p == null ? null : Util.findMethodDeclarationInType(p.sym, methName, type);
				if (methSym != null) {
					typeChecker.checkApply(inv, methSym);
					if (!methSym.isStatic()) {
						if (!meth.startsWith("this.")) {
							print("this");
							if (!anonymous) {
								print(".");
							}
						}
					} else {
						if (meth.startsWith("this.") && methSym.isStatic()) {
							report(inv, JSweetProblem.CANNOT_ACCESS_STATIC_MEMBER_ON_THIS, methSym.getSimpleName());
						}
						if (!JSweetConfig.GLOBALS_CLASS_NAME.equals(methSym.owner.getSimpleName().toString())) {
							print("" + methSym.owner.getSimpleName());
							if (methSym.owner.isEnum()) {
								print(Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_SUFFIX);
							}
							if (!anonymous) {
								print(".");
							}
						}
					}
				} else {
					if (getScope().defaultMethodScope) {
						TypeElement target = Util.getStaticImportTarget(
								getContext().getDefaultMethodCompilationUnit(getParent(MethodTree.class)), methName);
						if (target != null) {
							print(getRootRelativeName(target) + ".");
						}
					} else {
						TypeElement target = Util.getStaticImportTarget(getCompilationUnit(), methName);
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
								if ((method = Util.findMethodDeclarationInType(parent.sym, methName, type)) != null) {
									break;
								}
							}
						}
						if (method != null) {
							if (method.isStatic()) {
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
				MemberSelectTree staticFieldAccess = (MemberSelectTree) staticImport.qualid;
				methSym = Util.findMethodDeclarationInType(staticFieldAccess.selected.type.tsym, methName, type);
				if (methSym != null) {
					Map<String, VariableElement> vars = new HashMap<>();
					Util.fillAllVariablesInScope(vars, getStack(), inv, getParent(MethodTree.class));
					if (vars.containsKey(methSym.getSimpleName().toString())) {
						report(inv, JSweetProblem.HIDDEN_INVOCATION, methSym.getSimpleName());
					}
					if (!context.useModules && methSym.owner.getSimpleName().toString().equals(GLOBALS_CLASS_NAME)
							&& methSym.owner.owner != null
							&& !methSym.owner.owner.getSimpleName().toString().equals(GLOBALS_PACKAGE_NAME)) {
						String prefix = getRootRelativeName(methSym.owner.owner);
						if (!StringUtils.isEmpty(prefix)) {
							print(getRootRelativeName(methSym.owner.owner) + ".");
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
					if (JSweetConfig.isLibPath(methSym.getEnclosingElement().getQualifiedName().toString())) {
						methodName = methName.toLowerCase();
					}
				}
			}
		} else {
			if (inv.meth instanceof MemberSelectTree) {
				ExpressionTree selected = ((MemberSelectTree) inv.meth).selected;
				if (context.isFunctionalType(selected.type.tsym)) {
					anonymous = true;
				}
				methSym = Util.findMethodDeclarationInType(selected.type.tsym, methName, type);
				if (methSym != null) {
					typeChecker.checkApply(inv, methSym);
				}
			}
		}

		boolean isStatic = methSym == null || methSym.isStatic();
		if (!Util.hasVarargs(methSym) //
				|| !inv.args.isEmpty() && (inv.args.last().type.getKind() != TypeKind.ARRAY
						// we dont use apply if var args type differ
						|| !context.types.erasure(((ArrayType) inv.args.last().type).elemtype).equals(
								context.types.erasure(((ArrayType) methSym.getParameters().last().type).elemtype)))) {
			applyVarargs = false;
		}

		if (anonymous) {
			applyVarargs = false;
			if (inv.meth instanceof MemberSelectTree) {
				ExpressionTree selected = ((MemberSelectTree) inv.meth).selected;
				print(selected);
			}
		} else {
			// method with name
			if (inv.meth instanceof MemberSelectTree && applyVarargs && !targetIsThisOrStaticImported && !isStatic) {
				print("(o => o");

				String accessedMemberName;
				if (keywordHandled) {
					accessedMemberName = ((MemberSelectTree) inv.meth).name.toString();
				} else {
					if (methSym == null) {
						methSym = (ExecutableElement) ((MemberSelectTree) inv.meth).sym;
					}
					if (methSym != null) {
						accessedMemberName = context.getActualName(methSym);
					} else {
						accessedMemberName = ((MemberSelectTree) inv.meth).name.toString();
					}
				}
				print(getTSMemberAccess(accessedMemberName, true));
			} else if (methodName != null) {
				print(getTSMemberAccess(methodName, removeLastChar('.')));
			} else {
				if (keywordHandled) {
					print(inv.meth);
				} else {
					if (methSym == null && inv.meth instanceof MemberSelectTree
							&& ((MemberSelectTree) inv.meth).sym instanceof ExecutableElement) {
						methSym = (ExecutableElement) ((MemberSelectTree) inv.meth).sym;
					}
					if (methSym != null && inv.meth instanceof MemberSelectTree) {
						ExpressionTree selected = ((MemberSelectTree) inv.meth).selected;
						if (!GLOBALS_CLASS_NAME.equals(selected.type.tsym.getSimpleName().toString())) {
							if (getScope().innerClassNotStatic
									&& ("this".equals(selected.toString()) || selected.toString().endsWith(".this"))) {
								printInnerClassAccess(methSym.name.toString(), methSym.getKind(),
										methSym.getParameters().size());
							} else {
								print(selected).print(".");
							}
						} else {
							if (context.useModules) {
								if (!((TypeElement) selected.type.tsym).sourcefile.getName()
										.equals(getCompilationUnit().sourcefile.getName())) {
									// TODO: when using several qualified
									// Globals classes, we
									// need to disambiguate (use qualified
									// name with
									// underscores)
									print(GLOBALS_CLASS_NAME).print(".");
								}
							}

							Map<String, VariableElement> vars = new HashMap<>();
							Util.fillAllVariablesInScope(vars, getStack(), inv, getParent(MethodTree.class));
							if (vars.containsKey(methName)) {
								report(inv, JSweetProblem.HIDDEN_INVOCATION, methName);
							}
						}
					}
					if (methSym != null) {
						if (context.isInvalidOverload(methSym) && !methSym.getParameters().isEmpty()
								&& !Util.hasTypeParameters(methSym) && !Util.hasVarargs(methSym)
								&& getParent(MethodTree.class) != null
								&& !getParent(MethodTree.class).sym.isDefault()) {
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
						print(inv.meth);
					}
				}
			}
		}

		if (applyVarargs) {
			print(".apply");
		} else {
			if (inv.typeargs != null && !inv.typeargs.isEmpty()) {
				print("<");
				for (ExpressionTree argument : inv.typeargs) {
					substituteAndPrintType(argument).print(",");
				}
				removeLastChar();
				print(">");
			} else {
				// force type arguments to any because they are inferred to
				// {} by default
				if (methSym != null && !methSym.getTypeParameters().isEmpty()) {
					TypeElement target = (TypeElement) methSym.getEnclosingElement();
					if (!target.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
						// invalid overload type parameters are erased
						Overload overload = context.getOverload(target, methSym);
						boolean inOverload = overload != null && overload.methods.size() > 1;
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
			} else if (inv.meth instanceof MemberSelectTree && !targetIsThisOrStaticImported && !isStatic) {
				contextVar = "o";
			}

			print(contextVar + ", ");

			if (inv.args.size() > 1) {
				print("[");
			}
		}

		int argsLength = applyVarargs ? inv.args.size() - 1 : inv.args.size();

		if (getScope().innerClassNotStatic && "super".equals(methName)) {
			TypeElement s = getParent(ClassTree.class).extending.type.tsym;
			if (s.getEnclosingElement() instanceof TypeElement && !s.isStatic()) {
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
			methSym = p == null ? null : Util.findMethodDeclarationInType(p.sym, "this", type);
		}
		for (int i = 0; i < argsLength; i++) {
			ExpressionTree arg = inv.args.get(i);
			if (inv.meth.type != null) {
				// varargs transmission with TS ... notation
				List<TypeMirror> argTypes = ((MethodType) inv.meth.type).argtypes;
				TypeMirror paramType = i < argTypes.size() ? argTypes.get(i) : argTypes.get(argTypes.size() - 1);
				if (i == argsLength - 1 && !applyVarargs && methSym != null && methSym.isVarArgs()) {
					if (arg instanceof IdentifierTree && ((IdentifierTree) arg).sym instanceof VariableElement) {
						VariableElement var = (VariableElement) ((IdentifierTree) arg).sym;
						if (var.owner instanceof ExecutableElement && ((ExecutableElement) var.owner).isVarArgs()
								&& ((ExecutableElement) var.owner).getParameters().last() == var) {
							print("...");
						}
					}
				}
				if (!substituteAssignedExpression(paramType, arg)) {
					print(arg);
				}
			} else {
				// this should never happen but we fall back just in case
				print(arg);
			}
			if (i < argsLength - 1) {
				print(", ");
			}
		}

		if (applyVarargs) {
			if (inv.args.size() > 1) {
				// we cast array to any[] to avoid concat error on
				// different
				// types
				print("].concat(<any[]>");
			}

			print(inv.args.last());

			if (inv.args.size() > 1) {
				print(")");
			}
			if (inv.meth instanceof MemberSelectTree && !targetIsThisOrStaticImported && !isStatic) {
				print("))(").print(((MemberSelectTree) inv.meth).selected);
			}
		}

		print(")");

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
		for (ImportTree i : compilationUnit.getImports()) {
			if (i.staticImport) {
				if (i.qualid.toString().endsWith(JSweetConfig.GLOBALS_CLASS_NAME + "." + methName)) {
					return i;
				}
			}
		}
		return null;
	}

	private String getStaticContainerFullName(ImportTree importDecl) {
		if (importDecl.getQualifiedIdentifier() instanceof MemberSelectTree) {
			MemberSelectTree fa = (MemberSelectTree) importDecl.getQualifiedIdentifier();
			String name = context.getRootRelativeJavaName(fa.selected.type.tsym);
			// function is a top-level global function (no need to import)
			if (JSweetConfig.GLOBALS_CLASS_NAME.equals(name)) {
				return null;
			}
			boolean globals = name.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME);
			if (globals) {
				name = name.substring(0, name.length() - JSweetConfig.GLOBALS_CLASS_NAME.length() - 1);
			}
			// function belong to the current package (no need to import)
			if (compilationUnit.packge.getQualifiedName().toString().startsWith(name)) {
				return null;
			}
			return name;
		}

		return null;
	}

	/**
	 * Prints an identifier.
	 */
	@Override
	public void visitIdent(IdentifierTree ident) {
		String name = ident.toString();

		if (getScope().inlinedConstructorArgs != null) {
			if (ident.sym instanceof VariableElement && getScope().inlinedConstructorArgs.contains(name)) {
				print("__args[" + getScope().inlinedConstructorArgs.indexOf(name) + "]");
				return;
			}
		}

		if (!getAdapter().substitute(ExtendedElementFactory.INSTANCE.create(ident))) {
			boolean lazyInitializedStatic = false;
			// add this of class name if ident is a field
			if (ident.sym instanceof VariableElement && !ident.sym.name.equals(context.names._this)
					&& !ident.sym.name.equals(context.names._super)) {
				VariableElement varSym = (VariableElement) ident.sym; // findFieldDeclaration(currentClass,
				// ident.name);
				if (varSym != null) {
					if (varSym.owner instanceof TypeElement) {
						if (context.getFieldNameMapping(varSym) != null) {
							name = context.getFieldNameMapping(varSym);
						} else {
							name = getIdentifier(varSym);
						}
						if (!varSym.getModifiers().contains(Modifier.STATIC)) {
							printInnerClassAccess(varSym.name.toString(), ElementKind.FIELD);
						} else {
							if (isLazyInitialized(varSym)) {
								lazyInitializedStatic = true;
							}
							if (!varSym.owner.getQualifiedName().toString().endsWith("." + GLOBALS_CLASS_NAME)) {
								if (!context.useModules && !varSym.owner.equals(getParent(ClassTree.class).sym)) {
									String prefix = context.getRootRelativeName(null, varSym.owner);
									if (!StringUtils.isEmpty(prefix)) {
										print(context.getRootRelativeName(null, varSym.owner));
										if (lazyInitializedStatic && varSym.owner.isEnum()) {
											print(ENUM_WRAPPER_CLASS_SUFFIX);
										}
										print(".");
									}
								} else {
									if (!varSym.owner.getSimpleName().toString().equals(GLOBALS_PACKAGE_NAME)) {
										print(varSym.owner.getSimpleName().toString());
										if (lazyInitializedStatic && varSym.owner.isEnum()) {
											print(ENUM_WRAPPER_CLASS_SUFFIX);
										}
										print(".");
									}
								}
							} else {
								if (!context.useModules) {
									String prefix = context.getRootRelativeName(null, varSym.owner);
									prefix = prefix.substring(0, prefix.length() - GLOBALS_CLASS_NAME.length());
									if (!prefix.equals(GLOBALS_PACKAGE_NAME + ".")
											&& !prefix.endsWith("." + GLOBALS_PACKAGE_NAME + ".")) {
										print(prefix);
									}
								}
							}
						}
					} else {
						if (varSym.owner instanceof ExecutableElement && isAnonymousClass()
								&& getScope(1).finalVariables
										.get(getScope(1).anonymousClasses.indexOf(getParent(ClassTree.class)))
										.contains(varSym)) {
							print("this.");
						} else {
							if (!context.useModules && varSym.owner instanceof ExecutableElement) {
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
			if (ident.sym instanceof TypeElement) {
				TypeElement clazz = (TypeElement) ident.sym;
				boolean prefixAdded = false;
				if (getScope().defaultMethodScope) {
					if (Util.isImported(getContext().getDefaultMethodCompilationUnit(getParent(MethodTree.class)),
							clazz)) {
						String rootRelativeName = getRootRelativeName(clazz.getEnclosingElement());
						if (!rootRelativeName.isEmpty()) {
							print(rootRelativeName + ".");
							PackageElement identifierPackage = clazz.packge();
							String pathToModulePackage = Util.getRelativePath(compilationUnit.packge,
									identifierPackage);
							if (pathToModulePackage == null) {
								pathToModulePackage = ".";
							}
							File moduleFile = new File(new File(pathToModulePackage),
									clazz.owner.getSimpleName().toString());
							useModule(false, identifierPackage, ident, clazz.owner.getSimpleName().toString(),
									moduleFile.getPath().replace('\\', '/'), null);
						}
						prefixAdded = true;
					}
				}
				// add parent class name if ident is an inner class of the
				// current class
				if (!prefixAdded && clazz.getEnclosingElement() instanceof TypeElement) {
					if (context.useModules) {
						print(clazz.getEnclosingElement().getSimpleName() + ".");
						prefixAdded = true;
					} else {
						// if the class has not been imported, we need to add
						// the containing class prefix
						if (!getCompilationUnit().getImports().stream()
								.map(i -> i.qualid.type == null ? null : i.qualid.type.tsym)
								.anyMatch(t -> t == clazz)) {
							print(getClassName(clazz.getEnclosingElement()) + ".");
							prefixAdded = true;
						}
					}
				}
				if (!prefixAdded && !context.useModules && !clazz.equals(getParent(ClassTree.class).sym)) {
					print(getRootRelativeName(clazz));
				} else {
					if (context.hasClassNameMapping(ident.sym)) {
						print(context.getClassNameMapping(ident.sym));
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
	}

	/**
	 * Prints a type apply (<code>T<P1,...PN></code>) tree.
	 */
	@Override
	public void visitTypeApply(ParameterizedTypeTree typeApply) {
		substituteAndPrintType(typeApply);
	}

	private int initAnonymousClass(NewClassTree newClass) {
		int anonymousClassIndex = getScope().anonymousClasses.indexOf(newClass.def);
		if (anonymousClassIndex == -1) {
			anonymousClassIndex = getScope().anonymousClasses.size();
			getScope().anonymousClasses.add(newClass.def);
			getScope().anonymousClassesConstructors.add(newClass);
			LinkedHashSet<VariableElement> finalVars = new LinkedHashSet<>();
			getScope().finalVariables.add(finalVars);
			new TreeScanner() {
				public void visitIdent(IdentifierTree var) {
					if (var.sym != null && (var.sym instanceof VariableElement)) {
						VariableElement varSymbol = (VariableElement) var.sym;
						if (varSymbol.getEnclosingElement() instanceof ExecutableElement && varSymbol
								.getEnclosingElement().getEnclosingElement() == getParent(ClassTree.class).sym) {
							finalVars.add((VariableElement) var.sym);
						}
					}
				}
			}.visitClassDef(newClass.def);

		}
		return anonymousClassIndex;
	}

	/**
	 * Prints a new-class expression tree.
	 */
	@Override
	public void visitNewClass(NewClassTree newClass) {
		TypeElement clazz = ((TypeElement) newClass.clazz.type.tsym);
		if (clazz.getSimpleName().toString().equals(JSweetConfig.GLOBALS_CLASS_NAME)) {
			report(newClass, JSweetProblem.GLOBAL_CANNOT_BE_INSTANTIATED);
			return;
		}
		if (getScope().localClasses.stream().map(c -> c.type).anyMatch(t -> t.equals(newClass.type))) {
			print("new ").print(getScope().name + ".").print(newClass.clazz.toString());
			print("(").printConstructorArgList(newClass, true).print(")");
			return;
		}
		boolean isInterface = context.isInterface(clazz);
		if (newClass.def != null || isInterface) {
			if (context.isAnonymousClass(newClass)) {
				int anonymousClassIndex = initAnonymousClass(newClass);
				print("new ").print(getScope().name + "." + getScope().name + ANONYMOUS_PREFIX + anonymousClassIndex);
				if (newClass.def.getModifiers().getFlags().contains(Modifier.STATIC)) {
					printAnonymousClassTypeArgs(newClass);
				}
				print("(").printConstructorArgList(newClass, false).print(")");
				return;
			}

			if (isInterface
					|| context.hasAnnotationType(newClass.clazz.type.tsym, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
				if (isInterface) {
					print("<any>");
				}

				Set<String> interfaces = new HashSet<>();
				context.grabSupportedInterfaceNames(interfaces, clazz);
				if (!interfaces.isEmpty()) {
					print("Object.defineProperty(");
				}
				print("{").println().startIndent();
				boolean statementPrinted = false;
				boolean initializationBlockFound = false;
				if (newClass.def != null) {
					for (Tree m : newClass.def.getMembers()) {
						if (m instanceof BlockTree) {
							initializationBlockFound = true;
							List<VariableElement> initializedVars = new ArrayList<>();
							for (Tree s : ((BlockTree) m).stats) {
								boolean currentStatementPrinted = false;
								if (s instanceof ExpressionStatementTree
										&& ((ExpressionStatementTree) s).expr instanceof AssignmentTree) {
									AssignmentTree assignment = (AssignmentTree) ((ExpressionStatementTree) s).expr;
									VariableElement var = null;
									if (assignment.lhs instanceof MemberSelectTree) {
										var = Util.findFieldDeclaration(clazz,
												((MemberSelectTree) assignment.lhs).name);
										printIndent().print(var.getSimpleName().toString());
									} else if (assignment.lhs instanceof IdentifierTree) {
										var = Util.findFieldDeclaration(clazz, ((IdentifierTree) assignment.lhs).name);
										printIndent().print(assignment.lhs.toString());
									} else {
										continue;
									}
									initializedVars.add(var);
									print(": ").print(assignment.rhs).print(",").println();
									currentStatementPrinted = true;
									statementPrinted = true;
								} else if (s instanceof ExpressionStatementTree
										&& ((ExpressionStatementTree) s).expr instanceof MethodInvocationTree) {
									MethodInvocationTree invocation = (MethodInvocationTree) ((ExpressionStatementTree) s).expr;
									MethodInvocationElement invocationElement = (MethodInvocationElement) ExtendedElementFactory.INSTANCE
											.create(invocation);
									if (invocationElement.getMethodName()
											.equals(JSweetConfig.INDEXED_SET_FUCTION_NAME)) {
										if (invocation.getArguments().size() == 3) {
											if ("this".equals(invocation.getArguments().get(0).toString())) {
												printIndent().print(invocation.args.tail.head).print(": ")
														.print(invocation.args.tail.tail.head).print(",").println();
											}
											currentStatementPrinted = true;
											statementPrinted = true;
										} else {
											printIndent().print(invocation.args.head).print(": ")
													.print(invocation.args.tail.head).print(",").println();
											currentStatementPrinted = true;
											statementPrinted = true;
										}
									}
								}
								if (!currentStatementPrinted) {
									report(s, JSweetProblem.INVALID_INITIALIZER_STATEMENT);
								}
							}
							for (Element s : clazz.getEnclosedElements()) {
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
							if (!method.sym.isConstructor()) {
								printIndent().print(method.getName() + ": (");
								for (VariableTree param : method.getParameters()) {
									print(param.getName() + ", ");
								}
								if (!method.getParameters().isEmpty()) {
									removeLastChars(2);
								}
								print(") => ");
								print(method.body);
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
					for (Element s : clazz.getEnclosedElements()) {
						if (s instanceof VariableElement) {
							if (!context.hasAnnotationType(s, JSweetConfig.ANNOTATION_OPTIONAL)) {
								report(newClass, JSweetProblem.UNINITIALIZED_FIELD, s);
							}
						}
					}
				}

				println().endIndent().printIndent().print("}");
				if (!interfaces.isEmpty()) {
					print(", '" + INTERFACES_FIELD_NAME + "', { configurable: true, value: ");
					print("[");
					for (String i : interfaces) {
						print("\"").print(i).print("\",");
					}
					removeLastChar();
					print("]");
					print(" })");
				}
			} else {

				// ((target : DataStruct3) => {
				// target['i'] = 1;
				// target['s2'] = "";
				// return target
				// })(new DataStruct3());

				print("((target:").print(newClass.clazz).print(") => {").println().startIndent();
				for (Tree m : newClass.def.getMembers()) {
					if (m instanceof BlockTree) {
						for (Tree s : ((BlockTree) m).stats) {
							boolean currentStatementPrinted = false;
							if (s instanceof ExpressionStatementTree
									&& ((ExpressionStatementTree) s).expr instanceof AssignmentTree) {
								AssignmentTree assignment = (AssignmentTree) ((ExpressionStatementTree) s).expr;
								VariableElement var = null;
								if (assignment.lhs instanceof MemberSelectTree) {
									var = Util.findFieldDeclaration(clazz, ((MemberSelectTree) assignment.lhs).name);
									printIndent().print("target['").print(var.getSimpleName().toString()).print("']");
								} else if (assignment.lhs instanceof IdentifierTree) {
									printIndent().print("target['").print(assignment.lhs.toString()).print("']");
								} else {
									continue;
								}
								print(" = ").print(assignment.rhs).print(";").println();
								currentStatementPrinted = true;
							} else if (s instanceof ExpressionStatementTree
									&& ((ExpressionStatementTree) s).expr instanceof MethodInvocationTree) {
								MethodInvocationTree invocation = (MethodInvocationTree) ((ExpressionStatementTree) s).expr;
								MethodInvocationElement invocationElement = (MethodInvocationElement) ExtendedElementFactory.INSTANCE
										.create(invocation);
								if (invocationElement.getMethodName().equals(JSweetConfig.INDEXED_SET_FUCTION_NAME)) {
									if (invocation.getArguments().size() == 3) {
										if ("this".equals(invocation.getArguments().get(0).toString())) {
											printIndent().print("target[").print(invocation.args.tail.head).print("]")
													.print(" = ").print(invocation.args.tail.tail.head).print(";")
													.println();
										}
										currentStatementPrinted = true;
									} else {
										printIndent().print("target[").print(invocation.args.head).print("]")
												.print(" = ").print(invocation.args.tail.head).print(";").println();
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
				print("new ").print(newClass.clazz).print("(").printArgList(null, newClass.args).print("))");
			}
		} else {
			if (context.hasAnnotationType(newClass.clazz.type.tsym, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
				print("{}");
			} else {
				getAdapter().substituteNewClass(new NewClassElementSupport(newClass));
			}
		}

	}

	/**
	 * Prints a new-class expression tree (default behavior).
	 */
	public void printDefaultNewClass(NewClassTree newClass) {
		String mappedType = context.getTypeMappingTarget(newClass.clazz.type.toString());
		if (typeChecker.checkType(newClass, null, newClass.clazz)) {

			boolean applyVarargs = true;
			ExecutableElement methSym = (ExecutableElement) newClass.constructor;
			if (newClass.args.size() == 0 || !Util.hasVarargs(methSym) //
					|| newClass.args.last().type.getKind() != TypeKind.ARRAY
					// we dont use apply if var args type differ
					|| !context.types.erasure(((ArrayType) newClass.args.last().type).elemtype).equals(
							context.types.erasure(((ArrayType) methSym.getParameters().last().type).elemtype))) {
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
					print(newClass.clazz);
				}
				print(", [null");
				for (int i = 0; i < newClass.args.length() - 1; i++) {
					print(", ").print(newClass.args.get(i));
				}
				print("].concat(<any[]>").print(newClass.args.last()).print(")))");
			} else {
				if (newClass.clazz instanceof ParameterizedTypeTree) {
					ParameterizedTypeTree typeApply = (ParameterizedTypeTree) newClass.clazz;
					mappedType = context.getTypeMappingTarget(typeApply.clazz.type.toString());
					print("new ");
					if (mappedType != null) {
						print(Java2TypeScriptTranslator.mapConstructorType(mappedType));
					} else {
						print(typeApply.clazz);
					}
					if (!typeApply.arguments.isEmpty()) {
						print("<").printTypeArgList(typeApply.arguments).print(">");
					} else {
						// erase types since the diamond (<>)
						// operator
						// does not exists in TypeScript
						printAnyTypeArguments(((TypeElement) newClass.clazz.type.tsym).getTypeParameters().length());
					}
					print("(").printConstructorArgList(newClass, false).print(")");
				} else {
					print("new ");
					if (mappedType != null) {
						print(Java2TypeScriptTranslator.mapConstructorType(mappedType));
					} else {
						print(newClass.clazz);
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
		boolean printed = false;
		if (localClass || (getScope().anonymousClasses.contains(newClass.def)
				&& !newClass.def.getModifiers().getFlags().contains(Modifier.STATIC))) {
			print("this");
			if (!newClass.args.isEmpty()) {
				print(", ");
			}
			printed = true;
		} else if ((newClass.clazz.type.tsym.getEnclosingElement() instanceof TypeElement
				&& !newClass.clazz.type.tsym.getModifiers().contains(Modifier.STATIC))) {
			print("this");
			ClassTree parent = getParent(ClassTree.class);
			TypeElement parentSymbol = parent == null ? null : parent.sym;
			if (newClass.clazz.type.tsym.getEnclosingElement() != parentSymbol) {
				print("." + PARENT_CLASS_FIELD_NAME);
			}
			if (!newClass.args.isEmpty()) {
				print(", ");
			}
			printed = true;
		}

		MethodType t = (MethodType) newClass.constructorType;

		printArgList(t == null ? null : t.argtypes, newClass.args);
		int index = getScope().anonymousClasses.indexOf(newClass.def);
		if (index >= 0 && !getScope().finalVariables.get(index).isEmpty()) {
			if (printed || !newClass.args.isEmpty()) {
				print(", ");
			}
			for (VariableElement v : getScope().finalVariables.get(index)) {
				print(v.getSimpleName().toString());
				print(", ");
			}
			removeLastChars(2);
		}
		return this;
	}

	/**
	 * Prints a literal.
	 */
	@Override
	public void visitLiteral(LiteralTree literal) {
		String s = literal.toString();
		switch (literal.typetag) {
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
		print(s);
	}

	/**
	 * Prints an array access tree.
	 */
	@Override
	public void visitIndexed(JCArrayAccess arrayAccess) {
		if (!getAdapter().substituteArrayAccess(new ArrayAccessElementSupport(arrayAccess))) {
			print(arrayAccess.indexed).print("[")
					.substituteAndPrintAssignedExpression(context.symtab.intType, arrayAccess.index).print("]");
		}
	}

	/**
	 * Prints a foreach loop tree.
	 */
	@Override
	public void visitForeachLoop(EnhancedForLoopTree foreachLoop) {
		String indexVarName = "index" + Util.getId();
		boolean[] hasLength = { false };
		TypeElement targetType = foreachLoop.expr.type.tsym;
		Util.scanMemberDeclarationsInType(targetType, getAdapter().getErasedTypes(), element -> {
			if (element instanceof VariableElement) {
				if ("length".equals(element.getSimpleName().toString())
						&& Util.isNumber(((VariableElement) element).type)) {
					hasLength[0] = true;
					return false;
				}
			}
			return true;
		});
		if (!getAdapter().substituteForEachLoop(new ForeachLoopElementSupport(foreachLoop), hasLength[0],
				indexVarName)) {
			boolean noVariable = foreachLoop.expr instanceof IdentifierTree
					|| foreachLoop.expr instanceof MemberSelectTree;
			if (noVariable) {
				print("for(" + VAR_DECL_KEYWORD + " " + indexVarName + "=0; " + indexVarName + " < ")
						.print(foreachLoop.expr).print("." + "length" + "; " + indexVarName + "++) {").println()
						.startIndent().printIndent();
				print(VAR_DECL_KEYWORD + " " + foreachLoop.var.name.toString() + " = ").print(foreachLoop.expr)
						.print("[" + indexVarName + "];").println();
			} else {
				String arrayVarName = "array" + Util.getId();
				print("{").println().startIndent().printIndent();
				print(VAR_DECL_KEYWORD + " " + arrayVarName + " = ").print(foreachLoop.expr).print(";").println()
						.printIndent();
				print("for(" + VAR_DECL_KEYWORD + " " + indexVarName + "=0; " + indexVarName + " < " + arrayVarName
						+ ".length; " + indexVarName + "++) {").println().startIndent().printIndent();
				print(VAR_DECL_KEYWORD + " " + foreachLoop.var.name.toString() + " = " + arrayVarName + "["
						+ indexVarName + "];").println();
			}
			visitBeforeForEachBody(foreachLoop);
			printIndent().print(foreachLoop.body);
			endIndent().println().printIndent().print("}");
			if (!noVariable) {
				endIndent().println().printIndent().print("}");
			}
		}
	}

	protected void visitBeforeForEachBody(EnhancedForLoopTree foreachLoop) {
	}

	/**
	 * Prints a type identifier tree.
	 */
	@Override
	public void visitTypeIdent(JCPrimitiveTypeTree type) {
		switch (type.typetag) {
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
	}

	private boolean singlePrecisionFloats() {
		return !context.options.isDisableSinglePrecisionFloats()
				&& context.options.getEcmaTargetVersion().higherThan(EcmaScriptComplianceLevel.ES3);
	}

	/**
	 * Prints a binary operator tree.
	 */
	@Override
	public void visitBinary(BinaryTree binary) {
		if (!getAdapter().substituteBinaryOperator(new BinaryOperatorElementSupport(binary))) {
			String op = binary.operator.name.toString();
			boolean forceParens = false;
			boolean booleanOp = false;
			if (context.types.isSameType(context.symtab.booleanType,
					context.types.unboxedTypeOrType(binary.lhs.type))) {
				booleanOp = true;
				if ("^".equals(op)) {
					forceParens = true;
				}
				if ("|".equals(op) || "&".equals(op)) {
					print("((lhs, rhs) => lhs " + op + op + " rhs)(").print(binary.lhs).print(", ").print(binary.rhs)
							.print(")");
					return;
				}
			}
			boolean closeParen = false;
			boolean truncate = false;
			if (Util.isIntegral(binary.type) && binary.getKind() == Kind.DIVIDE) {
				if (binary.type.getKind() == TypeKind.LONG) {
					print("(n => n<0?Math.ceil(n):Math.floor(n))(");
					closeParen = true;
				} else {
					print("(");
					truncate = true;
				}
			}
			if (singlePrecisionFloats() && binary.type.getKind() == TypeKind.FLOAT) {
				print("(<any>Math).fround(");
				closeParen = true;
			}
			boolean charWrapping = Util.isArithmeticOrLogicalOperator(binary.getKind())
					|| Util.isComparisonOperator(binary.getKind());
			boolean actualCharWrapping = false;
			if (charWrapping
					&& context.types.isSameType(context.symtab.charType,
							context.types.unboxedTypeOrType(binary.lhs.type))
					&& !(binary.rhs.type.tsym == context.symtab.stringType.tsym)) {
				actualCharWrapping = true;
				if (binary.lhs instanceof LiteralTree) {
					printBinaryLeftOperand(binary);
					print(".charCodeAt(0)");
				} else {
					print("(c => c.charCodeAt==null?<any>c:c.charCodeAt(0))(").print(binary.lhs).print(")");
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
				if (charWrapping
						&& context.types.isSameType(context.symtab.charType,
								context.types.unboxedTypeOrType(binary.rhs.type))
						&& !(binary.lhs.type.tsym == context.symtab.stringType.tsym)) {
					actualCharWrapping = true;
				}
			}

			if (!actualCharWrapping && ("==".equals(op) || "!=".equals(op))) {
				switch (getComparisonMode()) {
				case FORCE_STRICT:
					op += "=";
					break;
				case STRICT:
					if (!(Util.isNullLiteral(binary.lhs) || Util.isNullLiteral(binary.rhs))) {
						op += "=";
					}
					break;
				default:
					break;
				}
			}

			space().print(op).space();
			if (charWrapping
					&& context.types.isSameType(context.symtab.charType,
							context.types.unboxedTypeOrType(binary.rhs.type))
					&& !(binary.lhs.type.tsym == context.symtab.stringType.tsym)) {
				if (binary.rhs instanceof LiteralTree) {
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
	}

	protected void printBinaryRightOperand(BinaryTree binary) {
		print(binary.rhs);
	}

	protected void printBinaryLeftOperand(BinaryTree binary) {
		print(binary.lhs);
	}

	/**
	 * Prints an <code>if</code> tree.
	 */
	@Override
	public void visitIf(IfTree ifStatement) {
		print("if").print(ifStatement.cond).print(" ");
		print(ifStatement.thenpart);
		if (!(ifStatement.thenpart instanceof BlockTree)) {
			if (!statementsWithNoSemis.contains(ifStatement.thenpart.getClass())) {
				print(";");
			}
		}
		if (ifStatement.elsepart != null) {
			print(" else ");
			print(ifStatement.elsepart);
			if (!(ifStatement.elsepart instanceof BlockTree)) {
				if (!statementsWithNoSemis.contains(ifStatement.elsepart.getClass())) {
					print(";");
				}
			}
		}
	}

	/**
	 * Prints a <code>return</code> tree.
	 */
	@Override
	public void visitReturn(ReturnTree returnStatement) {
		print("return");
		if (returnStatement.expr != null) {
			Tree parentFunction = getFirstParent(MethodTree.class, LambdaExpressionTree.class);
			if (returnStatement.expr.type == null) {
				report(returnStatement, JSweetProblem.CANNOT_ACCESS_THIS,
						parentFunction == null ? returnStatement.toString() : parentFunction.toString());
				return;
			}
			print(" ");
			TypeMirror returnType = null;
			if (parentFunction != null) {
				if (parentFunction instanceof MethodTree) {
					returnType = ((MethodTree) parentFunction).restype.type;
				} else {
					// TODO: this cannot work! Calculate the return type of the
					// lambda
					// either from the functional type type arguments, of from
					// the method defining the lambda's signature
					// returnType = ((LambdaExpressionTree) parentFunction).type;
				}
			}
			if (!substituteAssignedExpression(returnType, returnStatement.expr)) {
				print(returnStatement.expr);
			}
		}
	}

	private boolean staticInitializedAssignment = false;

	private VariableElement getStaticInitializedField(Tree expr) {
		if (expr instanceof IdentifierTree) {
			return context.lazyInitializedStatics.contains(((IdentifierTree) expr).sym)
					? (VariableElement) ((IdentifierTree) expr).sym
					: null;
		} else if (expr instanceof MemberSelectTree) {
			return context.lazyInitializedStatics.contains(((MemberSelectTree) expr).sym)
					? (VariableElement) ((MemberSelectTree) expr).sym
					: null;
		} else {
			return null;
		}
	}

	/**
	 * Prints an assignment operator tree (<code>+=, -=, *=, ...</code>).
	 */
	@Override
	public void visitAssignop(CompoundAssignmentTree assignOp) {
		if (!getAdapter().substituteAssignmentWithOperator(new AssignmentWithOperatorElementSupport(assignOp))) {
			boolean expand = staticInitializedAssignment = (getStaticInitializedField(assignOp.lhs) != null);
			boolean expandChar = context.types.isSameType(context.symtab.charType,
					context.types.unboxedTypeOrType(assignOp.lhs.type));
			print(assignOp.lhs);
			staticInitializedAssignment = false;
			String op = assignOp.operator.name.toString();

			if (context.types.isSameType(context.symtab.booleanType,
					context.types.unboxedTypeOrType(assignOp.lhs.type))) {
				if ("|".equals(op)) {
					print(" = ").print(assignOp.rhs).print(" || ").print(assignOp.lhs);
					return;
				} else if ("&".equals(op)) {
					print(" = ").print(assignOp.rhs).print(" && ").print(assignOp.lhs);
					return;
				}
			}

			boolean castToIntegral = "/".equals(op) //
					&& Util.isIntegral(assignOp.lhs.type) //
					&& Util.isIntegral(assignOp.rhs.type);

			if (expandChar) {
				print(" = String.fromCharCode(")
						.substituteAndPrintAssignedExpression(context.symtab.intType, assignOp.lhs)
						.print(" " + op + " ")
						.substituteAndPrintAssignedExpression(context.symtab.intType, assignOp.rhs).print(")");
				return;
			}

			if (expand || castToIntegral) {
				print(" = ");

				if (castToIntegral) {
					print("(n => n<0?Math.ceil(n):Math.floor(n))(");
				}

				print(assignOp.lhs);
				print(" " + op + " ");
				if (context.types.isSameType(context.symtab.charType,
						context.types.unboxedTypeOrType(assignOp.rhs.type))) {
					substituteAndPrintAssignedExpression(context.symtab.intType, assignOp.rhs);
				} else {
					printAssignWithOperatorRightOperand(assignOp);
				}

				if (castToIntegral) {
					print(")");
				}

				return;
			}

			print(" " + op + "= ");
			if (context.types.isSameType(context.symtab.charType, context.types.unboxedTypeOrType(assignOp.rhs.type))) {
				// TypeMirror lhsType = assignOp.lhs.type;
				boolean isLeftOperandString = (assignOp.lhs.type.tsym == context.symtab.stringType.tsym);
				TypeMirror rightPromotedType = isLeftOperandString ? context.symtab.charType : context.symtab.intType;
				substituteAndPrintAssignedExpression(rightPromotedType, assignOp.rhs);
			} else {
				printAssignWithOperatorRightOperand(assignOp);
			}
		}
	}

	protected void printAssignWithOperatorRightOperand(CompoundAssignmentTree assignOp) {
		print(assignOp.rhs);
	}

	/**
	 * Prints a <code>condition?trueExpr:falseExpr</code> tree.
	 */
	@Override
	public void visitConditional(ConditionalExpressionTree conditional) {
		print(conditional.cond);
		print("?");
		if (!substituteAssignedExpression(
				rootConditionalAssignedTypes.isEmpty() ? null : rootConditionalAssignedTypes.peek(),
				conditional.truepart)) {
			print(conditional.truepart);
		}
		print(":");
		if (!substituteAssignedExpression(
				rootConditionalAssignedTypes.isEmpty() ? null : rootConditionalAssignedTypes.peek(),
				conditional.falsepart)) {
			print(conditional.falsepart);
		}
		if (!rootConditionalAssignedTypes.isEmpty()) {
			rootConditionalAssignedTypes.pop();
		}
	}

	/**
	 * Prints a <code>for</code> loop tree.
	 */
	@Override
	public void visitForLoop(ForLoopTree forLoop) {
		print("for(").printArgList(null, forLoop.init).print("; ").print(forLoop.cond).print("; ")
				.printArgList(null, forLoop.step).print(") ");
		print("{");
		visitBeforeForBody(forLoop);
		print(forLoop.body).print(";");
		print("}");
	}

	protected void visitBeforeForBody(ForLoopTree forLoop) {
	}

	/**
	 * Prints a <code>continue</code> tree.
	 */
	@Override
	public void visitContinue(ContinueTree continueStatement) {
		print("continue");
		if (continueStatement.label != null) {
			print(" ").print(continueStatement.label.toString());
		}
	}

	/**
	 * Prints a <code>break</code> tree.
	 */
	@Override
	public void visitBreak(BreakTree breakStatement) {
		print("break");
		if (breakStatement.label != null) {
			print(" ").print(breakStatement.label.toString());
		}
	}

	/**
	 * Prints a labeled statement tree.
	 */
	@Override
	public void visitLabelled(LabeledStatementTree labelledStatement) {
		Tree parent = getParent(MethodTree.class);
		if (parent == null) {
			parent = getParent(BlockTree.class);
			while (parent != null && getParent(BlockTree.class, parent) != null) {
				parent = getParent(BlockTree.class, parent);
			}
		}
		boolean[] used = { false };
		new TreeScanner() {
			public void visitBreak(BreakTree b) {
				if (b.label != null && labelledStatement.label.equals(b.label)) {
					used[0] = true;
				}
			}

			public void visitContinue(JCContinue c) {
				if (c.label != null && labelledStatement.label.equals(c.label)) {
					used[0] = true;
				}
			}
		}.scan(parent);
		if (!used[0]) {
			print("/*");
		}
		print(labelledStatement.label.toString()).print(":");
		if (!used[0]) {
			print("*/");
		}
		print(" ");
		print(labelledStatement.body);
	}

	/**
	 * Prints an array type tree.
	 */
	@Override
	public void visitTypeArray(ArrayTypeTree arrayType) {
		print(arrayType.elemtype).print("[]");
	}

	/**
	 * Prints a new array tree.
	 */
	@Override
	public void visitNewArray(JCNewArray newArray) {
		if (newArray.elemtype != null) {
			typeChecker.checkType(newArray, null, newArray.elemtype);
		}
		if (newArray.dims != null && !newArray.dims.isEmpty()) {
			if (newArray.dims.size() == 1) {
				if (newArray.dims.head instanceof LiteralTree
						&& ((int) ((LiteralTree) newArray.dims.head).value) <= 10) {
					boolean hasElements = false;
					print("[");
					for (int i = 0; i < (int) ((LiteralTree) newArray.dims.head).value; i++) {
						print(Util.getTypeInitialValue(newArray.elemtype.type) + ", ");
						hasElements = true;
					}
					if (hasElements) {
						removeLastChars(2);
					}
					print("]");
				} else {
					print("(s => { let a=[]; while(s-->0) a.push(" + Util.getTypeInitialValue(newArray.elemtype.type)
							+ "); return a; })(").print(newArray.dims.head).print(")");
				}
			} else {
				print("<any> (function(dims) { " + VAR_DECL_KEYWORD
						+ " allocate = function(dims) { if(dims.length==0) { return "
						+ Util.getTypeInitialValue(newArray.elemtype.type) + "; } else { " + VAR_DECL_KEYWORD
						+ " array = []; for(" + VAR_DECL_KEYWORD
						+ " i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})");
				print("([");
				printArgList(null, newArray.dims);
				print("])");
			}
		} else {
			print("[");
			if (newArray.elems != null && !newArray.elems.isEmpty()) {
				for (ExpressionTree e : newArray.elems) {
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
	}

	protected boolean inRollback = false;

	/**
	 * Prints a unary operator tree.
	 */
	@Override
	public void visitUnary(JCUnary unary) {
		if (!getAdapter().substituteUnaryOperator(new UnaryOperatorElementSupport(unary))) {
			if (!inRollback) {
				StatementTree statement = null;
				VariableElement[] staticInitializedField = { null };
				switch (unary.getTag()) {
				case POSTDEC:
				case POSTINC:
				case PREDEC:
				case PREINC:
					staticInitializedAssignment = (staticInitializedField[0] = getStaticInitializedField(
							unary.arg)) != null;
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
						scan(tree);
					});
				}
			} else {
				inRollback = false;
			}
			switch (unary.getTag()) {
			case POS:
				print("+").print(unary.arg);
				break;
			case NEG:
				print("-").print(unary.arg);
				break;
			case POSTDEC:
			case POSTINC:
				print(unary.arg);
				print(unary.operator.name.toString());
				break;
			default:
				print(unary.operator.name.toString());
				print(unary.arg);
				break;
			}
		}
	}

	/**
	 * Prints a <code>switch</code> tree.
	 */
	@Override
	public void visitSwitch(SwitchTree switchStatement) {
		print("switch(");
		print(switchStatement.selector);
		if (context.types.isSameType(context.symtab.charType,
				context.types.unboxedTypeOrType(switchStatement.selector.type))) {
			print(".charCodeAt(0)");
		}
		print(") {").println();
		for (CaseTree caseStatement : switchStatement.cases) {
			printIndent();
			print(caseStatement);
		}
		printIndent().print("}");
	}

	protected void printCaseStatementPattern(ExpressionTree pattern) {
	}

	/**
	 * Prints a <code>case</code> tree.
	 */
	@Override
	public void visitCase(CaseTree caseStatement) {
		if (caseStatement.pat != null) {
			print("case ");
			if (!getAdapter().substituteCaseStatementPattern(new CaseElementSupport(caseStatement),
					ExtendedElementFactory.INSTANCE.create(caseStatement.pat))) {
				if (caseStatement.pat.type.isPrimitive()
						|| context.types.isSameType(context.symtab.stringType, caseStatement.pat.type)) {
					if (caseStatement.pat instanceof IdentifierTree) {
						Object value = ((VariableElement) ((IdentifierTree) caseStatement.pat).sym).getConstValue();
						if (context.types.isSameType(context.symtab.stringType, caseStatement.pat.type)) {
							print("\"" + value + "\" /* " + caseStatement.pat + " */");
						} else {
							print("" + value + " /* " + caseStatement.pat + " */");
						}
					} else {
						if (context.types.isSameType(context.symtab.charType, caseStatement.pat.type)) {
							ExpressionTree caseExpression = caseStatement.pat;
							if (caseExpression instanceof JCTypeCast) {
								caseExpression = ((JCTypeCast) caseExpression).expr;
							}

							if (caseExpression instanceof LiteralTree) {
								print("" + ((LiteralTree) caseExpression).value + " /* " + caseStatement.pat + " */");
							} else {
								print(caseExpression);
							}

						} else {
							print(caseStatement.pat);
						}
					}
				} else {
					print(getRootRelativeName(caseStatement.pat.type.tsym) + "." + caseStatement.pat);
				}
			}
		} else {
			print("default");
		}
		print(":");
		println().startIndent();
		for (StatementTree statement : caseStatement.stats) {
			printIndent();
			print(statement);
			if (!statementsWithNoSemis.contains(statement.getClass())) {
				print(";");
			}
			println();
		}
		endIndent();
	}

	/**
	 * Prints a type cast tree.
	 */
	@Override
	public void visitTypeCast(JCTypeCast cast) {
		if (substituteAssignedExpression(cast.type, cast.expr)) {
			return;
		}
		if (Util.isIntegral(cast.type)) {
			if (cast.type.getKind() == TypeKind.LONG) {
				print("(n => n<0?Math.ceil(n):Math.floor(n))(");
			} else {
				print("(");
			}
		}
		if (!context.hasAnnotationType(cast.clazz.type.tsym, ANNOTATION_ERASED, ANNOTATION_OBJECT_TYPE,
				ANNOTATION_FUNCTIONAL_INTERFACE)) {
			// Java is more permissive than TypeScript when casting type
			// variables
			if (cast.expr.type.getKind() == TypeKind.TYPEVAR) {
				print("<any>");
			} else {
				print("<");
				substituteAndPrintType(cast.clazz).print(">");
				// Java always allows casting when an interface or a type param
				// is involved
				// (that's weak!!)
				if (cast.expr.type.tsym.isInterface() || cast.type.tsym.isInterface()
						|| cast.clazz.type.getKind() == TypeKind.TYPEVAR) {
					print("<any>");
				}
			}
		}
		print(cast.expr);
		if (Util.isIntegral(cast.type)) {
			if (cast.type.getKind() == TypeKind.LONG) {
				print(")");
			} else {
				print("|0)");
			}
		}
	}

	/**
	 * Prints a <code>do - while</code> loop tree.
	 */
	@Override
	public void visitDoLoop(DoWhileLoopTree doWhileLoop) {
		print("do ");
		print("{");
		visitBeforeDoWhileBody(doWhileLoop);
		if (doWhileLoop.body instanceof BlockTree) {
			print(doWhileLoop.body);
		} else {
			print(doWhileLoop.body).print(";");
		}
		print("}");
		print(" while(").print(doWhileLoop.cond).print(")");
	}

	protected void visitBeforeDoWhileBody(DoWhileLoopTree doWhileLoop) {
	}

	/**
	 * Prints a <code>while</code> loop tree.
	 */
	@Override
	public void visitWhileLoop(WhileLoopTree whileLoop) {
		print("while(").print(whileLoop.cond).print(") ");
		print("{");
		visitBeforeWhileBody(whileLoop);
		print(whileLoop.body);
		print("}");
	}

	protected void visitBeforeWhileBody(WhileLoopTree whileLoop) {
	}

	/**
	 * Prints a variable assignment tree.
	 */
	@Override
	public void visitAssign(AssignmentTree assign) {
		if (!getAdapter().substituteAssignment(new AssignmentElementSupport(assign))) {
			staticInitializedAssignment = getStaticInitializedField(assign.lhs) != null;
			print(assign.lhs).print(isAnnotationScope ? ": " : " = ");
			if (!substituteAssignedExpression(assign.lhs.type, assign.rhs)) {
				print(assign.rhs);
			}
			staticInitializedAssignment = false;
		}
	}

	/**
	 * Prints a <code>try</code> tree.
	 */
	@Override
	public void visitTry(TryTree tryStatement) {
		boolean resourced = tryStatement.resources != null && !tryStatement.resources.isEmpty();
		if (resourced) {
			for (Tree resource : tryStatement.resources) {
				print(resource).println(";").printIndent();
			}
		} else if (tryStatement.catchers.isEmpty() && tryStatement.finalizer == null) {
			report(tryStatement, JSweetProblem.TRY_WITHOUT_CATCH_OR_FINALLY);
		}
		print("try ").print(tryStatement.body);
		if (tryStatement.catchers.size() > 1) {
			print(" catch(__e) {").startIndent();
			for (CatchTree catcher : tryStatement.catchers) {
				println().printIndent().print("if");
				printInstanceOf("__e", null, catcher.param.type);
				print(" {").startIndent().println().printIndent();
				// if (!context.options.isUseJavaApis() &&
				// catcher.param.type.toString().startsWith("java.")) {
				// print(catcher.param).print(" = ").print("__e;").println();
				// } else {
				print(catcher.param).print(" = <");
				substituteAndPrintType(catcher.param.getType());
				print(">__e;").println();
				// }
				printBlockStatements(catcher.body.getStatements());
				endIndent().println().printIndent().print("}");
			}
			endIndent().println().printIndent().print("}");
		} else if (tryStatement.catchers.size() == 1) {
			print(tryStatement.catchers.head);
		}
		if (resourced || tryStatement.finalizer != null) {
			print(" finally {");
			if (resourced) {
				// resources are closed in reverse order, before finally block is executed
				startIndent();
				for (Tree resource : tryStatement.resources.reverse()) {
					if (resource instanceof VariableTree) {
						println().printIndent().print(((VariableTree) resource).name + ".close();");
					}
				}
				endIndent();
			}
			if (tryStatement.finalizer != null) {
				startIndent();// .printIndent();
				for (StatementTree statement : tryStatement.finalizer.getStatements()) {
					println().printIndent().print(statement).print(";");
				}
				endIndent();
			}
			println().printIndent().print("}"); // closes finally block
		}
	}

	/**
	 * Prints a <code>catch</code> tree.
	 */
	@Override
	public void visitCatch(CatchTree catcher) {
		print(" catch(").print(catcher.param.name.toString()).print(") ");
		print(catcher.body);
	}

	/**
	 * Prints a lambda expression tree.
	 */
	@Override
	public void visitLambda(LambdaExpressionTree lamba) {
		boolean regularFunction = false;
		if (getParent() instanceof MethodInvocationTree
				&& ((MethodInvocationTree) getParent()).meth.toString().endsWith("function")
				&& getParentOfParent() instanceof MethodInvocationTree
				&& ((MethodInvocationTree) getParentOfParent()).meth.toString().endsWith("$noarrow")) {
			MethodInvocationElement invocation = (MethodInvocationElement) ExtendedElementFactory.INSTANCE
					.create(getParent());
			if (JSweetConfig.UTIL_CLASSNAME.equals(invocation.getMethod().getEnclosingElement().toString())) {
				regularFunction = true;
			}
		}
		Map<String, VariableElement> varAccesses = new HashMap<>();
		Util.fillAllVariableAccesses(varAccesses, lamba);
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
				Util.fillAllVariablesInScope(varDefs, getStack(), lamba, getStack().get(i));
			}
			finalVars.retainAll(varDefs.values());
		}
		if (!finalVars.isEmpty()) {
			print("((");
			for (VariableElement var : finalVars) {
				print(var.name.toString()).print(",");
			}
			removeLastChar();
			print(") => {").println().startIndent().printIndent().print("return ");
		}
		getScope().skipTypeAnnotations = true;
		if (regularFunction) {
			print("function(").printArgList(null, lamba.params).print(") ");
		} else {
			print("(").printArgList(null, lamba.params).print(") => ");
		}
		getScope().skipTypeAnnotations = false;
		print(lamba.body);

		if (!finalVars.isEmpty()) {
			endIndent().println().printIndent().print("})(");
			for (VariableElement var : finalVars) {
				print(var.name.toString()).print(",");
			}
			removeLastChar();
			print(")");
		}
	}

	/**
	 * Prints a reference tree.
	 */
	@Override
	public void visitReference(JCMemberReference memberReference) {
		String memberReferenceSimpleName = memberReference.expr.type.tsym.getSimpleName().toString();
		boolean printAsInstanceMethod = !memberReference.sym.isStatic()
				&& !"<init>".equals(memberReference.name.toString())
				&& !JSweetConfig.GLOBALS_CLASS_NAME.equals(memberReferenceSimpleName);
		boolean exprIsInstance = memberReference.expr.toString().equals("this")
				|| memberReference.expr.toString().equals("super")
				|| (memberReference.expr instanceof IdentifierTree
						&& ((IdentifierTree) memberReference.expr).sym instanceof VariableElement)
				|| (memberReference.expr instanceof MemberSelectTree
						&& ((MemberSelectTree) memberReference.expr).sym instanceof VariableElement);

		if (memberReference.sym instanceof ExecutableElement) {
			ExecutableElement method = (ExecutableElement) memberReference.sym;
			if (getParent() instanceof JCTypeCast) {
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
					print(var.name.toString());
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

		if (JSweetConfig.GLOBALS_CLASS_NAME.equals(memberReference.expr.type.tsym.getSimpleName().toString())) {
			print(memberReference.name.toString());
		} else {
			if ("<init>".equals(memberReference.name.toString())) {
				if (context.types.isArray(memberReference.expr.type)) {
					print("new Array<");
					substituteAndPrintType(((ArrayTypeTree) memberReference.expr).elemtype);
					print(">");
				} else {
					print("new ").print(memberReference.expr);
				}
			} else {
				if (printAsInstanceMethod && !exprIsInstance) {
					print("instance$").print(memberReferenceSimpleName);
				} else {
					print(memberReference.expr);
				}
				print(".").print(memberReference.name.toString());
			}
		}

		if (memberReference.sym instanceof ExecutableElement) {
			ExecutableElement method = (ExecutableElement) memberReference.sym;

			print("(");
			if (method.getParameters() != null) {
				for (VariableElement var : method.getParameters()) {
					print(var.name.toString());
					print(",");
				}
				if (!method.getParameters().isEmpty()) {
					removeLastChar();
				}
			}
			print(")");
			print(" }");
			if (getParent() instanceof JCTypeCast) {
				print(")");
			}
		}

	}

	/**
	 * Prints a type parameter tree.
	 */
	@Override
	public void visitTypeParameter(JCTypeParameter typeParameter) {
		print(typeParameter.name.toString());
		if (typeParameter.bounds != null && !typeParameter.bounds.isEmpty()) {
			print(" extends ");
			for (ExpressionTree e : typeParameter.bounds) {
				substituteAndPrintType(e).print(" & ");
			}
			removeLastChars(3);
		}
	}

	/** Prints a <code>synchronized</code> tree. */
	@Override
	public void visitSynchronized(SynchronizedTree sync) {
		report(sync, JSweetProblem.SYNCHRONIZATION);
		if (sync.body != null) {
			print(sync.body);
		}
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
		if (!(getParent() instanceof JCParens)) {
			print("(");
		}
		if (checkFirstArrayElement
				|| !getAdapter().substituteInstanceof(exprStr, ExtendedElementFactory.INSTANCE.create(expr), type)) {
			if (TYPE_MAPPING.containsKey(type.toString())) {
				print("typeof ");
				print(exprStr, expr);
				if (checkFirstArrayElement)
					print("[0]");
				print(" === ").print("'" + TYPE_MAPPING.get(type.toString()).toLowerCase() + "'");
			} else if (type.tsym.isEnum()) {
				print("typeof ");
				print(exprStr, expr);
				if (checkFirstArrayElement)
					print("[0]");
				print(" === 'number'");
			} else if (type.toString().startsWith(JSweetConfig.FUNCTION_CLASSES_PACKAGE + ".")
					|| type.toString().startsWith("java.util.function.")
					|| Runnable.class.getName().equals(type.toString())
					|| context.hasAnnotationType(type.tsym, JSweetConfig.ANNOTATION_FUNCTIONAL_INTERFACE)) {
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
					print(").length == " + context.getFunctionalTypeParameterCount(type));
				}
			} else {
				print(exprStr, expr);
				if (checkFirstArrayElement)
					print("[0]");
				if (context.isInterface(type.tsym)) {
					print(" != null && ");
					print("(");
					print(exprStr, expr);
					if (checkFirstArrayElement)
						print("[0]");
					print("[\"" + INTERFACES_FIELD_NAME + "\"]").print(" != null && ");
					print(exprStr, expr);
					if (checkFirstArrayElement)
						print("[0]");
					print("[\"" + INTERFACES_FIELD_NAME + "\"].indexOf(\"")
							.print(type.tsym.getQualifiedName().toString()).print("\") >= 0");
					print(" || ");
					print(exprStr, expr);
					if (checkFirstArrayElement)
						print("[0]");
					print(".constructor != null && ");
					print(exprStr, expr);
					if (checkFirstArrayElement)
						print("[0]");
					print(".constructor[\"" + INTERFACES_FIELD_NAME + "\"]").print(" != null && ");
					print(exprStr, expr);
					if (checkFirstArrayElement)
						print("[0]");
					print(".constructor[\"" + INTERFACES_FIELD_NAME + "\"].indexOf(\"")
							.print(type.tsym.getQualifiedName().toString()).print("\") >= 0");
					if (CharSequence.class.getName().equals(type.tsym.getQualifiedName().toString())) {
						print(" || typeof ");
						print(exprStr, expr);
						if (checkFirstArrayElement)
							print("[0]");
						print(" === \"string\"");
					}
					print(")");
				} else {
					if (type.tsym instanceof TypeVariableSymbol
							|| Object.class.getName().equals(type.tsym.getQualifiedName().toString())) {
						print(" != null");
					} else {
						String qualifiedName = getQualifiedTypeName(type.tsym, false, false);
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
								print(".length==0 || ");
								print(exprStr, expr);
								print("[0] == null ||");
								if (t.elemtype instanceof ArrayType) {
									print(exprStr, expr);
									print("[0] instanceof Array");
								} else {
									printInstanceOf(exprStr, expr, t.elemtype, true);
								}
								print(")");
							}
						}
					}
				}
			}
		}
		if (!(getParent() instanceof JCParens)) {
			print(")");
		}
	}

	/**
	 * Prints an <code>instanceof</code> tree.
	 */
	@Override
	public void visitTypeTest(JCInstanceOf instanceOf) {
		printInstanceOf(null, instanceOf.expr, instanceOf.clazz.type);
	}

	/**
	 * Prints an throw statement tree.
	 */
	@Override
	public void visitThrow(JCThrow throwStatement) {
		print("throw ").print(throwStatement.expr);
	}

	/**
	 * Prints an assert tree.
	 */
	@Override
	public void visitAssert(JCAssert assertion) {
		if (!context.options.isIgnoreAssertions()) {
			String assertCode = assertion.toString().replace("\"", "'");
			print("if(!(").print(assertion.cond).print(
					")) throw new Error(\"Assertion error line " + getCurrentLine() + ": " + assertCode + "\");");
		}
	}

	/**
	 * Prints an annotation tree.
	 */
	@Override
	public void visitAnnotation(JCAnnotation annotation) {
		if (!context.hasAnnotationType(annotation.type.tsym, JSweetConfig.ANNOTATION_DECORATOR)) {
			return;
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
		} else if (getParentOfParent() instanceof ClassTree) {
			print("()");
		}
		println().printIndent();
	}

	Stack<TypeMirror> rootConditionalAssignedTypes = new Stack<>();
	Stack<TypeMirror> rootArrayAssignedTypes = new Stack<>();

	@Override
	protected boolean substituteAssignedExpression(TypeMirror assignedType, ExpressionTree expression) {
		if (assignedType == null) {
			return false;
		}
		if (assignedType.isInterface() && expression.type.tsym.isEnum()) {
			String relTarget = getRootRelativeName((Element) expression.type.tsym);
			print(relTarget).print("[\"" + Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_WRAPPERS + "\"][")
					.print(expression).print("]");
			return true;
		}
		if (expression instanceof ConditionalExpressionTree) {
			rootConditionalAssignedTypes.push(assignedType);
			return false;
		}
		if (expression instanceof JCNewArray && assignedType instanceof ArrayType) {
			rootArrayAssignedTypes.push(((ArrayType) assignedType).elemtype);
			return false;
		}
		if (assignedType.getTag() == TypeTag.CHAR && expression.type.getTag() != TypeTag.CHAR) {
			print("String.fromCharCode(").print(expression).print(")");
			return true;
		} else if (Util.isNumber(assignedType) && expression.type.getTag() == TypeTag.CHAR) {
			print("(").print(expression).print(").charCodeAt(0)");
			return true;
		} else if (singlePrecisionFloats() && assignedType.getTag() == TypeTag.FLOAT
				&& expression.type.getTag() == TypeTag.DOUBLE) {
			print("(<any>Math).fround(").print(expression).print(")");
			return true;
		} else {
			if (expression instanceof LambdaExpressionTree) {
				if (assignedType.tsym.isInterface() && !context.isFunctionalType(assignedType.tsym)) {
					LambdaExpressionTree lambda = (LambdaExpressionTree) expression;
					ExecutableElement method = (ExecutableElement) assignedType.tsym.getEnclosedElements().get(0);
					print("{ " + method.getSimpleName() + " : ").print(lambda).print(" }");
					return true;
				}
			} else if (expression instanceof NewClassTree) {
				NewClassTree newClass = (NewClassTree) expression;
				if (newClass.def != null && context.isFunctionalType(assignedType.tsym)) {
					List<Tree> defs = newClass.def.defs;
					boolean printed = false;
					for (Tree def : defs) {
						if (def instanceof MethodTree) {
							if (printed) {
								// should never happen... report error?
							}
							MethodTree method = (MethodTree) def;
							if (method.sym.isConstructor()) {
								continue;
							}
							getStack().push(method);
							print("(").printArgList(null, method.getParameters()).print(") => ").print(method.body);
							getStack().pop();
							printed = true;
						}
					}
					if (printed) {
						return true;
					}
				} else {
					// object assignment to functional type
					if ((newClass.def == null && context.isFunctionalType(assignedType.tsym))) {
						ExecutableElement method;
						for (Element s : assignedType.tsym.getEnclosedElements()) {
							if (s instanceof ExecutableElement) {
								// TODO also check that the method is compatible
								// (here we just apply to the first found
								// method)
								method = (ExecutableElement) s;
								print("(");
								for (VariableElement p : method.getParameters()) {
									print(p.getSimpleName().toString()).print(", ");
								}
								if (!method.getParameters().isEmpty()) {
									removeLastChars(2);
								}
								print(") => { return new ").print(newClass.clazz).print("(").printArgList(null,
										newClass.args);
								print(").").print(method.getSimpleName().toString()).print("(");
								for (VariableElement p : method.getParameters()) {
									print(p.getSimpleName().toString()).print(", ");
								}
								if (!method.getParameters().isEmpty()) {
									removeLastChars(2);
								}
								print("); }");
								return true;
							}
						}

					}
					// raw generic type
					if (!newClass.type.tsym.getTypeParameters().isEmpty() && newClass.typeargs.isEmpty()) {
						print("<any>(").print(expression).print(")");
						return true;
					}
				}
			} else if (!(expression instanceof LambdaExpressionTree || expression instanceof JCMemberReference)
					&& context.isFunctionalType(assignedType.tsym)) {
				// disallow typing to force objects to be passed as function
				// (may require runtime checks later on)
				print("<any>(").print(expression).print(")");
				return true;
			} else if (expression instanceof MethodInvocationTree) {
				// disable type checking when the method returns a type variable
				// because it may to be correctly set in the invocation
				ExecutableElement m = (ExecutableElement) Util
						.getAccessedSymbol(((MethodInvocationTree) expression).meth);
				if (m != null && m.getReturnType() instanceof TypeVar
						&& m.getReturnType().tsym.getEnclosingElement() == m) {
					print("<any>(").print(expression).print(")");
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Gets the qualified name for a given type symbol.
	 */
	@Override
	public String getQualifiedTypeName(TypeElement type, boolean globals, boolean ignoreLangTypes) {
		String qualifiedName = super.getQualifiedTypeName(type, globals, ignoreLangTypes);
		String typeName = type.getQualifiedName().toString();
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
		if (type.isEnum()) {
			qualifiedName += ENUM_WRAPPER_CLASS_SUFFIX;
		}
		return qualifiedName;
	}

	private static final Void returnNothing() {
		return null;
	}
}
