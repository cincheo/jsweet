/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.transpiler.typescript;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jsweet.transpiler.util.Util.getRootRelativeJavaName;
import static org.jsweet.transpiler.util.Util.getRootRelativeName;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.OverloadScanner.Overload;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import org.jsweet.transpiler.util.Util;
import org.jsweet.transpiler.util.VariableKind;

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type.ArrayType;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
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

/**
 * This is a TypeScript printer for translating the Java AST to a TypeScript
 * program.
 * 
 * @author Renaud Pawlak
 */
public class Java2TypeScriptTranslator extends AbstractTreePrinter {

	private boolean skipTypeAnnotations = false;

	/**
	 * Creates a new TypeScript translator.
	 * 
	 * @param logHandler
	 *            the handler for logging and error reporting
	 * @param context
	 *            the AST scanning context
	 * @param compilationUnit
	 *            the compilation unit to be translated
	 * @param preserveSourceLineNumbers
	 *            if true, the printer tries to preserve the line numbers of the
	 *            original Java code, for debugging purpose
	 */
	public Java2TypeScriptTranslator(TranspilationHandler logHandler, JSweetContext context, JCCompilationUnit compilationUnit,
			boolean preserveSourceLineNumbers) {
		super(logHandler, context, compilationUnit, new Java2TypeScriptAdapter(), preserveSourceLineNumbers);
	}

	/**
	 * '.ts' for TypeScript output.
	 */
	@Override
	public String getTargetFilesExtension() {
		return ".ts";
	}

	private static java.util.List<Class<?>> statementsWithNoSemis = Arrays
			.asList(new Class<?>[] { JCIf.class, JCForLoop.class, JCEnhancedForLoop.class, JCSwitch.class });

	private List<String> compilationUnitMainCalls = new LinkedList<String>();
	private JCMethodDecl mainMethod;

	private List<JCImport> imports = new ArrayList<JCImport>();

	private boolean globalModule = false;

	private PackageSymbol topLevelPackage;

	private void useModule(PackageSymbol targetPackage, JCTree sourceTree, String targetName, String moduleName) {
		if (context.useModules) {
			context.packageDependencies.add(targetPackage);
			context.packageDependencies.add(compilationUnit.packge);
			context.packageDependencies.addEdge(compilationUnit.packge, targetPackage);
		}
		context.registerUsedModule(moduleName);
		Set<String> importedNames = context.getImportedNames(compilationUnit.packge);
		if (!importedNames.contains(targetName)) {
			if (context.useModules) {
				print("import " + targetName + " = require(\"" + moduleName + "\"); ").println();
			}
			importedNames.add(targetName);
		}
	}

	private void checkRootPackageParent(JCCompilationUnit topLevel, PackageSymbol rootPackage, PackageSymbol parentPackage) {
		if (parentPackage == null) {
			return;
		}
		if (Util.hasAnnotationType(parentPackage, JSweetConfig.ANNOTATION_ROOT)) {
			report(topLevel.getPackageName(), JSweetProblem.ENCLOSED_ROOT_PACKAGES, rootPackage.getQualifiedName().toString(),
					parentPackage.getQualifiedName().toString());
		}
		for (Symbol s : parentPackage.getEnclosedElements()) {
			if (!(s instanceof PackageSymbol)) {
				report(topLevel.getPackageName(), JSweetProblem.CLASS_OUT_OF_ROOT_PACKAGE_SCOPE, s.getQualifiedName().toString(),
						rootPackage.getQualifiedName().toString());
			}
		}
		checkRootPackageParent(topLevel, rootPackage, (PackageSymbol)parentPackage.owner);
	}

	@Override
	public void visitTopLevel(JCCompilationUnit topLevel) {
		printIndent().print("\"Generated from Java with JSweet " + JSweetConfig.getVersionNumber() + " - http://www.jsweet.org\";").println();
		PackageSymbol rootPackage = Util.getFirstEnclosingRootPackage(topLevel.packge);
		if (rootPackage != null) {
			checkRootPackageParent(topLevel, rootPackage, (PackageSymbol)rootPackage.owner);
		}

		topLevelPackage = Util.getTopLevelPackage(topLevel.packge);
		if (topLevelPackage != null) {
			context.topLevelPackageNames.add(topLevelPackage.getQualifiedName().toString());
		}

		footer.delete(0, footer.length());

		imports.clear();
		String packge = topLevel.packge.toString();

		globalModule = JSweetConfig.GLOBALS_PACKAGE_NAME.equals(packge) || packge.endsWith("." + JSweetConfig.GLOBALS_PACKAGE_NAME);
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

		if (!context.useModules) {
			context.clearImportedNames(compilationUnit.packge);
		}

		if (context.useModules) {
			// export-import submodules
			File parent = new File(compilationUnit.getSourceFile().getName()).getParentFile();
			for (File file : parent.listFiles()) {
				if (file.isDirectory() && !file.getName().startsWith(".")) {
					boolean containsSourceFile = false;
					for (File child : file.listFiles()) {
						for (File sourceFile : SourceFile.toFiles(context.sourceFiles)) {
							if (child.getAbsolutePath().equals(sourceFile.getAbsolutePath())) {
								containsSourceFile = true;
								break;
							}
						}
					}
					if (containsSourceFile) {
						Set<String> importedNames = context.getImportedNames(compilationUnit.packge);
						if (!importedNames.contains(file.getName())) {
							print("export import " + file.getName() + " = require('./" + file.getName() + "/" + JSweetConfig.MODULE_FILE_NAME + "');")
									.println();
							importedNames.add(file.getName());
						}
					}
				}
			}
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
							if (Util.hasAnnotationType(qualified.sym, JSweetConfig.ANNOTATION_MODULE)) {
								String actualName = Util.getAnnotationValue(qualified.sym, JSweetConfig.ANNOTATION_MODULE, null);
								useModule(null, importDecl, qualified.name.toString(), actualName);
							}
						} else {
							// static import case (imported fields and methods)
							if (qualified.selected instanceof JCFieldAccess) {
								JCFieldAccess qualifier = (JCFieldAccess) qualified.selected;
								if (qualifier.sym != null) {
									try {
										for (Symbol importedMember : qualifier.sym.getEnclosedElements()) {
											if (qualified.name.equals(importedMember.getSimpleName())) {
												if (Util.hasAnnotationType(importedMember, JSweetConfig.ANNOTATION_MODULE)) {
													String actualName = Util.getAnnotationValue(importedMember, JSweetConfig.ANNOTATION_MODULE, null);
													useModule(null, importDecl, importedMember.getSimpleName().toString(), actualName);
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
				if (importDecl.isStatic() && (qualified.selected instanceof JCFieldAccess)) {
					qualified = (JCFieldAccess) qualified.selected;
				}
				if (qualified.sym instanceof ClassSymbol) {
					ClassSymbol importedClass = (ClassSymbol) qualified.sym;
					if (Util.isSourceType(importedClass)) {
						PackageSymbol targetRootPackage = Util.getTopLevelPackage(importedClass);
						if (targetRootPackage == null) {
							continue;
						}
						String targetRootPackageName = targetRootPackage.getSimpleName().toString();
						String pathToReachRootPackage = Util.getRelativePath("/" + Util.getRootRelativeJavaName(compilationUnit.packge).replace('.', '/'),
								"/" + targetRootPackageName);
						if (pathToReachRootPackage == null) {
							pathToReachRootPackage = ".";
							// continue;
						}
						File moduleFile = new File(new File(pathToReachRootPackage), JSweetConfig.MODULE_FILE_NAME);
						useModule((PackageSymbol) qualified.sym.getEnclosingElement(), importDecl, targetRootPackageName,
								moduleFile.getPath().replace('\\', '/'));
					}
				}
			}
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
					if (!identifierPackage.getSimpleName().toString().equals(compilationUnit.packge.getSimpleName().toString())) {
						useModule(identifierPackage, identifier, identifierPackage.getSimpleName().toString(), moduleFile.getPath().replace('\\', '/'));
					}
				} else if (identifier.sym instanceof ClassSymbol) {
					if (JSweetConfig.GLOBALS_PACKAGE_NAME.equals(identifier.sym.getEnclosingElement().getSimpleName().toString())) {
						String pathToModulePackage = Util.getRelativePath(compilationUnit.packge, identifier.sym.getEnclosingElement());
						if (pathToModulePackage == null) {
							return;
						}
						File moduleFile = new File(new File(pathToModulePackage), JSweetConfig.MODULE_FILE_NAME);
						if (!identifier.sym.getEnclosingElement().equals(compilationUnit.packge.getSimpleName().toString())) {
							useModule((PackageSymbol) identifier.sym.getEnclosingElement(), identifier, JSweetConfig.GLOBALS_PACKAGE_NAME,
									moduleFile.getPath().replace('\\', '/'));
						}
					}
				}
			}

			@Override
			public void visitApply(JCMethodInvocation invocation) {
				// TODO: same for static variables
				if (invocation.meth instanceof JCIdent && JSweetConfig.TS_STRICT_MODE_KEYWORDS.contains(invocation.meth.toString().toLowerCase())) {
					PackageSymbol invocationPackage = (PackageSymbol) ((JCIdent) invocation.meth).sym.getEnclosingElement().getEnclosingElement();
					String rootRelativeInvocationPackageName = Util.getRootRelativeName(invocationPackage);
					if (rootRelativeInvocationPackageName.indexOf('.') == -1) {
						super.visitApply(invocation);
						return;
					}
					String targetRootPackageName = rootRelativeInvocationPackageName.substring(0, rootRelativeInvocationPackageName.indexOf('.'));
					String pathToReachRootPackage = Util.getRelativePath("/" + compilationUnit.packge.getQualifiedName().toString().replace('.', '/'),
							"/" + targetRootPackageName);
					if (pathToReachRootPackage == null) {
						super.visitApply(invocation);
						return;
					}
					File moduleFile = new File(new File(pathToReachRootPackage), JSweetConfig.MODULE_FILE_NAME);
					if (!invocationPackage.toString().equals(compilationUnit.packge.getSimpleName().toString())) {
						useModule(invocationPackage, invocation, targetRootPackageName, moduleFile.getPath().replace('\\', '/'));
					}
				}
				super.visitApply(invocation);
			}

		};
		inlinedModuleScanner.scan(compilationUnit);

		if (!globalModule && !context.useModules) {
			printIndent().print("module ").print(rootRelativePackageName).print(" {").startIndent().println();
		}

		compilationUnitMainCalls.clear();

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
			removeLastChar().endIndent().printIndent().print("}");
		}

		if (footer.length() > 0) {
			println().print(footer.toString());
		}
		for (String mainMethodCall : compilationUnitMainCalls) {
			println().print(mainMethodCall).println();
		}
		globalModule = false;
	}

	private boolean interfaceScope = false;

	private boolean enumScope = false;

	private boolean removedSuperclass = false;

	private boolean declareClassScope;

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		if (getParent() instanceof JCClassDecl) {
			report(classdecl, JSweetProblem.INNER_CLASS, classdecl.name);
			return;
		}

		declareClassScope = Util.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_AMBIENT);
		interfaceScope = false;
		removedSuperclass = false;
		enumScope = false;
		boolean globals = JSweetConfig.GLOBALS_CLASS_NAME.equals(classdecl.name.toString());
		if (!globals) {
			if (!globalModule) {
				print("export ");
			}
			if (Util.isInterface(classdecl.sym)) {
				print("interface ");
				interfaceScope = true;
			} else {
				if (classdecl.getKind() == Kind.ENUM) {
					enumScope = true;
					print("enum ");
				} else {
					if (declareClassScope) {
						print("declare ");
					}

					print("class ");
				}
			}
			print(classdecl.name.toString());
			if (interfaceScope && classdecl.getKind() == Kind.CLASS) {
				if (!classdecl.mods.getFlags().contains(Modifier.ABSTRACT)) {
					report(classdecl, JSweetProblem.INTERFACE_MUST_BE_ABSTRACT, classdecl.name);
				}
			}
			if (classdecl.typarams != null && classdecl.typarams.size() > 0) {
				print("<").printArgList(classdecl.typarams).print(">");
			}
			if (classdecl.extending != null) {
				if (!JSweetConfig.isJDKReplacementMode() && !(JSweetConfig.OBJECT_CLASSNAME.equals(classdecl.extending.type.toString())
						|| Object.class.getName().equals(classdecl.extending.type.toString()))) {
					if (!interfaceScope && Util.isInterface(classdecl.extending.type.tsym)) {
						print(" implements ");
					} else {
						print(" extends ");
					}
					print(classdecl.extending);
				} else {
					removedSuperclass = true;
				}
			}
			print(" {").println().startIndent();
		}

		for (JCTree def : classdecl.defs) {
			if (def instanceof JCVariableDecl) {
				if (enumScope && ((JCVariableDecl) def).type.tsym != classdecl.type.tsym) {
					report(def, ((JCVariableDecl) def).name, JSweetProblem.INVALID_FIELD_IN_ENUM);
					continue;
				}
				if (!getAdapter().needsVariableDecl((JCVariableDecl) def, VariableKind.FIELD)) {
					continue;
				}
			}
			printIndent();
			int pos = getCurrentPosition();
			print(def);
			if (getCurrentPosition() == pos) {
				removeLastIndent();
				continue;
			}
			if (def instanceof JCVariableDecl) {
				if (enumScope) {
					print(",");
				} else {
					print(";");
					println();
					println();
				}
			} else {
				println();
				println();
			}
		}
		if (!globals) {
			removeLastChar().endIndent().printIndent().print("}");
		}

		if (mainMethod != null && mainMethod.getParameters().size() < 2) {
			String mainClassName = getRootRelativeName(classdecl.sym);
			if (context.useModules) {
				int dotIndex = mainClassName.lastIndexOf(".");
				mainClassName = mainClassName.substring(dotIndex + 1);
			}
			if (globals) {
				int dotIndex = mainClassName.lastIndexOf(".");
				if (dotIndex == -1) {
					mainClassName = "";
				} else {
					mainClassName = mainClassName.substring(0, dotIndex);
				}
			}

			String mainMethodQualifier = mainClassName;
			if (!isBlank(mainClassName)) {
				mainMethodQualifier = mainClassName + ".";
			}
			context.entryFiles.add(new File(compilationUnit.sourcefile.getName()));
			compilationUnitMainCalls
					.add(mainMethodQualifier + JSweetConfig.MAIN_FUNCTION_NAME + "(" + (mainMethod.getParameters().isEmpty() ? "" : "null") + ");");
		}

		interfaceScope = false;
	}

	private String getTSMethodName(JCMethodDecl methodDecl) {
		String name = methodDecl.name.toString();
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

	private String getTypeInitalValue(String typeName) {
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

	@Override
	public void visitMethodDef(JCMethodDecl methodDecl) {
		JCClassDecl parent = (JCClassDecl) getParent();

		boolean constructor = methodDecl.name.toString().equals("<init>");
		if (enumScope) {
			if (constructor) {
				if (parent.pos != methodDecl.pos) {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_CONSTRUCTOR_IN_ENUM);
				}
			} else {
				report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_IN_ENUM);
			}
			return;
		}

		Overload overload = context.getOverload(parent.sym, methodDecl.name.toString());
		boolean inOverload = overload != null && overload.methods.size() > 1;
		if (inOverload) {
			if (!overload.isValid) {
				report(methodDecl, methodDecl.name, JSweetProblem.INVALID_OVERLOAD, methodDecl.name);
				return;
			} else {
				if (!overload.coreMethod.equals(methodDecl.sym)) {
					return;
				}
			}
		}

		if (methodDecl.mods.getFlags().contains(Modifier.NATIVE)) {
			if (!declareClassScope) {
				report(methodDecl, methodDecl.name, JSweetProblem.NATIVE_MODIFIER_IS_NOT_ALLOWED, methodDecl.name);
			}
		} else {
			if (declareClassScope && !constructor) {
				report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name, parent.name);
			}
		}

		if (methodDecl.name.toString().equals("constructor")) {
			report(methodDecl, methodDecl.name, JSweetProblem.CONSTRUCTOR_MEMBER);
		}
		VarSymbol v = Util.findFieldDeclaration(parent.sym, methodDecl.name);
		if (v != null) {
			report(methodDecl, methodDecl.name, JSweetProblem.METHOD_CONFLICTS_FIELD, methodDecl.name, v.owner);
		}
		if (JSweetConfig.MAIN_FUNCTION_NAME.equals(methodDecl.name.toString()) && methodDecl.mods.getFlags().contains(Modifier.STATIC)
				&& !Util.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_DISABLED)) {
			mainMethod = methodDecl;
		}
		if (methodDecl.pos == parent.pos) {
			return;
		}

		boolean globals = JSweetConfig.GLOBALS_CLASS_NAME.equals(parent.name.toString());
		if (globals) {
			if (constructor) {
				report(methodDecl, methodDecl.name, JSweetProblem.GLOBAL_CONSTRUCTOR_DEF);
				return;
			}

			if (context.useModules) {
				if (methodDecl.mods.getFlags().contains(Modifier.PUBLIC)) {
					print("export ");
				}
			} else {
				if (!globalModule) {
					print("export ");
				}
			}
			print("function ");
		} else {
			if (methodDecl.mods.getFlags().contains(Modifier.PUBLIC)) {
				if (!interfaceScope) {
					print("public ");
				}
			}
			if (methodDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
				if (!interfaceScope) {
					print("private ");
				} else {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_PRIVATE_IN_INTERFACE, methodDecl.name, parent.name);
				}
			}
			if (methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
				if (!interfaceScope) {
					print("static ");
				} else {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_STATIC_IN_INTERFACE, methodDecl.name, parent.name);
				}
			}
		}
		printIdentifier(getTSMethodName(methodDecl));
		if (methodDecl.typarams != null && !methodDecl.typarams.isEmpty()) {
			print("<").printArgList(methodDecl.typarams).print(">");
		}
		print("(");
		int i = 0;
		for (JCVariableDecl param : methodDecl.params) {
			print(param);
			if (inOverload && overload.defaultValues[i] != null) {
				print(" = ").print(overload.defaultValues[i]);
			}
			print(", ");
			i++;
		}
		if (!methodDecl.params.isEmpty()) {
			removeLastChars(2);
		}
		print(") ");
		if (methodDecl.restype != null && methodDecl.restype.type.getTag() != TypeTag.VOID) {
			print(": ");
			getAdapter().substituteAndPrintType(methodDecl.restype);
		}
		if (methodDecl.getBody() == null) {
			if (!interfaceScope && methodDecl.mods.getFlags().contains(Modifier.ABSTRACT)) {
				print(" {");
				String typeName = methodDecl.restype.toString();
				if (!"void".equals(typeName)) {
					print(" return ").print(getTypeInitalValue(typeName)).print("; ");
				}
				print("}");
			} else {
				print(";");
			}
		} else {
			boolean hasStatements = methodDecl.getBody().getStatements().isEmpty();
			if (interfaceScope) {
				report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name, parent.name);
			}
			if (declareClassScope) {
				if (!constructor || hasStatements) {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name, parent.name);
				}
				print(";");
			} else {
				print(" ").print(methodDecl.getBody());
			}
		}
	}

	@Override
	public void visitBlock(JCBlock block) {
		JCTree parent = getParent();
		boolean globals = (parent instanceof JCClassDecl) && JSweetConfig.GLOBALS_CLASS_NAME.equals(((JCClassDecl) parent).name.toString());
		boolean initializer = (parent instanceof JCClassDecl) && !globals;
		int static_i = 0;
		int i = 0;
		if (initializer) {
			if (interfaceScope) {
				report(block, JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, ((JCClassDecl) parent).name);
			}
			for (JCTree m : ((JCClassDecl) parent).getMembers()) {
				if (m instanceof JCBlock) {
					if (((JCBlock) m).isStatic()) {
						static_i++;
						if (block == m) {
							print("static __static_initializer_" + static_i + "() : any ");
							break;
						}
					} else {
						i++;
						if (block == m) {
							print("__initializer_" + i + "() : any ");
							break;
						}
					}
				}
			}
		}
		if (!globals) {
			print("{").println().startIndent();
		}

		for (JCStatement statement : block.stats) {
			printIndent();
			int pos = getCurrentPosition();
			print(statement);
			if (getCurrentPosition() == pos) {
				removeLastIndent();
				continue;
			}
			if (!statementsWithNoSemis.contains(statement.getClass())) {
				print(";");
			}
			println();
		}
		if (!globals) {
			endIndent().printIndent().print("}");
		}
		if (initializer) {
			if (block.isStatic()) {
				println().printIndent().print("static __static_initializer_" + static_i + "_var : any = " + getParent(JCClassDecl.class).getSimpleName()
						+ ".__static_initializer_" + static_i + "();");
			} else {
				println().printIndent().print("__initializer_" + i + "_var : any = this.__initializer_" + i + "();");
			}
		}
	}

	@Override
	public void visitVarDef(JCVariableDecl varDecl) {
		if (getParent().getKind() == Kind.ENUM) {
			print(varDecl.name.toString());
		} else {
			JCTree parent = getParent();
			String name = varDecl.name.toString();

			if (parent instanceof JCClassDecl) {
				MethodSymbol m = Util.findMethodDeclarationInType(context.types, ((JCClassDecl) parent).sym, name, null);
				if (m != null) {
					report(varDecl, varDecl.name, JSweetProblem.FIELD_CONFLICTS_METHOD, name, m.owner);
				}
				if (!interfaceScope && name.equals("constructor")) {
					report(varDecl, varDecl.name, JSweetProblem.CONSTRUCTOR_MEMBER);
				}
			} else {
				if (JSweetConfig.JS_KEYWORDS.contains(varDecl.name.toString())) {
					report(varDecl, varDecl.name, JSweetProblem.JS_KEYWORD_CONFLICT, name, name);
					name = "_jsweet_" + name;
				}
			}

			boolean globals = (parent instanceof JCClassDecl) && JSweetConfig.GLOBALS_CLASS_NAME.equals(((JCClassDecl) parent).name.toString());

			if (!globals && parent instanceof JCClassDecl) {
				if (varDecl.mods.getFlags().contains(Modifier.PUBLIC)) {
					if (!interfaceScope) {
						print("public ");
					}
				}
				if (varDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
					if (!interfaceScope) {
						print("private ");
					} else {
						report(varDecl, varDecl.name, JSweetProblem.INVALID_PRIVATE_IN_INTERFACE, varDecl.name, ((JCClassDecl) parent).name);
					}
				}
				if (varDecl.mods.getFlags().contains(Modifier.STATIC)) {
					if (!interfaceScope) {
						print("static ");
					} else {
						report(varDecl, varDecl.name, JSweetProblem.INVALID_STATIC_IN_INTERFACE, varDecl.name, ((JCClassDecl) parent).name);
					}
				}
			}
			if (!interfaceScope && parent instanceof JCClassDecl) {
				if (Util.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_OPTIONAL)) {
					report(varDecl, varDecl.name, JSweetProblem.USELESS_OPTIONAL_ANNOTATION, varDecl.name, ((JCClassDecl) parent).name);
				}
			}
			if (globals || !(parent instanceof JCClassDecl || parent instanceof JCMethodDecl || parent instanceof JCLambda)) {
				print("var ");
			}

			if (isVarargs(varDecl)) {
				print("...");
			}

			printIdentifier(name);
			if (interfaceScope && Util.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_OPTIONAL)) {
				print("?");
			}
			if (!skipTypeAnnotations) {
				if (typeChecker.checkType(varDecl, varDecl.name, varDecl.vartype)) {
					print(": ");
					getAdapter().substituteAndPrintType(varDecl.vartype);
				}
			}
			if (varDecl.init != null) {
				if (!globals && parent instanceof JCClassDecl && interfaceScope) {
					report(varDecl, varDecl.name, JSweetProblem.INVALID_FIELD_INITIALIZER_IN_INTERFACE, varDecl.name, ((JCClassDecl) parent).name);
				} else {
					print(" = ").print(varDecl.init);
				}
			}
		}
	}

	private boolean isVarargs(JCVariableDecl varDecl) {
		return (varDecl.mods.flags & Flags.VARARGS) == Flags.VARARGS;
	}

	private boolean hasVarargs(MethodSymbol methodSymbol) {
		return methodSymbol != null && methodSymbol.getParameters().length() > 0 && (methodSymbol.flags() & Flags.VARARGS) != 0;
	}

	@Override
	public void visitParens(JCParens parens) {
		print("(");
		super.visitParens(parens);
		print(")");
	}

	@Override
	public void visitImport(JCImport importDecl) {
		imports.add(importDecl);
		String qualId = importDecl.getQualifiedIdentifier().toString();
		if (qualId.endsWith("*") && !qualId.endsWith("." + JSweetConfig.GLOBALS_CLASS_NAME + ".*")) {
			report(importDecl, JSweetProblem.WILDCARD_IMPORT);
			return;
		}
		String adaptedQualId = getAdapter().needsImport(importDecl, qualId);
		if (adaptedQualId != null && adaptedQualId.contains(".")) {
			String[] namePath = adaptedQualId.split("\\.");
			String name = namePath[namePath.length - 1];
			name = getAdapter().getIdentifier(name);
			if (context.useModules) {
				if (!context.getImportedNames(compilationUnit.packge).contains(name)) {
					print("import ").print(name).print(" = ").print(adaptedQualId).print(";");
					context.registerImportedName(compilationUnit.packge, name);
				}
			} else {
				if (topLevelPackage == null) {
					if (context.globalImports.contains(name)) {
						// Tsc global package does allow multiple import with
						// the same name in the global namespace (bug?)
						return;
					}
					context.globalImports.add(name);
				}
				print("import ").print(name).print(" = ").print(adaptedQualId).print(";");
			}
		}

	}

	@Override
	public void visitSelect(JCFieldAccess fieldAccess) {
		if (!getAdapter().substituteFieldAccess(fieldAccess)) {
			if ("class".equals(fieldAccess.name.toString())) {
				print(fieldAccess.selected);
			} else {
				print(fieldAccess.selected).print(".").printIdentifier(fieldAccess.name.toString());
			}
		}
	}

	private JCImport getStaticImport(String methName) {
		for (JCImport i : imports) {
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
			String name = getRootRelativeJavaName(fa.selected.type.tsym);
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
			// MethodSymbol staticMethSymbol =
			// Util.findMethodDeclarationInType(printer.context.types,
			// fa.selected.type.tsym, "" + fa.name, null);
			// String methodName = Util.getActualName(staticMethSymbol);
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
			if (methName.equals("super") && removedSuperclass) {
				return;
			}

			boolean applyVarargs = true;
			if (JSweetConfig.NEW_FUNCTION_NAME.equals(methName)) {
				print("new ");
				applyVarargs = false;
			}

			boolean anonymous = JSweetConfig.ANONYMOUS_FUNCTION_NAME.equals(methName) || JSweetConfig.ANONYMOUS_STATIC_FUNCTION_NAME.equals(methName)
					|| JSweetConfig.NEW_FUNCTION_NAME.equals(methName);
			boolean targetIsThisOrStaticImported = meth.startsWith(methName) || meth.startsWith("this.");

			MethodType type = (MethodType) inv.meth.type;
			MethodSymbol methSym = null;
			String methodName = null;
			if (targetIsThisOrStaticImported) {
				JCImport staticImport = getStaticImport(methName);
				if (staticImport == null) {
					methSym = Util.findMethodDeclarationInType(context.types, getParent(JCClassDecl.class).sym, methName, type);
					if (methSym != null) {
						typeChecker.checkApply(inv, methSym);
						if (!methSym.getModifiers().contains(Modifier.STATIC)) {
							if (!meth.startsWith("this.")) {
								print("this");
								if (!anonymous) {
									print(".");
								}
							}
						} else {
							if (!JSweetConfig.GLOBALS_CLASS_NAME.equals(methSym.owner.getSimpleName().toString())) {
								print("" + methSym.owner.getSimpleName());
								if (!anonymous) {
									print(".");
								}
							}
						}
					}
				} else {
					JCFieldAccess staticFieldAccess = (JCFieldAccess) staticImport.qualid;
					methSym = Util.findMethodDeclarationInType(context.types, staticFieldAccess.selected.type.tsym, methName, type);
					// staticImported = true;
					if (JSweetConfig.TS_STRICT_MODE_KEYWORDS.contains(methName.toLowerCase())) {
						// if method is a reserved TS keyword, no "static
						// import" possible, fully qualified mode
						String targetClass = getStaticContainerFullName(staticImport);
						if (!isBlank(targetClass)) {
							print(targetClass);
							print(".");
						}
						if (JSweetConfig.isLibPath(methSym.getEnclosingElement().getQualifiedName().toString())) {
							methodName = methName.toLowerCase();
						}
					}
				}
			} else {
				if (inv.meth instanceof JCFieldAccess) {
					JCExpression selected = ((JCFieldAccess) inv.meth).selected;
					methSym = Util.findMethodDeclarationInType(context.types, selected.type.tsym, methName, type);
					if (methSym != null) {
						typeChecker.checkApply(inv, methSym);
					}
				}
			}

			boolean isStatic = methSym == null || methSym.isStatic();
			if (!hasVarargs(methSym) //
					|| inv.args.last().type.getKind() != TypeKind.ARRAY
					// we dont use apply if var args type differ
					|| !((ArrayType) inv.args.last().type).elemtype.equals(((ArrayType) methSym.params().last().type).elemtype)) {
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
					print(")." + ((JCFieldAccess) inv.meth).name);
				} else if (methodName != null) {
					print(methodName);
				} else {
					print(inv.meth);
				}
			}

			if (applyVarargs) {
				print(".apply");
			}

			if (inv.typeargs != null && !inv.typeargs.isEmpty()) {
				print("<");
				for (JCExpression argument : inv.typeargs) {
					getAdapter().substituteAndPrintType(argument).print(",");
				}
				removeLastChar();
				print(">");
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
			for (int i = 0; i < argsLength; i++) {
				JCExpression arg = inv.args.get(i);
				print(arg);
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
		if (!getAdapter().substituteIdentifier(ident)) {
			// add this of class name if ident is a field
			if (ident.sym instanceof VarSymbol && !ident.sym.name.equals(context.names._this) && !ident.sym.name.equals(context.names._super)) {
				VarSymbol varSym = (VarSymbol) ident.sym; // findFieldDeclaration(currentClass,
				// ident.name);
				if (varSym != null) {
					if (varSym.owner instanceof ClassSymbol) {
						if (!varSym.getModifiers().contains(Modifier.STATIC)) {
							print("this.");
						} else {
							print(varSym.owner.getSimpleName() + ".");
						}
					} else {
						if (JSweetConfig.JS_KEYWORDS.contains(name)) {
							name = "_jsweet_" + name;
						}
					}
				}
			}
			printIdentifier(name);
		}
	}

	@Override
	public void visitTypeApply(JCTypeApply typeApply) {
		getAdapter().substituteAndPrintType(typeApply);
	}

	@Override
	public void visitNewClass(JCNewClass newClass) {
		ClassSymbol clazz = ((ClassSymbol) newClass.clazz.type.tsym);
		if (clazz.name.toString().endsWith(JSweetConfig.GLOBALS_CLASS_NAME)) {
			report(newClass, JSweetProblem.GLOBAL_CANNOT_BE_INSTANTIATED);
			return;
		}
		boolean isInterface = Util.isInterface(clazz);
		if (newClass.def != null || isInterface) {
			if (isInterface || Util.hasAnnotationType(newClass.clazz.type.tsym, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
				print("{").println().startIndent();
				if (newClass.def != null) {
					for (JCTree m : newClass.def.getMembers()) {
						if (m instanceof JCBlock) {
							List<VarSymbol> initializedVars = new ArrayList<>();
							boolean statementPrinted = false;
							for (JCTree s : ((JCBlock) m).stats) {
								boolean currentStatementPrinted = false;
								if (s instanceof JCExpressionStatement && ((JCExpressionStatement) s).expr instanceof JCAssign) {
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
								} else if (s instanceof JCExpressionStatement && ((JCExpressionStatement) s).expr instanceof JCMethodInvocation) {
									JCMethodInvocation invocation = (JCMethodInvocation) ((JCExpressionStatement) s).expr;
									String meth = invocation.meth.toString();
									if (meth.equals(JSweetConfig.INDEXED_SET_FUCTION_NAME)
											|| meth.equals(JSweetConfig.UTIL_CLASSNAME + "." + JSweetConfig.INDEXED_SET_FUCTION_NAME)) {
										printIndent().print(invocation.args.head).print(": ").print(invocation.args.tail.head).print(",").println();
										currentStatementPrinted = true;
										statementPrinted = true;
									}
								}
								if (!currentStatementPrinted) {
									report(s, JSweetProblem.INVALID_INITIALIZER_STATEMENT);
								}
							}
							for (Symbol s : clazz.getEnclosedElements()) {
								if (s instanceof VarSymbol) {
									if (!initializedVars.contains(s)) {
										if (!Util.hasAnnotationType(s, JSweetConfig.ANNOTATION_OPTIONAL)) {
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
				println().endIndent().printIndent().print("}");
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
							if (s instanceof JCExpressionStatement && ((JCExpressionStatement) s).expr instanceof JCAssign) {
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
							} else if (s instanceof JCExpressionStatement && ((JCExpressionStatement) s).expr instanceof JCMethodInvocation) {
								JCMethodInvocation invocation = (JCMethodInvocation) ((JCExpressionStatement) s).expr;
								String meth = invocation.meth.toString();
								if (meth.equals(JSweetConfig.INDEXED_SET_FUCTION_NAME)
										|| meth.equals(JSweetConfig.UTIL_CLASSNAME + "." + JSweetConfig.INDEXED_SET_FUCTION_NAME)) {
									printIndent().print("target[").print(invocation.args.head).print("]").print(" = ").print(invocation.args.tail.head)
											.print(";").println();
									currentStatementPrinted = true;
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
			if (Util.hasAnnotationType(newClass.clazz.type.tsym, JSweetConfig.ANNOTATION_ERASED)) {
				if (newClass.args.length() != 1) {
					report(newClass, JSweetProblem.ERASED_CLASS_CONSTRUCTOR);
				}
				print("(").print(newClass.args.head).print(")");
			} else if (Util.hasAnnotationType(newClass.clazz.type.tsym, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
				print("{}");
			} else {
				if (!getAdapter().substituteNewClass(newClass)) {
					if (typeChecker.checkType(newClass, null, newClass.clazz)) {
						print("new ").print(newClass.clazz).print("(").printArgList(newClass.args).print(")");
					}
				}
			}
		}
	}

	@Override
	public void visitLiteral(JCLiteral literal) {
		String s = literal.toString();
		if (literal.typetag == TypeTag.FLOAT) {
			if (s.endsWith("F")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		print(s);
	}

	@Override
	public void visitIndexed(JCArrayAccess arrayAccess) {
		print(arrayAccess.indexed).print("[").print(arrayAccess.index).print("]");
	}

	@Override
	public void visitForeachLoop(JCEnhancedForLoop foreachLoop) {
		String itVarName = "__it_" + Util.getId();
		String exprVarName = "__expr_" + Util.getId();
		print("var " + exprVarName + " = ").print(foreachLoop.expr).print("; ");
		print("for(var " + itVarName + "=0;" + itVarName + "<" + exprVarName + ".length;" + itVarName + "++) { ")
				.print("var " + foreachLoop.var.name.toString() + "=" + exprVarName + "[" + itVarName + "];").println();
		startIndent().printIndent().print(foreachLoop.body);
		endIndent().print("}");
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
		print(binary.lhs);
		space().print(binary.operator.name.toString()).space();
		print(binary.rhs);
	}

	@Override
	public void visitIf(JCIf ifStatement) {
		print("if(").print(ifStatement.cond).print(") ");
		print(ifStatement.thenpart);
		if (ifStatement.elsepart != null) {
			print(" else ");
			print(ifStatement.elsepart);
		}
	}

	@Override
	public void visitReturn(JCReturn returnStatement) {
		print("return");
		if (returnStatement.expr != null) {
			print(" ").print(returnStatement.expr);
		}
	}

	@Override
	public void visitAssignop(JCAssignOp op) {
		print(op.lhs);
		print(op.operator.name.toString() + "=");
		print(op.rhs);
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
		print("for(").printArgList(forLoop.init).print("; ").print(forLoop.cond).print("; ").printArgList(forLoop.step).print(") ");
		print(forLoop.body);
	}

	@Override
	public void visitContinue(JCContinue continueStatement) {
		print("continue");
	}

	@Override
	public void visitBreak(JCBreak breakStatement) {
		if (breakStatement.label != null) {
			report(breakStatement, JSweetProblem.LABELS_ARE_NOT_SUPPORTED);
		}
		print("break");
	}

	@Override
	public void visitLabelled(JCLabeledStatement labelledStatement) {
		report(labelledStatement, JSweetProblem.LABELS_ARE_NOT_SUPPORTED);
		super.visitLabelled(labelledStatement);
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
		// if (newArray.elemtype != null) {
		// print("new ");
		// newArray.elemtype.accept(this);
		// }
		// if (newArray.dims.isEmpty()) {
		// print("[]");
		// } else {
		// for (JCExpression dim : newArray.dims) {
		// print("[");
		// dim.accept(this);
		// print("]");
		// }
		// }
		print("[");
		if (newArray.elems != null) {
			printArgList(newArray.elems);
		}
		print("]");
	}

	@Override
	public void visitUnary(JCUnary unary) {
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

	@Override
	public void visitCase(JCCase caseStatement) {
		if (caseStatement.pat != null) {
			print("case ");
			if (caseStatement.pat.type.isPrimitive() || String.class.getName().equals(caseStatement.pat.type.toString())) {
				print(caseStatement.pat);
			} else {
				print(getRootRelativeName(caseStatement.pat.type.tsym) + "." + caseStatement.pat);
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
		if (getAdapter().needsTypeCast(cast)) {
			print("<").getAdapter().substituteAndPrintType(cast.clazz).print(">");
		}
		print(cast.expr);
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
			print(assign.lhs).print(" = ").print(assign.rhs);
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
		if (tryStatement.catchers.size() > 1) {
			report(tryStatement, JSweetProblem.TRY_WITH_MULTIPLE_CATCHES);
		}
		print("try ").print(tryStatement.body);
		for (JCTree catcher : tryStatement.catchers) {
			print(catcher);
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
			while (i > 0 && getStack().get(i).getKind() != Kind.LAMBDA_EXPRESSION && getStack().get(i).getKind() != Kind.METHOD) {
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
			print(") => { return ");
		}
		this.skipTypeAnnotations = true;
		print("(").printArgList(lamba.params).print(") => ");
		this.skipTypeAnnotations = false;
		print(lamba.body);

		if (!finalVars.isEmpty()) {
			print("})(");
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
			print("(");
			if (method.params != null) {
				for (VarSymbol var : method.params) {
					print(var.name.toString());
					print(",");
				}
				if (!method.params.isEmpty()) {
					removeLastChar();
				}
			}
			print(")");
			print(" => { return ");
		}

		if (memberReference.expr.type.toString().endsWith(JSweetConfig.GLOBALS_CLASS_NAME)) {
			print(memberReference.name.toString());
		} else {
			print(memberReference.expr).print(".").print(memberReference.name.toString());
		}

		if (memberReference.sym instanceof MethodSymbol) {
			MethodSymbol method = (MethodSymbol) memberReference.sym;

			print("(");
			if (method.params != null) {
				for (VarSymbol var : method.params) {
					print(var.name.toString());
					print(",");
				}
				if (!method.params.isEmpty()) {
					removeLastChar();
				}
			}
			print(")");
			print(" }");
		}

	}

	@Override
	public void visitTypeParameter(JCTypeParameter typeParameter) {
		print(typeParameter.name.toString());
		if (typeParameter.bounds != null && !typeParameter.bounds.isEmpty()) {
			print(" extends ");
			for (JCExpression e : typeParameter.bounds) {
				getAdapter().substituteAndPrintType(e).print(",");
			}
			removeLastChar();
		}
	}

	@Override
	public void visitSynchronized(JCSynchronized sync) {
		report(sync, JSweetProblem.SYNCHRONIZATION);
		if (sync.body != null) {
			print(sync.body);
		}
	}

	@Override
	public void visitTypeTest(JCInstanceOf instanceOf) {
		if (Util.isInterface(instanceOf.clazz.type.tsym)) {
			report(instanceOf, JSweetProblem.INVALID_INSTANCEOF_INTERFACE);
		}
		print(instanceOf.expr).print(" instanceof ").print(instanceOf.clazz);
	}

	@Override
	public void visitThrow(JCThrow throwStatement) {
		print("throw ").print(throwStatement.expr);
	}

}
