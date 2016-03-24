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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ArrayType;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.parser.Tokens.Comment;
import com.sun.tools.javac.tree.JCTree;
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

/**
 * This is a TypeScript printer for translating the Java AST to a TypeScript
 * program.
 * 
 * @author Renaud Pawlak
 */
public class Java2TypeScriptTranslator extends AbstractTreePrinter {

	protected static Logger logger = Logger.getLogger(Java2TypeScriptTranslator.class);

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

	private static Map<String, String> instanceOfTypeMapping = new HashMap<String, String>();

	static {
		instanceOfTypeMapping.put("java.lang.String", "String");
		instanceOfTypeMapping.put("java.lang.Number", "Number");
		instanceOfTypeMapping.put("java.lang.Integer", "Number");
		instanceOfTypeMapping.put("java.lang.Float", "Number");
		instanceOfTypeMapping.put("java.lang.Double", "Number");
		instanceOfTypeMapping.put("java.lang.Short", "Number");
		instanceOfTypeMapping.put("java.lang.Character", "String");
		instanceOfTypeMapping.put("java.lang.Byte", "Number");
		instanceOfTypeMapping.put("java.lang.Boolean", "Boolean");
		instanceOfTypeMapping.put("int", "Number");
		instanceOfTypeMapping.put("float", "Number");
		instanceOfTypeMapping.put("double", "Number");
		instanceOfTypeMapping.put("short", "Number");
		instanceOfTypeMapping.put("char", "String");
		instanceOfTypeMapping.put("boolean", "Boolean");
		instanceOfTypeMapping.put("byte", "Number");
	}

	private JCMethodDecl mainMethod;

	private List<JCImport> imports = new ArrayList<JCImport>();

	private boolean globalModule = false;

	private PackageSymbol topLevelPackage;

	private boolean defaultMethodScope = false;

	private boolean innerClassScope = false;

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

	private boolean checkRootPackageParent(JCCompilationUnit topLevel, PackageSymbol rootPackage, PackageSymbol parentPackage) {
		if (parentPackage == null) {
			return true;
		}
		if (Util.hasAnnotationType(parentPackage, JSweetConfig.ANNOTATION_ROOT)) {
			report(topLevel.getPackageName(), JSweetProblem.ENCLOSED_ROOT_PACKAGES, rootPackage.getQualifiedName().toString(),
					parentPackage.getQualifiedName().toString());
			return false;
		}
		for (Symbol s : parentPackage.getEnclosedElements()) {
			if (!(s instanceof PackageSymbol)) {
				report(topLevel.getPackageName(), JSweetProblem.CLASS_OUT_OF_ROOT_PACKAGE_SCOPE, s.getQualifiedName().toString(),
						rootPackage.getQualifiedName().toString());
				return false;
			}
		}
		return checkRootPackageParent(topLevel, rootPackage, (PackageSymbol) parentPackage.owner);
	}

	@Override
	public void visitTopLevel(JCCompilationUnit topLevel) {
		printIndent().print("\"Generated from Java with JSweet " + JSweetConfig.getVersionNumber() + " - http://www.jsweet.org\";").println();
		PackageSymbol rootPackage = Util.getFirstEnclosingRootPackage(topLevel.packge);
		if (rootPackage != null) {
			if (!checkRootPackageParent(topLevel, rootPackage, (PackageSymbol) rootPackage.owner)) {
				return;
			}
		}
		context.rootPackages.add(rootPackage);
		if (context.useModules && context.rootPackages.size() > 1) {
			if (!context.reportedMultipleRootPackages) {
				report(topLevel.getPackageName(), JSweetProblem.MULTIPLE_ROOT_PACKAGES_NOT_ALLOWED_WITH_MODULES, context.rootPackages.toString());
				context.reportedMultipleRootPackages = true;
			}
			return;
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
			File[] sourceFiles = SourceFile.toFiles(context.sourceFiles);
			for (File file : parent.listFiles()) {
				if (file.isDirectory() && !file.getName().startsWith(".")) {
					if (Util.containsFile(file, sourceFiles)) {
						Set<String> importedNames = context.getImportedNames(compilationUnit.packge);
						if (!importedNames.contains(file.getName())) {
							logger.debug(topLevel.getSourceFile().getName() + " export import: " + file);
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
						if (qualified.sym.getEnclosingElement() instanceof PackageSymbol) {
							useModule((PackageSymbol) qualified.sym.getEnclosingElement(), importDecl, targetRootPackageName,
									moduleFile.getPath().replace('\\', '/'));
						}
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
			printIndent().print("namespace ").print(rootRelativePackageName).print(" {").startIndent().println();
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
			removeLastChar().endIndent().printIndent().print("}");
		}

		if (footer.length() > 0) {
			println().print(footer.toString());
		}
		globalModule = false;
	}

	private boolean interfaceScope = false;

	private boolean sharedMode = false;

	private boolean enumScope = false;

	private boolean removedSuperclass = false;

	private boolean declareClassScope;

	private void printDocComment(JCTree element, boolean indent) {
		if (compilationUnit.docComments != null && compilationUnit.docComments.hasComment(element)) {
			Comment comment = compilationUnit.docComments.getComment(element);
			String[] lines = comment.getText().split("\n");
			if (isPreserveLineNumbers()) {
				print("/**").print("\n");
				for (String line : lines) {
					print(" * ").print(line.trim()).print("\n");
				}
				print(" ").print("*/\n");
			} else {
				if (indent) {
					printIndent();
				}
				print("/**").println();
				for (String line : lines) {
					printIndent().print(" * ").print(line.trim()).print("\n");
				}
				removeLastChar();
				println().printIndent().print(" ").print("*/\n");
				if (!indent) {
					printIndent();
				}
			}
		}
	}

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		if (Util.hasAnnotationType(classdecl.type.tsym, JSweetConfig.ANNOTATION_OBJECT_TYPE)) {
			// object types are ignored
			return;
		}
		if (Util.hasAnnotationType(classdecl.type.tsym, JSweetConfig.ANNOTATION_ERASED)) {
			// erased types are ignored
			return;
		}
		if (getParent() instanceof JCClassDecl && !innerClassScope) {
			// if (!(classdecl.sym.isInterface() ||
			// classdecl.getModifiers().getFlags().contains(Modifier.STATIC))) {
			// report(classdecl, JSweetProblem.INNER_CLASS, classdecl.name);
			// }
			return;
		}

		declareClassScope = Util.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_AMBIENT);
		interfaceScope = false;
		removedSuperclass = false;
		enumScope = false;
		HashSet<Entry<JCClassDecl, JCMethodDecl>> defaultMethods = null;
		boolean globals = JSweetConfig.GLOBALS_CLASS_NAME.equals(classdecl.name.toString());
		if (globals && classdecl.extending != null) {
			report(classdecl, JSweetProblem.GLOBALS_CLASS_CANNOT_HAVE_SUPERCLASS);
		}
		if (!globals) {
			if (classdecl.extending != null && JSweetConfig.GLOBALS_CLASS_NAME.equals(classdecl.extending.type.tsym.getSimpleName().toString())) {
				report(classdecl, JSweetProblem.GLOBALS_CLASS_CANNOT_BE_SUBCLASSED);
				return;
			}
			printDocComment(classdecl, false);
			if (!globalModule || context.useModules) {
				print("export ");
			}
			if (Util.isInterface(classdecl.sym)) {
				print("interface ");
				interfaceScope = true;
				Object o = Util.getAnnotationValue(classdecl.sym, JSweetConfig.ANNOTATION_INTERFACE, null);
				sharedMode = ("" + o).endsWith("SHARED");
			} else {
				if (classdecl.getKind() == Kind.ENUM) {
					enumScope = true;
					print("enum ");
				} else {
					if (declareClassScope) {
						print("declare ");
					}
					defaultMethods = new HashSet<>();
					Util.findDefaultMethodsInType(defaultMethods, context, classdecl.sym);
					print("class ");
				}
			}
			print(classdecl.name.toString());
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

		if (defaultMethods != null && !defaultMethods.isEmpty()) {
			defaultMethodScope = true;
			for (Entry<JCClassDecl, JCMethodDecl> entry : defaultMethods) {
				stack.push(entry.getKey());
				printIndent().print(entry.getValue()).println();
				stack.pop();
			}
			defaultMethodScope = false;
		}

		if (enumScope) {
			printIndent();
		}
		if (globals) {
			removeLastIndent();
		}

		for (JCTree def : classdecl.defs) {
			if (!sharedMode && interfaceScope && ((def instanceof JCMethodDecl && ((JCMethodDecl) def).sym.isStatic())
					|| (def instanceof JCVariableDecl && ((JCVariableDecl) def).sym.isStatic()))) {
				// static interface members will be printed at the end in a
				// namespace
				continue;
			}
			if (def instanceof JCVariableDecl) {
				if (enumScope && ((JCVariableDecl) def).type.tsym != classdecl.type.tsym) {
					report(def, ((JCVariableDecl) def).name, JSweetProblem.INVALID_FIELD_IN_ENUM);
					continue;
				}
				if (!getAdapter().needsVariableDecl((JCVariableDecl) def, VariableKind.FIELD)) {
					continue;
				}
			}
			if (!enumScope) {
				printIndent();
			}
			int pos = getCurrentPosition();
			print(def);
			if (getCurrentPosition() == pos) {
				if (!enumScope) {
					removeLastIndent();
				}
				continue;
			}
			if (def instanceof JCVariableDecl) {
				if (enumScope) {
					print(", ");
				} else {
					print(";").println().println();
				}
			} else {
				println().println();
			}
		}

		removeLastChar();

		if (enumScope) {
			removeLastChar().println();
		}

		if (!enumScope) {
			if (!Util.isInterface(classdecl.sym)) {
				if (printInterfacesField(classdecl.sym, " = ", "")) {
					print(";").println();
				}
			}
		}

		if (!globals) {
			endIndent().printIndent().print("}");
		}

		// print valid inner classes
		boolean nameSpace = false;
		for (JCTree def : classdecl.defs) {
			if (def instanceof JCClassDecl) {
				JCClassDecl cdef = (JCClassDecl) def;
				if (!nameSpace) {
					nameSpace = true;
					println().printIndent().print("export namespace ").print(classdecl.getSimpleName().toString()).print(" {").startIndent();
				}
				innerClassScope = true;
				println().printIndent().print(cdef);
				innerClassScope = false;
			}
		}
		if (nameSpace) {
			println().endIndent().printIndent().print("}").println();
		}

		if (!sharedMode && interfaceScope) {
			// print static members of interfaces
			nameSpace = false;
			for (JCTree def : classdecl.defs) {
				if ((def instanceof JCMethodDecl && ((JCMethodDecl) def).sym.isStatic())
						|| (def instanceof JCVariableDecl && ((JCVariableDecl) def).sym.isStatic())) {
					if (!nameSpace) {
						nameSpace = true;
						println().printIndent().print("export namespace ").print(classdecl.getSimpleName().toString()).print(" {").startIndent();
					}
					// innerClassScope = true;
					println().printIndent().print(def);
					// innerClassScope = false;
				}
			}
			if (nameSpace) {
				println().endIndent().printIndent().print("}").println();
			}
		}

		if (mainMethod != null && mainMethod.getParameters().size() < 2 && mainMethod.sym.getEnclosingElement().equals(classdecl.sym)) {
			String mainClassName = getQualifiedTypeName(classdecl.sym, globals);
			String mainMethodQualifier = mainClassName;
			if (!isBlank(mainClassName)) {
				mainMethodQualifier = mainClassName + ".";
			}
			context.entryFiles.add(new File(compilationUnit.sourcefile.getName()));
			context.addFooterStatement(
					mainMethodQualifier + JSweetConfig.MAIN_FUNCTION_NAME + "(" + (mainMethod.getParameters().isEmpty() ? "" : "null") + ");");
		}
		interfaceScope = false;
		sharedMode = false;
	}

	private String getQualifiedTypeName(TypeSymbol type, boolean globals) {
		String qualifiedName = getRootRelativeName(type);
		if (context.useModules) {
			int dotIndex = qualifiedName.lastIndexOf(".");
			qualifiedName = qualifiedName.substring(dotIndex + 1);
		}
		if (globals) {
			int dotIndex = qualifiedName.lastIndexOf(".");
			if (dotIndex == -1) {
				qualifiedName = "";
			} else {
				qualifiedName = qualifiedName.substring(0, dotIndex);
			}
		}
		return qualifiedName;
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
		if (Util.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_ERASED)) {
			// erased elements are ignored
			return;
		}
		JCClassDecl parent = (JCClassDecl) getParent();

		if (JSweetConfig.INDEXED_GET_FUCTION_NAME.equals(methodDecl.getName().toString()) && methodDecl.getParameters().size() == 1) {
			print("[").print(methodDecl.getParameters().head).print("]: ");
			getAdapter().substituteAndPrintType(methodDecl.restype).print(";");
			return;
		}

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
		boolean inCoreWrongOverload = false;
		if (inOverload) {
			if (!overload.isValid) {
				if (overload.coreMethod.equals(methodDecl)) {
					inCoreWrongOverload = true;
				} else {
					if (methodDecl.sym.isConstructor()) {
						return;
					}
				}
			} else {
				if (!overload.coreMethod.equals(methodDecl)) {
					return;
				}
			}
		}

		boolean ambient = Util.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_AMBIENT);
		int jsniLine = -1;
		String[] content = null;

		if (methodDecl.mods.getFlags().contains(Modifier.NATIVE)) {
			if (!declareClassScope && !ambient && !interfaceScope) {
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
			if (declareClassScope && !constructor && !interfaceScope && !methodDecl.mods.getFlags().contains(Modifier.DEFAULT)) {
				report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name, parent.name);
			}
		}

		if (methodDecl.name.toString().equals("constructor")) {
			report(methodDecl, methodDecl.name, JSweetProblem.CONSTRUCTOR_MEMBER);
		}
		VarSymbol v = Util.findFieldDeclaration(parent.sym, methodDecl.name);
		if (v != null && context.getFieldNameMapping(v) == null) {
			report(methodDecl, methodDecl.name, JSweetProblem.METHOD_CONFLICTS_FIELD, methodDecl.name, v.owner);
		}
		if (JSweetConfig.MAIN_FUNCTION_NAME.equals(methodDecl.name.toString()) && methodDecl.mods.getFlags().contains(Modifier.STATIC)
				&& !Util.hasAnnotationType(methodDecl.sym, JSweetConfig.ANNOTATION_DISABLED)) {
			// ignore main methods in inner classes
			if (!innerClassScope) {
				mainMethod = methodDecl;
			}
		}
		if (methodDecl.pos == parent.pos) {
			return;
		}

		boolean globals = JSweetConfig.GLOBALS_CLASS_NAME.equals(parent.name.toString());
		if (!sharedMode) {
			globals = globals || (interfaceScope && methodDecl.mods.getFlags().contains(Modifier.STATIC));
		}
		printDocComment(methodDecl, false);
		if (globals) {
			if (constructor) {
				report(methodDecl, methodDecl.name, JSweetProblem.GLOBAL_CONSTRUCTOR_DEF);
				return;
			}

			if (!methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
				report(methodDecl, methodDecl.name, JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
				return;
			}

			if (context.useModules) {
				if (!methodDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
					print("export ");
				}
			} else {
				if (!globalModule) {
					print("export ");
				}
			}
			if (ambient) {
				print("declare ");
			}
			print("function ");
		} else {
			if (methodDecl.mods.getFlags().contains(Modifier.PUBLIC)) {
				if (!interfaceScope) {
					print("public ");
				}
			}
			if (methodDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
				if (!constructor) {
					if (!interfaceScope) {
						print("private ");
					} else {
						if (sharedMode) {
							print("public ");
						} else {
							report(methodDecl, methodDecl.name, JSweetProblem.INVALID_PRIVATE_IN_INTERFACE, methodDecl.name, parent.name);
						}
					}
				}
			}
			if (methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
				if (!interfaceScope) {
					print("static ");
				} else {
					if (sharedMode) {
						return;
					}
				}
			}
			if (ambient) {
				report(methodDecl, methodDecl.name, JSweetProblem.WRONG_USE_OF_AMBIENT, methodDecl.name);
			}
		}
		if (!Util.hasAnnotationType(parent.sym, FunctionalInterface.class.getName())) {
			if (inOverload && !overload.isValid && !inCoreWrongOverload) {
				print(getOverloadMethodName(methodDecl));
			} else {
				printIdentifier(getTSMethodName(methodDecl));
			}
		}
		if (methodDecl.typarams != null && !methodDecl.typarams.isEmpty()) {
			print("<").printArgList(methodDecl.typarams).print(">");
		}
		print("(");
		if (inCoreWrongOverload) {
			for (JCVariableDecl param : methodDecl.params) {
				printIdentifier(param.name.toString()).print("?").print(" : ").print("any");
				print(", ");
			}
		} else {
			int i = 0;
			for (JCVariableDecl param : methodDecl.params) {
				print(param);
				if (inOverload && overload.isValid && overload.defaultValues[i] != null) {
					print(" = ").print(overload.defaultValues[i]);
				}
				print(", ");
				i++;
			}
		}
		if (!methodDecl.params.isEmpty()) {
			removeLastChars(2);
		}
		print(")");
		if (methodDecl.restype != null && methodDecl.restype.type.getTag() != TypeTag.VOID) {
			print(" : ");
			getAdapter().substituteAndPrintType(methodDecl.restype);
		} else if (inCoreWrongOverload && !methodDecl.sym.isConstructor()) {
			print(" : any");
		}
		if (methodDecl.getBody() == null || (methodDecl.mods.getFlags().contains(Modifier.DEFAULT) && !defaultMethodScope)) {
			if (jsniLine != -1) {
				int line = jsniLine;
				print(" {").println().startIndent();
				String jsCode = content[line].substring(content[line].indexOf("/*-{") + 4).trim();
				if (!StringUtils.isEmpty(jsCode)) {
					printIndent().print(jsCode).println();
				}
				line++;
				while (!content[line].contains("}-*/")) {
					jsCode = content[line++].trim();
					printIndent().print(jsCode).println();
				}
				jsCode = content[line].substring(0, content[line].indexOf("}-*/")).trim();
				if (!StringUtils.isEmpty(jsCode)) {
					printIndent().print(jsCode).println();
				}
				endIndent().printIndent().print("}");
			} else {
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
			}
		} else {
			boolean hasStatements = methodDecl.getBody().getStatements().isEmpty();
			if (interfaceScope) {
				if (sharedMode) {
					print(";");
					return;
				} else {
					if (!methodDecl.mods.getFlags().contains(Modifier.STATIC)) {
						report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name, parent.name);
					}
				}
			}
			if (declareClassScope) {
				if (!constructor || hasStatements) {
					report(methodDecl, methodDecl.name, JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, methodDecl.name, parent.name);
				}
				print(";");
			} else {
				if (inCoreWrongOverload) {
					print(" {").println().startIndent().printIndent();
					for (int i = 0; i < overload.methods.size(); i++) {
						JCMethodDecl method = overload.methods.get(i);
						if (i > 0) {
							print(" else ");
						}
						print("if(");
						printMethodParamsTest(methodDecl, method);
						print(") ");
						if (i == 0 || method.sym.isConstructor()) {
							printInlinedConstructorBody(overload, method, methodDecl.params);
						} else {
							print("{").println().startIndent().printIndent();
							if (!method.sym.isConstructor()) {
								print("return ");
							}
							if (method.sym.isStatic()) {
								print(getQualifiedTypeName(parent.sym, false).toString());
							} else {
								print("this");
							}
							print(".").print(getOverloadMethodName(method)).print("(");
							for (int j = 0; j < method.params.size(); j++) {
								print(overload.coreMethod.params.get(j).name.toString()).print(", ");
							}
							if (!method.params.isEmpty()) {
								removeLastChars(2);
							}
							print(");");
							println().endIndent().printIndent().print("}");
						}
					}
					print(" else throw new Error('invalid overload');");
					endIndent().println().printIndent().print("}");
				} else {
					print(" ").print(methodDecl.getBody());
				}
			}
		}
	}

	private void printInlinedConstructorBody(Overload overload, JCMethodDecl method, List<? extends JCTree> args) {
		print("{").println().startIndent();
		for (int j = 0; j < method.params.size(); j++) {
			if (args.get(j) instanceof JCVariableDecl) {
				if (method.params.get(j).name.equals(((JCVariableDecl) args.get(j)).name)) {
					continue;
				} else {
					printIndent().print("var ").printIdentifier(method.params.get(j).name.toString()).print(" : ").print("any").print(" = ")
							.printIdentifier(((JCVariableDecl) args.get(j)).name.toString()).print(";").println();
				}
			} else {
				if (method.params.get(j).name.toString().equals(args.get(j).toString())) {
					continue;
				} else {
					printIndent().print("var ").printIdentifier(method.params.get(j).name.toString()).print(" : ").print("any").print(" = ").print(args.get(j))
							.print(";").println();
				}
			}
		}
		boolean skipFirst = false;
		if (!method.getBody().stats.isEmpty() && method.getBody().stats.get(0).toString().startsWith("this(")) {
			skipFirst = true;
			JCMethodInvocation inv = (JCMethodInvocation) ((JCExpressionStatement) method.getBody().stats.get(0)).expr;
			MethodSymbol ms = Util.findMethodDeclarationInType(context.types, (TypeSymbol) overload.coreMethod.sym.getEnclosingElement(), inv);
			for (JCMethodDecl md : overload.methods) {
				if (md.sym.equals(ms)) {
					printIndent();
					printInlinedConstructorBody(overload, md, inv.args);
					println();
				}
			}

		}
		printBlockStatements(skipFirst ? method.getBody().stats.tail : method.getBody().stats);
		endIndent().printIndent().print("}");
	}

	private void printBlockStatements(List<JCStatement> statements) {
		for (JCStatement statement : statements) {
			printIndent();
			int pos = getCurrentPosition();
			print(statement);
			if (getCurrentPosition() == pos) {
				removeLastIndent();
				continue;
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
	}

	private String getOverloadMethodName(JCMethodDecl method) {
		if (method.sym.isConstructor()) {
			return "constructor";
		}
		StringBuilder sb = new StringBuilder(method.getName().toString());
		sb.append("$");
		for (JCVariableDecl p : method.params) {
			sb.append(p.type.tsym.getSimpleName());
			sb.append("_");
		}
		if (!method.params.isEmpty()) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	private void printMethodParamsTest(JCMethodDecl coreMethod, JCMethodDecl m) {
		int i = 0;
		for (; i < m.params.size(); i++) {
			printInstanceOf(coreMethod.params.get(i).name.toString(), null, m.params.get(i).type);
			print(" && ");
		}
		for (; i < coreMethod.params.size(); i++) {
			print(coreMethod.params.get(i).name.toString()).print(" == null");
			print(" && ");
		}
		removeLastChars(4);
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
				if (sharedMode) {
					return;
				} else {
					report(block, JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, ((JCClassDecl) parent).name);
				}
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

		printBlockStatements(block.stats);

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
		if (Util.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_ERASED)) {
			// erased elements are ignored
			return;
		}
		if (Util.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_STRING_TYPE)) {
			// string type fields are ignored
			return;
		}

		if (getParent().getKind() == Kind.ENUM) {
			print(varDecl.name.toString());
		} else {
			JCTree parent = getParent();
			String name = varDecl.name.toString();
			if (context.getFieldNameMapping(varDecl.sym) != null) {
				name = context.getFieldNameMapping(varDecl.sym);
			}

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
					name = JSweetConfig.JS_KEYWORD_PREFIX + name;
				}
			}

			boolean globals = (parent instanceof JCClassDecl) && JSweetConfig.GLOBALS_CLASS_NAME.equals(((JCClassDecl) parent).name.toString());

			if (globals && !varDecl.mods.getFlags().contains(Modifier.STATIC)) {
				report(varDecl, varDecl.name, JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
				return;
			}

			if (!sharedMode) {
				globals = globals || (parent instanceof JCClassDecl && (((JCClassDecl) parent).sym.isInterface() || interfaceScope && varDecl.sym.isStatic()));
			}

			printDocComment(varDecl, false);
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
						if (!sharedMode) {
							report(varDecl, varDecl.name, JSweetProblem.INVALID_PRIVATE_IN_INTERFACE, varDecl.name, ((JCClassDecl) parent).name);
						}
					}
				}
				if (varDecl.mods.getFlags().contains(Modifier.STATIC)) {
					if (!interfaceScope) {
						print("static ");
					} else {
						if (sharedMode) {
							return;
						}
					}
				}
			}
			if (!interfaceScope && parent instanceof JCClassDecl) {
				if (Util.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_OPTIONAL)) {
					report(varDecl, varDecl.name, JSweetProblem.USELESS_OPTIONAL_ANNOTATION, varDecl.name, ((JCClassDecl) parent).name);
				}
			}
			boolean ambient = Util.hasAnnotationType(varDecl.sym, JSweetConfig.ANNOTATION_AMBIENT);
			if (globals || !(parent instanceof JCClassDecl || parent instanceof JCMethodDecl || parent instanceof JCLambda)) {
				if (globals) {
					if (context.useModules) {
						if (!varDecl.mods.getFlags().contains(Modifier.PRIVATE)) {
							print("export ");
						}
					} else {
						if (!globalModule) {
							print("export ");
						}
					}
					if (ambient) {
						print("declare ");
					}
				}
				print("var ");
			} else {
				if (ambient) {
					report(varDecl, varDecl.name, JSweetProblem.WRONG_USE_OF_AMBIENT, varDecl.name);
				}
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
					print(" : ");
					getAdapter().substituteAndPrintType(varDecl.vartype);
				}
			}
			if (varDecl.init != null) {
				if (!globals && parent instanceof JCClassDecl && interfaceScope) {
					if (!sharedMode) {
						report(varDecl, varDecl.name, JSweetProblem.INVALID_FIELD_INITIALIZER_IN_INTERFACE, varDecl.name, ((JCClassDecl) parent).name);
					}
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
				// if (Util.isIntegral(fieldAccess.type)) {
				// print("(");
				// }
				if (fieldAccess.sym instanceof VarSymbol && context.getFieldNameMapping(fieldAccess.sym) != null) {
					print(fieldAccess.selected).print(".").print(context.getFieldNameMapping(fieldAccess.sym));
				} else {
					print(fieldAccess.selected).print(".").printIdentifier(fieldAccess.name.toString());
				}
				// if (Util.isIntegral(fieldAccess.type)) {
				// print("|0)");
				// }
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
			boolean targetIsThisOrStaticImported = meth.startsWith(methName) || meth.startsWith("this." + methName);

			MethodType type = (MethodType) inv.meth.type;
			MethodSymbol methSym = null;
			String methodName = null;
			boolean keywordHandled = false;
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
							if (meth.startsWith("this.") && methSym.getModifiers().contains(Modifier.STATIC)) {
								report(inv, JSweetProblem.CANNOT_ACCESS_STATIC_MEMBER_ON_THIS, methSym.getSimpleName());
							}
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
					if (methSym != null) {
						Map<String, VarSymbol> vars = new HashMap<>();
						Util.fillAllVariablesInScope(vars, getStack(), inv, getParent(JCMethodDecl.class));
						if (vars.containsKey(methSym.getSimpleName().toString())) {
							report(inv, JSweetProblem.HIDDEN_INVOCATION, methSym.getSimpleName());
						}
					}
					// staticImported = true;
					if (JSweetConfig.TS_STRICT_MODE_KEYWORDS.contains(methName.toLowerCase())) {
						// if method is a reserved TS keyword, no "static
						// import" possible, fully qualified mode
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
					if (Util.hasAnnotationType(selected.type.tsym, FunctionalInterface.class.getName())) {
						anonymous = true;
					}
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
					print(").");
					if (keywordHandled) {
						print(((JCFieldAccess) inv.meth).name.toString());
					} else {
						if (methSym == null) {
							methSym = (MethodSymbol) ((JCFieldAccess) inv.meth).sym;
						}
						if (methSym != null) {
							print(Util.getActualName(methSym));
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
						if (methSym == null && inv.meth instanceof JCFieldAccess) {
							methSym = (MethodSymbol) ((JCFieldAccess) inv.meth).sym;
						}
						if (methSym != null && inv.meth instanceof JCFieldAccess) {
							JCExpression selected = ((JCFieldAccess) inv.meth).selected;
							print(selected).print(".");
						}
						if (methSym != null) {
							print(Util.getActualName(methSym));
						} else {
							print(inv.meth);
						}
					}
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
		// if (Util.isIntegral(ident.type) && getParent() instanceof JCBinary) {
		// print("(");
		// }
		String name = ident.toString();
		if (!getAdapter().substituteIdentifier(ident)) {
			// add this of class name if ident is a field
			if (ident.sym instanceof VarSymbol && !ident.sym.name.equals(context.names._this) && !ident.sym.name.equals(context.names._super)) {
				VarSymbol varSym = (VarSymbol) ident.sym; // findFieldDeclaration(currentClass,
				// ident.name);
				if (varSym != null) {
					if (varSym.owner instanceof ClassSymbol) {
						if (context.getFieldNameMapping(varSym) != null) {
							name = context.getFieldNameMapping(varSym);
						}
						if (!varSym.getModifiers().contains(Modifier.STATIC)) {
							print("this.");
						} else {
							print(varSym.owner.getSimpleName() + ".");
						}
					} else {
						if (JSweetConfig.JS_KEYWORDS.contains(name)) {
							name = JSweetConfig.JS_KEYWORD_PREFIX + name;
						}
					}
				}
			}
			printIdentifier(name);
		}
		// if (Util.isIntegral(ident.type) && getParent() instanceof JCBinary) {
		// print("|0)");
		// }
	}

	@Override
	public void visitTypeApply(JCTypeApply typeApply) {
		getAdapter().substituteAndPrintType(typeApply);
	}

	private boolean printInterfacesField(TypeSymbol type, String separator, String prefix) {
		Set<String> interfaces = new HashSet<>();
		Util.grabSupportedInterfaceNames(interfaces, type);
		if (!interfaces.isEmpty()) {
			print(prefix);
			println().printIndent().print("__interfaces");
			print(separator);
			print("[");
			for (String i : interfaces) {
				print("\"").print(i).print("\",");
			}
			removeLastChar();
			print("]");
			return true;
		}
		return false;
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
				if (isInterface) {
					print("<").print(newClass.clazz).print(">").print("<any>");
				}
				print("{").println().startIndent();
				boolean statementPrinted = false;
				if (newClass.def != null) {
					for (JCTree m : newClass.def.getMembers()) {
						if (m instanceof JCBlock) {
							List<VarSymbol> initializedVars = new ArrayList<>();
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
										if (invocation.getArguments().size() == 3) {
											if ("this".equals(invocation.getArguments().get(0).toString())) {
												printIndent().print(invocation.args.tail.head).print(": ").print(invocation.args.tail.tail.head).print(",")
														.println();
											}
											currentStatementPrinted = true;
											statementPrinted = true;
										} else {
											printIndent().print(invocation.args.head).print(": ").print(invocation.args.tail.head).print(",").println();
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

				printInterfacesField(clazz, " : ", statementPrinted ? "," : "");

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
									if (invocation.getArguments().size() == 3) {
										if ("this".equals(invocation.getArguments().get(0).toString())) {
											printIndent().print("target[").print(invocation.args.tail.head).print("]").print(" = ")
													.print(invocation.args.tail.tail.head).print(";").println();
										}
										currentStatementPrinted = true;
									} else {
										printIndent().print("target[").print(invocation.args.head).print("]").print(" = ").print(invocation.args.tail.head)
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
		// if (Util.isIntegral(arrayAccess.type)) {
		// print("(");
		// }
		print(arrayAccess.indexed).print("[").print(arrayAccess.index).print("]");
		// if (Util.isIntegral(arrayAccess.type)) {
		// print("|0)");
		// }
	}

	@Override
	public void visitForeachLoop(JCEnhancedForLoop foreachLoop) {
		String indexVarName = "index" + Util.getId();
		boolean noVariable = foreachLoop.expr instanceof JCIdent || foreachLoop.expr instanceof JCFieldAccess;
		if (noVariable) {
			print("for(var " + indexVarName + "=0; " + indexVarName + " < ").print(foreachLoop.expr).print(".length; " + indexVarName + "++) {").println()
					.startIndent().printIndent();
			print("var " + foreachLoop.var.name.toString() + " = ").print(foreachLoop.expr).print("[" + indexVarName + "];").println();
		} else {
			String arrayVarName = "array" + Util.getId();
			print("{").println().startIndent().printIndent();
			print("var " + arrayVarName + " = ").print(foreachLoop.expr).print(";").println().printIndent();
			print("for(var " + indexVarName + "=0; " + indexVarName + " < " + arrayVarName + ".length; " + indexVarName + "++) {").println().startIndent()
					.printIndent();
			print("var " + foreachLoop.var.name.toString() + " = " + arrayVarName + "[" + indexVarName + "];").println();
		}
		printIndent().print(foreachLoop.body);
		endIndent().println().printIndent().print("}");
		if (!noVariable) {
			endIndent().println().printIndent().print("}");
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
			print("(");
		}
		print(binary.lhs);
		space().print(binary.operator.name.toString()).space();
		print(binary.rhs);
		if (Util.isIntegral(binary.type) && binary.getKind() == Kind.DIVIDE) {
			print("|0)");
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
				print("new Array(").print(newArray.dims.head).print(")");
			} else {
				print("<any> (function(dims) { var allocate = function(dims) { if(dims.length==0) { return undefined; } else { var array = []; for(var i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})");
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
		if (Util.isIntegral(cast.type)) {
			print("(");
		}
		if (getAdapter().needsTypeCast(cast)) {
			print("<").getAdapter().substituteAndPrintType(cast.clazz).print(">");
		}
		print(cast.expr);
		if (Util.isIntegral(cast.type)) {
			print("|0)");
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
			print(") => {").println().startIndent().printIndent().print("return ");
		}
		this.skipTypeAnnotations = true;
		print("(").printArgList(lamba.params).print(") => ");
		this.skipTypeAnnotations = false;
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

	private void printInstanceOf(String exprStr, JCTree expr, Type type) {
		if (!(getParent() instanceof JCParens)) {
			print("(");
		}
		if (instanceOfTypeMapping.containsKey(type.toString())) {
			print("typeof ");
			if (exprStr == null) {
				print(expr);
			} else {
				print(exprStr);
			}
			print(" === ").print("'" + instanceOfTypeMapping.get(type.toString()).toLowerCase() + "'");
		} else {
			if (exprStr == null) {
				print(expr);
			} else {
				print(exprStr);
			}
			if (Util.isInterface(type.tsym)) {
				print(" != null && ");
				if (exprStr == null) {
					print(expr);
				} else {
					print(exprStr);
				}
				print("[\"__interfaces\"]").print(" != null && ");
				if (exprStr == null) {
					print(expr);
				} else {
					print(exprStr);
				}
				print("[\"__interfaces\"].indexOf(\"").print(type.tsym.getQualifiedName().toString()).print("\") >= 0");
			} else {
				print(" instanceof ").print(getQualifiedTypeName(type.tsym, false));
			}
		}
		if (!(getParent() instanceof JCParens)) {
			print(")");
		}
	}

	@Override
	public void visitTypeTest(JCInstanceOf instanceOf) {
		// if (Util.isInterface(instanceOf.clazz.type.tsym)) {
		// report(instanceOf, JSweetProblem.INVALID_INSTANCEOF_INTERFACE);
		// }
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
			print("if(!(").print(assertion.cond).print(")) throw new Error(\"Assertion error line " + getCurrentLine() + ": " + assertCode + "\");");
		}
	}

}
