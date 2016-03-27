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
package org.jsweet.transpiler.util;

import static org.jsweet.transpiler.util.Util.getRootRelativeName;

import org.jsweet.transpiler.JSweetProblem;

import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCWildcard;
import com.sun.tools.javac.util.Name;

/**
 * A printer adapter, which can be overridden to change the default printer
 * behavior.
 * 
 * @author Renaud Pawlak
 */
public abstract class AbstractPrinterAdapter {

	private AbstractTreePrinter printer;

	/**
	 * A flags that indicates if ths adapter is printing type parameters.
	 */
	public boolean inTypeParameters = false;

	/**
	 * Reports a problem during the printing phase.
	 * 
	 * @param tree
	 *            the code where the problem occurred
	 * @param problem
	 *            the reported problem
	 * @param params
	 *            the parameters if any
	 */
	protected void report(JCTree tree, JSweetProblem problem, Object... params) {
		printer.report(tree, problem, params);
	}

	/**
	 * Reports a problem during the printing phase.
	 * 
	 * @param tree
	 *            the code where the problem occurred
	 * @param name
	 *            the name of the element if any
	 * @param problem
	 *            the reported problem
	 * @param params
	 *            the parameters if any
	 */
	protected void report(JCTree tree, Name name, JSweetProblem problem, Object... params) {
		printer.report(tree, name, problem, params);
	}

	/**
	 * Gets the print value of the given literal tree.
	 */
	public String getLiteralStringValue(JCTree tree) {
		return (String) ((JCLiteral) tree).value;
	}

	/**
	 * Substitutes the value of an array access expression.
	 * 
	 * @param arrayAccess
	 *            the array access being printed
	 * @return true if substituted
	 */
	public boolean substituteArrayAccess(JCArrayAccess arrayAccess) {
		return false;
	}

	/**
	 * Substitutes the value of an identifier.
	 * 
	 * @param identifier
	 *            the identifier being printed
	 * @return true if substituted
	 */
	public boolean substituteIdentifier(JCIdent identifier) {
		return false;
	}

	/**
	 * Substitutes the value of a <code>new</code> expression.
	 * 
	 * @param newClass
	 *            the new being printed
	 * @return true if substituted
	 */
	public boolean substituteNewClass(JCNewClass newClass) {
		return false;
	}

	/**
	 * Substitutes the value of a <em>field access</em> expression.
	 * 
	 * @param fieldAccess
	 *            the field access being printed
	 * @return true if substituted
	 */
	public boolean substituteFieldAccess(JCFieldAccess fieldAccess) {
		return false;
	}

	/**
	 * Returns true if the candidate method matches the target method.
	 */
	public boolean matchesMethod(String targetClassName, String targetMethodName, String candidateClassName, String candidateMethodName) {
		if (targetClassName != null) {
			return (candidateClassName == null || targetClassName.equals(candidateClassName))
					&& (candidateMethodName == null || targetMethodName.equals(candidateMethodName));
		} else {
			return targetMethodName.equals(candidateMethodName);
		}
	}

	public boolean matchesWithResultType(JCMethodInvocation invocation, Class<?> resultClass, String methodName) {
		String[] select = invocation.getMethodSelect().toString().split("\\.");
		if (methodName.equals(select[select.length - 1]) && resultClass.getName().equals(((MethodType) invocation.getMethodSelect().type).restype.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean matchesField(JCFieldAccess fieldAccess, Class<?> targetClass, String fieldName) {
		return (fieldName.equals(fieldAccess.name.toString()) && targetClass.getName().equals(fieldAccess.selected.type.getModelType().toString()));
	}

	public JCFieldAccess matchesField(JCAssign assignment, Class<?> targetClass, String fieldName) {
		if (assignment.lhs instanceof JCFieldAccess) {
			JCFieldAccess fa = (JCFieldAccess) assignment.lhs;
			if (matchesField(fa, targetClass, fieldName)) {
				return fa;
			}
		}
		return null;
	}

	/**
	 * Returns the import qualified id if the given import requires an import
	 * statement to be printed.
	 * 
	 * @param importDecl
	 *            the given import declaration
	 * @param qualifiedName
	 *            the qualified import id
	 * @return the possibly adapted qualified id or null if the import should be
	 *         ignored by the printer
	 */
	public String needsImport(JCImport importDecl, String qualifiedName) {
		if (importDecl.isStatic()) {
			return null;
		} else {
			return getRootRelativeName(importDecl.getQualifiedIdentifier().type.tsym);
		}
	}

	/**
	 * Tells if this cast is required to be printed or not.
	 */
	public boolean needsTypeCast(JCTypeCast cast) {
		return true;
	}

	public AbstractTreePrinter substituteAndPrintType(JCTree typeTree) {
		return substituteAndPrintType(typeTree, false);
	}

	public AbstractTreePrinter substituteAndPrintType(JCTree typeTree, boolean arrayComponent) {
		if (typeTree instanceof JCTypeApply) {
			JCTypeApply typeApply = ((JCTypeApply) typeTree);
			substituteAndPrintType(typeApply.clazz, arrayComponent);
			if (!typeApply.arguments.isEmpty()) {
				getPrinter().print("<");
				for (JCExpression argument : typeApply.arguments) {
					substituteAndPrintType(argument, arrayComponent).print(",");
				}
				if (typeApply.arguments.length() > 0) {
					getPrinter().removeLastChar();
				}
				getPrinter().print(">");
			}
			return getPrinter();
		} else if (typeTree instanceof JCWildcard) {
			JCWildcard wildcard = ((JCWildcard) typeTree);
			String name = getPrinter().getContext().getWildcardName(wildcard);
			if (name == null) {
				return getPrinter().print("any");
			} else {
				getPrinter().print(name);
				if (inTypeParameters) {
					getPrinter().print(" extends ");
					return substituteAndPrintType(wildcard.getBound(), arrayComponent);
				} else {
					return getPrinter();
				}
			}
		} else {
			if (typeTree instanceof JCArrayTypeTree) {
				return substituteAndPrintType(((JCArrayTypeTree) typeTree).elemtype, true).print("[]");
			}

			return getPrinter().print(typeTree);
		}
	}

	/**
	 * Tells if the printer needs to print the given variable declaration.
	 */
	public boolean needsVariableDecl(JCVariableDecl variableDecl, VariableKind kind) {
		return true;
	}

	/**
	 * Substitutes the value of a <em>method invocation</em> expression.
	 * 
	 * @param invocation
	 *            the invocation being printed
	 * @return true if substituted
	 */
	public boolean substituteMethodInvocation(JCMethodInvocation invocation) {
		return false;
	}

	/**
	 * Substitutes the value of a <em>field assignment</em> expression.
	 * 
	 * @param assign
	 *            the field assignment being printed
	 * @return true if substituted
	 */
	public boolean substituteAssignment(JCAssign assign) {
		return false;
	}

	/**
	 * Gets the printer attached to this adapter.
	 */
	public AbstractTreePrinter getPrinter() {
		return printer;
	}

	/**
	 * Sets the printer attached to this adapter.
	 */
	public void setPrinter(AbstractTreePrinter printer) {
		this.printer = printer;
	}

	/**
	 * Gets the adapted identifier string.
	 */
	public String getIdentifier(String identifier) {
		return identifier;
	}

}
