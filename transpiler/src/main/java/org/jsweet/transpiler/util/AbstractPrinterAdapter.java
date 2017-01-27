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
package org.jsweet.transpiler.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetProblem;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Symbol.TypeVariableSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
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
public abstract class AbstractPrinterAdapter<C extends JSweetContext> {

	private AbstractTreePrinter<C> printer;

	/**
	 * A flags that indicates that this adapter is printing type parameters.
	 */
	public boolean inTypeParameters = false;

	/**
	 * A flags that indicates that this adapter is not substituting types.
	 */
	public boolean disableTypeSubstitution = false;

	/**
	 * A list of type variables to be erased (mapped to any).
	 */
	public Set<TypeVariableSymbol> typeVariablesToErase = new HashSet<>();

	/**
	 * Prints a tree by delegating to the printer.
	 */
	protected AbstractTreePrinter<C> print(JCTree tree) {
		return printer.print(tree);
	}

	/**
	 * Prints a string by delegating to the printer.
	 */
	protected AbstractTreePrinter<C> print(String string) {
		return printer.print(string);
	}

	/**
	 * Prints an argument list by delegating to the printer.
	 */
	protected AbstractTreePrinter<C> printArgList(List<? extends JCTree> args) {
		return printer.printArgList(args);
	}

	/**
	 * Print either a string, or a tree if the string is null.
	 */
	protected void print(String exprStr, JCTree expr) {
		if (exprStr == null) {
			print(expr);
		} else {
			print(exprStr);
		}
	}

	/**
	 * Prints an indentation for the current indentation value.
	 */
	public AbstractTreePrinter<C> printIndent() {
		return printer.printIndent();
	}

	/**
	 * Increments the current indentation value.
	 */
	public AbstractTreePrinter<C> startIndent() {
		return printer.startIndent();
	}

	/**
	 * Decrements the current indentation value.
	 */
	public AbstractTreePrinter<C> endIndent() {
		return printer.endIndent();
	}

	/**
	 * Gets the printer's stack.
	 */
	public Stack<JCTree> getStack() {
		return printer.getStack();
	}

	/**
	 * Gets the parent element in the printer's scanning stack.
	 */
	public JCTree getParent() {
		return printer.getParent();
	}

	/**
	 * Gets the parent element in the printer's scanning stack.
	 */
	public <T extends JCTree> T getParent(Class<T> type) {
		return printer.getParent(type);
	}

	/**
	 * Gets the printer's current compilation unit.
	 */
	public JCCompilationUnit getCompilationUnit() {
		return printer.getCompilationUnit();
	}

	public String getRootRelativeName(Symbol symbol) {
		return printer.getRootRelativeName(symbol);
	}

	public String getRootRelativeName(Symbol symbol, boolean useJavaNames) {
		return printer.getRootRelativeName(symbol, useJavaNames);
	}

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
	public boolean matchesMethod(String targetClassName, String targetMethodName, String candidateClassName,
			String candidateMethodName) {
		if (targetClassName != null) {
			return (candidateClassName == null || targetClassName.equals(candidateClassName))
					&& (candidateMethodName == null || targetMethodName.equals(candidateMethodName));
		} else {
			return targetMethodName.equals(candidateMethodName);
		}
	}

	public boolean matchesWithResultType(JCMethodInvocation invocation, Class<?> resultClass, String methodName) {
		String[] select = invocation.getMethodSelect().toString().split("\\.");
		if (methodName.equals(select[select.length - 1])
				&& resultClass.getName().equals(((MethodType) invocation.getMethodSelect().type).restype.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean matchesField(JCFieldAccess fieldAccess, Class<?> targetClass, String fieldName) {
		return (fieldName.equals(fieldAccess.name.toString())
				&& targetClass.getName().equals(fieldAccess.selected.type.getModelType().toString()));
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
			return getPrinter().getRootRelativeName(importDecl.getQualifiedIdentifier().type.tsym);
		}
	}

	/**
	 * Tells if this cast is required to be printed or not.
	 */
	public boolean needsTypeCast(JCTypeCast cast) {
		return true;
	}

	public AbstractTreePrinter<C> substituteAndPrintType(JCTree typeTree) {
		return substituteAndPrintType(typeTree, false, inTypeParameters, true, disableTypeSubstitution);
	}

	public AbstractTreePrinter<C> substituteAndPrintType(JCTree typeTree, boolean arrayComponent,
			boolean inTypeParameters, boolean completeRawTypes, boolean disableSubstitution) {
		if (typeTree instanceof JCTypeApply) {
			JCTypeApply typeApply = ((JCTypeApply) typeTree);
			substituteAndPrintType(typeApply.clazz, arrayComponent, inTypeParameters, false, disableSubstitution);
			if (!typeApply.arguments.isEmpty() && !"any".equals(getPrinter().getLastPrintedString(3))
					&& !"Object".equals(getPrinter().getLastPrintedString(6))) {
				getPrinter().print("<");
				for (JCExpression argument : typeApply.arguments) {
					substituteAndPrintType(argument, arrayComponent, false, completeRawTypes, false).print(", ");
				}
				if (typeApply.arguments.length() > 0) {
					getPrinter().removeLastChars(2);
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
					return substituteAndPrintType(wildcard.getBound(), arrayComponent, false, completeRawTypes,
							disableSubstitution);
				} else {
					return getPrinter();
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
				getPrinter().print(typeTree);
				getPrinter().print("<");
				for (int i = 0; i < typeTree.type.tsym.getTypeParameters().length(); i++) {
					getPrinter().print("any, ");
				}
				getPrinter().removeLastChars(2);
				getPrinter().print(">");
				return getPrinter();
			} else {
				return getPrinter().print(typeTree);
			}
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
	public AbstractTreePrinter<C> getPrinter() {
		return printer;
	}

	/**
	 * Sets the printer attached to this adapter.
	 */
	public void setPrinter(AbstractTreePrinter<C> printer) {
		this.printer = printer;
	}

	/**
	 * Gets the adapted identifier string.
	 */
	public String getIdentifier(Symbol symbol) {
		return symbol.name.toString();
	}

	/**
	 * Returns the qualified type name.
	 */
	public String getQualifiedTypeName(TypeSymbol type, boolean globals) {
		return getPrinter().getRootRelativeName(type);
	}

	public abstract Set<String> getErasedTypes();

	/**
	 * Substitutes if required an expression that is being assigned to a given
	 * type.
	 */
	public boolean substituteAssignedExpression(Type assignedType, JCExpression expression) {
		return false;
	}

	/**
	 * Substitutes if necessary the given foreach loop.
	 * 
	 * @param the
	 *            foreach loop to print
	 * @param targetHasLength
	 *            true if the iterable defines a public length field
	 * @param indexVarName
	 *            a possible (fresh) variable name that can used to iterate
	 */
	public boolean substituteForEachLoop(JCEnhancedForLoop foreachLoop, boolean targetHasLength, String indexVarName) {
		return false;
	}

	/**
	 * Tells if a super class has to be erased in the generated source.
	 */
	public boolean eraseSuperClass(JCClassDecl classdecl, ClassSymbol superClass) {
		return false;
	}

	/**
	 * Tells if a super interface has to be erased in the generated source.
	 */
	public boolean eraseSuperInterface(JCClassDecl classdecl, ClassSymbol superInterface) {
		return false;
	}

	/**
	 * Tells if this adapter substitutes types in extends or implements clauses.
	 */
	public boolean isSubstituteSuperTypes() {
		return false;
	}

	/**
	 * Substitutes if necessary an instanceof expression.
	 * 
	 * @param exprStr
	 *            the expression being tested as a string (null if provided as a
	 *            tree)
	 * @param expr
	 *            the expression being tested as a tree (null if provided as a
	 *            string)
	 * @param type
	 *            the type of the instanceof expression
	 * @return true if substituted
	 */
	public boolean substituteInstanceof(String exprStr, JCTree expr, Type type) {
		return false;
	}

	/**
	 * Substitutes if necessary the pattern of a case statement.
	 */
	public boolean substituteCaseStatementPattern(JCCase caseStatement, JCExpression pattern) {
		return false;
	}

	/**
	 * Adapts the JavaDoc comment for a given element.
	 * 
	 * @param element
	 *            the documented element ({@link JCClassDecl},
	 *            {@link JCMethodDecl}, or {@link JCVariableDecl})
	 * @param commentLines
	 *            the comment lines, to be modified by the function if necessary
	 *            (clearing the list will remove the JavaDoc comment)
	 */
	public void adaptDocComment(JCTree element, List<String> commentLines) {
	}
}
