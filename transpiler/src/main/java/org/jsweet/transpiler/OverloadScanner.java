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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import com.google.common.collect.Iterables;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;

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
 * @author Louis Grignon
 */
public class OverloadScanner extends AbstractTreeScanner {

	private int pass = 1;

	public static class OverloadMethodEntry {
		public final CompilationUnitTree compilationUnit;
		public final ClassTree enclosingClassTree;
		public final MethodTree methodTree;
		public final ExecutableElement methodElement;
		public final TypeMirror methodType;

		protected OverloadMethodEntry(CompilationUnitTree compilationUnit, ClassTree enclosingClassTree,
				MethodTree methodTree, ExecutableElement methodElement, TypeMirror methodType) {
			this.compilationUnit = compilationUnit;
			this.enclosingClassTree = enclosingClassTree;
			this.methodTree = methodTree;
			this.methodElement = methodElement;
			this.methodType = methodType;
		}

		@Override
		public int hashCode() {
			return methodTree.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof OverloadMethodEntry && methodTree == ((OverloadMethodEntry) other).methodTree;
		}
	}

	/**
	 * Gathers methods overloading each other.
	 * 
	 * @author Renaud Pawlak
	 * @author Louis Grignon
	 */
	public static class Overload {
		/**
		 * The method name.
		 */
		public String methodName;

		private final List<OverloadMethodEntry> entries = new ArrayList<>();

		/**
		 * The methods carrying the same name.
		 */
		public Iterable<MethodTree> getMethods() {
			return entries.stream().map(entry -> entry.methodTree).collect(toList());
		}

		public MethodTree getMethodAt(int i) {
			return entries.get(i).methodTree;
		}

		/**
		 * Tells if this overload is valid wrt to JSweet conventions.
		 */
		public boolean isValid = true;

		private OverloadMethodEntry coreEntry;

		/**
		 * @see #getCoreEntry()
		 */
		public MethodTree getCoreMethod() {
			return coreEntry == null ? null : coreEntry.methodTree;
		}
		
		/**
		 * The core method of the overload, that is to say the one holding the
		 * implementation.
		 */		
		public OverloadMethodEntry getCoreEntry() {
			return coreEntry;
		}

		/**
		 * The default values for the parameters of the core method.
		 */
		public Map<Integer, Tree> defaultValues;

		/**
		 * A flag to tell if this overload was printed out (used by the printer).
		 */
		public boolean printed = false;

		private final List<String> parameterNames = new ArrayList<>();
		private final JSweetContext context;
		private final CompilationUnitTree compilationUnit;
		private final Util util;
		private final Types types;

		public Overload(JSweetContext context, CompilationUnitTree compilationUnit) {
			this.context = context;
			this.util = context.util;
			this.types = context.types;
			this.compilationUnit = compilationUnit;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(
					"overload(" + methodName + ")[" + getMethodsCount() + "," + isValid + "]");
			if (getMethodsCount() > 1) {
				for (OverloadMethodEntry entry : entries) {
					ExecutableElement methodElement = context.util.getElementForTree(entry.methodTree, compilationUnit);
					sb.append("\n      # " + methodElement.getEnclosingElement() + "." + methodElement);
				}
			}
			return sb.toString();
		}

		public int getMethodsCount() {
			return entries.size();
		}

		/**
		 * Returns parameter count of the method having the fewer parameters.
		 */
		public int getSmallerParameterCount() {
			return getMethodAt(getMethodsCount() - 1).getParameters().size();
		}

		/**
		 * Gets the parameter name at the given index for an invalid overload.
		 * 
		 * @param index the parameter's index
		 * @return a unique parameter name that reflects the overloaded parameter
		 */
		public String getParameterName(int index) {
			return parameterNames.get(index);
		}

		/**
		 * Checks the validity of the overload and calculates the default values.
		 */
		public void calculate() {
			if (getMethodsCount() < 2) {
				coreEntry = entries.get(0);
				return;
			}
			entries.sort((OverloadMethodEntry m1Entry, OverloadMethodEntry m2Entry) -> {
				MethodTree m1 = m1Entry.methodTree;
				MethodTree m2 = m2Entry.methodTree;

				ExecutableElement m1Element = m1Entry.methodElement;
				ExecutableElement m2Element = m2Entry.methodElement;
				int i = m2.getParameters().size() - m1.getParameters().size();
				if (i == 0) {
					isValid = false;
					for (int j = 0; j < m1.getParameters().size(); j++) {
						TypeMirror m1ParamType = types
								.erasure(util.getTypeForTree(m1.getParameters().get(j), compilationUnit));
						TypeMirror m2ParamType = types
								.erasure(util.getTypeForTree(m2.getParameters().get(j), compilationUnit));
						if (types.isAssignable(m1ParamType, m2ParamType)) {
							i--;
						}
						if (types.isAssignable(m2ParamType, m1ParamType)) {
							i++;
						}

						if (i == 0) {
							boolean core1 = util.isCoreType(m1ParamType);
							if (util.isStringType(m1ParamType)) {
								core1 = false;
							}
							boolean core2 = util.isCoreType(m2ParamType);
							if (util.isStringType(m2ParamType)) {
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
									|| (util.isInterface(m1Element.getEnclosingElement())
											&& !m1.getModifiers().getFlags().contains(Modifier.DEFAULT));
							boolean abstract2 = m2.getModifiers().getFlags().contains(Modifier.ABSTRACT)
									|| (util.isInterface(m2Element.getEnclosingElement())
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
				if (m1Element.getEnclosingElement() != m2Element.getEnclosingElement()) {
					isValid = false;
				}
				return i;
			});
			coreEntry = entries.get(0);

			TypeMirror coreMethodType = coreEntry.methodType;
			coreMethodType = util.erasureRecursive(coreMethodType);
			for (OverloadMethodEntry currentEntry : new ArrayList<>(entries)) {
				if (currentEntry == coreEntry) {
					continue;
				}
				if (coreMethodType.toString().equals(currentEntry.methodType.toString())) {
					entries.remove(currentEntry);
				}
			}

			if (isValid) {
				defaultValues = new HashMap<>();
			}

			if (getMethodsCount() > 1 && isValid) {
				for (OverloadMethodEntry entry : entries) {
					MethodTree methodDecl = entry.methodTree;
					if (methodDecl.getBody() != null && methodDecl.getBody().getStatements().size() == 1) {
						if (entry != coreEntry) {
							List<? extends StatementTree> statements = methodDecl.getBody().getStatements();
							MethodInvocationTree invocation = null;
							StatementTree stat = statements.get(0);
							if (stat instanceof ReturnTree) {
								if (((ReturnTree) stat).getExpression() instanceof MethodInvocationTree) {
									invocation = (MethodInvocationTree) ((ReturnTree) stat).getExpression();
								}
							} else if (stat instanceof ExpressionStatementTree) {
								if (((ExpressionStatementTree) stat).getExpression() instanceof MethodInvocationTree) {
									invocation = (MethodInvocationTree) ((ExpressionStatementTree) stat)
											.getExpression();
								}
							}
							if (invocation == null) {
								isValid = false;
							} else {
								ExecutableElement methodElement = util.getElementForTree(methodDecl, compilationUnit);
								ExecutableElement invokedMethodElement = context.util.findMethodDeclarationInType(
										(TypeElement) methodElement.getEnclosingElement(), invocation);
								if (invokedMethodElement != null
										&& invokedMethodElement.getSimpleName().toString().equals(methodName)) {
									String inv = invocation.getMethodSelect().toString();
									if (!(inv.equals(methodName) || inv.equals("this." + methodName)
											|| /* constructor case */ inv.equals("this"))) {
										isValid = false;
									}
									if (isValid && invocation.getArguments() != null) {
										for (int i = 0; i < invocation.getArguments().size(); i++) {
											ExpressionTree expr = invocation.getArguments().get(i);
											if (context.util.isConstant(expr, compilationUnit)) {
												defaultValues.put(i, expr);
											} else {
												if (!(expr instanceof IdentifierTree
														&& i < methodDecl.getParameters().size()
														&& methodDecl.getParameters().get(i).getName().toString()
																.equals(((IdentifierTree) expr).getName()
																		.toString()))) {
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
		}

		private boolean hasMethodType(MethodTree searchedMethod) {

			Element searchedMethodElement = util.getElementForTree(searchedMethod, compilationUnit);
			TypeMirror searchedMethodType = util.erasureRecursive(util.getTypeForTree(searchedMethod, compilationUnit));
			for (MethodTree thisMethod : getMethods()) {
				Element thisMethodElement = util.getElementForTree(thisMethod, compilationUnit);
				TypeMirror thisMethodType = util.erasureRecursive(util.getTypeForTree(thisMethod, compilationUnit));

				boolean match = thisMethodType.toString().equals(searchedMethodType.toString());
				if (match && thisMethodElement.getEnclosingElement() != searchedMethodElement.getEnclosingElement()) {
					this.isValid = false;
				}
				if (match) {
					return true;
				}
			}
			return false;
		}

		private void safeAdd(OverloadMethodEntry methodEntry) {
			if (!entries.contains(methodEntry) && !hasMethodType(methodEntry.methodTree)) {
				entries.add(methodEntry);
			}
		}

		private MethodTree[] clone(Iterable<MethodTree> initialList) {
			return Iterables.toArray(initialList, MethodTree.class);
		}

		/**
		 * Merges the given overload with a subclass one.
		 */
		public void merge(Overload subOverload) {
			// merge default methods
			for (OverloadMethodEntry overloadMethodEntry : entries) {
				MethodTree overloadMethod = overloadMethodEntry.methodTree;
				if (overloadMethod.getModifiers().getFlags().contains(Modifier.DEFAULT)) {
					boolean overriden = false;

					for (MethodTree subOverloadMethod : clone(subOverload.getMethods())) {
						if (subOverloadMethod.getParameters().size() == overloadMethod.getParameters().size()) {
							overriden = true;
							for (int i = 0; i < subOverloadMethod.getParameters().size(); i++) {
								TypeMirror overloadParamType = util
										.getTypeForTree(overloadMethod.getParameters().get(i), compilationUnit);
								TypeMirror subOverloadParamType = util
										.getTypeForTree(subOverloadMethod.getParameters().get(i), compilationUnit);

								if (!types.isAssignable(overloadParamType, subOverloadParamType)) {
									overriden = false;
								}
							}
						}
					}
					if (!overriden) {
						subOverload.safeAdd(overloadMethodEntry);
					}
				}
			}
			// merge other methods
			boolean merge = false;
			for (OverloadMethodEntry subOverloadEntry : new ArrayList<>(subOverload.entries)) {
				MethodTree subOverloadMethod = subOverloadEntry.methodTree;
				boolean overrides = false;
				for (OverloadMethodEntry overloadEntry : new ArrayList<>(entries)) {
					MethodTree overloadMethod = overloadEntry.methodTree;
					if (subOverloadMethod.getParameters().size() == overloadMethod.getParameters().size()) {
						overrides = true;
						for (int i = 0; i < subOverloadMethod.getParameters().size(); i++) {
							TypeMirror overloadParamType = util.getTypeForTree(overloadMethod.getParameters().get(i),
									compilationUnit);
							TypeMirror subOverloadParamType = util
									.getTypeForTree(subOverloadMethod.getParameters().get(i), compilationUnit);
							if (!types.isAssignable(overloadParamType, subOverloadParamType)) {
								overrides = false;
							}
						}
					}
				}
				merge = merge || !overrides;
			}

			merge = merge || getMethodsCount() > 1;

			if (merge) {
				for (OverloadMethodEntry overloadEntry : entries) {
					subOverload.safeAdd(overloadEntry);
				}
			}
		}

		public ExecutableElement getCoreMethodElement() {
			return coreEntry.methodElement;
		}

		public void register(CompilationUnitTree compilationUnit, ClassTree enclosingClassTree, MethodTree methodTree) {

			ExecutableElement methodElement = util.getElementForTree(methodTree, compilationUnit);
			this.entries.add(new OverloadMethodEntry(compilationUnit, enclosingClassTree, methodTree, methodElement,
					methodElement.asType()));
		}
	}

	/**
	 * Creates a new overload scanner.
	 */
	public OverloadScanner(TranspilationHandler logHandler, JSweetContext context) {
		super(logHandler, context, null);
	}

	private void inspectSuperTypes(TypeElement clazz, Overload overload, MethodTree method) {
		if (clazz == null) {
			return;
		}
		Overload superOverload = context.getOverload(clazz, toElement(method));
		if (superOverload != null && superOverload != overload) {
			superOverload.merge(overload);
		}
		inspectSuperTypes((TypeElement) context.types.asElement(clazz.getSuperclass()), overload, method);
		for (TypeMirror interfaceType : clazz.getInterfaces()) {
			inspectSuperTypes((TypeElement) context.types.asElement(interfaceType), overload, method);
		}
	}

	@Override
	public Void visitClass(ClassTree classTree, Trees trees) {
		TypeElement classElement = toElement(classTree);
		if (classElement.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".") || context
				.hasAnnotationType(classElement, JSweetConfig.ANNOTATION_ERASED, JSweetConfig.ANNOTATION_AMBIENT)) {
			return null;
		}

		for (Tree member : classTree.getMembers()) {
			if (member instanceof MethodTree) {
				processMethod(classTree, (MethodTree) member);
			}
		}

		HashSet<Entry<ClassTree, MethodTree>> defaultMethods = new HashSet<>();
		util().findDefaultMethodsInType(defaultMethods, context, classElement);
		for (Entry<ClassTree, MethodTree> defaultMethodWithEnclosingClass : defaultMethods) {
			processMethod(classTree, defaultMethodWithEnclosingClass.getValue());
		}
		// scan all AST because of anonymous classes that may appear everywhere
		// (including in field initializers)
		return super.visitClass(classTree, trees);
	}

	private void processMethod(ClassTree enclosingClassdecl, MethodTree method) {
		ExecutableElement methodElement = toElement(method);
		TypeElement enclosingClassElement = toElement(enclosingClassdecl);
		if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_ERASED, JSweetConfig.ANNOTATION_AMBIENT)) {
			return;
		}
		Overload overload = context.getOrCreateOverload(enclosingClassElement, methodElement);
		if (pass == 1) {
			overload.register(getCompilationUnit(), enclosingClassdecl, method);
		} else {
			if (methodElement.getKind() != ElementKind.CONSTRUCTOR) {
				inspectSuperTypes(enclosingClassElement, overload, method);
			}
		}
	}

	@Override
	public Void visitCompilationUnit(CompilationUnitTree cu, Trees trees) {
		setCompilationUnit(cu);
		super.visitCompilationUnit(cu, trees);
		setCompilationUnit(null);

		return null;
	}

	/**
	 * Processes all the overload of a given compilation unit list.
	 */
	public void process(List<CompilationUnitTree> cuList) {
		for (CompilationUnitTree cu : cuList) {
			scan(cu, getContext().trees);
		}
		pass++;
		for (CompilationUnitTree cu : cuList) {
			scan(cu, getContext().trees);
		}
		for (Overload overload : context.getAllOverloads()) {
			overload.calculate();
			if (overload.getMethodsCount() > 1 && !overload.isValid) {
				if (overload.getCoreMethodElement().getKind() == ElementKind.CONSTRUCTOR) {
					context.classesWithWrongConstructorOverload
							.add((TypeElement) overload.getCoreMethodElement().getEnclosingElement());
				}
			}
		}
	}

}
