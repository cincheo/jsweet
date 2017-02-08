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
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
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
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.Name;

/**
 * A printer adapter, which can be overridden to change the default printer
 * behavior.
 * 
 * @author Renaud Pawlak
 */
public abstract class AbstractPrinterAdapter<C extends JSweetContext> {

	private AbstractPrinterAdapter<C> parentAdapter;

	private AbstractTreePrinter<C> printer;

	protected C context;

	public AbstractPrinterAdapter(AbstractPrinterAdapter<C> parentAdapter) {
		super();
		this.parentAdapter = parentAdapter;
	}

	/**
	 * Gets the transpiler's context.
	 */
	public C getContext() {
		if (context != null) {
			return context;
		} else {
			if (getParentAdapter() != null) {
				context = getParentAdapter().getContext();
			}
		}
		return context;
	}

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
		return parentAdapter == null ? false : parentAdapter.substituteArrayAccess(arrayAccess);
	}

	/**
	 * Substitutes the value of an identifier.
	 * 
	 * @param identifier
	 *            the identifier being printed
	 * @return true if substituted
	 */
	public boolean substituteIdentifier(JCIdent identifier) {
		return parentAdapter == null ? false : parentAdapter.substituteIdentifier(identifier);
	}

	/**
	 * Substitutes the value of a <code>new</code> expression.
	 * 
	 * @param newClass
	 *            the new being printed
	 * @return true if substituted
	 */
	public boolean substituteNewClass(JCNewClass newClass) {
		return parentAdapter == null ? false : parentAdapter.substituteNewClass(newClass);
	}

	/**
	 * Substitutes the value of a <em>field access</em> expression.
	 * 
	 * @param fieldAccess
	 *            the field access being printed
	 * @return true if substituted
	 */
	public boolean substituteFieldAccess(JCFieldAccess fieldAccess) {
		return parentAdapter == null ? false : parentAdapter.substituteFieldAccess(fieldAccess);
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
		return parentAdapter == null ? true : parentAdapter.needsTypeCast(cast);
	}

	public AbstractTreePrinter<C> substituteAndPrintType(JCTree typeTree) {
		return substituteAndPrintType(typeTree, false, inTypeParameters, true, disableTypeSubstitution);
	}

	protected abstract AbstractTreePrinter<C> substituteAndPrintType(JCTree typeTree, boolean arrayComponent,
			boolean inTypeParameters, boolean completeRawTypes, boolean disableSubstitution);

	/**
	 * Tells if the printer needs to print the given variable declaration.
	 */
	public boolean needsVariableDecl(JCVariableDecl variableDecl, VariableKind kind) {
		return parentAdapter == null ? true : parentAdapter.needsVariableDecl(variableDecl, kind);
	}

	/**
	 * Substitutes the value of a <em>method invocation</em> expression.
	 * 
	 * @param invocation
	 *            the invocation being printed
	 * @return true if substituted
	 */
	public boolean substituteMethodInvocation(JCMethodInvocation invocation) {
		return parentAdapter == null ? false : parentAdapter.substituteMethodInvocation(invocation);
	}

	/**
	 * Substitutes the value of a <em>field assignment</em> expression.
	 * 
	 * @param assign
	 *            the field assignment being printed
	 * @return true if substituted
	 */
	public boolean substituteAssignment(JCAssign assign) {
		return parentAdapter == null ? false : parentAdapter.substituteAssignment(assign);
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
		return parentAdapter == null ? symbol.name.toString() : parentAdapter.getIdentifier(symbol);
	}

	/**
	 * Returns the qualified type name.
	 */
	public String getQualifiedTypeName(TypeSymbol type, boolean globals) {
		return parentAdapter == null ? getPrinter().getRootRelativeName(type)
				: parentAdapter.getQualifiedTypeName(type, globals);
	}

	public abstract Set<String> getErasedTypes();

	/**
	 * Substitutes if required an expression that is being assigned to a given
	 * type.
	 */
	public boolean substituteAssignedExpression(Type assignedType, JCExpression expression) {
		return parentAdapter == null ? false : parentAdapter.substituteAssignedExpression(assignedType, expression);
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
		return parentAdapter == null ? false
				: parentAdapter.substituteForEachLoop(foreachLoop, targetHasLength, indexVarName);
	}

	/**
	 * Tells if a super class has to be erased in the generated source.
	 */
	public boolean eraseSuperClass(JCClassDecl classdecl, ClassSymbol superClass) {
		return parentAdapter == null ? false : parentAdapter.eraseSuperClass(classdecl, superClass);
	}

	/**
	 * Tells if a super interface has to be erased in the generated source.
	 */
	public boolean eraseSuperInterface(JCClassDecl classdecl, ClassSymbol superInterface) {
		return parentAdapter == null ? false : parentAdapter.eraseSuperInterface(classdecl, superInterface);
	}

	/**
	 * Tells if this adapter substitutes types in extends or implements clauses.
	 */
	public boolean isSubstituteSuperTypes() {
		return parentAdapter == null ? false : parentAdapter.isSubstituteSuperTypes();
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
		return parentAdapter == null ? false : parentAdapter.substituteInstanceof(exprStr, expr, type);
	}

	/**
	 * Substitutes if necessary the pattern of a case statement.
	 */
	public boolean substituteCaseStatementPattern(JCCase caseStatement, JCExpression pattern) {
		return parentAdapter == null ? false : parentAdapter.substituteCaseStatementPattern(caseStatement, pattern);
	}

	/**
	 * Adapts the JavaDoc comment for a given element.
	 * 
	 * @param element
	 *            the documented element ({@link JCClassDecl},
	 *            {@link JCMethodDecl}, or {@link JCVariableDecl})
	 * @param commentText
	 *            the comment text if any (null when no comment)
	 * @return the adapted comment (null will remove the JavaDoc comment)
	 */
	public String adaptDocComment(JCTree element, String commentText) {
		return commentText;
	}

	/**
	 * Gets the parent adapter. By default, an adapter delegates to the parent
	 * adapter when the behavior is not overridden.
	 */
	public AbstractPrinterAdapter<C> getParentAdapter() {
		return parentAdapter;
	}

	/**
	 * Sets the parent adapter. By default, an adapter delegates to the parent
	 * adapter when the behavior is not overridden.
	 */
	public void setParentAdapter(AbstractPrinterAdapter<C> parentAdapter) {
		this.parentAdapter = parentAdapter;
	}
}
