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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

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

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Attribute.Compound;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.TypeVariableSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ArrayType;
import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.code.Type.TypeVar;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.parser.Tokens.Comment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCAssignOp;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCBreak;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCatch;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCConditional;
import com.sun.tools.javac.tree.JCTree.JCContinue;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCInstanceOf;
import com.sun.tools.javac.tree.JCTree.JCLabeledStatement;
import com.sun.tools.javac.tree.JCTree.JCLambda;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMemberReference;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCSwitch;
import com.sun.tools.javac.tree.JCTree.JCSynchronized;
import com.sun.tools.javac.tree.JCTree.JCThrow;
import com.sun.tools.javac.tree.JCTree.JCTry;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCWildcard;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Name;

/**
 * This is a TypeScript printer for translating the Java AST to a TypeScript
 * program.
 * 
 * @author Renaud Pawlak
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

		private JCMethodDecl mainMethod;

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

		private List<JCClassDecl> anonymousClasses = new ArrayList<>();

		private List<JCNewClass> anonymousClassesConstructors = new ArrayList<>();

		private List<LinkedHashSet<VarSymbol>> finalVariables = new ArrayList<>();

		private boolean hasConstructorOverloadWithSuperClass;

		private List<JCVariableDecl> fieldsWithInitializers = new ArrayList<>();

		private List<String> inlinedConstructorArgs = null;

		private List<JCClassDecl> localClasses = new ArrayList<>();

		private List<String> generatedMethodNames = new ArrayList<>();

		// to be accessed in the parent scope
		private boolean isAnonymousClass = false;
		// to be accessed in the parent scope
		private boolean isInnerClass = false;
		// to be accessed in the parent scope
		private boolean isLocalClass = false;

		private boolean constructor = false;

		private boolean decoratorScope = false;

		public String getName() {
			return name;
		}

		protected JCMethodDecl getMainMethod() {
			return mainMethod;
		}

		protected boolean isInterfaceScope() {
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

		public List<JCClassDecl> getAnonymousClasses() {
			return anonymousClasses;
		}

		public List<JCNewClass> getAnonymousClassesConstructors() {
			return anonymousClassesConstructors;
		}

		public List<LinkedHashSet<VarSymbol>> getFinalVariables() {
			return finalVariables;
		}

		public boolean isHasConstructorOverloadWithSuperClass() {
			return hasConstructorOverloadWithSuperClass;
		}

		public List<JCVariableDecl> getFieldsWithInitializers() {
			return fieldsWithInitializers;
		}

		public List<String> getInlinedConstructorArgs() {
			return inlinedConstructorArgs;
		}

		public List<JCClassDecl> getLocalClasses() {
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
		scope.push(new ClassScope());
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
	 * @param adapter
	 *            an object that can tune various aspects of the TypeScript code
	 *            generation
	 * @param logHandler
	 *            the handler for logging and error reporting
	 * @param context
	 *            the AST scanning context
	 * @param compilationUnit
	 *            the compilation unit to be translated
	 * @param fillSourceMap
	 *            if true, the printer generates the source maps, for debugging
	 *            purpose
	 */
	public Java2TypeScriptTranslator(PrinterAdapter adapter, TranspilationHandler logHandler, JSweetContext context,
			JCCompilationUnit compilationUnit, boolean fillSourceMap) {
		super(logHandler, context, compilationUnit, adapter, fillSourceMap);
	}

	private static java.util.List<Class<?>> statementsWithNoSemis = Arrays
			.asList(new Class<?>[] { JCIf.class, JCForLoop.class, JCEnhancedForLoop.class, JCSwitch.class });

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

	private PackageSymbol topLevelPackage;

	private void useModule(boolean require, PackageElement targetPackage, JCTree sourceTree, String targetName,
			String moduleName, Symbol sourceElement) {
		if (context.useModules) {
			context.packageDependencies.add((PackageSymbol) targetPackage);
			context.packageDependencies.add(compilationUnit.packge);
			context.packageDependencies.addEdge(compilationUnit.packge, (PackageSymbol) targetPackage);
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

	private boolean checkRootPackageParent(JCCompilationUnit topLevel, PackageSymbol rootPackage,
			PackageSymbol parentPackage) {
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
		for (Symbol s : parentPackage.getEnclosedElements()) {
			if (s instanceof ClassSymbol) {
				if (Util.isSourceElement(s)) {
					report(topLevel.getPackageName(), JSweetProblem.CLASS_OUT_OF_ROOT_PACKAGE_SCOPE,
							s.getQualifiedName().toString(), rootPackage.getQualifiedName().toString());
					return false;
				}
			}
		}
		return checkRootPackageParent(topLevel, rootPackage, (PackageSymbol) parentPackage.owner);
	}

	private ModuleImportDescriptor getModuleImportDescriptor(String importedName, TypeElement importedClass) {
		return getAdapter().getModuleImportDescriptor(new CompilationUnitElementSupport(compilationUnit), importedName,
				importedClass);
	}

	private boolean isMappedOrErasedType(Symbol symbol) {
		return context.isMappedType(symbol.type.tsym.toString()) || context.hasAnnotationType(symbol, JSweetConfig.ANNOTATION_ERASED);
	}

	/**
	 * Prints a compilation unit tree.
	 */
	@Override
	public void visitTopLevel(JCCompilationUnit topLevel) {

		if (context.isPackageErased(topLevel.packge)) {
			return;
		}

		isDefinitionScope = topLevel.packge.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".");

		if (context.hasAnnotationType(topLevel.packge, JSweetConfig.ANNOTATION_MODULE)) {
			context.addExportedElement(
					context.getAnnotationValue(topLevel.packge, JSweetConfig.ANNOTATION_MODULE, String.class, null),
					topLevel.packge, getCompilationUnit());
		}

		PackageSymbol rootPackage = context.getFirstEnclosingRootPackage(topLevel.packge);
		if (rootPackage != null) {
			if (!checkRootPackageParent(topLevel, rootPackage, (PackageSymbol) rootPackage.owner)) {
				return;
			}
		}
		context.importedTopPackages.clear();
		context.rootPackages.add(rootPackage);

		topLevelPackage = context.getTopLevelPackage(topLevel.packge);
		if (topLevelPackage != null) {
			context.topLevelPackageNames.add(topLevelPackage.getQualifiedName().toString());
		}

		footer.delete(0, footer.length());

		setCompilationUnit(topLevel);

		String packge = topLevel.packge.toString();

		boolean globalModule = JSweetConfig.GLOBALS_PACKAGE_NAME.equals(packge)
				|| packge.endsWith("." + JSweetConfig.GLOBALS_PACKAGE_NAME);
		String rootRelativePackageName = "";
		if (!globalModule) {
			rootRelativePackageName = getRootRelativeName(topLevel.packge);
			if (rootRelativePackageName.length() == 0) {
				globalModule = true;
			}
		}

		List<String> packageSegments = new ArrayList<String>(Arrays.asList(rootRelativePackageName.split("\\.")));
		packageSegments.retainAll(JSweetConfig.TS_TOP_LEVEL_KEYWORDS);
		if (!packageSegments.isEmpty()) {
			report(topLevel.getPackageName(), JSweetProblem.PACKAGE_NAME_CONTAINS_KEYWORD, packageSegments);
		}

		// generate requires by looking up imported external modules

		for (JCImport importDecl : topLevel.getImports()) {

			TreeScanner importedModulesScanner = new TreeScanner() {
				@Override
				public void scan(JCTree tree) {
					if (tree instanceof JCFieldAccess) {
						JCFieldAccess qualified = (JCFieldAccess) tree;
						if (qualified.sym != null) {
							// regular import case (qualified.sym is a package)
							if (context.hasAnnotationType(qualified.sym, JSweetConfig.ANNOTATION_MODULE)) {
								String targetName = createImportAliasFromFieldAccess(qualified);
								String actualName = context.getAnnotationValue(qualified.sym,
										JSweetConfig.ANNOTATION_MODULE, String.class, null);
								useModule(true, null, importDecl, targetName, actualName,
										((PackageSymbol) qualified.sym));
							}
						} else {
							// static import case (imported fields and methods)
							if (qualified.selected instanceof JCFieldAccess) {
								JCFieldAccess qualifier = (JCFieldAccess) qualified.selected;
								if (qualifier.sym != null) {
									try {
										for (Symbol importedMember : qualifier.sym.getEnclosedElements()) {
											if (qualified.name.equals(importedMember.getSimpleName())) {
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
										// TODO: sometimes,
										// getEnclosedElement
										// fails because of string types
										// (find
										// out why if possible)
										e.printStackTrace();
									}
								}
							}
						}
					}
					super.scan(tree);
				}
			};
			importedModulesScanner.scan(importDecl.qualid);
		}

		for (JCImport importDecl : topLevel.getImports()) {
			if (importDecl.qualid instanceof JCFieldAccess) {
				JCFieldAccess qualified = (JCFieldAccess) importDecl.qualid;
				String importedName = qualified.name.toString();
				if (importDecl.isStatic() && (qualified.selected instanceof JCFieldAccess)) {
					qualified = (JCFieldAccess) qualified.selected;
				}
				if (qualified.sym instanceof ClassSymbol) {
					boolean globals = JSweetConfig.GLOBALS_CLASS_NAME.equals(qualified.sym.getSimpleName().toString());
					if (!globals) {
						importedName = qualified.name.toString();
					}
					ClassSymbol importedClass = (ClassSymbol) qualified.sym;
					String qualId = importDecl.getQualifiedIdentifier().toString();
					String adaptedQualId = getAdapter().needsImport(new ImportElementSupport(importDecl), qualId);
					if (globals || adaptedQualId != null) {
						ModuleImportDescriptor moduleImport = getModuleImportDescriptor(importedName, importedClass);
						if (moduleImport != null) {
							useModule(false, moduleImport.getTargetPackage(), importDecl,
									moduleImport.getImportedName(), moduleImport.getPathToImportedClass(), null);
						}
					}
				}
			}
		}

		if (context.useModules) {
			TreeScanner usedTypesScanner = new TreeScanner() {

				private HashSet<String> names = new HashSet<>();

				private void checkType(TypeSymbol type) {
					if (type instanceof ClassSymbol && !isMappedOrErasedType(type)) {
						String name = type.getSimpleName().toString();
						if (!names.contains(name)) {
							names.add(name);
							ModuleImportDescriptor moduleImport = getModuleImportDescriptor(name, (ClassSymbol) type);
							if (moduleImport != null) {
								useModule(false, moduleImport.getTargetPackage(), null, moduleImport.getImportedName(),
										moduleImport.getPathToImportedClass(), null);
							}
						}
					}
				}

				@Override
				public void scan(JCTree t) {
					if (t instanceof JCImport) {
						return;
					}
					if (t != null && t.type != null && t.type.tsym instanceof ClassSymbol) {
						if (!(t instanceof JCTypeApply)) {
							checkType(t.type.tsym);
						}
					}
					super.scan(t);
				}

			};
			usedTypesScanner.scan(compilationUnit);
		}

		// require root modules when using fully qualified names or reserved
		// keywords
		new TreeScanner() {
			Stack<JCTree> stack = new Stack<>();

			public void scan(JCTree t) {
				if (t != null) {
					stack.push(t);
					try {
						super.scan(t);
					} finally {
						stack.pop();
					}
				}
			}

			@SuppressWarnings("unchecked")
			public <T extends JCTree> T getParent(Class<T> type) {
				for (int i = this.stack.size() - 2; i >= 0; i--) {
					if (type.isAssignableFrom(this.stack.get(i).getClass())) {
						return (T) this.stack.get(i);
					}
				}
				return null;
			}

			public void visitIdent(JCIdent identifier) {
				if (identifier.sym instanceof PackageSymbol) {
					// ignore packages in imports
					if (getParent(JCImport.class) != null) {
						return;
					}
					boolean isSourceType = false;
					for (int i = stack.size() - 2; i >= 0; i--) {
						JCTree tree = stack.get(i);
						if (!(tree instanceof JCFieldAccess)) {
							break;
						} else {
							JCFieldAccess fa = (JCFieldAccess) tree;
							if ((fa.sym instanceof ClassSymbol) && Util.isSourceElement(fa.sym)) {
								isSourceType = true;
								break;
							}
						}
					}
					if (!isSourceType) {
						return;
					}
					PackageSymbol identifierPackage = (PackageSymbol) identifier.sym;
					String pathToModulePackage = Util.getRelativePath(compilationUnit.packge, identifierPackage);
					if (pathToModulePackage == null) {
						return;
					}
					File moduleFile = new File(new File(pathToModulePackage), JSweetConfig.MODULE_FILE_NAME);
					if (!identifierPackage.getSimpleName().toString()
							.equals(compilationUnit.packge.getSimpleName().toString())) {
						useModule(false, identifierPackage, identifier, identifierPackage.getSimpleName().toString(),
								moduleFile.getPath().replace('\\', '/'), null);
					}
				} else if (identifier.sym instanceof ClassSymbol) {
					if (JSweetConfig.GLOBALS_PACKAGE_NAME
							.equals(identifier.sym.getEnclosingElement().getSimpleName().toString())) {
						String pathToModulePackage = Util.getRelativePath(compilationUnit.packge,
								identifier.sym.getEnclosingElement());
						if (pathToModulePackage == null) {
							return;
						}
						File moduleFile = new File(new File(pathToModulePackage), JSweetConfig.MODULE_FILE_NAME);
						if (!identifier.sym.getEnclosingElement()
								.equals(compilationUnit.packge.getSimpleName().toString())) {
							useModule(false, (PackageSymbol) identifier.sym.getEnclosingElement(), identifier,
									JSweetConfig.GLOBALS_PACKAGE_NAME, moduleFile.getPath().replace('\\', '/'), null);
						}
					}
				}
			}

			@Override
			public void visitApply(JCMethodInvocation invocation) {
				// TODO: same for static variables
				if (invocation.meth instanceof JCIdent
						&& JSweetConfig.TS_STRICT_MODE_KEYWORDS.contains(invocation.meth.toString().toLowerCase())) {
					PackageSymbol invocationPackage = (PackageSymbol) ((JCIdent) invocation.meth).sym
							.getEnclosingElement().getEnclosingElement();
					String rootRelativeInvocationPackageName = getRootRelativeName(invocationPackage);
					if (rootRelativeInvocationPackageName.indexOf('.') == -1) {
						super.visitApply(invocation);
						return;
					}
					String targetRootPackageName = rootRelativeInvocationPackageName.substring(0,
							rootRelativeInvocationPackageName.indexOf('.'));
					String pathToReachRootPackage = Util.getRelativePath(
							"/" + compilationUnit.packge.getQualifiedName().toString().replace('.', '/'),
							"/" + targetRootPackageName);
					if (pathToReachRootPackage == null) {
						super.visitApply(invocation);
						return;
					}
					File moduleFile = new File(new File(pathToReachRootPackage), JSweetConfig.MODULE_FILE_NAME);
					if (!invocationPackage.toString().equals(compilationUnit.packge.getSimpleName().toString())) {
						useModule(false, invocationPackage, invocation, targetRootPackageName,
								moduleFile.getPath().replace('\\', '/'), null);
					}
				}
				super.visitApply(invocation);
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

		for (JCTree def : topLevel.defs) {
			if (!(def instanceof JCClassDecl)) {
				print(def);
			}
		}

		for (JCTree def : Util.getSortedClassDeclarations(topLevel.defs)) {
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

	private String createImportAliasFromFieldAccess(JCFieldAccess access) {
		String name = extractNameFromAnnotatedSymbol(access.sym);
		if (name != null) {
			return name;
		} else {
			return access.name.toString();
		}
	}

	private String createImportAliasFromSymbol(Symbol symbol) {
		String name = extractNameFromAnnotatedSymbol(symbol);
		if (name != null) {
			return name;
		} else {
			return symbol.getSimpleName().toString();
		}
	}

	private String extractNameFromAnnotatedSymbol(Symbol symbol) {
		if (context.hasAnnotationType(symbol, JSweetConfig.ANNOTATION_NAME)) {
			return context.getAnnotationValue(symbol, JSweetConfig.ANNOTATION_NAME, String.class, null);
		} else {
			return null;
		}
	}

	private void printDocComment(JCTree element) {
		printDocComment(element, false);
	}

	private void printDocComment(JCTree element, boolean newline) {
		if (compilationUnit != null && compilationUnit.docComments != null) {
			Comment comment = compilationUnit.docComments.getComment(element);
			String commentText = JSDoc.adaptDocComment(context, getCompilationUnit(), element,
					comment == null ? null : comment.getText());

			Element elt = null;
			if (element instanceof JCVariableDecl) {
				elt = ((JCVariableDecl) element).sym;
			} else if (element instanceof JCMethodDecl) {
				elt = ((JCMethodDecl) element).sym;
			} else if (element instanceof JCClassDecl) {
				elt = ((JCClassDecl) element).sym;
			}
			if (elt != null) {
				commentText = getAdapter().adaptDocComment(elt, commentText);
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

	private void printAnonymousClassTypeArgs(JCNewClass newClass) {
		if ((newClass.clazz instanceof JCTypeApply)) {
			JCTypeApply tapply = (JCTypeApply) newClass.clazz;
			if (tapply.getTypeArguments() != null && !tapply.getTypeArguments().isEmpty()) {
				boolean printed = false;
				print("<");
				for (JCExpression targ : tapply.getTypeArguments()) {
					if (targ.type.tsym instanceof TypeVariableSymbol) {
						printed = true;
						print(targ).print(", ");
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

	public final AbstractTreePrinter substituteAndPrintType(JCTree typeTree) {
		return substituteAndPrintType(typeTree, false, inTypeParameters, true, disableTypeSubstitution);
	}

	private AbstractTreePrinter printArguments(List<JCExpression> arguments) {
		int i = 1;
		for (JCExpression argument : arguments) {
			printArgument(argument, i++).print(", ");
		}
		if (arguments.size() > 0) {
			removeLastChars(2);
		}
		return this;
	}

	private AbstractTreePrinter printArgument(JCExpression argument, int i) {
		print("p" + i + ": ");
		substituteAndPrintType(argument, false, false, true, false);
		return this;
	}

	@SuppressWarnings("unlikely-arg-type")
	private AbstractTreePrinter substituteAndPrintType(JCTree typeTree, boolean arrayComponent,
			boolean inTypeParameters, boolean completeRawTypes, boolean disableSubstitution) {
		if (typeTree.type.tsym instanceof TypeVariableSymbol) {
			if (getAdapter().typeVariablesToErase.contains(typeTree.type.tsym)) {
				return print("any");
			}
		}
		if (!disableSubstitution) {
			if (context.hasAnnotationType(typeTree.type.tsym, ANNOTATION_ERASED)) {
				return print("any");
			}
			if (context.hasAnnotationType(typeTree.type.tsym, ANNOTATION_OBJECT_TYPE)) {
				// TODO: in case of object types, we should replace with the org
				// object type...
				return print("any");
			}
			String typeFullName = typeTree.type.getModelType().toString(); // typeTree.type.tsym.getQualifiedName().toString();
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
			if (typeTree instanceof JCTypeApply) {
				JCTypeApply typeApply = ((JCTypeApply) typeTree);
				String typeName = typeApply.clazz.toString();
				String mappedTypeName = context.getTypeMappingTarget(typeName);
				if (mappedTypeName != null && mappedTypeName.endsWith("<>")) {
					print(typeName.substring(0, mappedTypeName.length() - 2));
					return this;
				}

				if (typeFullName.startsWith(TUPLE_CLASSES_PACKAGE + ".")) {
					print("[");
					for (JCExpression argument : typeApply.arguments) {
						substituteAndPrintType(argument, arrayComponent, inTypeParameters, completeRawTypes, false)
								.print(",");
					}
					if (typeApply.arguments.length() > 0) {
						removeLastChar();
					}
					print("]");
					return this;
				}
				if (typeFullName.startsWith(UNION_CLASS_NAME)) {
					print("(");
					for (JCExpression argument : typeApply.arguments) {
						print("(");
						substituteAndPrintType(argument, arrayComponent, inTypeParameters, completeRawTypes, false);
						print(")");
						print("|");
					}
					if (typeApply.arguments.length() > 0) {
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
							printArguments(typeApply.arguments);
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
							printArguments(typeApply.arguments.subList(0, typeApply.arguments.length() - 1));
						}
						print(") => ");
						substituteAndPrintType(typeApply.arguments.get(typeApply.arguments.length() - 1),
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
							substituteAndPrintType(typeApply.arguments.get(0), arrayComponent, inTypeParameters,
									completeRawTypes, false);
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
							printArguments(typeApply.arguments);
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
						printArgument(typeApply.arguments.head, 1);
						if (typeName.startsWith("Binary")) {
							print(", ");
							printArgument(typeApply.arguments.head, 2);
						}
						print(") => ");
						substituteAndPrintType(typeApply.arguments.head, arrayComponent, inTypeParameters,
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
				if (!(typeTree instanceof JCArrayTypeTree) && typeFullName.startsWith("java.util.function.")) {
					// case of a raw functional type (programmer's mistake)
					return print("any");
				}
				String mappedType = context.getTypeMappingTarget(typeFullName);
				if (mappedType != null) {
					if (mappedType.endsWith("<>")) {
						print(mappedType.substring(0, mappedType.length() - 2));
					} else {
						print(mappedType);
						if (completeRawTypes && !typeTree.type.tsym.getTypeParameters().isEmpty()
								&& !context.getTypeMappingTarget(typeFullName).equals("any")) {
							printAnyTypeArguments(typeTree.type.tsym.getTypeParameters().size());
						}
					}
					return this;
				}
			}
			for (BiFunction<ExtendedElement, String, Object> mapping : context.getFunctionalTypeMappings()) {
				Object mapped = mapping.apply(new ExtendedElementSupport<JCTree>(typeTree), typeFullName);
				if (mapped instanceof String) {
					print((String) mapped);
					return this;
				} else if (mapped instanceof JCTree) {
					substituteAndPrintType((JCTree) mapped);
					return this;
				} else if (mapped instanceof TypeMirror) {
					print(getAdapter().getMappedType((TypeMirror) mapped));
					return this;
				}
			}
		}

		if (typeTree instanceof JCTypeApply) {
			JCTypeApply typeApply = ((JCTypeApply) typeTree);
			substituteAndPrintType(typeApply.clazz, arrayComponent, inTypeParameters, false, disableSubstitution);
			if (!typeApply.arguments.isEmpty() && !"any".equals(getLastPrintedString(3))
					&& !"Object".equals(getLastPrintedString(6))) {
				print("<");
				for (JCExpression argument : typeApply.arguments) {
					substituteAndPrintType(argument, arrayComponent, false, completeRawTypes, false).print(", ");
				}
				if (typeApply.arguments.length() > 0) {
					removeLastChars(2);
				}
				print(">");
			}
			return this;
		} else if (typeTree instanceof JCWildcard) {
			JCWildcard wildcard = ((JCWildcard) typeTree);
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
			if (typeTree instanceof JCArrayTypeTree) {
				return substituteAndPrintType(((JCArrayTypeTree) typeTree).elemtype, true, inTypeParameters,
						completeRawTypes, disableSubstitution).print("[]");
			}
			if (completeRawTypes && typeTree.type.tsym.getTypeParameters() != null
					&& !typeTree.type.tsym.getTypeParameters().isEmpty()) {
				// raw type case (Java warning)
				print(typeTree);
				print("<");
				for (int i = 0; i < typeTree.type.tsym.getTypeParameters().length(); i++) {
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

	private String getClassName(Symbol clazz) {
		String name;
		if (context.hasClassNameMapping(clazz)) {
			name = context.getClassNameMapping(clazz);
		} else {
			name = clazz.getSimpleName().toString();
		}
		if (clazz.isEnum()) {
			name += ENUM_WRAPPER_CLASS_SUFFIX;
		}
		return name;
	}

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		if (context.isIgnored(classdecl)) {
			getAdapter().afterType(classdecl.sym);
			return;
		}
		String name = classdecl.getSimpleName().toString();
		if (context.hasClassNameMapping(classdecl.sym)) {
			name = context.getClassNameMapping(classdecl.sym);
		}

		if (!scope.isEmpty() && getScope().anonymousClasses.contains(classdecl)) {
			name = getScope().name + ANONYMOUS_PREFIX + getScope().anonymousClasses.indexOf(classdecl);
		}

		JCTree testParent = getFirstParent(JCClassDecl.class, JCMethodDecl.class);
		if (testParent != null && testParent instanceof JCMethodDecl) {
			if (!isLocalClass()) {
				getScope().localClasses.add(classdecl);
				return;
			}
		}

		enterScope();
		getScope().name = name;

		JCClassDecl parent = getParent(JCClassDecl.class);
		List<TypeVariableSymbol> parentTypeVars = new ArrayList<>();
		if (parent != null) {
			getScope().innerClass = true;
			if (!classdecl.getModifiers().getFlags().contains(Modifier.STATIC)) {
				getScope().innerClassNotStatic = true;
				if (parent.getTypeParameters() != null) {
					parentTypeVars.addAll(parent.getTypeParameters().stream().map(t -> (TypeVariableSymbol) t.type.tsym)
							.collect(Collectors.toList()));
					getAdapter().typeVariablesToErase.addAll(parentTypeVars);
				}
			}
		}
		getScope().declareClassScope = context.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_AMBIENT)
				|| isDefinitionScope;
		getScope().interfaceScope = false;
		getScope().removedSuperclass = false;
		getScope().enumScope = false;
		getScope().enumWrapperClassScope = false;

		if (getScope().declareClassScope) {
			if (context.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_DECORATOR)) {
				print("declare function ").print(name).print("(...args: any[]);").println();
				exitScope();
				return;
			}
		} else {
			if (context.lookupDecoratorAnnotation(classdecl.sym.getQualifiedName().toString()) != null) {
				JCTree[] globalDecoratorFunction = context
						.lookupGlobalMethod(classdecl.sym.getQualifiedName().toString());
				if (globalDecoratorFunction == null) {
					report(classdecl, JSweetProblem.CANNOT_FIND_GLOBAL_DECORATOR_FUNCTION,
							classdecl.sym.getQualifiedName());
				} else {
					getScope().decoratorScope = true;
					enter(globalDecoratorFunction[0]);
					print(globalDecoratorFunction[1]);
					exit();
					getScope().decoratorScope = false;
				}
				exitScope();
				return;
			}
		}

		HashSet<Entry<JCClassDecl, JCMethodDecl>> defaultMethods = null;
		boolean globals = JSweetConfig.GLOBALS_CLASS_NAME.equals(classdecl.name.toString());
		if (globals && classdecl.extending != null) {
			report(classdecl, JSweetProblem.GLOBALS_CLASS_CANNOT_HAVE_SUPERCLASS);
		}
		List<Type> implementedInterfaces = new ArrayList<>();

		if (!globals) {
			if (classdecl.extending != null && JSweetConfig.GLOBALS_CLASS_NAME
					.equals(classdecl.extending.type.tsym.getSimpleName().toString())) {
				report(classdecl, JSweetProblem.GLOBALS_CLASS_CANNOT_BE_SUBCLASSED);
				return;
			}
			if (!(classdecl.getKind() == Kind.ENUM && scope.size() > 1 && getScope(1).isComplexEnum)) {
				printDocComment(classdecl);
			} else {
				print("/** @ignore */").println().printIndent();
			}
			print(classdecl.mods);

			if (!isTopLevelScope() || context.useModules || isAnonymousClass() || isInnerClass() || isLocalClass()) {
				print("export ");
			}
			if (context.isInterface(classdecl.sym)) {
				print("interface ");
				getScope().interfaceScope = true;
			} else {
				if (classdecl.getKind() == Kind.ENUM) {
					if (getScope().declareClassScope && !(getIndent() != 0 && isDefinitionScope)) {
						print("declare ");
					}
					if (scope.size() > 1 && getScope(1).isComplexEnum) {
						if (Util.hasAbstractMethod(classdecl.sym)) {
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
					Util.findDefaultMethodsInType(defaultMethods, context, classdecl.sym);
					if (classdecl.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
						print("abstract ");
					}
					print("class ");
				}
			}

			print(name + (getScope().enumWrapperClassScope ? ENUM_WRAPPER_CLASS_SUFFIX : ""));

			if (classdecl.typarams != null && classdecl.typarams.size() > 0) {
				print("<").printArgList(null, classdecl.typarams).print(">");
			} else if (isAnonymousClass() && classdecl.getModifiers().getFlags().contains(Modifier.STATIC)) {
				JCNewClass newClass = getScope(1).anonymousClassesConstructors
						.get(getScope(1).anonymousClasses.indexOf(classdecl));
				printAnonymousClassTypeArgs(newClass);
			}
			Type mixin = null;
			if (context.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_MIXIN)) {
				mixin = context.getAnnotationValue(classdecl.sym, JSweetConfig.ANNOTATION_MIXIN, Type.class, null);
				for (Compound c : classdecl.sym.getAnnotationMirrors()) {
					if (JSweetConfig.ANNOTATION_MIXIN.equals(c.type.toString())) {
						String targetName = getRootRelativeName(((Attribute.Class) c.values.head.snd).classType.tsym);
						String mixinName = getRootRelativeName(classdecl.sym);
						if (!mixinName.equals(targetName)) {
							report(classdecl, JSweetProblem.WRONG_MIXIN_NAME, mixinName, targetName);
						} else {
							if (((Attribute.Class) c.values.head.snd).classType.tsym.equals(classdecl.sym)) {
								report(classdecl, JSweetProblem.SELF_MIXIN_TARGET, mixinName);
							}
						}
					}
				}
			}

			// print EXTENDS
			boolean extendsInterface = false;
			if (classdecl.extending != null) {

				boolean removeIterable = false;
				if (context.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_SYNTACTIC_ITERABLE)
						&& classdecl.extending.type.tsym.getQualifiedName().toString()
								.equals(Iterable.class.getName())) {
					removeIterable = true;
				}

				if (!removeIterable && !JSweetConfig.isJDKReplacementMode()
						&& !(JSweetConfig.OBJECT_CLASSNAME.equals(classdecl.extending.type.toString())
								|| Object.class.getName().equals(classdecl.extending.type.toString()))
						&& !(mixin != null && context.types.isSameType(mixin, classdecl.extending.type))
						&& !(getAdapter().eraseSuperClass(classdecl.sym,
								(ClassSymbol) classdecl.extending.type.tsym))) {
					if (!getScope().interfaceScope && context.isInterface(classdecl.extending.type.tsym)) {
						extendsInterface = true;
						print(" implements ");
						implementedInterfaces.add(classdecl.extending.type);
					} else {
						print(" extends ");
					}
					if (getScope().enumWrapperClassScope && getScope(1).anonymousClasses.contains(classdecl)) {
						print(classdecl.extending.toString() + ENUM_WRAPPER_CLASS_SUFFIX);
					} else {
						disableTypeSubstitution = !getAdapter().isSubstituteSuperTypes();
						substituteAndPrintType(classdecl.extending);
						disableTypeSubstitution = false;
					}
					if (context.classesWithWrongConstructorOverload.contains(classdecl.sym)) {
						getScope().hasConstructorOverloadWithSuperClass = true;
					}
				} else {
					getScope().removedSuperclass = true;
				}
			}

			// print IMPLEMENTS
			if (classdecl.implementing != null && !classdecl.implementing.isEmpty() && !getScope().enumScope) {
				List<JCExpression> implementing = new ArrayList<>(classdecl.implementing);

				if (context.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_SYNTACTIC_ITERABLE)) {
					for (JCExpression itf : classdecl.implementing) {
						if (itf.type.tsym.getQualifiedName().toString().equals(Iterable.class.getName())) {
							implementing.remove(itf);
						}
					}
				}
				// erase Java interfaces
				for (JCExpression itf : classdecl.implementing) {
					if (context.isFunctionalType(itf.type.tsym)
							|| getAdapter().eraseSuperInterface(classdecl.sym, (ClassSymbol) itf.type.tsym)) {
						implementing.remove(itf);
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
					for (JCExpression itf : implementing) {
						disableTypeSubstitution = !getAdapter().isSubstituteSuperTypes();
						substituteAndPrintType(itf);
						disableTypeSubstitution = false;
						implementedInterfaces.add(itf.type);
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
			for (Entry<JCClassDecl, JCMethodDecl> entry : defaultMethods) {
				if (!(entry.getValue().type instanceof MethodType)) {
					continue;
				}
				MethodSymbol s = Util.findMethodDeclarationInType(context.types, classdecl.sym,
						entry.getValue().getName().toString(), (MethodType) entry.getValue().type);
				if (s == null || s == entry.getValue().sym) {
					getAdapter().typeVariablesToErase
							.addAll(((ClassSymbol) s.getEnclosingElement()).getTypeParameters());
					printIndent().print(entry.getValue()).println();
					getAdapter().typeVariablesToErase
							.removeAll(((ClassSymbol) s.getEnclosingElement()).getTypeParameters());
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

		for (JCTree def : classdecl.defs) {
			if (def instanceof JCClassDecl) {
				getScope().hasInnerClass = true;
			}
			if (def instanceof JCVariableDecl) {
				JCVariableDecl var = (JCVariableDecl) def;
				if (!var.sym.isStatic() && var.init != null) {
					getScope().fieldsWithInitializers.add((JCVariableDecl) def);
				}
			}
		}

		if (!globals && !getScope().enumScope && !context.isInterface(classdecl.sym)
				&& context.getStaticInitializerCount(classdecl.sym) > 0) {
			printIndent().print("static __static_initialized : boolean = false;").println();
			int liCount = context.getStaticInitializerCount(classdecl.sym);
			String prefix = getClassName(classdecl.sym) + ".";
			printIndent().print("static __static_initialize() { ");
			print("if(!" + prefix + "__static_initialized) { ");
			print(prefix + "__static_initialized = true; ");
			for (int i = 0; i < liCount; i++) {
				print(prefix + "__static_initializer_" + i + "(); ");
			}
			print("} }").println().println();
			String qualifiedClassName = getQualifiedTypeName(classdecl.sym, globals, true);
			context.addTopFooterStatement(
					(isBlank(qualifiedClassName) ? "" : qualifiedClassName + ".__static_initialize();"));
		}

		boolean hasUninitializedFields = false;

		for (JCTree def : classdecl.defs) {
			if (getScope().interfaceScope && ((def instanceof JCMethodDecl && ((JCMethodDecl) def).sym.isStatic())
					|| (def instanceof JCVariableDecl && ((JCVariableDecl) def).sym.isStatic()))) {
				// static interface members are printed in a namespace
				continue;
			}
			if (getScope().interfaceScope && def instanceof JCMethodDecl) {
				// object method should not be defined otherwise they will have
				// to be implemented
				if (Util.isOverridingBuiltInJavaObjectMethod(((JCMethodDecl) def).sym)) {
					continue;
				}
			}
			if (def instanceof JCClassDecl) {
				// inner types are be printed in a namespace
				continue;
			}
			if (def instanceof JCVariableDecl) {
				if (getScope().enumScope && ((JCVariableDecl) def).sym.getKind() != ElementKind.ENUM_CONSTANT) {
					getScope().isComplexEnum = true;
					continue;
				}
				if (!((JCVariableDecl) def).getModifiers().getFlags().contains(Modifier.STATIC)
						&& ((JCVariableDecl) def).init == null) {
					hasUninitializedFields = true;
				}
			}
			if (def instanceof JCBlock && !((JCBlock) def).isStatic()) {
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
			if (def instanceof JCVariableDecl) {
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
		if (!getScope().interfaceScope && classdecl.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
			List<MethodSymbol> methods = new ArrayList<>();
			for (Type t : implementedInterfaces) {
				context.grabMethodsToBeImplemented(methods, t.tsym);
			}
			methods.sort((m1, m2) -> m1.getSimpleName().compareTo(m2.getSimpleName()));
			Map<Name, String> signatures = new HashMap<>();
			for (MethodSymbol meth : methods) {
				if (meth.type instanceof MethodType && !context.hasAnnotationType(meth, JSweetConfig.ANNOTATION_ERASED) && !isMappedOrErasedType(meth.owner)) {
					// do not generate default abstract method for already
					// generated methods
					if (getScope().generatedMethodNames.contains(meth.name.toString())) {
						continue;
					}
					MethodSymbol s = Util.findMethodDeclarationInType(getContext().types, classdecl.sym,
							meth.getSimpleName().toString(), (MethodType) meth.type, true);
					if (Object.class.getName().equals(s.getEnclosingElement().toString())) {
						s = null;
					}
					boolean printAbstractDeclaration = false;
					if (s != null) {
						if (!s.getEnclosingElement().equals(classdecl.sym)) {
							if (!(s.isDefault() || (!context.isInterface((TypeSymbol) s.getEnclosingElement())
									&& !s.getModifiers().contains(Modifier.ABSTRACT)))) {
								printAbstractDeclaration = true;
							}
						}
					}

					if (printAbstractDeclaration) {
						Overload o = context.getOverload(classdecl.sym, meth);
						if (o != null && o.methods.size() > 1 && !o.isValid) {
							if (!meth.type.equals(o.coreMethod.type)) {
								printAbstractDeclaration = false;
							}
						}
					}
					if (s == null || printAbstractDeclaration) {
						String signature = getContext().types.erasure(meth.type).toString();
						if (!(signatures.containsKey(meth.name) && signatures.get(meth.name).equals(signature))) {
							printAbstractMethodDeclaration(meth);
							signatures.put(meth.name, signature);
						}
					}
				}
			}
		}

		if (!getScope().hasDeclaredConstructor
				&& !(getScope().interfaceScope || getScope().enumScope || getScope().declareClassScope)) {
			Set<String> interfaces = new HashSet<>();
			context.grabSupportedInterfaceNames(interfaces, classdecl.sym);
			if (!interfaces.isEmpty() || getScope().innerClass || getScope().innerClassNotStatic
					|| hasUninitializedFields) {
				printIndent().print("constructor(");
				boolean hasArgs = false;
				if (getScope().innerClassNotStatic) {
					print(PARENT_CLASS_FIELD_NAME + ": any");
					hasArgs = true;
				}
				int anonymousClassIndex = scope.size() > 1 ? getScope(1).anonymousClasses.indexOf(classdecl) : -1;
				if (anonymousClassIndex != -1) {
					for (int i = 0; i < getScope(1).anonymousClassesConstructors.get(anonymousClassIndex).args
							.length(); i++) {
						if (!hasArgs) {
							hasArgs = true;
						} else {
							print(", ");
						}
						print("__arg" + i + ": any");
					}
					for (VarSymbol v : getScope(1).finalVariables.get(anonymousClassIndex)) {
						if (!hasArgs) {
							hasArgs = true;
						} else {
							print(", ");
						}
						print("private " + v.getSimpleName() + ": any");
					}
				}

				print(") {").startIndent().println();
				if (classdecl.extending != null && !getScope().removedSuperclass
						&& !context.isInterface(classdecl.extending.type.tsym)) {
					printIndent().print("super(");
					boolean hasArg = false;
					if (getScope().innerClassNotStatic) {
						TypeSymbol s = classdecl.extending.type.tsym;
						if (s.getEnclosingElement() instanceof ClassSymbol && !s.isStatic()) {
							print(PARENT_CLASS_FIELD_NAME);
							hasArg = true;
						}
					}
					if (anonymousClassIndex != -1) {
						for (int i = 0; i < getScope(1).anonymousClassesConstructors.get(anonymousClassIndex).args
								.length(); i++) {
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
				printInstanceInitialization(classdecl, null);
				endIndent().printIndent().print("}").println().println();
			}
		}

		removeLastChar();

		if (getScope().enumWrapperClassScope && !getScope(1).anonymousClasses.contains(classdecl)) {
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
					&& !(getScope().enumWrapperClassScope && classdecl.sym.isAnonymous())) {
				if (!classdecl.sym.isAnonymous()) {
					println().printIndent()
							.print(getScope().enumWrapperClassScope ? classdecl.sym.getSimpleName().toString() : name)
							.print("[\"" + CLASS_NAME_IN_CONSTRUCTOR + "\"] = ")
							.print("\"" + classdecl.sym.getQualifiedName().toString() + "\";");
				}
				Set<String> interfaces = new HashSet<>();
				context.grabSupportedInterfaceNames(interfaces, classdecl.sym);
				if (!interfaces.isEmpty()) {
					println().printIndent()
							.print(getScope().enumWrapperClassScope ? classdecl.sym.getSimpleName().toString() : name)
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
			visitClassDef(classdecl);
		}

		boolean nameSpace = false;

		if (getScope().interfaceScope) {
			// print static members of interfaces
			for (JCTree def : classdecl.defs) {
				if ((def instanceof JCMethodDecl && ((JCMethodDecl) def).sym.isStatic())
						|| (def instanceof JCVariableDecl && ((JCVariableDecl) def).sym.isStatic())) {
					if (def instanceof JCVariableDecl && context.hasAnnotationType(((JCVariableDecl) def).sym,
							ANNOTATION_STRING_TYPE, JSweetConfig.ANNOTATION_ERASED)) {
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

						print("namespace ").print(classdecl.getSimpleName().toString()).print(" {").startIndent();
					}
					println().println().printIndent().print(def);
					if (def instanceof JCVariableDecl) {
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
		for (JCTree def : Util.getSortedClassDeclarations(classdecl.defs)) {
			if (def instanceof JCClassDecl) {
				JCClassDecl cdef = (JCClassDecl) def;
				if (context.isIgnored(cdef)) {
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
		for (JCClassDecl cdef : getScope().anonymousClasses) {
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
		for (JCClassDecl cdef : getScope().localClasses) {
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

		if (getScope().enumScope && getScope().isComplexEnum && !getScope().anonymousClasses.contains(classdecl)) {
			println().printIndent().print(classdecl.sym.getSimpleName().toString())
					.print("[\"" + ENUM_WRAPPER_CLASS_WRAPPERS + "\"] = [");
			int index = 0;
			for (JCTree tree : classdecl.defs) {
				if (tree instanceof JCVariableDecl
						&& ((JCVariableDecl) tree).sym.getKind() == ElementKind.ENUM_CONSTANT) {
					JCVariableDecl varDecl = (JCVariableDecl) tree;
					// enum fields are not part of the enum auxiliary class but
					// will initialize the enum values
					JCNewClass newClass = (JCNewClass) varDecl.init;
					JCClassDecl clazz = classdecl;
					try {
						int anonymousClassIndex = getScope().anonymousClasses.indexOf(newClass.def);
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
						print("\"" + varDecl.sym.name.toString() + "\"");
						if (!newClass.args.isEmpty()) {
							print(", ");
						}
						printArgList(null, newClass.args).print(")");
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
				&& getScope().mainMethod.sym.getEnclosingElement().equals(classdecl.sym)) {
			String mainClassName = getQualifiedTypeName(classdecl.sym, globals, true);
			String mainMethodQualifier = mainClassName;
			if (!isBlank(mainClassName)) {
				mainMethodQualifier = mainClassName + ".";
			}
			context.entryFiles.add(new File(compilationUnit.sourcefile.getName()));
			context.addFooterStatement(mainMethodQualifier + JSweetConfig.MAIN_FUNCTION_NAME + "("
					+ (getScope().mainMethod.getParameters().isEmpty() ? "" : "null") + ");");
		}

		getAdapter().typeVariablesToErase.removeAll(parentTypeVars);
		exitScope();

		getAdapter().afterType(classdecl.sym);

	}

	private void printAbstractMethodDeclaration(MethodSymbol method) {
		printIndent().print("public abstract ").print(method.getSimpleName().toString());
		print("(");
		if (method.getParameters() != null && !method.getParameters().isEmpty()) {
			for (VarSymbol var : method.getParameters()) {
				print(var.name.toString()).print("?: any");
				print(", ");
			}
			removeLastChars(2);
		}
		print(")");
		print(": any;").println();
	}

	private String getTSMethodName(JCMethodDecl methodDecl) {
		String name = context.getActualName(methodDecl.sym);
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

	protected boolean isDebugMode(JCMethodDecl methodDecl) {
		return methodDecl != null && !getScope().constructor && context.options.isDebugMode()
				&& !(context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_NO_DEBUG) || context
						.hasAnnotationType(methodDecl.sym.getEnclosingElement(), JSweetConfig.ANNOTATION_NO_DEBUG));
	}

	private boolean isInterfaceMethod(JCClassDecl parent, JCMethodDecl method) {
		return context.isInterface(parent.sym) && !method.sym.isStatic();
	}

	/**
	 * Prints a method tree.
	 */
	@Override
	public void visitMethodDef(JCMethodDecl methodDecl) {

		if (context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_ERASED)) {
			// erased elements are ignored
			return;
		}

		JCClassDecl parent = (JCClassDecl) getParent();

		if (parent != null && methodDecl.pos == parent.pos && !getScope().enumWrapperClassScope) {
			return;
		}

		if (JSweetConfig.INDEXED_GET_FUCTION_NAME.equals(methodDecl.getName().toString())
				&& methodDecl.getParameters().size() == 1) {
			print("[").print(methodDecl.getParameters().head).print("]: ");
			substituteAndPrintType(methodDecl.restype).print(";");
			return;
		}

		getScope().constructor = methodDecl.sym.isConstructor();
		if (getScope().enumScope) {
			if (getScope().constructor) {
				if (parent != null && parent.pos != methodDecl.pos) {
					getScope().isComplexEnum = true;
				}
			} else {
				getScope().isComplexEnum = true;
			}
			return;
		}

		// do not generate definition if parent class already declares method to avoid
		// wrong override error with overloads ({ scale(number); scale(number, number);
		// } cannot be overriden with { scale(number) } only)
		if (getScope().isDeclareClassScope() && parent.getExtendsClause() != null
				&& parent.getExtendsClause().type instanceof ClassType) {
			ClassType superClassType = (ClassType) parent.getExtendsClause().type;
			MethodSymbol superMethod = Util.findMethodDeclarationInType(context.types, superClassType.tsym,
					methodDecl.getName().toString(), (MethodType) methodDecl.type);
			if (superMethod != null) {
				return;
			}
		}

		Overload overload = null;
		boolean inOverload = false;
		boolean inCoreWrongOverload = false;
		if (parent != null) {
			overload = context.getOverload(parent.sym, methodDecl.sym);
			inOverload = overload != null && overload.methods.size() > 1;
			if (inOverload) {
				if (!overload.isValid) {
					if (!printCoreMethodDelegate) {
						if (overload.coreMethod.equals(methodDecl)) {
							inCoreWrongOverload = true;
							if (!isInterfaceMethod(parent, methodDecl) && !methodDecl.sym.isConstructor()
									&& parent.sym.equals(overload.coreMethod.sym.getEnclosingElement())) {
								printCoreMethodDelegate = true;
								visitMethodDef(overload.coreMethod);
								println().println().printIndent();
								printCoreMethodDelegate = false;
							}
						} else {
							if (methodDecl.sym.isConstructor()) {
								return;
							}
							boolean addCoreMethod = false;
							addCoreMethod = !overload.printed
									&& overload.coreMethod.sym.getEnclosingElement() != parent.sym
									&& (!overload.coreMethod.sym.getModifiers().contains(Modifier.ABSTRACT)
											|| isInterfaceMethod(parent, methodDecl)
											|| !context.types.isSubtype(parent.sym.type,
													overload.coreMethod.sym.getEnclosingElement().type));
							if (!overload.printed && !addCoreMethod && overload.coreMethod.type instanceof MethodType) {
								addCoreMethod = Util.findMethodDeclarationInType(context.types, parent.sym,
										methodDecl.getName().toString(), (MethodType) overload.coreMethod.type) == null;
							}
							if (addCoreMethod) {
								visitMethodDef(overload.coreMethod);
								overload.printed = true;
								if (!isInterfaceMethod(parent, methodDecl)) {
									println().println().printIndent();
								}
							}
							if (isInterfaceMethod(parent, methodDecl)) {
								return;
							}
						}
					}
				} else {
					if (!overload.coreMethod.equals(methodDecl)) {
						return;
					}
				}
			}
		}

		boolean ambient = context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_AMBIENT);

		if (inOverload && !inCoreWrongOverload && (ambient || isDefinitionScope)) {
			// do not generate method stubs for definitions
			return;
		}

		if (isDebugMode(methodDecl)) {
			printMethodModifiers(methodDecl, parent, getScope().constructor, inOverload, overload);
			print(getTSMethodName(methodDecl)).print("(");
			printArgList(null, methodDecl.params);
			print(") : ");
			substituteAndPrintType(methodDecl.getReturnType());
			print(" {").println();
			startIndent().printIndent();
			if (!context.types.isSameType(context.symtab.voidType, methodDecl.sym.getReturnType())) {
				print("return ");
			}
			print("__debug_exec('" + parent.sym.getQualifiedName() + "', '" + methodDecl.getName() + "', ");
			if (!methodDecl.params.isEmpty()) {
				print("[");
				for (JCVariableDecl param : methodDecl.params) {
					print("'" + param.getName() + "', ");
				}
				removeLastChars(2);
				print("]");
			} else {
				print("undefined");
			}
			print(", this, arguments, ");
			if (methodDecl.sym.isStatic()) {
				print(methodDecl.sym.getEnclosingElement().getSimpleName().toString());
			} else {
				print("this");
			}
			print("." + GENERATOR_PREFIX + getTSMethodName(methodDecl) + "(");
			for (JCVariableDecl param : methodDecl.params) {
				print(context.getActualName(param.sym) + ", ");
			}
			if (!methodDecl.params.isEmpty()) {
				removeLastChars(2);
			}
			print("));");
			println().endIndent().printIndent();
			print("}").println().println().printIndent();
		}

		print(methodDecl.mods);

		if (methodDecl.mods.getFlags().contains(Modifier.NATIVE)) {
			if (!getScope().declareClassScope && !ambient && !getScope().interfaceScope) {
				report(methodDecl, methodDecl.name, JSweetProblem.NATIVE_MODIFIER_IS_NOT_ALLOWED, methodDecl.name);
			}
		} else {
			if (getScope().declareClassScope && !getScope().constructor && !getScope().interfaceScope
					&& !methodDecl.mods.getFlags().contains(Modifier.DEFAULT)) {
				report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name,
						parent == null ? "<no class>" : parent.name);
			}
		}

		if (methodDecl.name.toString().equals("constructor")) {
			report(methodDecl, methodDecl.name, JSweetProblem.CONSTRUCTOR_MEMBER);
		}
		if (parent != null) {
			VarSymbol v = Util.findFieldDeclaration(parent.sym, methodDecl.name);
			if (v != null && context.getFieldNameMapping(v) == null) {
				if (isDefinitionScope) {
					return;
				} else {
					report(methodDecl, methodDecl.name, JSweetProblem.METHOD_CONFLICTS_FIELD, methodDecl.name, v.owner);
				}
			}
		}
		if (JSweetConfig.MAIN_FUNCTION_NAME.equals(methodDecl.name.toString())
				&& methodDecl.mods.getFlags().contains(Modifier.STATIC)
				&& !context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_DISABLED)) {
			// ignore main methods in inner classes
			if (scope.size() == 1 || (scope.size() == 2 && getScope().enumWrapperClassScope)) {
				getScope().mainMethod = methodDecl;
			}
		}

		boolean globals = parent == null ? false : JSweetConfig.GLOBALS_CLASS_NAME.equals(parent.name.toString());
		globals = globals || (getScope().interfaceScope && methodDecl.mods.getFlags().contains(Modifier.STATIC));
		if (!(inOverload && !inCoreWrongOverload)) {
			printDocComment(methodDecl);
		}

		if (parent == null) {
			printAsyncKeyword(methodDecl);

			print("function ");
		} else if (globals) {
			if (getScope().constructor && methodDecl.sym.isPrivate() && methodDecl.getParameters().isEmpty()) {
				return;
			}
			if (getScope().constructor) {
				report(methodDecl, methodDecl.name, JSweetProblem.GLOBAL_CONSTRUCTOR_DEF);
				return;
			}
			if (context.lookupDecoratorAnnotation((parent.sym.getQualifiedName() + "." + methodDecl.name)
					.replace(JSweetConfig.GLOBALS_CLASS_NAME + ".", "")) != null) {
				if (!getScope().decoratorScope) {
					return;
				}
			}

			if (!methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
				report(methodDecl, methodDecl.name, JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
				return;
			}

			if (context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_MODULE)) {
				getContext().addExportedElement(
						context.getAnnotationValue(methodDecl.sym, JSweetConfig.ANNOTATION_MODULE, String.class, null),
						methodDecl.sym, getCompilationUnit());
			}

			if (context.useModules) {
				if (!methodDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
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

			printAsyncKeyword(methodDecl);

			print("function ");
		} else {
			printMethodModifiers(methodDecl, parent, getScope().constructor, inOverload, overload);

			printAsyncKeyword(methodDecl);

			if (ambient) {
				report(methodDecl, methodDecl.name, JSweetProblem.WRONG_USE_OF_AMBIENT, methodDecl.name);
			}
		}
		if (parent == null || !context.isFunctionalType(parent.sym)) {
			if (isDebugMode(methodDecl)) {
				print("*").print(GENERATOR_PREFIX);
			}
			if (inOverload && !overload.isValid && !inCoreWrongOverload) {
				print(getOverloadMethodName(methodDecl.sym));
			} else {
				String tsMethodName = getTSMethodName(methodDecl);
				getScope().generatedMethodNames.add(tsMethodName);
				if (doesMemberNameRequireQuotes(tsMethodName)) {
					print("'" + tsMethodName + "'");
				} else {
					print(tsMethodName);
				}
			}
		}
		if ((methodDecl.typarams != null && !methodDecl.typarams.isEmpty())
				|| (getContext().getWildcards(methodDecl.sym) != null)) {
			inTypeParameters = true;
			print("<");
			if (methodDecl.typarams != null && !methodDecl.typarams.isEmpty()) {
				printArgList(null, methodDecl.typarams);
				if (getContext().getWildcards(methodDecl.sym) != null) {
					print(", ");
				}
			}
			if (getContext().getWildcards(methodDecl.sym) != null) {
				printArgList(null, getContext().getWildcards(methodDecl.sym), this::substituteAndPrintType);
			}
			print(">");
			inTypeParameters = false;
		}
		print("(");
		printMethodArgs(methodDecl, overload, inOverload, inCoreWrongOverload, getScope());
		print(")");
		if (inCoreWrongOverload && !methodDecl.sym.isConstructor()) {
			print(" : any");
		} else {
			if (methodDecl.restype != null && methodDecl.restype.type.getTag() != TypeTag.VOID) {
				print(" : ");
				substituteAndPrintType(methodDecl.restype);
			}
		}
		if (inCoreWrongOverload && isInterfaceMethod(parent, methodDecl)) {
			print(";");
			return;
		}
		if (methodDecl.getBody() == null && !(inCoreWrongOverload && !getScope().declareClassScope)
				|| (methodDecl.mods.getFlags().contains(Modifier.DEFAULT) && !getScope().defaultMethodScope)) {
			if (!getScope().interfaceScope && methodDecl.getModifiers().getFlags().contains(Modifier.ABSTRACT)
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
				if (!methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name,
							parent == null ? "<no class>" : parent.name);
				}
			}
			if (getScope().declareClassScope) {
				if (!getScope().constructor
						|| (methodDecl.getBody() != null && methodDecl.getBody().getStatements().isEmpty())) {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name,
							parent == null ? "<no class>" : parent.name);
				}
				print(";");
			} else {
				if (inCoreWrongOverload) {
					print(" {").println().startIndent().printIndent();

					boolean wasPrinted = false;
					for (int i = 0; i < overload.methods.size(); i++) {
						JCMethodDecl method = overload.methods.get(i);
						if (context.isInterface((ClassSymbol) method.sym.getEnclosingElement())
								&& !method.getModifiers().getFlags().contains(Modifier.DEFAULT)
								&& !method.getModifiers().getFlags().contains(Modifier.STATIC)) {
							continue;
						}
						if (!Util.isParent(parent.sym, (ClassSymbol) method.sym.getEnclosingElement())) {
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
							if (parent.sym != method.sym.getEnclosingElement()
									&& context.getOverload((ClassSymbol) method.sym.getEnclosingElement(),
											method.sym).coreMethod == method) {
								print("{").println().startIndent().printIndent();

								if ((method.getBody() == null || (method.mods.getFlags().contains(Modifier.DEFAULT)
										&& !getScope().defaultMethodScope)) && !getScope().interfaceScope
										&& method.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
									print(" throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); ");
								} else {
									String tsMethodAccess = getTSMemberAccess(getTSMethodName(methodDecl), true);
									print("super" + tsMethodAccess);
									print("(");
									for (int j = 0; j < method.getParameters().size(); j++) {
										print(avoidJSKeyword(
												overload.coreMethod.getParameters().get(j).name.toString()))
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
								// temporary cast to any because of Java
								// generics
								// bug
								print("return <any>");
								if (method.sym.isStatic()) {
									print(getQualifiedTypeName(parent.sym, false, false).toString());
								} else {
									print("this");
								}
								print(".").print(getOverloadMethodName(method.sym)).print("(");
								for (int j = 0; j < method.getParameters().size(); j++) {
									print(avoidJSKeyword(overload.coreMethod.getParameters().get(j).name.toString()))
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
					print(" else throw new Error('invalid overload');");
					endIndent().println().printIndent().print("}");
				} else {
					print(" ").print("{").println().startIndent();

					String replacedBody = null;
					if (context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_REPLACE)) {
						replacedBody = (String) context.getAnnotationValue(methodDecl.sym,
								JSweetConfig.ANNOTATION_REPLACE, String.class, null);
					}
					int position = getCurrentPosition();
					if (replacedBody == null || BODY_MARKER.matcher(replacedBody).find()) {
						enter(methodDecl.getBody());
						if (!methodDecl.getBody().stats.isEmpty()
								&& methodDecl.getBody().stats.head.toString().startsWith("super(")) {
							printBlockStatement(methodDecl.getBody().stats.head);
							if (parent != null) {
								printInstanceInitialization(parent, methodDecl.sym);
							}
							printBlockStatements(methodDecl.getBody().stats.tail);
						} else {
							if (parent != null) {
								printInstanceInitialization(parent, methodDecl.sym);
							}
							printBlockStatements(methodDecl.getBody().stats);
						}
						exit();
						if (replacedBody != null) {
							String orgBody = getOutput().substring(position);
							removeLastChars(getCurrentPosition() - position);
							replacedBody = BODY_MARKER.matcher(replacedBody).replaceAll(orgBody);
							replacedBody = BASE_INDENT_MARKER.matcher(replacedBody).replaceAll(getIndentString());
							replacedBody = INDENT_MARKER.matcher(replacedBody).replaceAll(INDENT);
							replacedBody = METHOD_NAME_MARKER.matcher(replacedBody)
									.replaceAll(methodDecl.getName().toString());
							replacedBody = CLASS_NAME_MARKER.matcher(replacedBody)
									.replaceAll(parent.sym.getQualifiedName().toString());
						}
					}
					if (replacedBody != null) {
						if (methodDecl.sym.isConstructor()) {
							getScope().hasDeclaredConstructor = true;
						}
						printIndent().print(replacedBody).println();
					}
					endIndent().printIndent().print("}");
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
	 */
	protected void printAsyncKeyword(JCMethodDecl methodDecl) {
		if (getScope().declareClassScope || getScope().interfaceScope) {
			return;
		}

		if (context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_ASYNC)) {
			print(" async ");
		}
	}

	protected void printMethodArgs(JCMethodDecl methodDecl, Overload overload, boolean inOverload,
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
		for (JCVariableDecl param : methodDecl.getParameters()) {
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
			for (JCVariableDecl param : methodDecl.getParameters()) {
				print(param).println(";");
			}
			print("}");
		}
	}

	protected void printMethodModifiers(JCMethodDecl methodDecl, JCClassDecl parent, boolean constructor,
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

	protected void printVariableInitialization(JCClassDecl clazz, JCVariableDecl var) {
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

	protected void printInstanceInitialization(JCClassDecl clazz, MethodSymbol method) {
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
			for (JCTree member : clazz.defs) {
				if (member instanceof JCVariableDecl) {
					JCVariableDecl var = (JCVariableDecl) member;
					if (!var.sym.isStatic() && !context.hasAnnotationType(var.sym, JSweetConfig.ANNOTATION_ERASED)) {
						printVariableInitialization(clazz, var);
					}
				} else if (member instanceof JCBlock) {
					JCBlock block = (JCBlock) member;
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

	private void printInlinedMethod(Overload overload, JCMethodDecl method, List<? extends JCTree> args) {
		print("{").println().startIndent();
		if (getScope().innerClassNotStatic && getScope().constructor) {
			// the __parent added parameter is not part of the actual arguments
			printIndent().print(VAR_DECL_KEYWORD + " __args = Array.prototype.slice.call(arguments, [1]);").println();
		} else {
			printIndent().print(VAR_DECL_KEYWORD + " __args = arguments;").println();
		}
		for (int j = 0; j < method.getParameters().size(); j++) {
			if (args.get(j) instanceof JCVariableDecl) {
				if (method.getParameters().get(j).name.equals(((JCVariableDecl) args.get(j)).name)) {
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
				JCMethodInvocation inv = (JCMethodInvocation) ((JCExpressionStatement) method.getBody().stats
						.get(0)).expr;
				MethodSymbol ms = Util.findMethodDeclarationInType(context.types,
						(TypeSymbol) overload.coreMethod.sym.getEnclosingElement(), inv);
				for (JCMethodDecl md : overload.methods) {
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
				com.sun.tools.javac.util.List<JCStatement> stats = skipFirst ? method.getBody().stats.tail
						: method.getBody().stats;
				if (!stats.isEmpty() && stats.head.toString().startsWith("super(")) {
					printBlockStatement(stats.head);
					printFieldInitializations();
					if (!initialized) {
						printInstanceInitialization(getParent(JCClassDecl.class), method.sym);
					}
					if (!stats.tail.isEmpty()) {
						printIndent().print("((").print(") => {").startIndent().println();
						printBlockStatements(stats.tail);
						endIndent().printIndent().print("})(").print(");").println();
					}
				} else {
					if (!initialized) {
						printInstanceInitialization(getParent(JCClassDecl.class), method.sym);
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
		JCClassDecl clazz = getParent(JCClassDecl.class);
		for (JCTree t : clazz.getMembers()) {
			if (t instanceof JCVariableDecl && !getScope().fieldsWithInitializers.contains(t)) {
				JCVariableDecl field = (JCVariableDecl) t;
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
		for (JCVariableDecl field : getScope().fieldsWithInitializers) {
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

	protected void printBlockStatements(List<JCStatement> statements) {
		for (JCStatement statement : statements) {
			if (context.options.isDebugMode()) {
				JCMethodDecl methodDecl = getParent(JCMethodDecl.class);
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
							public void scan(JCTree tree) {
								if (tree == statement) {
									throw new RuntimeException();
								}
								boolean contextChange = false;
								if (tree instanceof JCBlock || tree instanceof JCEnhancedForLoop
										|| tree instanceof JCLambda || tree instanceof JCForLoop
										|| tree instanceof JCDoWhileLoop) {
									locals.push(new ArrayList<>());
									contextChange = true;
								}
								if (tree instanceof JCVariableDecl) {
									locals.peek().add(((JCVariableDecl) tree).name.toString());
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

	private void printBlockStatement(JCStatement statement) {
		printIndent();
		int pos = getCurrentPosition();
		print(statement);
		if (getCurrentPosition() == pos) {
			removeLastIndent();
			return;
		}
		if (!statementsWithNoSemis.contains(statement.getClass())) {
			if (statement instanceof JCLabeledStatement) {
				if (!statementsWithNoSemis.contains(((JCLabeledStatement) statement).body.getClass())) {
					print(";");
				}
			} else {
				print(";");
			}
		}
		println();
	}

	private String getOverloadMethodName(MethodSymbol method) {
		if (method.isConstructor()) {
			return "constructor";
		}
		StringBuilder sb = new StringBuilder(method.getSimpleName().toString());
		sb.append("$");
		for (VarSymbol p : method.getParameters()) {
			sb.append(context.types.erasure(p.type).toString().replace('.', '_').replace("[]", "_A"));
			sb.append("$");
		}
		if (!method.getParameters().isEmpty()) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	private void checkType(TypeSymbol type) {
		if (type instanceof ClassSymbol && !isMappedOrErasedType(type)) {
			String name = type.getSimpleName().toString();
			ModuleImportDescriptor moduleImport = getModuleImportDescriptor(name, (ClassSymbol) type);
			if (moduleImport != null) {
				useModule(false, moduleImport.getTargetPackage(), null, moduleImport.getImportedName(),
						moduleImport.getPathToImportedClass(), null);
			}
		}
	}

	private void printMethodParamsTest(Overload overload, JCMethodDecl m) {
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
	public void visitBlock(JCBlock block) {
		JCTree parent = getParent();
		boolean globals = (parent instanceof JCClassDecl)
				&& JSweetConfig.GLOBALS_CLASS_NAME.equals(((JCClassDecl) parent).name.toString());
		boolean initializer = (parent instanceof JCClassDecl) && !globals;
		if (initializer) {
			if (getScope().interfaceScope) {
				report(block, JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, ((JCClassDecl) parent).name);
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

	private void printStaticInitializer(JCBlock block) {
		if (getScope().isEnumScope()) {
			// static blocks are initialized in the enum wrapper class
			return;
		}

		if (getScope().isDeclareClassScope()) {
			// static init block are erased in declare class
			return;
		}

		int static_i = 0;
		for (JCTree m : ((JCClassDecl) getParent()).getMembers()) {
			if (m instanceof JCBlock) {
				if (((JCBlock) m).isStatic()) {
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

	private boolean isLazyInitialized(VarSymbol var) {
		return var.isStatic() && context.lazyInitializedStatics.contains(var)
				&& /* enum fields are not lazy initialized */ !(var.isEnum()
						&& var.getEnclosingElement().equals(var.type.tsym));
	}

	/**
	 * Prints a variable declaration tree.
	 */
	@Override
	public void visitVarDef(JCVariableDecl varDecl) {
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
			if (varDecl.init instanceof JCNewClass) {
				JCNewClass newClass = (JCNewClass) varDecl.init;
				if (newClass.def != null) {
					initAnonymousClass(newClass);
				}
			}
		} else {
			JCTree parent = getParent();

			if (getScope().enumWrapperClassScope && varDecl.sym.getKind() == ElementKind.ENUM_CONSTANT) {
				return;
			}

			String name = getIdentifier(varDecl.sym);
			if (context.getFieldNameMapping(varDecl.sym) != null) {
				name = context.getFieldNameMapping(varDecl.sym);
			}

			boolean confictInDefinitionScope = false;

			if (parent instanceof JCClassDecl) {
				MethodSymbol m = Util.findMethodDeclarationInType(context.types, ((JCClassDecl) parent).sym, name,
						null);
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

			boolean globals = (parent instanceof JCClassDecl)
					&& JSweetConfig.GLOBALS_CLASS_NAME.equals(((JCClassDecl) parent).name.toString());

			if (globals && !varDecl.mods.getFlags().contains(Modifier.STATIC)) {
				report(varDecl, varDecl.name, JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
				return;
			}

			globals = globals || (parent instanceof JCClassDecl && (((JCClassDecl) parent).sym.isInterface()
					|| getScope().interfaceScope && varDecl.sym.isStatic()));

			if (parent instanceof JCClassDecl) {
				printDocComment(varDecl);
			}

			print(varDecl.mods);

			if (!globals && parent instanceof JCClassDecl) {
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
								((JCClassDecl) parent).name);
					}
				}

				if (varDecl.mods.getFlags().contains(Modifier.STATIC)) {
					if (!getScope().interfaceScope) {
						print("static ");
					}
				}
			}
			if (!getScope().interfaceScope && parent instanceof JCClassDecl) {
				if (context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_OPTIONAL)) {
					report(varDecl, varDecl.name, JSweetProblem.USELESS_OPTIONAL_ANNOTATION, varDecl.name,
							((JCClassDecl) parent).name);
				}
			}
			boolean ambient = context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_AMBIENT);
			if (globals || !(parent instanceof JCClassDecl || parent instanceof JCMethodDecl
					|| parent instanceof JCLambda)) {
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
				if (!(inArgListTail && (parent instanceof JCForLoop))) {
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
				JCClassDecl clazz = (JCClassDecl) parent;
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
					 * if (getScope().enumWrapperClassScope) { JCNewClass newClass = (JCNewClass)
					 * varDecl.init; print("new "
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
					if (!(parent instanceof JCClassDecl && getScope().innerClassNotStatic && !varDecl.sym.isStatic()
							&& !Util.isConstantOrNullField(varDecl))) {
						if (!globals && parent instanceof JCClassDecl && getScope().interfaceScope) {
							report(varDecl, varDecl.name, JSweetProblem.INVALID_FIELD_INITIALIZER_IN_INTERFACE,
									varDecl.name, ((JCClassDecl) parent).name);
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
	public void visitImport(JCImport importDecl) {
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
		print("this.");
		JCClassDecl parent = getParent(JCClassDecl.class);
		int level = 0;
		boolean foundInParent = Util.findFirstDeclarationInClassAndSuperClasses(parent.sym, accessedElementName,
				kind) != null;
		if (!foundInParent) {
			while (getScope(level++).innerClassNotStatic) {
				parent = getParent(JCClassDecl.class, parent);
				if (parent != null && Util.findFirstDeclarationInClassAndSuperClasses(parent.sym, accessedElementName,
						kind) != null) {
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
	public void visitSelect(JCFieldAccess fieldAccess) {
		if (!getAdapter().substitute(ExtendedElementFactory.INSTANCE.create(fieldAccess))) {
			if (fieldAccess.selected.type.tsym instanceof PackageSymbol) {
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
				if (fieldAccess.type instanceof Type.ClassType
						&& context.isInterface(((Type.ClassType) fieldAccess.type).typarams_field.head.tsym)) {
					print("\"").print(context
							.getRootRelativeJavaName(((Type.ClassType) fieldAccess.type).typarams_field.head.tsym))
							.print("\"");
				} else {
					String name = fieldAccess.selected.type.tsym.toString();
					if (context.isMappedType(name)) {
						String target = context.getTypeMappingTarget(name);
						if (CONSTRUCTOR_TYPE_MAPPING.containsKey(target)) {
							print(mapConstructorType(target));
						} else {
							print("\"").print(context.getRootRelativeJavaName(
									((Type.ClassType) fieldAccess.type).typarams_field.head.tsym)).print("\"");
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
					JCClassDecl parent = getParent(JCClassDecl.class);
					int level = 0;
					boolean foundInParent = false;
					while (getScope(level++).innerClassNotStatic) {
						parent = getParent(JCClassDecl.class, parent);
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
					if (selected.equals("super") && (fieldAccess.sym instanceof VarSymbol)) {
						print("this.");
					} else if (getScope().innerClassNotStatic
							&& ("this".equals(selected) || selected.endsWith(".this"))) {
						printInnerClassAccess(fieldAccess.name.toString(), ElementKind.FIELD);
					} else {
						boolean accessSubstituted = false;
						if (fieldAccess.sym instanceof VarSymbol) {
							VarSymbol varSym = (VarSymbol) fieldAccess.sym;
							if (varSym.isStatic() && varSym.owner.isInterface()
									&& varSym.owner != Util.getAccessedSymbol(fieldAccess.selected)) {
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
				if (fieldAccess.sym instanceof VarSymbol && context.getFieldNameMapping(fieldAccess.sym) != null) {
					fieldName = context.getFieldNameMapping(fieldAccess.sym);
				} else {
					fieldName = getIdentifier(fieldAccess.sym);
				}
				if (fieldAccess.sym instanceof ClassSymbol) {
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
				if (fieldAccess.sym instanceof VarSymbol && isLazyInitialized((VarSymbol) fieldAccess.sym)) {
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
	public void visitApply(JCMethodInvocation inv) {

		boolean debugMode = false;
		if (context.options.isDebugMode()) {
			if (Util.getAccessedSymbol(inv.meth) instanceof MethodSymbol) {
				MethodSymbol methodSymbol = (MethodSymbol) Util.getAccessedSymbol(inv.meth);
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
	public void printDefaultMethodInvocation(JCMethodInvocation inv) {
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
		MethodSymbol methSym = null;
		String methodName = null;
		boolean keywordHandled = false;
		if (targetIsThisOrStaticImported) {
			JCImport staticImport = getStaticGlobalImport(methName);
			if (staticImport == null) {
				JCClassDecl p = getParent(JCClassDecl.class);
				methSym = p == null ? null : Util.findMethodDeclarationInType(context.types, p.sym, methName, type);
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
						TypeSymbol target = Util.getStaticImportTarget(
								getContext().getDefaultMethodCompilationUnit(getParent(JCMethodDecl.class)), methName);
						if (target != null) {
							print(getRootRelativeName(target) + ".");
						}
					} else {
						TypeSymbol target = Util.getStaticImportTarget(getCompilationUnit(), methName);
						if (target != null) {
							print(getRootRelativeName(target) + ".");
						}
					}

					if (getScope().innerClass) {
						JCClassDecl parent = getParent(JCClassDecl.class);
						int level = 0;
						MethodSymbol method = null;
						if (parent != null) {
							while (getScope(level++).innerClass) {
								parent = getParent(JCClassDecl.class, parent);
								if ((method = Util.findMethodDeclarationInType(context.types, parent.sym, methName,
										type)) != null) {
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
				JCFieldAccess staticFieldAccess = (JCFieldAccess) staticImport.qualid;
				methSym = Util.findMethodDeclarationInType(context.types, staticFieldAccess.selected.type.tsym,
						methName, type);
				if (methSym != null) {
					Map<String, VarSymbol> vars = new HashMap<>();
					Util.fillAllVariablesInScope(vars, getStack(), inv, getParent(JCMethodDecl.class));
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
			if (inv.meth instanceof JCFieldAccess) {
				JCExpression selected = ((JCFieldAccess) inv.meth).selected;
				if (context.isFunctionalType(selected.type.tsym)) {
					anonymous = true;
				}
				methSym = Util.findMethodDeclarationInType(context.types, selected.type.tsym, methName, type);
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
			if (inv.meth instanceof JCFieldAccess) {
				JCExpression selected = ((JCFieldAccess) inv.meth).selected;
				print(selected);
			}
		} else {
			// method with name
			if (inv.meth instanceof JCFieldAccess && applyVarargs && !targetIsThisOrStaticImported && !isStatic) {
				print("(o => o");

				String accessedMemberName;
				if (keywordHandled) {
					accessedMemberName = ((JCFieldAccess) inv.meth).name.toString();
				} else {
					if (methSym == null) {
						methSym = (MethodSymbol) ((JCFieldAccess) inv.meth).sym;
					}
					if (methSym != null) {
						accessedMemberName = context.getActualName(methSym);
					} else {
						accessedMemberName = ((JCFieldAccess) inv.meth).name.toString();
					}
				}
				print(getTSMemberAccess(accessedMemberName, true));
			} else if (methodName != null) {
				print(getTSMemberAccess(methodName, removeLastChar('.')));
			} else {
				if (keywordHandled) {
					print(inv.meth);
				} else {
					if (methSym == null && inv.meth instanceof JCFieldAccess
							&& ((JCFieldAccess) inv.meth).sym instanceof MethodSymbol) {
						methSym = (MethodSymbol) ((JCFieldAccess) inv.meth).sym;
					}
					if (methSym != null && inv.meth instanceof JCFieldAccess) {
						JCExpression selected = ((JCFieldAccess) inv.meth).selected;
						if (!GLOBALS_CLASS_NAME.equals(selected.type.tsym.getSimpleName().toString())) {
							if (getScope().innerClassNotStatic
									&& ("this".equals(selected.toString()) || selected.toString().endsWith(".this"))) {
								printInnerClassAccess(methSym.name.toString(), methSym.getKind());
							} else {
								print(selected).print(".");
							}
						} else {
							if (context.useModules) {
								if (!((ClassSymbol) selected.type.tsym).sourcefile.getName()
										.equals(getCompilationUnit().sourcefile.getName())) {
									// TODO: when using several qualified
									// Globals classes, we
									// need to disambiguate (use qualified
									// name with
									// underscores)
									print(GLOBALS_CLASS_NAME).print(".");
								}
							}

							Map<String, VarSymbol> vars = new HashMap<>();
							Util.fillAllVariablesInScope(vars, getStack(), inv, getParent(JCMethodDecl.class));
							if (vars.containsKey(methName)) {
								report(inv, JSweetProblem.HIDDEN_INVOCATION, methName);
							}
						}
					}
					if (methSym != null) {
						if (context.isInvalidOverload(methSym) && !methSym.getParameters().isEmpty()
								&& !Util.hasTypeParameters(methSym) && !Util.hasVarargs(methSym)
								&& getParent(JCMethodDecl.class) != null
								&& !getParent(JCMethodDecl.class).sym.isDefault()) {
							if (context.isInterface((TypeSymbol) methSym.getEnclosingElement())) {
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
				for (JCExpression argument : inv.typeargs) {
					substituteAndPrintType(argument).print(",");
				}
				removeLastChar();
				print(">");
			} else {
				// force type arguments to any because they are inferred to
				// {} by default
				if (methSym != null && !methSym.getTypeParameters().isEmpty()) {
					ClassSymbol target = (ClassSymbol) methSym.getEnclosingElement();
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
			} else if (inv.meth instanceof JCFieldAccess && !targetIsThisOrStaticImported && !isStatic) {
				contextVar = "o";
			}

			print(contextVar + ", ");

			if (inv.args.size() > 1) {
				print("[");
			}
		}

		int argsLength = applyVarargs ? inv.args.size() - 1 : inv.args.size();

		if (getScope().innerClassNotStatic && "super".equals(methName)) {
			TypeSymbol s = getParent(JCClassDecl.class).extending.type.tsym;
			if (s.getEnclosingElement() instanceof ClassSymbol && !s.isStatic()) {
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
			JCClassDecl p = getParent(JCClassDecl.class);
			methSym = p == null ? null : Util.findMethodDeclarationInType(context.types, p.sym, "this", type);
		}
		for (int i = 0; i < argsLength; i++) {
			JCExpression arg = inv.args.get(i);
			if (inv.meth.type != null) {
				// varargs transmission with TS ... notation
				List<Type> argTypes = ((MethodType) inv.meth.type).argtypes;
				Type paramType = i < argTypes.size() ? argTypes.get(i) : argTypes.get(argTypes.size() - 1);
				if (i == argsLength - 1 && !applyVarargs && methSym != null && methSym.isVarArgs()) {
					if (arg instanceof JCIdent && ((JCIdent) arg).sym instanceof VarSymbol) {
						VarSymbol var = (VarSymbol) ((JCIdent) arg).sym;
						if (var.owner instanceof MethodSymbol && ((MethodSymbol) var.owner).isVarArgs()
								&& ((MethodSymbol) var.owner).getParameters().last() == var) {
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
			if (inv.meth instanceof JCFieldAccess && !targetIsThisOrStaticImported && !isStatic) {
				print("))(").print(((JCFieldAccess) inv.meth).selected);
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

	private JCImport getStaticGlobalImport(String methName) {
		if (compilationUnit == null) {
			return null;
		}
		for (JCImport i : compilationUnit.getImports()) {
			if (i.staticImport) {
				if (i.qualid.toString().endsWith(JSweetConfig.GLOBALS_CLASS_NAME + "." + methName)) {
					return i;
				}
			}
		}
		return null;
	}

	private String getStaticContainerFullName(JCImport importDecl) {
		if (importDecl.getQualifiedIdentifier() instanceof JCFieldAccess) {
			JCFieldAccess fa = (JCFieldAccess) importDecl.getQualifiedIdentifier();
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
	public void visitIdent(JCIdent ident) {
		String name = ident.toString();

		if (getScope().inlinedConstructorArgs != null) {
			if (ident.sym instanceof VarSymbol && getScope().inlinedConstructorArgs.contains(name)) {
				print("__args[" + getScope().inlinedConstructorArgs.indexOf(name) + "]");
				return;
			}
		}

		if (!getAdapter().substitute(ExtendedElementFactory.INSTANCE.create(ident))) {
			boolean lazyInitializedStatic = false;
			// add this of class name if ident is a field
			if (ident.sym instanceof VarSymbol && !ident.sym.name.equals(context.names._this)
					&& !ident.sym.name.equals(context.names._super)) {
				VarSymbol varSym = (VarSymbol) ident.sym; // findFieldDeclaration(currentClass,
				// ident.name);
				if (varSym != null) {
					if (varSym.owner instanceof ClassSymbol) {
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
								if (!context.useModules && !varSym.owner.equals(getParent(JCClassDecl.class).sym)) {
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
						if (varSym.owner instanceof MethodSymbol && isAnonymousClass()
								&& getScope(1).finalVariables
										.get(getScope(1).anonymousClasses.indexOf(getParent(JCClassDecl.class)))
										.contains(varSym)) {
							print("this.");
						} else {
							if (!context.useModules && varSym.owner instanceof MethodSymbol) {
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
			if (ident.sym instanceof ClassSymbol) {
				ClassSymbol clazz = (ClassSymbol) ident.sym;
				boolean prefixAdded = false;
				if (getScope().defaultMethodScope) {
					if (Util.isImported(getContext().getDefaultMethodCompilationUnit(getParent(JCMethodDecl.class)),
							clazz)) {
						String rootRelativeName = getRootRelativeName(clazz.getEnclosingElement());
						if (!rootRelativeName.isEmpty()) {
							print(rootRelativeName + ".");
						}
						prefixAdded = true;
					}
				}
				// add parent class name if ident is an inner class of the
				// current class
				if (!prefixAdded && clazz.getEnclosingElement() instanceof ClassSymbol) {
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
				if (!prefixAdded && !context.useModules && !clazz.equals(getParent(JCClassDecl.class).sym)) {
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
	public void visitTypeApply(JCTypeApply typeApply) {
		substituteAndPrintType(typeApply);
	}

	private int initAnonymousClass(JCNewClass newClass) {
		int anonymousClassIndex = getScope().anonymousClasses.indexOf(newClass.def);
		if (anonymousClassIndex == -1) {
			anonymousClassIndex = getScope().anonymousClasses.size();
			getScope().anonymousClasses.add(newClass.def);
			getScope().anonymousClassesConstructors.add(newClass);
			LinkedHashSet<VarSymbol> finalVars = new LinkedHashSet<>();
			getScope().finalVariables.add(finalVars);
			new TreeScanner() {
				public void visitIdent(JCIdent var) {
					if (var.sym != null && (var.sym instanceof VarSymbol)) {
						VarSymbol varSymbol = (VarSymbol) var.sym;
						if (varSymbol.getEnclosingElement() instanceof MethodSymbol && varSymbol.getEnclosingElement()
								.getEnclosingElement() == getParent(JCClassDecl.class).sym) {
							finalVars.add((VarSymbol) var.sym);
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
	public void visitNewClass(JCNewClass newClass) {
		ClassSymbol clazz = ((ClassSymbol) newClass.clazz.type.tsym);
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
					for (JCTree m : newClass.def.getMembers()) {
						if (m instanceof JCBlock) {
							initializationBlockFound = true;
							List<VarSymbol> initializedVars = new ArrayList<>();
							for (JCTree s : ((JCBlock) m).stats) {
								boolean currentStatementPrinted = false;
								if (s instanceof JCExpressionStatement
										&& ((JCExpressionStatement) s).expr instanceof JCAssign) {
									JCAssign assignment = (JCAssign) ((JCExpressionStatement) s).expr;
									VarSymbol var = null;
									if (assignment.lhs instanceof JCFieldAccess) {
										var = Util.findFieldDeclaration(clazz, ((JCFieldAccess) assignment.lhs).name);
										printIndent().print(var.getSimpleName().toString());
									} else if (assignment.lhs instanceof JCIdent) {
										var = Util.findFieldDeclaration(clazz, ((JCIdent) assignment.lhs).name);
										printIndent().print(assignment.lhs.toString());
									} else {
										continue;
									}
									initializedVars.add(var);
									print(": ").print(assignment.rhs).print(",").println();
									currentStatementPrinted = true;
									statementPrinted = true;
								} else if (s instanceof JCExpressionStatement
										&& ((JCExpressionStatement) s).expr instanceof JCMethodInvocation) {
									JCMethodInvocation invocation = (JCMethodInvocation) ((JCExpressionStatement) s).expr;
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
							for (Symbol s : clazz.getEnclosedElements()) {
								if (s instanceof VarSymbol) {
									if (!initializedVars.contains(s)) {
										if (!context.hasAnnotationType(s, JSweetConfig.ANNOTATION_OPTIONAL)) {
											report(m, JSweetProblem.UNINITIALIZED_FIELD, s);
										}
									}
								}
							}
						}
						if (m instanceof JCMethodDecl) {
							JCMethodDecl method = (JCMethodDecl) m;
							if (!method.sym.isConstructor()) {
								printIndent().print(method.getName() + ": (");
								for (JCVariableDecl param : method.getParameters()) {
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
					for (Symbol s : clazz.getEnclosedElements()) {
						if (s instanceof VarSymbol) {
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
				for (JCTree m : newClass.def.getMembers()) {
					if (m instanceof JCBlock) {
						for (JCTree s : ((JCBlock) m).stats) {
							boolean currentStatementPrinted = false;
							if (s instanceof JCExpressionStatement
									&& ((JCExpressionStatement) s).expr instanceof JCAssign) {
								JCAssign assignment = (JCAssign) ((JCExpressionStatement) s).expr;
								VarSymbol var = null;
								if (assignment.lhs instanceof JCFieldAccess) {
									var = Util.findFieldDeclaration(clazz, ((JCFieldAccess) assignment.lhs).name);
									printIndent().print("target['").print(var.getSimpleName().toString()).print("']");
								} else if (assignment.lhs instanceof JCIdent) {
									printIndent().print("target['").print(assignment.lhs.toString()).print("']");
								} else {
									continue;
								}
								print(" = ").print(assignment.rhs).print(";").println();
								currentStatementPrinted = true;
							} else if (s instanceof JCExpressionStatement
									&& ((JCExpressionStatement) s).expr instanceof JCMethodInvocation) {
								JCMethodInvocation invocation = (JCMethodInvocation) ((JCExpressionStatement) s).expr;
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
	public void printDefaultNewClass(JCNewClass newClass) {
		String mappedType = context.getTypeMappingTarget(newClass.clazz.type.toString());
		if (typeChecker.checkType(newClass, null, newClass.clazz)) {

			boolean applyVarargs = true;
			MethodSymbol methSym = (MethodSymbol) newClass.constructor;
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
				if (newClass.clazz instanceof JCTypeApply) {
					JCTypeApply typeApply = (JCTypeApply) newClass.clazz;
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
						printAnyTypeArguments(((ClassSymbol) newClass.clazz.type.tsym).getTypeParameters().length());
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
	public AbstractTreePrinter printConstructorArgList(JCNewClass newClass, boolean localClass) {
		boolean printed = false;
		if (localClass || (getScope().anonymousClasses.contains(newClass.def)
				&& !newClass.def.getModifiers().getFlags().contains(Modifier.STATIC))) {
			print("this");
			if (!newClass.args.isEmpty()) {
				print(", ");
			}
			printed = true;
		} else if ((newClass.clazz.type.tsym.getEnclosingElement() instanceof ClassSymbol
				&& !newClass.clazz.type.tsym.getModifiers().contains(Modifier.STATIC))) {
			print("this");
			JCClassDecl parent = getParent(JCClassDecl.class);
			ClassSymbol parentSymbol = parent == null ? null : parent.sym;
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
			for (VarSymbol v : getScope().finalVariables.get(index)) {
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
	public void visitLiteral(JCLiteral literal) {
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
	public void visitForeachLoop(JCEnhancedForLoop foreachLoop) {
		String indexVarName = "index" + Util.getId();
		boolean[] hasLength = { false };
		TypeSymbol targetType = foreachLoop.expr.type.tsym;
		Util.scanMemberDeclarationsInType(targetType, getAdapter().getErasedTypes(), element -> {
			if (element instanceof VarSymbol) {
				if ("length".equals(element.getSimpleName().toString()) && Util.isNumber(((VarSymbol) element).type)) {
					hasLength[0] = true;
					return false;
				}
			}
			return true;
		});
		if (!getAdapter().substituteForEachLoop(new ForeachLoopElementSupport(foreachLoop), hasLength[0],
				indexVarName)) {
			boolean noVariable = foreachLoop.expr instanceof JCIdent || foreachLoop.expr instanceof JCFieldAccess;
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

	protected void visitBeforeForEachBody(JCEnhancedForLoop foreachLoop) {
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
	public void visitBinary(JCBinary binary) {
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
				if (binary.lhs instanceof JCLiteral) {
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
				if (binary.rhs instanceof JCLiteral) {
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

	protected void printBinaryRightOperand(JCBinary binary) {
		print(binary.rhs);
	}

	protected void printBinaryLeftOperand(JCBinary binary) {
		print(binary.lhs);
	}

	/**
	 * Prints an <code>if</code> tree.
	 */
	@Override
	public void visitIf(JCIf ifStatement) {
		print("if").print(ifStatement.cond).print(" ");
		print(ifStatement.thenpart);
		if (!(ifStatement.thenpart instanceof JCBlock)) {
			if (!statementsWithNoSemis.contains(ifStatement.thenpart.getClass())) {
				print(";");
			}
		}
		if (ifStatement.elsepart != null) {
			print(" else ");
			print(ifStatement.elsepart);
			if (!(ifStatement.elsepart instanceof JCBlock)) {
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
	public void visitReturn(JCReturn returnStatement) {
		print("return");
		if (returnStatement.expr != null) {
			JCTree parentFunction = getFirstParent(JCMethodDecl.class, JCLambda.class);
			if (returnStatement.expr.type == null) {
				report(returnStatement, JSweetProblem.CANNOT_ACCESS_THIS,
						parentFunction == null ? returnStatement.toString() : parentFunction.toString());
				return;
			}
			print(" ");
			Type returnType = null;
			if (parentFunction != null) {
				if (parentFunction instanceof JCMethodDecl) {
					returnType = ((JCMethodDecl) parentFunction).restype.type;
				} else {
					// TODO: this cannot work! Calculate the return type of the
					// lambda
					// either from the functional type type arguments, of from
					// the method defining the lambda's signature
					// returnType = ((JCLambda) parentFunction).type;
				}
			}
			if (!substituteAssignedExpression(returnType, returnStatement.expr)) {
				print(returnStatement.expr);
			}
		}
	}

	private boolean staticInitializedAssignment = false;

	private VarSymbol getStaticInitializedField(JCTree expr) {
		if (expr instanceof JCIdent) {
			return context.lazyInitializedStatics.contains(((JCIdent) expr).sym) ? (VarSymbol) ((JCIdent) expr).sym
					: null;
		} else if (expr instanceof JCFieldAccess) {
			return context.lazyInitializedStatics.contains(((JCFieldAccess) expr).sym)
					? (VarSymbol) ((JCFieldAccess) expr).sym
					: null;
		} else {
			return null;
		}
	}

	/**
     * Prints an assignment operator tree (<code>+=, -=, *=, ...</code>).
     */
    @Override
    public void visitAssignop(JCAssignOp assignOp) {
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
                // Type lhsType = assignOp.lhs.type;
                boolean isLeftOperandString = (assignOp.lhs.type.tsym == context.symtab.stringType.tsym);
                Type rightPromotedType = isLeftOperandString ? context.symtab.charType : context.symtab.intType;
                substituteAndPrintAssignedExpression(rightPromotedType, assignOp.rhs);
            } else {
            	printAssignWithOperatorRightOperand(assignOp);
            }
        }
    }

	protected void printAssignWithOperatorRightOperand(JCAssignOp assignOp) {
		print(assignOp.rhs);
	}

	/**
	 * Prints a <code>condition?trueExpr:falseExpr</code> tree.
	 */
	@Override
	public void visitConditional(JCConditional conditional) {
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
	public void visitForLoop(JCForLoop forLoop) {
		print("for(").printArgList(null, forLoop.init).print("; ").print(forLoop.cond).print("; ")
				.printArgList(null, forLoop.step).print(") ");
		print("{");
		visitBeforeForBody(forLoop);
		print(forLoop.body).print(";");
		print("}");
	}

	protected void visitBeforeForBody(JCForLoop forLoop) {
	}

	/**
	 * Prints a <code>continue</code> tree.
	 */
	@Override
	public void visitContinue(JCContinue continueStatement) {
		print("continue");
		if (continueStatement.label != null) {
			print(" ").print(continueStatement.label.toString());
		}
	}

	/**
	 * Prints a <code>break</code> tree.
	 */
	@Override
	public void visitBreak(JCBreak breakStatement) {
		print("break");
		if (breakStatement.label != null) {
			print(" ").print(breakStatement.label.toString());
		}
	}

	/**
	 * Prints a labeled statement tree.
	 */
	@Override
	public void visitLabelled(JCLabeledStatement labelledStatement) {
		JCTree parent = getParent(JCMethodDecl.class);
		if (parent == null) {
			parent = getParent(JCBlock.class);
			while (parent != null && getParent(JCBlock.class, parent) != null) {
				parent = getParent(JCBlock.class, parent);
			}
		}
		boolean[] used = { false };
		new TreeScanner() {
			public void visitBreak(JCBreak b) {
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
	public void visitTypeArray(JCArrayTypeTree arrayType) {
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
				if (newArray.dims.head instanceof JCLiteral && ((int) ((JCLiteral) newArray.dims.head).value) <= 10) {
					boolean hasElements = false;
					print("[");
					for (int i = 0; i < (int) ((JCLiteral) newArray.dims.head).value; i++) {
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
				for (JCExpression e : newArray.elems) {
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
				JCStatement statement = null;
				VarSymbol[] staticInitializedField = { null };
				switch (unary.getTag()) {
				case POSTDEC:
				case POSTINC:
				case PREDEC:
				case PREINC:
					staticInitializedAssignment = (staticInitializedField[0] = getStaticInitializedField(
							unary.arg)) != null;
					if (staticInitializedAssignment) {
						statement = getParent(JCStatement.class);
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
	public void visitSwitch(JCSwitch switchStatement) {
		print("switch(");
		print(switchStatement.selector);
		if (context.types.isSameType(context.symtab.charType,
				context.types.unboxedTypeOrType(switchStatement.selector.type))) {
			print(".charCodeAt(0)");
		}
		print(") {").println();
		for (JCCase caseStatement : switchStatement.cases) {
			printIndent();
			print(caseStatement);
		}
		printIndent().print("}");
	}

	protected void printCaseStatementPattern(JCExpression pattern) {
	}

	/**
	 * Prints a <code>case</code> tree.
	 */
	@Override
	public void visitCase(JCCase caseStatement) {
		if (caseStatement.pat != null) {
			print("case ");
			if (!getAdapter().substituteCaseStatementPattern(new CaseElementSupport(caseStatement),
					ExtendedElementFactory.INSTANCE.create(caseStatement.pat))) {
				if (caseStatement.pat.type.isPrimitive()
						|| context.types.isSameType(context.symtab.stringType, caseStatement.pat.type)) {
					if (caseStatement.pat instanceof JCIdent) {
						Object value = ((VarSymbol) ((JCIdent) caseStatement.pat).sym).getConstValue();
						if (context.types.isSameType(context.symtab.stringType, caseStatement.pat.type)) {
							print("\"" + value + "\" /* " + caseStatement.pat + " */");
						} else {
							print("" + value + " /* " + caseStatement.pat + " */");
						}
					} else {
						if (context.types.isSameType(context.symtab.charType, caseStatement.pat.type)) {
							JCExpression caseExpression = caseStatement.pat;
							if (caseExpression instanceof JCTypeCast) {
								caseExpression = ((JCTypeCast) caseExpression).expr;
							}

							if (caseExpression instanceof JCLiteral) {
								print("" + ((JCLiteral) caseExpression).value + " /* " + caseStatement.pat + " */");
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
		for (JCStatement statement : caseStatement.stats) {
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
	public void visitDoLoop(JCDoWhileLoop doWhileLoop) {
		print("do ");
		print("{");
		visitBeforeDoWhileBody(doWhileLoop);
		if (doWhileLoop.body instanceof JCBlock) {
			print(doWhileLoop.body);
		} else {
			print(doWhileLoop.body).print(";");
		}
		print("}");
		print(" while(").print(doWhileLoop.cond).print(")");
	}

	protected void visitBeforeDoWhileBody(JCDoWhileLoop doWhileLoop) {
	}

	/**
	 * Prints a <code>while</code> loop tree.
	 */
	@Override
	public void visitWhileLoop(JCWhileLoop whileLoop) {
		print("while(").print(whileLoop.cond).print(") ");
		print("{");
		visitBeforeWhileBody(whileLoop);
		print(whileLoop.body);
		print("}");
	}

	protected void visitBeforeWhileBody(JCWhileLoop whileLoop) {
	}

	/**
	 * Prints a variable assignment tree.
	 */
	@Override
	public void visitAssign(JCAssign assign) {
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
	public void visitTry(JCTry tryStatement) {
		boolean resourced = tryStatement.resources != null && !tryStatement.resources.isEmpty();
		if (resourced) {
			for (JCTree resource : tryStatement.resources) {
				print(resource).println(";").printIndent();
			}
		} else if (tryStatement.catchers.isEmpty() && tryStatement.finalizer == null) {
			report(tryStatement, JSweetProblem.TRY_WITHOUT_CATCH_OR_FINALLY);
		}
		print("try ").print(tryStatement.body);
		if (tryStatement.catchers.size() > 1) {
			print(" catch(__e) {").startIndent();
			for (JCCatch catcher : tryStatement.catchers) {
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
				for (JCTree resource : tryStatement.resources.reverse()) {
					if (resource instanceof JCVariableDecl) {
						println().printIndent().print(((JCVariableDecl) resource).name + ".close();");
					}
				}
				endIndent();
			}
			if (tryStatement.finalizer != null) {
				startIndent();// .printIndent();
				for (JCStatement statement : tryStatement.finalizer.getStatements()) {
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
	public void visitCatch(JCCatch catcher) {
		print(" catch(").print(catcher.param.name.toString()).print(") ");
		print(catcher.body);
	}

	/**
	 * Prints a lambda expression tree.
	 */
	@Override
	public void visitLambda(JCLambda lamba) {
		boolean regularFunction = false;
		if (getParent() instanceof JCMethodInvocation
				&& ((JCMethodInvocation) getParent()).meth.toString().endsWith("function")
				&& getParentOfParent() instanceof JCMethodInvocation
				&& ((JCMethodInvocation) getParentOfParent()).meth.toString().endsWith("$noarrow")) {
			MethodInvocationElement invocation = (MethodInvocationElement) ExtendedElementFactory.INSTANCE
					.create(getParent());
			if (JSweetConfig.UTIL_CLASSNAME.equals(invocation.getMethod().getEnclosingElement().toString())) {
				regularFunction = true;
			}
		}
		Map<String, VarSymbol> varAccesses = new HashMap<>();
		Util.fillAllVariableAccesses(varAccesses, lamba);
		Collection<VarSymbol> finalVars = new ArrayList<>(varAccesses.values());
		if (!varAccesses.isEmpty()) {
			Map<String, VarSymbol> varDefs = new HashMap<>();
			int parentIndex = getStack().size() - 2;
			int i = parentIndex;
			JCStatement statement = null;
			while (i > 0 && getStack().get(i).getKind() != Kind.LAMBDA_EXPRESSION
					&& getStack().get(i).getKind() != Kind.METHOD) {
				if (statement == null && getStack().get(i) instanceof JCStatement) {
					statement = (JCStatement) getStack().get(i);
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
			for (VarSymbol var : finalVars) {
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
			for (VarSymbol var : finalVars) {
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
		boolean exprIsInstance = memberReference.expr.toString().equals("this") || memberReference.expr.toString().equals("super") ||
				(memberReference.expr instanceof JCIdent && ((JCIdent) memberReference.expr).sym instanceof VarSymbol) ||
				(memberReference.expr instanceof JCFieldAccess && ((JCFieldAccess) memberReference.expr).sym instanceof VarSymbol);

		if (memberReference.sym instanceof MethodSymbol) {
			MethodSymbol method = (MethodSymbol) memberReference.sym;
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
				for (VarSymbol var : method.getParameters()) {
					print(var.name.toString());
					print(",");
					argumentsPrinted++;
				}
			}
			if(argumentsPrinted > 0) {
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
					substituteAndPrintType(((JCArrayTypeTree) memberReference.expr).elemtype);
					print(">");
				} else {
					print("new ").print(memberReference.expr);
				}
			} else {
				if(printAsInstanceMethod && !exprIsInstance) {
					print("instance$").print(memberReferenceSimpleName);
				} else {
					print(memberReference.expr);
				}
				print(".").print(memberReference.name.toString());
			}
		}

		if (memberReference.sym instanceof MethodSymbol) {
			MethodSymbol method = (MethodSymbol) memberReference.sym;

			print("(");
			if (method.getParameters() != null) {
				for (VarSymbol var : method.getParameters()) {
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
			for (JCExpression e : typeParameter.bounds) {
				substituteAndPrintType(e).print(" & ");
			}
			removeLastChars(3);
		}
	}

	/** Prints a <code>synchronized</code> tree. */
	@Override
	public void visitSynchronized(JCSynchronized sync) {
		report(sync, JSweetProblem.SYNCHRONIZATION);
		if (sync.body != null) {
			print(sync.body);
		}
	}

	/**
	 * Prints either a string, or the tree if the the string is null.
	 * 
	 * @param exprStr
	 *            a string to be printed as is if not null
	 * @param expr
	 *            a tree to be printed if exprStr is null
	 */
	public void print(String exprStr, JCTree expr) {
		if (exprStr == null) {
			print(expr);
		} else {
			print(exprStr);
		}
	}

	private void printInstanceOf(String exprStr, JCTree expr, Type type) {
		printInstanceOf(exprStr, expr, type, false);
	}

	private void printInstanceOf(String exprStr, JCTree expr, Type type, boolean checkFirstArrayElement) {
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
			for (JCExpression e : annotation.getArguments()) {
				print(e);
				print(", ");
			}
			removeLastChars(2);
			print(" } ");
			isAnnotationScope = false;
			print(")");
		} else if (getParentOfParent() instanceof JCClassDecl) {
			print("()");
		}
		println().printIndent();
	}

	Stack<Type> rootConditionalAssignedTypes = new Stack<>();
	Stack<Type> rootArrayAssignedTypes = new Stack<>();

	@Override
	protected boolean substituteAssignedExpression(Type assignedType, JCExpression expression) {
		if (assignedType == null) {
			return false;
		}
		if (assignedType.isInterface() && expression.type.tsym.isEnum()) {
			String relTarget = getRootRelativeName((Symbol) expression.type.tsym);
			print(relTarget).print("[\"" + Java2TypeScriptTranslator.ENUM_WRAPPER_CLASS_WRAPPERS + "\"][")
					.print(expression).print("]");
			return true;
		}
		if (expression instanceof JCConditional) {
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
			if (expression instanceof JCLambda) {
				if (assignedType.tsym.isInterface() && !context.isFunctionalType(assignedType.tsym)) {
					JCLambda lambda = (JCLambda) expression;
					MethodSymbol method = (MethodSymbol) assignedType.tsym.getEnclosedElements().get(0);
					print("{ " + method.getSimpleName() + " : ").print(lambda).print(" }");
					return true;
				}
			} else if (expression instanceof JCNewClass) {
				JCNewClass newClass = (JCNewClass) expression;
				if (newClass.def != null && context.isFunctionalType(assignedType.tsym)) {
					List<JCTree> defs = newClass.def.defs;
					boolean printed = false;
					for (JCTree def : defs) {
						if (def instanceof JCMethodDecl) {
							if (printed) {
								// should never happen... report error?
							}
							JCMethodDecl method = (JCMethodDecl) def;
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
						MethodSymbol method;
						for (Symbol s : assignedType.tsym.getEnclosedElements()) {
							if (s instanceof MethodSymbol) {
								// TODO also check that the method is compatible
								// (here we just apply to the first found
								// method)
								method = (MethodSymbol) s;
								print("(");
								for (VarSymbol p : method.getParameters()) {
									print(p.getSimpleName().toString()).print(", ");
								}
								if (!method.getParameters().isEmpty()) {
									removeLastChars(2);
								}
								print(") => { return new ").print(newClass.clazz).print("(").printArgList(null,
										newClass.args);
								print(").").print(method.getSimpleName().toString()).print("(");
								for (VarSymbol p : method.getParameters()) {
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
			} else if (!(expression instanceof JCLambda || expression instanceof JCMemberReference)
					&& context.isFunctionalType(assignedType.tsym)) {
				// disallow typing to force objects to be passed as function
				// (may require runtime checks later on)
				print("<any>(").print(expression).print(")");
				return true;
			} else if (expression instanceof JCMethodInvocation) {
				// disable type checking when the method returns a type variable
				// because it may to be correctly set in the invocation
				MethodSymbol m = (MethodSymbol) Util.getAccessedSymbol(((JCMethodInvocation) expression).meth);
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
	public String getQualifiedTypeName(TypeSymbol type, boolean globals, boolean ignoreLangTypes) {
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
				Symbol s = type;
				qualifiedName = "";
				while (i >= 0 && !(s instanceof PackageSymbol)) {
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

}
