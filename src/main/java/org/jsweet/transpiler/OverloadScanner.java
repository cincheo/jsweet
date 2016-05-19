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
package org.jsweet.transpiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCStatement;

/**
 * This AST scanner detects method overloads and gather them into
 * {@link Overload} objects.
 * 
 * "Valid" overloads are the cases where the overload calls the overloaded
 * method with some constants. It can then be translated as a unique method
 * containing default parameter values.
 * 
 * "Wrong" overloads are all the other cases. A core generic untyped signature
 * is generated that englobles all the overloads and dispatch to the
 * implementations depending on the parameter types.
 * 
 * @author Renaud Pawlak
 */
public class OverloadScanner extends AbstractTreeScanner {

	Types types;
	int pass = 1;

	/**
	 * Gathers methods overloading each other.
	 * 
	 * @author Renaud Pawlak
	 */
	public static class Overload {
		/**
		 * The method name.
		 */
		public String methodName;
		/**
		 * The methods carrying the same name.
		 */
		public List<JCMethodDecl> methods = new ArrayList<>();
		/**
		 * Tells if this overload is valid wrt to JSweet conventions.
		 */
		public boolean isValid = true;
		/**
		 * The core method of the overload, that is to say the one holding the
		 * implementation.
		 */
		public JCMethodDecl coreMethod;
		/**
		 * The default values for the parameters of the core method.
		 */
		public Map<Integer, JCTree> defaultValues;

		/**
		 * A flag to tell if this overload was printed out (used by the
		 * printer).
		 */
		public boolean printed = false;

		@Override
		public String toString() {
			return "overload(" + methodName + ")[" + methods.size() + "," + isValid + "]";
		}

		/**
		 * Returns parameter count of the method having the fewer parameters.
		 */
		public int getSmallerParameterCount() {
			return methods.get(methods.size() - 1).getParameters().size();
		}

		/**
		 * Checks the validity of the overload and calculates the default
		 * values.
		 */
		public void calculate(Types types) {
			if (methods.size() < 2) {
				return;
			}
			methods.sort((m1, m2) -> {
				int i = m2.getParameters().size() - m1.getParameters().size();
				if (i == 0) {
					isValid = false;
					for (int j = 0; j < m1.getParameters().size(); j++) {
						if (types.isAssignable(types.erasure(m1.getParameters().get(j).type), types.erasure(m2.getParameters().get(j).type))) {
							i--;
						}
						if (types.isAssignable(types.erasure(m2.getParameters().get(j).type), types.erasure(m1.getParameters().get(j).type))) {
							i++;
						}
						if (i == 0) {
							boolean core1 = Util.isCoreType(m1.getParameters().get(j).type);
							boolean core2 = Util.isCoreType(m2.getParameters().get(j).type);
							if (core1 && !core2) {
								i--;
							}
							if (!core1 && core2) {
								i++;
							}
						}
						if (i == 0) {
							boolean abstract1 = m1.getModifiers().getFlags().contains(Modifier.ABSTRACT)
									|| (m1.sym.getEnclosingElement().isInterface() && !m1.getModifiers().getFlags().contains(Modifier.DEFAULT));
							boolean abstract2 = m2.getModifiers().getFlags().contains(Modifier.ABSTRACT)
									|| (m2.sym.getEnclosingElement().isInterface() && !m2.getModifiers().getFlags().contains(Modifier.DEFAULT));
							if (abstract1 && !abstract2) {
								i++;
							}
							if (!abstract1 && abstract2) {
								i--;
							}
						}
					}
				}
				// valid overloads can only be in the same classe because of
				// potential side effects in subclasses
				if (m1.sym.getEnclosingElement() != m2.sym.getEnclosingElement()) {
					isValid = false;
				}
				return i;
			});
			coreMethod = methods.get(0);

			Type coreMethodType = types.erasureRecursive(coreMethod.type);
			for (JCMethodDecl m : new ArrayList<>(methods)) {
				if (m == coreMethod) {
					continue;
				}
				if (coreMethodType.toString().equals(types.erasureRecursive(m.type).toString())) {
					methods.remove(m);
				}
			}

			if (isValid) {
				defaultValues = new HashMap<>();
			}

			if (methods.size() > 1 && isValid) {
				for (JCMethodDecl methodDecl : methods) {
					if (!methodDecl.equals(coreMethod)) {
						if (methodDecl.body != null && methodDecl.body.stats.size() == 1) {
							JCMethodInvocation invocation = null;
							JCStatement stat = methodDecl.body.stats.get(0);
							if (stat instanceof JCReturn) {
								if (((JCReturn) stat).expr instanceof JCMethodInvocation) {
									invocation = (JCMethodInvocation) ((JCReturn) stat).expr;
								}
							} else if (stat instanceof JCExpressionStatement) {
								if (((JCExpressionStatement) stat).expr instanceof JCMethodInvocation) {
									invocation = (JCMethodInvocation) ((JCExpressionStatement) stat).expr;
								}
							}
							if (invocation == null) {
								isValid = false;
							} else {
								MethodSymbol method = Util.findMethodDeclarationInType(types, (TypeSymbol) methodDecl.sym.getEnclosingElement(), invocation);
								if (method != null && method.getSimpleName().toString().equals(methodName)) {
									String inv = invocation.meth.toString();
									if (!(inv.equals(methodName) || inv.equals("this." + methodName)
											|| /* constructor case */ inv.equals("this"))) {
										isValid = false;
									}
									if (isValid && invocation.getArguments() != null) {
										for (int i = 0; i < invocation.getArguments().size(); i++) {
											JCExpression expr = invocation.getArguments().get(i);
											if (Util.isConstant(expr)) {
												defaultValues.put(i, expr);
											} else {
												if (!(expr instanceof JCIdent && i < methodDecl.params.length()
														&& methodDecl.params.get(i).name.equals(((JCIdent) expr).name))) {
													isValid = false;
													break;
												}
											}
										}
									}
								} else {
									isValid = false;
								}
							}
						} else {
							isValid = false;
						}
					}
				}
			}

		}

		private static boolean hasMethodType(Types types, Overload overload, JCMethodDecl method) {
			return overload.methods.stream().map(m -> types.erasureRecursive(m.type)).anyMatch(t -> {
				boolean match = t.toString().equals(types.erasureRecursive(method.type).toString());
				if (match && t.tsym.getEnclosingElement() != method.sym.getEnclosingElement()) {
					overload.isValid = false;
				}
				return match;
			});
		}

		private static void safeAdd(Types types, Overload overload, JCMethodDecl method) {
			if (!overload.methods.contains(method) && !hasMethodType(types, overload, method)) {
				overload.methods.add(method);
			}
		}

		/**
		 * Merges the given overload with a subclass one.
		 */
		public void merge(Types types, Overload subOverload) {
			// merge default methods
			for (JCMethodDecl m : methods) {
				if (m.getModifiers().getFlags().contains(Modifier.DEFAULT)) {
					boolean overriden = false;
					for (JCMethodDecl subm : new ArrayList<>(subOverload.methods)) {
						if (subm.getParameters().size() == m.getParameters().size()) {
							overriden = true;
							for (int i = 0; i < subm.getParameters().size(); i++) {
								if (!types.isAssignable(m.getParameters().get(i).type, subm.getParameters().get(i).type)) {
									overriden = false;
								}
							}
						}
					}
					if (!overriden) {
						safeAdd(types, subOverload, m);
					}
				}
			}
			// merge other methods
			boolean merge = false;
			for (JCMethodDecl subm : new ArrayList<>(subOverload.methods)) {
				boolean overrides = false;
				for (JCMethodDecl m : new ArrayList<>(methods)) {
					if (subm.getParameters().size() == m.getParameters().size()) {
						overrides = true;
						for (int i = 0; i < subm.getParameters().size(); i++) {
							if (!types.isAssignable(m.getParameters().get(i).type, subm.getParameters().get(i).type)) {
								overrides = false;
							}
						}
					}
				}
				if (!overrides) {
					merge = true;
					safeAdd(types, this, subm);
				}
			}

			merge = merge || methods.size() > 1;

			if (merge) {
				for (JCMethodDecl m : methods) {
					safeAdd(types, subOverload, m);
				}
			}
		}
	}

	/**
	 * Creates a new overload scanner.
	 */
	public OverloadScanner(TranspilationHandler logHandler, JSweetContext context) {
		super(logHandler, context, null);
		this.types = Types.instance(context);
	}

	private void inspectSuperTypes(ClassSymbol clazz, Overload overload, JCMethodDecl method) {
		if (clazz == null) {
			return;
		}
		Overload superOverload = context.getOverload(clazz, method.sym);
		if (superOverload != null && superOverload != overload) {
			superOverload.merge(types, overload);
		}
		inspectSuperTypes((ClassSymbol) clazz.getSuperclass().tsym, overload, method);
		for (Type t : clazz.getInterfaces()) {
			inspectSuperTypes((ClassSymbol) t.tsym, overload, method);
		}
	}

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		ClassSymbol clazz = classdecl.sym;
		for (JCTree member : classdecl.defs) {
			if (member instanceof JCMethodDecl) {
				if (Util.hasAnnotationType(((JCMethodDecl) member).sym, JSweetConfig.ANNOTATION_ERASED)) {
					continue;
				}
				JCMethodDecl method = (JCMethodDecl) member;
				Overload overload = context.getOrCreateOverload(clazz, method.sym);
				if (pass == 1) {
					overload.methods.add(method);
				} else {
					if (!((JCMethodDecl) member).sym.isConstructor()) {
						inspectSuperTypes(classdecl.sym, overload, method);
					}
				}
			}
			// scan inner classes
			if (member instanceof JCClassDecl) {
				scan(member);
			}
		}
	}

	@Override
	public void visitTopLevel(JCCompilationUnit cu) {
		setCompilationUnit(cu);
		super.visitTopLevel(cu);
		setCompilationUnit(null);
	}

	/**
	 * Processes all the overload of a given compilation unit list.
	 */
	public void process(List<JCCompilationUnit> cuList) {
		for (JCCompilationUnit cu : cuList) {
			scan(cu);
		}
		pass++;
		for (JCCompilationUnit cu : cuList) {
			scan(cu);
		}
		for (Overload overload : context.getAllOverloads()) {
			overload.calculate(types);
		}
	}

}
