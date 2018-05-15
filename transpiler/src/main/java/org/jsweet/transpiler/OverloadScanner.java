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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.element.Modifier;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symtab;
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

	private Types types;
	private int pass = 1;

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
		 * A flag to tell if this overload was printed out (used by the printer).
		 */
		public boolean printed = false;

		private List<String> parameterNames = new ArrayList<>();

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(
					"overload(" + methodName + ")[" + methods.size() + "," + isValid + "]");
			if (methods.size() > 1) {
				for (JCMethodDecl method : methods) {
					sb.append("\n      # " + method.sym.getEnclosingElement() + "." + method.sym);
				}
			}
			return sb.toString();
		}

		/**
		 * Returns parameter count of the method having the fewer parameters.
		 */
		public int getSmallerParameterCount() {
			return methods.get(methods.size() - 1).getParameters().size();
		}

		/**
		 * Gets the parameter name at the given index for an invalid overload.
		 * 
		 * @param index
		 *            the parameter's index
		 * @return a unique parameter name that reflects the overloaded parameter
		 */
		public String getParameterName(int index) {
			return parameterNames.get(index);
		}

		/**
		 * Checks the validity of the overload and calculates the default values.
		 */
		public void calculate(Types types, Symtab symtab) {
			if (methods.size() < 2) {
				coreMethod = methods.get(0);
				return;
			}
			methods.sort((m1, m2) -> {
				int i = m2.getParameters().size() - m1.getParameters().size();
				if (i == 0) {
					isValid = false;
					for (int j = 0; j < m1.getParameters().size(); j++) {
						if (types.isAssignable(types.erasure(m1.getParameters().get(j).type),
								types.erasure(m2.getParameters().get(j).type))) {
							i--;
						}
						if (types.isAssignable(types.erasure(m2.getParameters().get(j).type),
								types.erasure(m1.getParameters().get(j).type))) {
							i++;
						}

						if (i == 0) {
							boolean core1 = Util.isCoreType(m1.getParameters().get(j).type);
							if (m1.getParameters().get(j).type == symtab.stringType) {
								core1 = false;
							}
							boolean core2 = Util.isCoreType(m2.getParameters().get(j).type);
							if (m2.getParameters().get(j).type == symtab.stringType) {
								core2 = false;
							}
							if (!core1 && core2) {
								i--;
							}
							if (core1 && !core2) {
								i++;
							}
						}

						if (i == 0) {
							boolean abstract1 = m1.getModifiers().getFlags().contains(Modifier.ABSTRACT)
									|| (m1.sym.getEnclosingElement().isInterface()
											&& !m1.getModifiers().getFlags().contains(Modifier.DEFAULT));
							boolean abstract2 = m2.getModifiers().getFlags().contains(Modifier.ABSTRACT)
									|| (m2.sym.getEnclosingElement().isInterface()
											&& !m2.getModifiers().getFlags().contains(Modifier.DEFAULT));
							if (abstract1 && !abstract2) {
								i++;
							}
							if (!abstract1 && abstract2) {
								i--;
							}
						}
					}
				}

				// valid overloads can only be in the same class because of
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
					if (methodDecl.body != null && methodDecl.body.stats.size() == 1) {
						if (!methodDecl.equals(coreMethod)) {
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
								MethodSymbol method = Util.findMethodDeclarationInType(types,
										(TypeSymbol) methodDecl.sym.getEnclosingElement(), invocation);
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
														&& methodDecl.params.get(i).name
																.equals(((JCIdent) expr).name))) {
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
						}
					} else {
						isValid = false;
					}
				}
			}
			// call if used (for better naming in future releases)
			// initParameterNames();
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
								if (!types.isAssignable(m.getParameters().get(i).type,
										subm.getParameters().get(i).type)) {
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
				merge = merge || !overrides;
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
		if (clazz.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")
				|| context.hasAnnotationType(clazz, JSweetConfig.ANNOTATION_ERASED, JSweetConfig.ANNOTATION_AMBIENT)) {
			return;
		}

		for (JCTree member : classdecl.defs) {
			if (member instanceof JCMethodDecl) {
				processMethod(classdecl, (JCMethodDecl) member);
			}
		}

		HashSet<Entry<JCClassDecl, JCMethodDecl>> defaultMethods = new HashSet<>();
		Util.findDefaultMethodsInType(defaultMethods, context, classdecl.sym);
		for (Entry<JCClassDecl, JCMethodDecl> defaultMethodWithEnclosingClass : defaultMethods) {
			processMethod(classdecl, defaultMethodWithEnclosingClass.getValue());
		}
		// scan all AST because of anonymous classes that may appear everywhere
		// (including in field initializers)
		super.visitClassDef(classdecl);
	}

	private void processMethod(JCClassDecl enclosingClassdecl, JCMethodDecl method) {
		if (context.hasAnnotationType(method.sym, JSweetConfig.ANNOTATION_ERASED, JSweetConfig.ANNOTATION_AMBIENT)) {
			return;
		}
		Overload overload = context.getOrCreateOverload(enclosingClassdecl.sym, method.sym);
		if (pass == 1) {
			overload.methods.add(method);
		} else {
			if (!method.sym.isConstructor()) {
				inspectSuperTypes(enclosingClassdecl.sym, overload, method);
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
			overload.calculate(types, context.symtab);
			if (overload.methods.size() > 1 && !overload.isValid) {
				if (overload.coreMethod.sym.isConstructor()) {
					context.classesWithWrongConstructorOverload.add(overload.coreMethod.sym.enclClass());
				}
			}
		}
		// context.dumpOverloads(System.out);
	}

}
