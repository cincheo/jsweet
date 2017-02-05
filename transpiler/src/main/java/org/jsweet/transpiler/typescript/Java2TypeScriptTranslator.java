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
package org.jsweet.transpiler.typescript;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jsweet.JSweetConfig.ANNOTATION_STRING_TYPE;
import static org.jsweet.JSweetConfig.GLOBALS_CLASS_NAME;
import static org.jsweet.JSweetConfig.GLOBALS_PACKAGE_NAME;
import static org.jsweet.JSweetConfig.TS_IDENTIFIER_FORBIDDEN_CHARS;

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
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import org.jsweet.transpiler.util.Util;
import org.jsweet.transpiler.util.VariableKind;

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.TypeVariableSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ArrayType;
import com.sun.tools.javac.code.Type.MethodType;
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
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Name;

/**
 * This is a TypeScript printer for translating the Java AST to a TypeScript
 * program.
 * 
 * @author Renaud Pawlak
 */
public class Java2TypeScriptTranslator<C extends JSweetContext> extends AbstractTreePrinter<C> {

	public static final String PARENT_CLASS_FIELD_NAME = "__parent";
	public static final String INTERFACES_FIELD_NAME = "__interfaces";
	public static final String STATIC_INITIALIZATION_SUFFIX = "_$LI$";
	public static final String CLASS_NAME_IN_CONSTRUCTOR = "__class";
	public static final String ANONYMOUS_PREFIX = "$";
	public static final String ENUM_WRAPPER_CLASS_SUFFIX = "_$WRAPPER";
	public static final String ENUM_WRAPPER_CLASS_WRAPPERS = "_$wrappers";
	public static final String ENUM_WRAPPER_CLASS_NAME = "_$name";
	public static final String ENUM_WRAPPER_CLASS_ORDINAL = "_$ordinal";
	public static final String VAR_DECL_KEYWORD = "let";

	protected static Logger logger = Logger.getLogger(Java2TypeScriptTranslator.class);

	private static class ClassScope {
		private String name;

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

		// to be accessed in the parent scope
		private boolean isAnonymousClass = false;
		// to be accessed in the parent scope
		private boolean isInnerClass = false;
		// to be accessed in the parent scope
		private boolean isLocalClass = false;

	}

	private Stack<ClassScope> scope = new Stack<>();

	private boolean isAnnotationScope = false;

	private boolean isDefinitionScope = false;

	private boolean isTopLevelScope() {
		return getIndent() == 0;
	}

	private ClassScope getScope() {
		return scope.peek();
	}

	private ClassScope getScope(int i) {
		return scope.get(scope.size() - 1 - i);
	}

	public void enterScope() {
		scope.push(new ClassScope());
	}

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
	public Java2TypeScriptTranslator(Java2TypeScriptAdapter<C> adapter, TranspilationHandler logHandler, C context,
			JCCompilationUnit compilationUnit, boolean fillSourceMap) {
		super(logHandler, context, compilationUnit, adapter, fillSourceMap);
	}

	private static java.util.List<Class<?>> statementsWithNoSemis = Arrays
			.asList(new Class<?>[] { JCIf.class, JCForLoop.class, JCEnhancedForLoop.class, JCSwitch.class });

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

	private JCMethodDecl mainMethod;

	private PackageSymbol topLevelPackage;

	private void useModule(boolean require, PackageSymbol targetPackage, JCTree sourceTree, String targetName,
			String moduleName, Symbol sourceElement) {
		if (context.useModules) {
			context.packageDependencies.add(targetPackage);
			context.packageDependencies.add(compilationUnit.packge);
			context.packageDependencies.addEdge(compilationUnit.packge, targetPackage);
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
				if (require || GLOBALS_CLASS_NAME.equals(targetName)) {
					print("import " + targetName + " = require(\"" + moduleName + "\"); ").println();
				} else {
					print("import { " + targetName + " } from '" + moduleName + "'; ").println();
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
				if (Util.isSourceType((ClassSymbol) s)) {
					report(topLevel.getPackageName(), JSweetProblem.CLASS_OUT_OF_ROOT_PACKAGE_SCOPE,
							s.getQualifiedName().toString(), rootPackage.getQualifiedName().toString());
					return false;
				}
			}
		}
		return checkRootPackageParent(topLevel, rootPackage, (PackageSymbol) parentPackage.owner);
	}

	@Override
	public void visitTopLevel(JCCompilationUnit topLevel) {

		if (context.isPackageErased(topLevel.packge)) {
			return;
		}
		boolean noDefs = true;
		for (JCTree def : topLevel.defs) {
			if (def instanceof JCClassDecl) {
				if (!context.isIgnored(((JCClassDecl) def))) {
					noDefs = false;
				}
			}
		}
		// do not print the compilation unit at all if no defs are to be printed
		if (noDefs) {
			return;
		}

		isDefinitionScope = topLevel.packge.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".");

		if (context.hasAnnotationType(topLevel.packge, JSweetConfig.ANNOTATION_MODULE)) {
			context.addExportedElement(
					context.getAnnotationValue(topLevel.packge, JSweetConfig.ANNOTATION_MODULE, null), topLevel.packge, getCompilationUnit());
		}

		printIndent().print(
				"/* Generated from Java with JSweet " + JSweetConfig.getVersionNumber() + " - http://www.jsweet.org */")
				.println();
		PackageSymbol rootPackage = context.getFirstEnclosingRootPackage(topLevel.packge);
		if (rootPackage != null) {
			if (!checkRootPackageParent(topLevel, rootPackage, (PackageSymbol) rootPackage.owner)) {
				return;
			}
		}
		context.importedTopPackages.clear();
		context.rootPackages.add(rootPackage);
		// TODO: check relaxing @Root
		// if (context.useModules && context.rootPackages.size() > 1) {
		// if (!context.reportedMultipleRootPackages) {
		// report(topLevel.getPackageName(),
		// JSweetProblem.MULTIPLE_ROOT_PACKAGES_NOT_ALLOWED_WITH_MODULES,
		// context.rootPackages.toString());
		// context.reportedMultipleRootPackages = true;
		// }
		// return;
		// }

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
								String actualName = context.getAnnotationValue(qualified.sym,
										JSweetConfig.ANNOTATION_MODULE, null);
								useModule(true, null, importDecl, qualified.name.toString(), actualName,
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
													String actualName = context.getAnnotationValue(importedMember,
															JSweetConfig.ANNOTATION_MODULE, null);
													useModule(true, null, importDecl,
															importedMember.getSimpleName().toString(), actualName,
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
					ClassSymbol importedClass = (ClassSymbol) qualified.sym;
					if (Util.isSourceType(importedClass) && !importedClass.getQualifiedName().toString()
							.startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
						String importedModule = importedClass.sourcefile.getName();
						if (importedModule.equals(compilationUnit.sourcefile.getName())) {
							continue;
						}
						String pathToImportedClass = Util.getRelativePath(
								new File(compilationUnit.sourcefile.getName()).getParent(), importedModule);
						pathToImportedClass = pathToImportedClass.substring(0, pathToImportedClass.length() - 5);
						if (!pathToImportedClass.startsWith(".")) {
							pathToImportedClass = "./" + pathToImportedClass;
						}

						Symbol symbol = qualified.sym.getEnclosingElement();
						while (!(symbol instanceof PackageSymbol)) {
							importedName = symbol.getSimpleName().toString();
							symbol = symbol.getEnclosingElement();
						}
						if (symbol != null) {
							useModule(false, (PackageSymbol) symbol, importDecl, importedName,
									pathToImportedClass.replace('\\', '/'), null);
						}
					}
				}
			}
		}

		if (context.useModules) {
			TreeScanner usedTypesScanner = new TreeScanner() {

				private void checkType(TypeSymbol type) {
					if (type instanceof ClassSymbol) {
						if (type.getEnclosingElement().equals(compilationUnit.packge)) {
							String importedModule = ((ClassSymbol) type).sourcefile.getName();
							if (!importedModule.equals(compilationUnit.sourcefile.getName())) {
								importedModule = "./" + new File(importedModule).getName();
								importedModule = importedModule.substring(0, importedModule.length() - 5);
								useModule(false, (PackageSymbol) type.getEnclosingElement(), null,
										type.getSimpleName().toString(), importedModule, null);
							}
						}
					}
				}

				@Override
				public void scan(JCTree t) {
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
		TreeScanner inlinedModuleScanner = new TreeScanner() {
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
							if ((fa.sym instanceof ClassSymbol) && Util.isSourceType((ClassSymbol) fa.sym)) {
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
			mainMethod = null;

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

	private void printDocComment(JCTree element, boolean indent) {
		if (compilationUnit != null && compilationUnit.docComments != null) {
			Comment comment = compilationUnit.docComments.getComment(element);
			String commentText = getAdapter().adaptDocComment(element, comment == null ? null : comment.getText());

			List<String> lines = new ArrayList<>();
			if (comment != null) {
				lines.addAll(Arrays.asList(commentText.split("\n")));
			}
			if (!lines.isEmpty()) {
				if (indent) {
					printIndent();
				}
				print("/**").println();
				for (String line : lines) {
					printIndent().print(" * ").print(line.trim()).println();
				}
				removeLastChar();
				println().printIndent().print(" ").print("*/").println();
				if (!indent) {
					printIndent();
				}
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

	private boolean isAnonymousClass() {
		return scope.size() > 1 && getScope(1).isAnonymousClass;
	}

	private boolean isInnerClass() {
		return scope.size() > 1 && getScope(1).isInnerClass;
	}

	private boolean isLocalClass() {
		return scope.size() > 1 && getScope(1).isLocalClass;
	}

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		if (context.isIgnored(classdecl)) {
			return;
		}
		String name = classdecl.getSimpleName().toString();
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
			printDocComment(classdecl, false);
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
				print("<").printArgList(classdecl.typarams).print(">");
			} else if (isAnonymousClass() && classdecl.getModifiers().getFlags().contains(Modifier.STATIC)) {
				JCNewClass newClass = getScope(1).anonymousClassesConstructors
						.get(getScope(1).anonymousClasses.indexOf(classdecl));
				printAnonymousClassTypeArgs(newClass);
			}
			String mixin = null;
			if (context.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_MIXIN)) {
				mixin = context.getAnnotationValue(classdecl.sym, JSweetConfig.ANNOTATION_MIXIN, null);
			}

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
						&& !(mixin != null && mixin.equals(classdecl.extending.type.toString()))
						&& !(getAdapter().eraseSuperClass(classdecl, (ClassSymbol) classdecl.extending.type.tsym))) {
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
						getAdapter().disableTypeSubstitution = !getAdapter().isSubstituteSuperTypes();
						getAdapter().substituteAndPrintType(classdecl.extending);
						getAdapter().disableTypeSubstitution = false;
					}
					if (context.classesWithWrongConstructorOverload.contains(classdecl.sym)) {
						getScope().hasConstructorOverloadWithSuperClass = true;
					}
				} else {
					getScope().removedSuperclass = true;
				}
			}

			if (classdecl.implementing != null && !classdecl.implementing.isEmpty()) {
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
					if (getAdapter().eraseSuperInterface(classdecl, (ClassSymbol) itf.type.tsym)) {
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
						getAdapter().disableTypeSubstitution = !getAdapter().isSubstituteSuperTypes();
						getAdapter().substituteAndPrintType(itf);
						getAdapter().disableTypeSubstitution = false;
						implementedInterfaces.add(itf.type);
						print(", ");
					}
					removeLastChars(2);
				}
			}
			print(" {").println().startIndent();
		}

		if (getScope().innerClassNotStatic && !getScope().interfaceScope && !getScope().enumScope) {
			printIndent().print("public " + PARENT_CLASS_FIELD_NAME + ": any;").println();
		}

		if (defaultMethods != null && !defaultMethods.isEmpty()) {
			getScope().defaultMethodScope = true;
			for (Entry<JCClassDecl, JCMethodDecl> entry : defaultMethods) {
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

		if (!getScope().interfaceScope && classdecl.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
			List<MethodSymbol> methods = new ArrayList<>();
			for (Type t : implementedInterfaces) {
				context.grabMethodsToBeImplemented(methods, t.tsym);
			}
			Map<Name, String> signatures = new HashMap<>();
			for (MethodSymbol meth : methods) {
				if (meth.type instanceof MethodType) {
					MethodSymbol s = Util.findMethodDeclarationInType(getContext().types, classdecl.sym,
							meth.getSimpleName().toString(), (MethodType) meth.type, true);
					boolean printDefaultImplementation = false;
					if (s != null) {
						if (!s.getEnclosingElement().equals(classdecl.sym)) {
							if (!(s.isDefault() || (!context.isInterface((TypeSymbol) s.getEnclosingElement())
									&& !s.getModifiers().contains(Modifier.ABSTRACT)))) {
								printDefaultImplementation = true;
							}
						}
					}

					if (printDefaultImplementation) {
						Overload o = context.getOverload(classdecl.sym, meth);
						if (o != null && o.methods.size() > 1 && !o.isValid) {
							if (!meth.type.equals(o.coreMethod.type)) {
								printDefaultImplementation = false;
							}
						}
					}
					if (s == null || printDefaultImplementation) {
						String signature = getContext().types.erasure(meth.type).toString();
						if (!(signatures.containsKey(meth.name) && signatures.get(meth.name).equals(signature))) {
							printDefaultImplementation(meth);
							signatures.put(meth.name, signature);
						}
					}
				}
			}
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

		if (!globals && !context.isInterface(classdecl.sym) && context.getStaticInitializerCount(classdecl.sym) > 0) {
			printIndent().print("static __static_initialized : boolean = false;").println();
			int liCount = context.getStaticInitializerCount(classdecl.sym);
			String prefix = classdecl.getSimpleName().toString() + ".";
			printIndent().print("static __static_initialize() { ");
			print("if(!" + prefix + "__static_initialized) { ");
			print(prefix + "__static_initialized = true; ");
			for (int i = 0; i < liCount; i++) {
				print(prefix + "__static_initializer_" + i + "(); ");
			}
			print("} }").println().println();
			String qualifiedClassName = getQualifiedTypeName(classdecl.sym, globals);
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
			if (def instanceof JCClassDecl) {
				// inner types are be printed in a namespace
				continue;
			}
			if (def instanceof JCVariableDecl) {
				if (getScope().enumScope && ((JCVariableDecl) def).type.tsym != classdecl.type.tsym) {
					getScope().isComplexEnum = true;
					continue;
				}
				if (!getAdapter().needsVariableDecl((JCVariableDecl) def, VariableKind.FIELD)) {
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

		if (!getScope().hasDeclaredConstructor
				&& !(getScope().interfaceScope || getScope().enumScope || getScope().declareClassScope)) {
			Set<String> interfaces = new HashSet<>();
			context.grabSupportedInterfaceNames(interfaces, classdecl.sym);
			if (!interfaces.isEmpty() || getScope().innerClassNotStatic || hasUninitializedFields) {
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
					if (getScope().innerClassNotStatic) {
						TypeSymbol s = classdecl.extending.type.tsym;
						boolean hasArg = false;
						if (s.getEnclosingElement() instanceof ClassSymbol && !s.isStatic()) {
							print(PARENT_CLASS_FIELD_NAME);
							hasArg = true;
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
			if (getContext().options.isSupportGetClass() && !getScope().interfaceScope && !getScope().declareClassScope
					&& !getScope().enumScope && !classdecl.sym.isAnonymous()) {
				println().printIndent().print(classdecl.sym.getSimpleName().toString())
						.print("[\"" + CLASS_NAME_IN_CONSTRUCTOR + "\"] = ")
						.print("\"" + context.getRootRelativeName(null, classdecl.sym) + "\";");

				Set<String> interfaces = new HashSet<>();
				context.grabSupportedInterfaceNames(interfaces, classdecl.sym);
				if (!interfaces.isEmpty()) {
					println().printIndent().print(classdecl.sym.getSimpleName().toString())
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

		// inner, anonymous and local classes in a namespace
		// ======================
		// print valid inner classes
		boolean nameSpace = false;
		for (JCTree def : classdecl.defs) {
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
				if (tree instanceof JCVariableDecl && ((JCVariableDecl) tree).type.equals(classdecl.type)) {
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
						printArgList(newClass.args).print(")");
						print(", ");
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			removeLastChars(2);
			print("];").println();
		}

		if (getScope().interfaceScope) {
			// print static members of interfaces
			nameSpace = false;
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

		if (mainMethod != null && mainMethod.getParameters().size() < 2
				&& mainMethod.sym.getEnclosingElement().equals(classdecl.sym)) {
			String mainClassName = getQualifiedTypeName(classdecl.sym, globals);
			String mainMethodQualifier = mainClassName;
			if (!isBlank(mainClassName)) {
				mainMethodQualifier = mainClassName + ".";
			}
			context.entryFiles.add(new File(compilationUnit.sourcefile.getName()));
			context.addFooterStatement(mainMethodQualifier + JSweetConfig.MAIN_FUNCTION_NAME + "("
					+ (mainMethod.getParameters().isEmpty() ? "" : "null") + ");");
		}

		getAdapter().typeVariablesToErase.removeAll(parentTypeVars);
		exitScope();
	}

	private void printDefaultImplementation(MethodSymbol method) {
		printIndent().print("public abstract ").print(method.getSimpleName().toString());
		print("(");
		if (method.getParameters() != null && !method.getParameters().isEmpty()) {
			for (VarSymbol var : method.getParameters()) {
				print(var.name.toString()).print(": any");
				print(", ");
			}
			removeLastChars(2);
		}
		print(")");
		print(": any;").println();
	}

	private String getQualifiedTypeName(TypeSymbol type, boolean globals) {
		return getAdapter().getQualifiedTypeName(type, globals);
	}

	private String getTSMethodName(JCMethodDecl methodDecl) {
		String name = context.getActualName(methodDecl.sym);
		switch (name) {
		case "<init>":
			return "constructor";
		case JSweetConfig.ANONYMOUS_FUNCTION_NAME:
		case JSweetConfig.ANONYMOUS_STATIC_FUNCTION_NAME:
			return "";
		case JSweetConfig.NEW_FUNCTION_NAME:
			return "new";
		default:
			return name;
		}
	}

	private boolean printCoreMethodDelegate = false;

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
			getAdapter().substituteAndPrintType(methodDecl.restype).print(";");
			return;
		}

		boolean constructor = methodDecl.sym.isConstructor();
		if (getScope().enumScope) {
			if (constructor) {
				if (parent != null && parent.pos != methodDecl.pos) {
					getScope().isComplexEnum = true;
				}
			} else {
				getScope().isComplexEnum = true;
			}
			return;
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
							if (!context.isInterface(parent.sym) && !methodDecl.sym.isConstructor()
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
							if (!overload.printed && overload.coreMethod.sym.getEnclosingElement() != parent.sym
									&& !overload.coreMethod.sym.getModifiers().contains(Modifier.ABSTRACT)) {
								visitMethodDef(overload.coreMethod);
								overload.printed = true;
								if (!context.isInterface(parent.sym)) {
									println().println().printIndent();
								}
							}
							if (context.isInterface(parent.sym)) {
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

		int jsniLine = -1;
		String[] content = null;

		if (methodDecl.mods.getFlags().contains(Modifier.NATIVE)) {
			if (!getScope().declareClassScope && !ambient && !getScope().interfaceScope) {
				content = getGetSource(getCompilationUnit());
				if (content != null) {
					int line = 0;
					if (methodDecl.getParameters() != null && !methodDecl.getParameters().isEmpty()) {
						line = diagnosticSource.getLineNumber(methodDecl.getParameters().last().getStartPosition()) - 1;
					} else {
						line = diagnosticSource.getLineNumber(methodDecl.getStartPosition()) - 1;
					}
					if (content[line].contains("/*-{")) {
						jsniLine = line;
					} else {
						if (content[line + 1].contains("/*-{")) {
							jsniLine = line + 1;
						}
					}
				}
				if (jsniLine == -1) {
					report(methodDecl, methodDecl.name, JSweetProblem.NATIVE_MODIFIER_IS_NOT_ALLOWED, methodDecl.name);
				}
			}
		} else {
			if (getScope().declareClassScope && !constructor && !getScope().interfaceScope
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
			if (scope.size() == 1) {
				mainMethod = methodDecl;
			}
		}

		boolean globals = parent == null ? false : JSweetConfig.GLOBALS_CLASS_NAME.equals(parent.name.toString());
		globals = globals || (getScope().interfaceScope && methodDecl.mods.getFlags().contains(Modifier.STATIC));
		printDocComment(methodDecl, false);
		if (parent == null) {
			print("function ");
		} else if (globals) {
			if (constructor && methodDecl.sym.isPrivate() && methodDecl.getParameters().isEmpty()) {
				return;
			}
			if (constructor) {
				report(methodDecl, methodDecl.name, JSweetProblem.GLOBAL_CONSTRUCTOR_DEF);
				return;
			}

			if (!methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
				report(methodDecl, methodDecl.name, JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
				return;
			}

			if (context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_MODULE)) {
				getContext().addExportedElement(
						context.getAnnotationValue(methodDecl.sym, JSweetConfig.ANNOTATION_MODULE, null),
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
			print("function ");
		} else {
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
								print("private ");
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
			if (ambient) {
				report(methodDecl, methodDecl.name, JSweetProblem.WRONG_USE_OF_AMBIENT, methodDecl.name);
			}
		}
		if (parent == null || !context.hasAnnotationType(parent.sym, FunctionalInterface.class.getName())) {
			if (inOverload && !overload.isValid && !inCoreWrongOverload) {
				print(getOverloadMethodName(methodDecl.sym));
			} else {
				print(getTSMethodName(methodDecl));
			}
		}
		if ((methodDecl.typarams != null && !methodDecl.typarams.isEmpty())
				|| (getContext().getWildcards(methodDecl.sym) != null)) {
			getAdapter().inTypeParameters = true;
			print("<");
			if (methodDecl.typarams != null && !methodDecl.typarams.isEmpty()) {
				printArgList(methodDecl.typarams);
				if (getContext().getWildcards(methodDecl.sym) != null) {
					print(", ");
				}
			}
			if (getContext().getWildcards(methodDecl.sym) != null) {
				printArgList(getContext().getWildcards(methodDecl.sym), getAdapter()::substituteAndPrintType);
			}
			print(">");
			getAdapter().inTypeParameters = false;
		}
		print("(");
		if (inCoreWrongOverload) {
			getScope().eraseVariableTypes = true;
		}
		boolean paramPrinted = false;
		if (getScope().innerClassNotStatic && methodDecl.sym.isConstructor()) {
			print(PARENT_CLASS_FIELD_NAME + ": any, ");
			paramPrinted = true;
		}
		if (constructor && getScope().enumWrapperClassScope) {
			print((isAnonymousClass() ? "" : "protected ") + ENUM_WRAPPER_CLASS_ORDINAL + " : number, ");
			print((isAnonymousClass() ? "" : "protected ") + ENUM_WRAPPER_CLASS_NAME + " : string");
			if (!methodDecl.getParameters().isEmpty()) {
				print(", ");
			}
		}
		int i = 0;
		for (JCVariableDecl param : methodDecl.getParameters()) {
			print(param);
			if (inOverload && overload.isValid && overload.defaultValues.get(i) != null) {
				print(" = ").print(overload.defaultValues.get(i));
			}
			print(", ");
			i++;
			paramPrinted = true;
		}
		if (inCoreWrongOverload) {
			getScope().eraseVariableTypes = false;
		}
		if (paramPrinted) {
			removeLastChars(2);
		}
		print(")");
		if (inCoreWrongOverload && !methodDecl.sym.isConstructor()) {
			print(" : any");
		} else {
			if (methodDecl.restype != null && methodDecl.restype.type.getTag() != TypeTag.VOID) {
				print(" : ");
				getAdapter().substituteAndPrintType(methodDecl.restype);
			}
		}
		if (inCoreWrongOverload && context.isInterface(parent.sym)) {
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
			} else if (jsniLine != -1) {
				int line = jsniLine;
				print(" {").println().startIndent();
				String jsniCode = content[line].substring(content[line].indexOf("/*-{") + 4).trim();
				StringBuilder jsni = new StringBuilder();
				if (!StringUtils.isEmpty(jsniCode)) {
					jsni.append(jsniCode);
					jsni.append("\n");
				}
				line++;
				while (!content[line].contains("}-*/")) {
					jsniCode = content[line++].trim();
					jsni.append(jsniCode);
					jsni.append("\n");
				}
				jsniCode = content[line].substring(0, content[line].indexOf("}-*/")).trim();
				if (!StringUtils.isEmpty(jsniCode)) {
					jsni.append(jsniCode);
					jsni.append("\n");
				}
				if (!StringUtils.isEmpty(jsni)) {
					jsni.deleteCharAt(jsni.length() - 1);
				}
				String mergedCode = parseJSNI(jsni.toString());
				for (String s : mergedCode.split("\\n")) {
					printIndent().print(s).println();
				}
				endIndent().printIndent().print("}");
			} else {
				print(";");
			}
		} else {
			if (getScope().interfaceScope) {
				if (!methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name,
							parent == null ? "<no class>" : parent.name);
				}
			}
			if (getScope().declareClassScope) {
				if (!constructor || (methodDecl.getBody() != null && methodDecl.getBody().getStatements().isEmpty())) {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name,
							parent == null ? "<no class>" : parent.name);
				}
				print(";");
			} else {
				if (inCoreWrongOverload) {
					print(" {").println().startIndent().printIndent();

					boolean wasPrinted = false;
					for (i = 0; i < overload.methods.size(); i++) {
						JCMethodDecl method = overload.methods.get(i);
						if (context.isInterface((ClassSymbol) method.sym.getEnclosingElement())
								&& !method.getModifiers().getFlags().contains(Modifier.DEFAULT)) {
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
								print("{").println().startIndent().printIndent()
										.print("super." + getTSMethodName(methodDecl) + "(");
								for (int j = 0; j < method.getParameters().size(); j++) {
									print(avoidJSKeyword(overload.coreMethod.getParameters().get(j).name.toString()))
											.print(", ");
								}
								if (!method.getParameters().isEmpty()) {
									removeLastChars(2);
								}
								print(");");
								println().endIndent().printIndent().print("}");
							} else {
								print("{").println().startIndent().printIndent();
								// temporary cast to any because of Java
								// generics
								// bug
								print("return <any>");
								if (method.sym.isStatic()) {
									print(getQualifiedTypeName(parent.sym, false).toString());
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

					if (context.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_TYPE_SCRIPT_BODY)) {
						String replacedBody = (String) context.getAnnotationValue(methodDecl.sym,
								JSweetConfig.ANNOTATION_TYPE_SCRIPT_BODY, null);
						printIndent().print(replacedBody).println();
					} else {
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
					}
					endIndent().printIndent().print("}");
				}
			}
		}
	}

	protected void printVariableInitialization(JCClassDecl clazz, JCVariableDecl var) {
		if (getScope().innerClassNotStatic && !Util.isConstantOrNullField(var)) {
			String name = var.getName().toString();
			if (context.getFieldNameMapping(var.sym) != null) {
				name = context.getFieldNameMapping(var.sym);
			}
			printIndent().print("this.").print(name).print(" = ").print(var.init).print(";").println();
		} else if (var.init == null && Util.isCoreType(var.type)) {
			String name = var.getName().toString();
			if (context.getFieldNameMapping(var.sym) != null) {
				name = context.getFieldNameMapping(var.sym);
			}
			printIndent().print("this.").print(name).print(" = ").print(Util.getTypeInitialValue(var.type)).print(";")
					.println();
		}
	}

	protected void printInstanceInitialization(JCClassDecl clazz, MethodSymbol method) {
		if (getContext().options.isInterfaceTracking() && method == null || method.isConstructor()) {
			getScope().hasDeclaredConstructor = true;
			if (getScope().innerClassNotStatic) {
				printIndent().print("this." + PARENT_CLASS_FIELD_NAME + " = " + PARENT_CLASS_FIELD_NAME + ";")
						.println();
			}
			for (JCTree member : clazz.defs) {
				if (member instanceof JCVariableDecl) {
					JCVariableDecl var = (JCVariableDecl) member;
					printVariableInitialization(clazz, var);
				} else if (member instanceof JCBlock) {
					JCBlock block = (JCBlock) member;
					if (!block.isStatic()) {
						printIndent().print("(() => {").startIndent().println();
						printBlockStatements(block.stats);
						endIndent().printIndent().print("})();").println();
					}
				}
			}
		}
	}

	private String parseJSNI(String jsniCode) {
		return jsniCode.replaceAll("@[^:]*::[\\n]?([a-zA-Z_$][a-zA-Z\\d_$]*)[\\n]?\\([^)]*\\)", "$1")
				.replaceAll("@[^:]*::\\n?([a-zA-Z_$][a-zA-Z\\d_$]*)", "$1");
	}

	private void printInlinedMethod(Overload overload, JCMethodDecl method, List<? extends JCTree> args) {
		print("{").println().startIndent();
		printIndent().print(VAR_DECL_KEYWORD + " __args = Array.prototype.slice.call(arguments);").println();
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
			if (context.hasAnnotationType(method.sym, JSweetConfig.ANNOTATION_TYPE_SCRIPT_BODY)) {
				String replacedBody = (String) context.getAnnotationValue(method.sym,
						JSweetConfig.ANNOTATION_TYPE_SCRIPT_BODY, null);
				printIndent().print(replacedBody).println();
			} else {
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
		for (JCVariableDecl field : getScope().fieldsWithInitializers) {
			String name = getAdapter().getIdentifier(field.sym);
			if (context.getFieldNameMapping(field.sym) != null) {
				name = context.getFieldNameMapping(field.sym);
			}
			printIndent().print("this.").print(name).print(" = ").print(field.init).print(";").println();
		}
	}

	private void printBlockStatements(List<JCStatement> statements) {
		for (JCStatement statement : statements) {
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

	private void printMethodParamsTest(Overload overload, JCMethodDecl m) {
		int i = 0;
		for (; i < m.getParameters().size(); i++) {
			print("(");
			printInstanceOf(avoidJSKeyword(overload.coreMethod.getParameters().get(i).name.toString()), null,
					m.getParameters().get(i).type);
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

	@Override
	public void visitBlock(JCBlock block) {
		JCTree parent = getParent();
		boolean globals = (parent instanceof JCClassDecl)
				&& JSweetConfig.GLOBALS_CLASS_NAME.equals(((JCClassDecl) parent).name.toString());
		boolean initializer = (parent instanceof JCClassDecl) && !globals;
		int static_i = 0;
		if (initializer) {
			if (getScope().interfaceScope) {
				report(block, JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, ((JCClassDecl) parent).name);
			}
			if (!block.isStatic()) {
				// non-static blocks are initialized in the constructor
				return;
			}
			for (JCTree m : ((JCClassDecl) parent).getMembers()) {
				if (m instanceof JCBlock) {
					if (((JCBlock) m).isStatic()) {
						if (block == m) {
							print("static __static_initializer_" + static_i + "() ");
							break;
						}
						static_i++;
					}
				}
			}
		}
		if (!globals) {
			print("{").println().startIndent();
		}

		printBlockStatements(block.stats);

		if (!globals) {
			endIndent().printIndent().print("}");
		}
	}

	private String avoidJSKeyword(String name) {
		if (JSweetConfig.JS_KEYWORDS.contains(name)) {
			name = JSweetConfig.JS_KEYWORD_PREFIX + name;
		}
		return name;
	}

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
			print(varDecl.name.toString());
			if (varDecl.init instanceof JCNewClass) {
				JCNewClass newClass = (JCNewClass) varDecl.init;
				if (newClass.def != null) {
					initAnonymousClass(newClass);
				}
			}
		} else {
			JCTree parent = getParent();

			if (getScope().enumWrapperClassScope && varDecl.type.equals(parent.type)) {
				return;
			}

			String name = getAdapter().getIdentifier(varDecl.sym);
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
				if (context.bundleMode) {
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
				printDocComment(varDecl, false);
			}
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
						getContext().addExportedElement(
								context.getAnnotationValue(varDecl.sym, JSweetConfig.ANNOTATION_MODULE, null),
								varDecl.sym, getCompilationUnit());
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

			if (doesFieldNameRequireQuotes(name)) {
				print("'" + name + "'");
			} else {
				print(name);
			}

			if (!Util.isVarargs(varDecl) && (getScope().eraseVariableTypes || (getScope().interfaceScope
					&& context.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_OPTIONAL)))) {
				print("?");
			}
			if (!getScope().skipTypeAnnotations && !getScope().enumWrapperClassScope) {
				if (typeChecker.checkType(varDecl, varDecl.name, varDecl.vartype)) {
					print(" : ");
					if (confictInDefinitionScope) {
						print("any");
					} else {
						if (getScope().eraseVariableTypes) {
							print("any");
							if (Util.isVarargs(varDecl)) {
								print("[]");
							}
						} else {
							if (context.hasAnnotationType(varDecl.vartype.type.tsym, ANNOTATION_STRING_TYPE)) {
								print("\"");
								print(context.getAnnotationValue(varDecl.vartype.type.tsym, ANNOTATION_STRING_TYPE,
										varDecl.vartype.type.tsym.name.toString()).toString());
								print("\"");
							} else {
								getAdapter().substituteAndPrintType(varDecl.vartype);
							}
						}
					}
				}
			}
			if (context.lazyInitializedStatics.contains(varDecl.sym) && !getScope().enumWrapperClassScope) {
				JCClassDecl clazz = (JCClassDecl) parent;
				String prefix = clazz.getSimpleName().toString();
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
				getAdapter().substituteAndPrintType(varDecl.vartype);
				print(" { ");
				int liCount = context.getStaticInitializerCount(clazz.sym);
				if (liCount > 0) {
					if (!globals) {
						print(prefix + "__static_initialize(); ");
					}
				}
				if (varDecl.init != null && !isDefinitionScope) {
					print("if(" + prefix).print(name).print(" == null) ").print(prefix).print(name).print(" = ");
					if (getScope().enumWrapperClassScope) {
						JCNewClass newClass = (JCNewClass) varDecl.init;
						print("new ").print(clazz.getSimpleName().toString()).print("(").printArgList(newClass.args)
								.print(")");
					} else {
						if (!getAdapter().substituteAssignedExpression(varDecl.type, varDecl.init)) {
							print(varDecl.init);
						}
					}
					print("; ");
				}
				print("return ").print(prefix).print(name).print("; }");
				if (!globals) {
					String qualifiedClassName = getQualifiedTypeName(clazz.sym, globals);
					context.addTopFooterStatement((isBlank(qualifiedClassName) ? "" : qualifiedClassName + ".") + name
							+ STATIC_INITIALIZATION_SUFFIX + "();");
				}
			} else {
				if (varDecl.init != null) {
					if (!(parent instanceof JCClassDecl && getScope().innerClassNotStatic
							&& !Util.isConstantOrNullField(varDecl))) {
						if (!globals && parent instanceof JCClassDecl && getScope().interfaceScope) {
							report(varDecl, varDecl.name, JSweetProblem.INVALID_FIELD_INITIALIZER_IN_INTERFACE,
									varDecl.name, ((JCClassDecl) parent).name);
						} else {
							if (!(getScope().hasConstructorOverloadWithSuperClass
									&& getScope().fieldsWithInitializers.contains(varDecl))) {
								print(" = ");
								if (!getAdapter().substituteAssignedExpression(varDecl.type, varDecl.init)) {
									print(varDecl.init);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean doesFieldNameRequireQuotes(String name) {
		for (char c : name.toCharArray()) {
			if (TS_IDENTIFIER_FORBIDDEN_CHARS.contains(c)) {
				return true;
			}
		}
		return false; 
	}

	@Override
	public void visitParens(JCParens parens) {
		print("(");
		super.visitParens(parens);
		print(")");
	}

	@Override
	public void visitImport(JCImport importDecl) {
		String qualId = importDecl.getQualifiedIdentifier().toString();
		if (qualId.endsWith("*") && !qualId.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME + ".*")) {
			report(importDecl, JSweetProblem.WILDCARD_IMPORT);
			return;
		}
		String adaptedQualId = getAdapter().needsImport(importDecl, qualId);
		if (adaptedQualId != null && adaptedQualId.contains(".")) {
			if (importDecl.isStatic() && !qualId.contains("." + JSweetConfig.GLOBALS_CLASS_NAME + ".")) {
				if (!context.bundleMode) {
					print(VAR_DECL_KEYWORD + " ").print(qualId.substring(qualId.lastIndexOf('.') + 1)).print(": any = ")
							.print(qualId).print(";");
				}
			} else {
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
							print("import ").print(name).print(" = ").print(adaptedQualId).print(";");
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
					if (context.bundleMode) {
						// in bundle mode, we do not use imports to minimize
						// dependencies
						// (imports create unavoidable dependencies!)
						context.importedTopPackages.add(namePath[0]);
					} else {
						print("import ").print(name).print(" = ").print(adaptedQualId).print(";");
					}
				}
			}
		}

	}

	@Override
	public void visitSelect(JCFieldAccess fieldAccess) {
		if (!getAdapter().substituteFieldAccess(fieldAccess)) {
			if ("class".equals(fieldAccess.name.toString())) {
				if (fieldAccess.type instanceof Type.ClassType
						&& context.isInterface(((Type.ClassType) fieldAccess.type).typarams_field.head.tsym)) {
					print("\"").print(context
							.getRootRelativeJavaName(((Type.ClassType) fieldAccess.type).typarams_field.head.tsym))
							.print("\"");
				} else {
					print(fieldAccess.selected);
				}
			} else if ("this".equals(fieldAccess.name.toString()) && getScope().innerClassNotStatic) {
				print("this." + PARENT_CLASS_FIELD_NAME);
			} else if ("this".equals(fieldAccess.name.toString())) {
				print("this");
			} else {
				String selected = fieldAccess.selected.toString();
				if (!selected.equals(GLOBALS_CLASS_NAME)) {
					if (selected.equals("super") && (fieldAccess.sym instanceof VarSymbol)) {
						print("this.");
					} else {
						boolean accessSubstituted = false;
						if (fieldAccess.sym instanceof VarSymbol) {
							VarSymbol varSym = (VarSymbol) fieldAccess.sym;
							if (varSym.isStatic() && varSym.owner.isInterface()
									&& varSym.owner != Util.getSymbol(fieldAccess.selected)) {
								accessSubstituted = true;
								if (context.useModules) {
									// TODO: we assume it has been imported, but
									// it is clearly not always the case (to be
									// tested)
									print(varSym.owner.getSimpleName().toString()).print(".");
								} else {
									print(context.getRootRelativeName(null, varSym.owner)).print(".");
								}
							}
						}
						if (!accessSubstituted) {
							print(fieldAccess.selected).print(".");
						}
					}
				}

				if (fieldAccess.sym instanceof VarSymbol && context.getFieldNameMapping(fieldAccess.sym) != null) {
					print(context.getFieldNameMapping(fieldAccess.sym));
				} else {
					printIdentifier(fieldAccess.sym);
				}
				if (fieldAccess.sym instanceof VarSymbol && !fieldAccess.sym.owner.isEnum()
						&& context.lazyInitializedStatics.contains(fieldAccess.sym)) {
					if (!staticInitializedAssignment) {
						print(STATIC_INITIALIZATION_SUFFIX + "()");
					}
				}
			}
		}
	}

	private JCImport getStaticGlobalImport(String methName) {
		if (getCompilationUnit() == null) {
			return null;
		}
		for (JCImport i : getCompilationUnit().getImports()) {
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

	private long applyTargetRefCounter = 0;

	@Override
	public void visitApply(JCMethodInvocation inv) {
		if (!getAdapter().substituteMethodInvocation(inv)) {
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

			boolean anonymous = JSweetConfig.ANONYMOUS_FUNCTION_NAME.equals(methName)
					|| JSweetConfig.ANONYMOUS_STATIC_FUNCTION_NAME.equals(methName)
					|| JSweetConfig.NEW_FUNCTION_NAME.equals(methName);
			boolean targetIsThisOrStaticImported = meth.equals(methName) || meth.equals("this." + methName);

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
									print(ENUM_WRAPPER_CLASS_SUFFIX);
								}
								if (!anonymous) {
									print(".");
								}
							}
						}
					} else {
						if (getScope().defaultMethodScope) {
							TypeSymbol target = Util.getStaticImportTarget(
									getContext().getDefaultMethodCompilationUnit(getParent(JCMethodDecl.class)),
									methName);
							if (target != null) {
								print(getRootRelativeName(target) + ".");
							}
						} else {
							TypeSymbol target = Util.getStaticImportTarget(compilationUnit, methName);
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
									print("this.");
									for (int i = 0; i < level; i++) {
										print(PARENT_CLASS_FIELD_NAME + ".");
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
						if (context.bundleMode && methSym.owner.getSimpleName().toString().equals(GLOBALS_CLASS_NAME)
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
					if (context.hasAnnotationType(selected.type.tsym, FunctionalInterface.class.getName())) {
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
							|| !context.types.erasure(((ArrayType) inv.args.last().type).elemtype).equals(context.types
									.erasure(((ArrayType) methSym.getParameters().last().type).elemtype)))) {
				applyVarargs = false;
			}

			String targetVarName = null;
			if (anonymous) {
				if (inv.meth instanceof JCFieldAccess) {
					JCExpression selected = ((JCFieldAccess) inv.meth).selected;
					print(selected);
				}
			} else {
				if (inv.meth instanceof JCFieldAccess && applyVarargs && !targetIsThisOrStaticImported && !isStatic) {
					targetVarName = "this['__jswref_" + (applyTargetRefCounter++) + "']";
					print("(");
					print(targetVarName + " = ");
					print(((JCFieldAccess) inv.meth).selected);
					print(").");
					if (keywordHandled) {
						print(((JCFieldAccess) inv.meth).name.toString());
					} else {
						if (methSym == null) {
							methSym = (MethodSymbol) ((JCFieldAccess) inv.meth).sym;
						}
						if (methSym != null) {
							print(context.getActualName(methSym));
						} else {
							print(((JCFieldAccess) inv.meth).name.toString());
						}
					}
				} else if (methodName != null) {
					print(methodName);
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
							print(selected).print(".");
						}
						if (methSym != null) {
							if (context.isInvalidOverload(methSym) && !methSym.getParameters().isEmpty()
									&& !Util.hasTypeParameters(methSym) && !Util.hasVarargs(methSym)
									&& getParent(JCMethodDecl.class) != null
									&& !getParent(JCMethodDecl.class).sym.isDefault()) {
								if (methSym.getEnclosingElement().isInterface()) {
									if (getLastPrintedChar() == '.') {
										removeLastChar();
									}
									print("['" + getOverloadMethodName(methSym) + "']");
								} else {
									print(getOverloadMethodName(methSym));
								}
							} else {
								print(context.getActualName(methSym));
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
						getAdapter().substituteAndPrintType(argument).print(",");
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
				} else if (targetVarName != null) {
					contextVar = targetVarName;
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
					print(PARENT_CLASS_FIELD_NAME);
					if (argsLength > 0) {
						print(", ");
					}
				}
			}

			if (getScope().enumWrapperClassScope && isAnonymousClass() && "super".equals(methName)) {
				print(ENUM_WRAPPER_CLASS_ORDINAL + ", " + ENUM_WRAPPER_CLASS_NAME);
				if (argsLength > 0) {
					print(", ");
				}
			}

			for (int i = 0; i < argsLength; i++) {
				JCExpression arg = inv.args.get(i);
				if (inv.meth.type != null) {
					List<Type> argTypes = ((MethodType) inv.meth.type).argtypes;
					Type paramType = i < argTypes.size() ? argTypes.get(i) : argTypes.get(argTypes.size() - 1);
					if (!getAdapter().substituteAssignedExpression(paramType, arg)) {
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
			}

			print(")");
		}
	}

	@Override
	public void visitIdent(JCIdent ident) {
		String name = ident.toString();

		if (getScope().inlinedConstructorArgs != null) {
			if (ident.sym instanceof VarSymbol && getScope().inlinedConstructorArgs.contains(name)) {
				print("__args[" + getScope().inlinedConstructorArgs.indexOf(name) + "]");
				return;
			}
		}

		if (!getAdapter().substituteIdentifier(ident)) {
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
						}
						if (!varSym.getModifiers().contains(Modifier.STATIC)) {
							print("this.");
							JCClassDecl parent = getParent(JCClassDecl.class);
							int level = 0;
							boolean foundInParent = false;
							while (getScope(level++).innerClassNotStatic) {
								parent = getParent(JCClassDecl.class, parent);
								if (varSym.owner == parent.sym) {
									foundInParent = true;
									break;
								}
							}
							if (foundInParent) {
								for (int i = 0; i < level; i++) {
									print(PARENT_CLASS_FIELD_NAME + ".");
								}
							}
						} else {
							if (context.lazyInitializedStatics.contains(varSym)) {
								lazyInitializedStatic = true;
							}
							if (!varSym.owner.getQualifiedName().toString().endsWith("." + GLOBALS_CLASS_NAME)) {
								if (context.bundleMode && !varSym.owner.equals(getParent(JCClassDecl.class).sym)) {
									String prefix = context.getRootRelativeName(null, varSym.owner);
									if (!StringUtils.isEmpty(prefix)) {
										print(context.getRootRelativeName(null, varSym.owner) + ".");
									}
								} else {
									if (!varSym.owner.getSimpleName().toString().equals(GLOBALS_PACKAGE_NAME)) {
										print(varSym.owner.getSimpleName() + ".");
									}
								}
							} else {
								if (context.bundleMode) {
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
							if (context.bundleMode && varSym.owner instanceof MethodSymbol) {
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
						print(getRootRelativeName(clazz.getEnclosingElement()) + ".");
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
							print(clazz.getEnclosingElement().getSimpleName() + ".");
							prefixAdded = true;
						}
					}
				}
				if (!prefixAdded && context.bundleMode && !clazz.equals(getParent(JCClassDecl.class).sym)) {
					print(getRootRelativeName(clazz));
				} else {
					print(name);
				}
			} else {
				print(name);
				if (lazyInitializedStatic) {
					if (!staticInitializedAssignment) {
						print(STATIC_INITIALIZATION_SUFFIX + "()");
					}
				}
			}
		}
	}

	@Override
	public void visitTypeApply(JCTypeApply typeApply) {
		getAdapter().substituteAndPrintType(typeApply);
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

	@Override
	public void visitNewClass(JCNewClass newClass) {
		ClassSymbol clazz = ((ClassSymbol) newClass.clazz.type.tsym);
		if (clazz.name.toString().endsWith(JSweetConfig.GLOBALS_CLASS_NAME)) {
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
				if (getContext().options.isInterfaceTracking()) {
					context.grabSupportedInterfaceNames(interfaces, clazz);
					if (!interfaces.isEmpty()) {
						print("Object.defineProperty(");
					}
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
									String meth = invocation.meth.toString();
									if (meth.equals(JSweetConfig.INDEXED_SET_FUCTION_NAME)
											|| meth.equals(JSweetConfig.UTIL_CLASSNAME + "."
													+ JSweetConfig.INDEXED_SET_FUCTION_NAME)) {
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
							if (statementPrinted) {
								removeLastChars(2);
							}
						}
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
				if (getContext().options.isInterfaceTracking()) {
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
								String meth = invocation.meth.toString();
								if (meth.equals(JSweetConfig.INDEXED_SET_FUCTION_NAME) || meth.equals(
										JSweetConfig.UTIL_CLASSNAME + "." + JSweetConfig.INDEXED_SET_FUCTION_NAME)) {
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
				print("new ").print(newClass.clazz).print("(").printArgList(newClass.args).print("))");
			}
		} else {
			if (context.hasAnnotationType(newClass.clazz.type.tsym, JSweetConfig.ANNOTATION_ERASED)) {
				if (newClass.args.length() != 1) {
					report(newClass, JSweetProblem.ERASED_CLASS_CONSTRUCTOR);
				}
				print("(").print(newClass.args.head).print(")");
			} else if (context.hasAnnotationType(newClass.clazz.type.tsym, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
				print("{}");
			} else {
				if (!getAdapter().substituteNewClass(newClass)) {
					if (typeChecker.checkType(newClass, null, newClass.clazz)) {

						boolean applyVarargs = true;
						MethodSymbol methSym = (MethodSymbol) newClass.constructor;
						if (newClass.args.size() == 0 || !Util.hasVarargs(methSym) //
								|| newClass.args.last().type.getKind() != TypeKind.ARRAY
								// we dont use apply if var args type differ
								|| !context.types.erasure(((ArrayType) newClass.args.last().type).elemtype)
										.equals(context.types
												.erasure(((ArrayType) methSym.getParameters().last().type).elemtype))) {
							applyVarargs = false;
						}
						if (applyVarargs) {
							// this is necessary in case the user defines a
							// Function class that hides the global Function
							// class
							context.addGlobalsMapping("Function", "__Function");
							print("<any>new (__Function.prototype.bind.apply(").print(newClass.clazz).print(", [null");
							for (int i = 0; i < newClass.args.length() - 1; i++) {
								print(", ").print(newClass.args.get(i));
							}
							print("].concat(<any[]>").print(newClass.args.last()).print(")))");
						} else {
							if (newClass.clazz instanceof JCTypeApply) {
								JCTypeApply typeApply = (JCTypeApply) newClass.clazz;
								print("new ").print(typeApply.clazz);
								if (!typeApply.arguments.isEmpty()) {
									print("<").printTypeArgList(typeApply.arguments).print(">");
								} else {
									// erase types since the diamond (<>)
									// operator
									// does not exists in TypeScript
									printAnyTypeArguments(
											((ClassSymbol) newClass.clazz.type.tsym).getTypeParameters().length());
								}
								print("(").printConstructorArgList(newClass, false).print(")");
							} else {
								print("new ").print(newClass.clazz).print("(").printConstructorArgList(newClass, false)
										.print(")");
							}
						}
					}
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
	public AbstractTreePrinter<C> printConstructorArgList(JCNewClass newClass, boolean localClass) {
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

		printArgList(newClass.args);
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

	@Override
	public void visitIndexed(JCArrayAccess arrayAccess) {
		print(arrayAccess.indexed).print("[").print(arrayAccess.index).print("]");
	}

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
		if (!getAdapter().substituteForEachLoop(foreachLoop, hasLength[0], indexVarName)) {
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
			printIndent().print(foreachLoop.body);
			endIndent().println().printIndent().print("}");
			if (!noVariable) {
				endIndent().println().printIndent().print("}");
			}
		}
	}

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

	@Override
	public void visitBinary(JCBinary binary) {
		if (Util.isIntegral(binary.type) && binary.getKind() == Kind.DIVIDE) {
			if (binary.type.getKind() == TypeKind.LONG) {
				print("Math.floor(");
			} else {
				print("(");
			}
		}
		boolean charWrapping = Util.isArithmeticOperator(binary.getKind())
				|| Util.isComparisonOperator(binary.getKind());
		boolean actualCharWrapping = false;
		if (charWrapping && binary.lhs.type.isPrimitive() && context.symtab.charType.tsym == binary.lhs.type.tsym
				&& !(binary.rhs.type.tsym == context.symtab.stringType.tsym)) {
			actualCharWrapping = true;
			if (binary.lhs instanceof JCLiteral) {
				print(binary.lhs).print(".charCodeAt(0)");
			} else {
				print("(c => c.charCodeAt==null?<any>c:c.charCodeAt(0))(").print(binary.lhs).print(")");
			}
		} else {
			print(binary.lhs);
		}
		String op = binary.operator.name.toString();
		if (binary.lhs.type.getKind() == TypeKind.BOOLEAN) {
			if ("|".equals(op)) {
				op = "||";
			} else if ("&".equals(op)) {
				op = "&&";
			} else if ("^".equals(op)) {
				op = "!==";
			}
		}
		if ("==".equals(op) || "!=".equals(op)) {
			if (charWrapping && binary.rhs.type.isPrimitive() && context.symtab.charType.tsym == binary.rhs.type.tsym
					&& !(binary.lhs.type.tsym == context.symtab.stringType.tsym)) {
				actualCharWrapping = true;
			}
		}

		if ("==".equals(op) && !(Util.isNullLiteral(binary.lhs) || Util.isNullLiteral(binary.rhs))) {
			op = actualCharWrapping ? "==" : "===";
		} else if ("!=".equals(op) && !(Util.isNullLiteral(binary.lhs) || Util.isNullLiteral(binary.rhs))) {
			op = actualCharWrapping ? "!=" : "!==";
		}
		space().print(op).space();
		if (charWrapping && binary.rhs.type.isPrimitive() && context.symtab.charType.tsym == binary.rhs.type.tsym
				&& !(binary.lhs.type.tsym == context.symtab.stringType.tsym)) {
			if (binary.rhs instanceof JCLiteral) {
				print(binary.rhs).print(".charCodeAt(0)");
			} else {
				print("(c => c.charCodeAt==null?<any>c:c.charCodeAt(0))(").print(binary.rhs).print(")");
			}
		} else {
			print(binary.rhs);
		}
		if (Util.isIntegral(binary.type) && binary.getKind() == Kind.DIVIDE) {
			if (binary.type.getKind() == TypeKind.LONG) {
				print(")");
			} else {
				print("|0)");
			}
		}
	}

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
					returnType = ((JCLambda) parentFunction).type;
				}
			}
			if (!getAdapter().substituteAssignedExpression(returnType, returnStatement.expr)) {
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
					? (VarSymbol) ((JCFieldAccess) expr).sym : null;
		} else {
			return null;
		}
	}

	@Override
	public void visitAssignop(JCAssignOp assignOp) {
		boolean expand = staticInitializedAssignment = (getStaticInitializedField(assignOp.lhs) != null);
		print(assignOp.lhs);
		staticInitializedAssignment = false;
		String op = assignOp.operator.name.toString();
		if (assignOp.lhs.type.getKind() == TypeKind.BOOLEAN) {
			if ("|".equals(op)) {
				print(" = ").print(assignOp.lhs).print(" || ").print(assignOp.rhs);
				return;
			} else if ("&".equals(op)) {
				print(" = ").print(assignOp.lhs).print(" && ").print(assignOp.rhs);
				return;
			}
		}
		if (expand) {
			print(" = ").print(assignOp.lhs).print(" " + op + " ").print(assignOp.rhs);
			return;
		}
		print(" " + op + "= ");
		print(assignOp.rhs);
	}

	@Override
	public void visitConditional(JCConditional conditional) {
		print(conditional.cond);
		print("?");
		print(conditional.truepart);
		print(":");
		print(conditional.falsepart);
	}

	@Override
	public void visitForLoop(JCForLoop forLoop) {
		print("for(").printArgList(forLoop.init).print("; ").print(forLoop.cond).print("; ").printArgList(forLoop.step)
				.print(") ");
		print(forLoop.body);
	}

	@Override
	public void visitContinue(JCContinue continueStatement) {
		print("continue");
		if (continueStatement.label != null) {
			print(" ").print(continueStatement.label.toString());
		}
	}

	@Override
	public void visitBreak(JCBreak breakStatement) {
		print("break");
		if (breakStatement.label != null) {
			print(" ").print(breakStatement.label.toString());
		}
	}

	@Override
	public void visitLabelled(JCLabeledStatement labelledStatement) {
		print(labelledStatement.label.toString()).print(": ");
		print(labelledStatement.body);
	}

	@Override
	public void visitTypeArray(JCArrayTypeTree arrayType) {
		print(arrayType.elemtype).print("[]");
	}

	@Override
	public void visitNewArray(JCNewArray newArray) {
		if (newArray.elemtype != null) {
			typeChecker.checkType(newArray, null, newArray.elemtype);
		}
		if (newArray.dims != null && !newArray.dims.isEmpty()) {
			if (newArray.dims.size() == 1) {
				if (Util.isNumber(newArray.elemtype.type)) {
					if (newArray.dims.head instanceof JCLiteral
							&& ((int) ((JCLiteral) newArray.dims.head).value) <= 10) {
						boolean hasElements = false;
						print("[");
						for (int i = 0; i < (int) ((JCLiteral) newArray.dims.head).value; i++) {
							print("0, ");
							hasElements = true;
						}
						if (hasElements) {
							removeLastChars(2);
						}
						print("]");
					} else {
						print("(s => { let a=[]; while(s-->0) a.push(0); return a; })(").print(newArray.dims.head)
								.print(")");
					}
				} else {
					print("new Array(").print(newArray.dims.head).print(")");
				}
			} else {
				print("<any> (function(dims) { " + VAR_DECL_KEYWORD
						+ " allocate = function(dims) { if(dims.length==0) { return "
						+ (Util.isNumber(newArray.elemtype.type) ? "0" : "undefined") + "; } else { " + VAR_DECL_KEYWORD
						+ " array = []; for(" + VAR_DECL_KEYWORD
						+ " i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})");
				print("([");
				printArgList(newArray.dims);
				print("])");
			}
		} else {
			print("[");
			if (newArray.elems != null) {
				printArgList(newArray.elems);
			}
			print("]");
		}
	}

	boolean inRollback = false;

	@Override
	public void visitUnary(JCUnary unary) {
		if (getContext().options.isSupportSaticLazyInitialization()) {
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

	@Override
	public void visitSwitch(JCSwitch switchStatement) {
		print("switch(");
		print(switchStatement.selector);
		print(") {").println();
		for (JCCase caseStatement : switchStatement.cases) {
			printIndent();
			print(caseStatement);
		}
		printIndent().print("}");
	}

	protected void printCaseStatementPattern(JCExpression pattern) {
	}

	@Override
	public void visitCase(JCCase caseStatement) {
		if (caseStatement.pat != null) {
			print("case ");
			if (!getAdapter().substituteCaseStatementPattern(caseStatement, caseStatement.pat)) {
				if (caseStatement.pat.type.isPrimitive()
						|| String.class.getName().equals(caseStatement.pat.type.toString())) {
					print(caseStatement.pat);
				} else {
					if (context.useModules) {
						print(caseStatement.pat.type.tsym.getSimpleName() + "." + caseStatement.pat);
					} else {
						print(getRootRelativeName(caseStatement.pat.type.tsym) + "." + caseStatement.pat);
					}
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

	@Override
	public void visitTypeCast(JCTypeCast cast) {
		if (getAdapter().substituteAssignedExpression(cast.type, cast.expr)) {
			return;
		}
		if (Util.isIntegral(cast.type)) {
			if (cast.type.getKind() == TypeKind.LONG) {
				print("Math.floor(");
			} else {
				print("(");
			}
		}
		if (getAdapter().needsTypeCast(cast)) {
			// Java is more permissive than TypeScript when casting type
			// variables
			if (cast.expr.type.getKind() == TypeKind.TYPEVAR) {
				print("<any>");
			} else {
				print("<").getAdapter().substituteAndPrintType(cast.clazz).print(">");
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

	@Override
	public void visitDoLoop(JCDoWhileLoop doWhileLoop) {
		print("do ");
		print(doWhileLoop.body);
		print(" while(").print(doWhileLoop.cond).print(")");
	}

	@Override
	public void visitWhileLoop(JCWhileLoop whileLoop) {
		print("while(").print(whileLoop.cond).print(")");
		print(whileLoop.body);
	}

	@Override
	public void visitAssign(JCAssign assign) {
		if (!getAdapter().substituteAssignment(assign)) {
			staticInitializedAssignment = getStaticInitializedField(assign.lhs) != null;
			print(assign.lhs).print(isAnnotationScope ? ": " : " = ");
			if (!getAdapter().substituteAssignedExpression(assign.lhs.type, assign.rhs)) {
				print(assign.rhs);
			}
			staticInitializedAssignment = false;
		}
	}

	@Override
	public void visitTry(JCTry tryStatement) {
		if (tryStatement.resources != null && !tryStatement.resources.isEmpty()) {
			report(tryStatement, JSweetProblem.UNSUPPORTED_TRY_WITH_RESOURCE);
		}
		if (tryStatement.catchers.isEmpty() && tryStatement.finalizer == null) {
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
				getAdapter().substituteAndPrintType(catcher.param.getType());
				print(">__e;").println();
				// }
				printBlockStatements(catcher.body.getStatements());
				endIndent().println().printIndent().print("}");
			}
			endIndent().println().printIndent().print("}");
		} else if (tryStatement.catchers.size() == 1) {
			print(tryStatement.catchers.head);
		}
		if (tryStatement.finalizer != null) {
			print(" finally ").print(tryStatement.finalizer);
		}
	}

	@Override
	public void visitCatch(JCCatch catcher) {
		print(" catch(").print(catcher.param.name.toString()).print(") ");
		print(catcher.body);
	}

	@Override
	public void visitLambda(JCLambda lamba) {
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
		print("(").printArgList(lamba.params).print(") => ");
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

	@Override
	public void visitReference(JCMemberReference memberReference) {
		if (memberReference.sym instanceof MethodSymbol) {
			MethodSymbol method = (MethodSymbol) memberReference.sym;
			if (getParent() instanceof JCTypeCast) {
				print("(");
			}
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
			print(" => { return ");
		}

		if (memberReference.expr.type.toString().endsWith(JSweetConfig.GLOBALS_CLASS_NAME)) {
			print(memberReference.name.toString());
		} else {
			if ("<init>".equals(memberReference.name.toString())) {
				if (context.types.isArray(memberReference.expr.type)) {
					print("new Array<");
					getAdapter().substituteAndPrintType(((JCArrayTypeTree) memberReference.expr).elemtype);
					print(">");
				} else {
					print("new ").print(memberReference.expr);
				}
			} else {
				print(memberReference.expr).print(".").print(memberReference.name.toString());
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

	@Override
	public void visitTypeParameter(JCTypeParameter typeParameter) {
		print(typeParameter.name.toString());
		if (typeParameter.bounds != null && !typeParameter.bounds.isEmpty()) {
			print(" extends ");
			for (JCExpression e : typeParameter.bounds) {
				getAdapter().substituteAndPrintType(e).print(" & ");
			}
			removeLastChars(3);
		}
	}

	@Override
	public void visitSynchronized(JCSynchronized sync) {
		report(sync, JSweetProblem.SYNCHRONIZATION);
		if (sync.body != null) {
			print(sync.body);
		}
	}

	private void print(String exprStr, JCTree expr) {
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
		if (checkFirstArrayElement || !getAdapter().substituteInstanceof(exprStr, expr, type)) {
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
						print(" != null && ");
						print(exprStr, expr);
						if (checkFirstArrayElement)
							print("[0]");
						String qualifiedName = getQualifiedTypeName(type.tsym, false);
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
		if (!(getParent() instanceof JCParens)) {
			print(")");
		}
	}

	@Override
	public void visitTypeTest(JCInstanceOf instanceOf) {
		printInstanceOf(null, instanceOf.expr, instanceOf.clazz.type);
	}

	@Override
	public void visitThrow(JCThrow throwStatement) {
		print("throw ").print(throwStatement.expr);
	}

	@Override
	public void visitAssert(JCAssert assertion) {
		if (!context.options.isIgnoreAssertions()) {
			String assertCode = assertion.toString().replace("\"", "'");
			print("if(!(").print(assertion.cond).print(
					")) throw new Error(\"Assertion error line " + getCurrentLine() + ": " + assertCode + "\");");
		}
	}

	@Override
	public void visitAnnotation(JCAnnotation annotation) {
		if (!context.hasAnnotationType(annotation.type.tsym, JSweetConfig.ANNOTATION_DECORATOR)) {
			return;
		}
		print("@").print(annotation.getAnnotationType()).print("(");
		if (annotation.getArguments() != null && !annotation.getArguments().isEmpty()) {
			isAnnotationScope = true;
			print(" { ");
			for (JCExpression e : annotation.getArguments()) {
				print(e);
				print(", ");
			}
			removeLastChars(2);
			print(" } ");
			isAnnotationScope = false;
		}
		print(")").println().printIndent();
	}

}
