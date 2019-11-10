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

import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.SourcePosition;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.TypeChecker;
import org.jsweet.transpiler.extension.PrinterAdapter;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;

/**
 * A tree printer is a kind of tree scanner specialized in pretty printing the
 * scanned AST of a compilation unit (source file).
 * 
 * @author Renaud Pawlak
 */
public abstract class AbstractTreePrinter extends AbstractTreeScanner {

	private Stack<Position> positionStack = new Stack<>();

	/**
	 * A footer to be printed at the end of the output.
	 */
	public StringBuilder footer = new StringBuilder();

	/**
	 * The position stack of the scanner.
	 */
	public Stack<Position> getPositionStack() {
		return positionStack;
	}

	/**
	 * The constant string for indentation.
	 */
	protected static final String INDENT = "    ";

	private StringBuilder out = new StringBuilder();

	private int indent = 0;

	private PrinterAdapter adapter;

	/** A type checker instance. */
	public TypeChecker typeChecker;

	private int currentLine = 1;

	private int currentColumn = 0;

	private boolean fillSourceMap = true;

	/**
	 * The object storing source map information while printing.
	 */
	public SourceMap sourceMap = new SourceMap();

	/**
	 * Creates a new printer.
	 * 
	 * @param logHandler      the handler that reports logs and problems
	 * @param context         the scanning context
	 * @param compilationUnit the source file to be printed
	 * @param adapter         the printer adapter
	 * @param fillSourceMap   tells if printer fills the source map
	 */
	public AbstractTreePrinter(TranspilationHandler logHandler, JSweetContext context,
			CompilationUnitTree compilationUnit, PrinterAdapter adapter, boolean fillSourceMap) {
		super(logHandler, context, compilationUnit);
		this.typeChecker = new TypeChecker(this);
		this.adapter = adapter;
		this.adapter.setPrinter(this);
		this.fillSourceMap = fillSourceMap;
	}

	/**
	 * Gets this output of this printer.
	 */
	public String getOutput() {
		return out.toString();
	}

	/**
	 * Print a given AST.
	 */
	public AbstractTreePrinter print(Tree tree) {
		scan(tree, context.trees);
		return this;
	}

	/**
	 * Enters the given tree (se {@link #scan(Tree)}.
	 */
	protected void enter(Tree tree) {
		super.enter(tree);
		positionStack.push(new Position(getCurrentPosition(), currentLine, currentColumn));
		if (compilationUnit != null && inSourceMap(tree)) {
			SourcePosition sourcePosition = util().getSourcePosition(tree, compilationUnit);
			Position startPosition = sourcePosition.getStartPosition();

			sourceMap.addEntry(startPosition, positionStack.peek());
		}
	}

	private boolean inSourceMap(Tree tree) {
		return (tree instanceof ExpressionTree || tree instanceof StatementTree || tree instanceof MethodTree);
	}

	@Override
	protected void onRollbacked(Tree target) {
		super.onRollbacked(target);
		Position position = positionStack.peek();
		out.delete(position.getPosition(), out.length());
		currentColumn = position.getColumn();
		currentLine = position.getLine();
	}

	/**
	 * Exits the currently scanned tree.
	 */
	@Override
	protected void exit() {
		Tree tree = stack.peek();
		if (compilationUnit != null && tree instanceof BlockTree) {
			SourcePosition sourcePosition = util().getSourcePosition(tree, compilationUnit);
			Position endPosition = sourcePosition.getEndPosition();

			sourceMap.addEntry(endPosition, new Position(getCurrentPosition(), currentLine, currentColumn));
		}
		super.exit();
		positionStack.pop();
	}

	/**
	 * Gets the current character count of the output.
	 */
	public int getCurrentPosition() {
		return out.length();
	}

	/**
	 * Gets the lastly printed character.
	 */
	public char getLastPrintedChar() {
		return out.charAt(out.length() - 1);
	}

	/**
	 * Gets the last printed string for the given length.
	 */
	public String getLastPrintedString(int length) {
		return out.substring(out.length() - length);
	}

	/**
	 * Prints an indentation for the current indentation value.
	 */
	public AbstractTreePrinter printIndent() {
		for (int i = 0; i < indent; i++) {
			print(INDENT);
		}
		return this;
	}

	/**
	 * Returns the current indentation as a string.
	 */
	public String getIndentString() {
		String indentString = "";
		for (int i = 0; i < indent; i++) {
			indentString += INDENT;
		}
		return indentString;
	}

	/**
	 * Increments the current indentation value.
	 */
	public AbstractTreePrinter startIndent() {
		indent++;
		return this;
	}

	/**
	 * Decrements the current indentation value.
	 */
	public AbstractTreePrinter endIndent() {
		indent--;
		return this;
	}

	/**
	 * Outputs a string (new lines are not allowed).
	 */
	public AbstractTreePrinter print(String string) {
		out.append(string);
		currentColumn += string.length();
		return this;
	}

	/**
	 * Outputs a string and new line
	 */
	public AbstractTreePrinter println(String string) {
		return print(string).println();
	}

	/**
	 * Outputs an identifier.
	 */
	public AbstractTreePrinter printIdentifier(Element symbol) {
		String adaptedIdentifier = context.getActualName(symbol);
		return print(adaptedIdentifier);
	}

	public String getQualifiedTypeName(TypeElement type, boolean globals, boolean ignoreLangTypes) {
		return getRootRelativeName(type);
	}

	public String getIdentifier(Element symbol) {
		return context.getActualName(symbol);
	}

	/**
	 * Adds a space to the output.
	 */
	public AbstractTreePrinter space() {
		return print(" ");
	}

	/**
	 * removes last character if expectedChar
	 */
	public boolean removeLastChar(char expectedChar) {
		if (getLastPrintedChar() == expectedChar) {
			removeLastChar();
			return true;
		}
		return false;
	}

	/**
	 * Removes the last output character.
	 */
	public AbstractTreePrinter removeLastChar() {
		if (out.length() == 0) {
			return this;
		}
		if (out.charAt(out.length() - 1) == '\n') {
			currentLine--;
			currentColumn = 0;
		} else {
			currentColumn--;
		}
		out.deleteCharAt(out.length() - 1);
		return this;
	}

	/**
	 * Removes the last output characters.
	 */
	public AbstractTreePrinter removeLastChars(int count) {
		for (int i = 0; i < count; i++) {
			removeLastChar();
		}
		return this;
	}

	/**
	 * Removes the last printed indentation.
	 */
	public AbstractTreePrinter removeLastIndent() {
		removeLastChars(indent * INDENT.length());
		return this;
	}

	/**
	 * Outputs a new line.
	 */
	public AbstractTreePrinter println() {
		out.append("\n");
		currentLine++;
		currentColumn = 0;
		return this;
	}

	/**
	 * Gets the printed result as a string.
	 */
	public String getResult() {
		return out.toString();
	}

	/**
	 * Gets the adapter attached to this printer.
	 */
	public PrinterAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Sets the adapter attached to this printer.
	 */
	public void setAdapter(PrinterAdapter adapter) {
		this.adapter = adapter;
	}

	protected boolean inArgListTail = false;

	/**
	 * Prints a comma-separated list of subtrees.
	 */
	public AbstractTreePrinter printArgList(List<? extends TypeMirror> assignedTypes, List<? extends Tree> args,
			Consumer<Tree> printer) {
		int i = 0;
		for (Tree arg : args) {
			if (printer != null) {
				printer.accept(arg);
			} else {
				if (assignedTypes != null && (arg instanceof ExpressionTree) && i < assignedTypes.size()) {
					if (!substituteAssignedExpression(assignedTypes.get(i++), (ExpressionTree) arg)) {
						print(arg);
					}
				} else {
					print(arg);
				}
			}
			print(", ");
			inArgListTail = true;
		}
		inArgListTail = false;
		if (!args.isEmpty()) {
			removeLastChars(2);
		}
		return this;
	}

	/**
	 * Print an expression being assigned to a type (should handled necessary
	 * wraping/unwraping).
	 * 
	 * @param assignedType the type the expression is being assigned to
	 * @param expression   the assigned expression
	 */
	public AbstractTreePrinter substituteAndPrintAssignedExpression(TypeMirror assignedType,
			ExpressionTree expression) {
		if (!substituteAssignedExpression(assignedType, expression)) {
			print(expression);
		}
		return this;
	}

	/**
	 * Conditionally substitutes an expression when it is assigned to a given type.
	 * 
	 * @param assignedType the type the expression is being assigned to
	 * @param expression   the assigned expression
	 * @return true if the expression what subtituted
	 */
	abstract protected boolean substituteAssignedExpression(TypeMirror assignedType, ExpressionTree expression);

	/**
	 * Prints an invocation argument list, with type assignment.
	 */
	public AbstractTreePrinter printArgList(MethodInvocationTree invocationTree) {

		List<? extends ExpressionTree> arguments = invocationTree.getArguments();

		for (int i = 0; i < arguments.size(); i++) {
			ExpressionTree arg = arguments.get(i);
			TypeMirror methodType = util().getTypeForTree(invocationTree.getMethodSelect(), compilationUnit);
			if (methodType != null) {
				List<? extends TypeMirror> argTypes = ((ExecutableType) methodType).getParameterTypes();
				TypeMirror paramType = i < argTypes.size() ? argTypes.get(i) : argTypes.get(argTypes.size() - 1);
				if (!substituteAssignedExpression(paramType, arg)) {
					print(arg);
				}
			}
			if (i < arguments.size() - 1) {
				print(", ");
			}
		}
		return this;
	}

	public AbstractTreePrinter printArgList(List<? extends TypeMirror> assignedTypes, List<? extends Tree> args) {
		return printArgList(assignedTypes, args, null);
	}

	public abstract AbstractTreePrinter printConstructorArgList(NewClassTree newClass, boolean localClass);

	public abstract AbstractTreePrinter substituteAndPrintType(Tree typeTree);

	/**
	 * Prints a comma-separated list of variable names (no types).
	 */
	public AbstractTreePrinter printVarNameList(List<VariableTree> args) {
		for (VariableTree arg : args) {
			print(arg.getName().toString());
			print(", ");
		}
		if (!args.isEmpty()) {
			removeLastChars(2);
		}
		return this;
	}

	/**
	 * Prints a comma-separated list of type subtrees.
	 */
	public AbstractTreePrinter printTypeArgList(List<? extends Tree> args) {
		for (Tree arg : args) {
			substituteAndPrintType(arg);
			print(", ");
		}
		if (!args.isEmpty()) {
			removeLastChars(2);
		}
		return this;
	}

	/**
	 * Gets the current line of the printed output.
	 */
	public int getCurrentLine() {
		return currentLine;
	}

	/**
	 * Gets the current column of the printed output.
	 */
	public int getCurrentColumn() {
		return currentColumn;
	}

	/**
	 * Gets the current compilation unit.
	 */
	public CompilationUnitTree getCompilationUnit() {
		return compilationUnit;
	}

	/**
	 * Tells if this printer tries to preserve the original line numbers of the Java
	 * input.
	 */
	public boolean isFillSourceMap() {
		return fillSourceMap;
	}

	public String getRootRelativeName(Element symbol) {
		return context.getRootRelativeName(
				context.useModules ? context.getImportedElements(compilationUnit.getSourceFile().getName()) : null,
				symbol);
	}

	public String getRootRelativeName(Element symbol, boolean useJavaNames) {
		return context.getRootRelativeName(
				context.useModules ? context.getImportedElements(compilationUnit.getSourceFile().getName()) : null,
				symbol, useJavaNames);
	}

	public int getIndent() {
		return indent;
	}

}
