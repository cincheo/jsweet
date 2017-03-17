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

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.TypeChecker;
import org.jsweet.transpiler.extension.PrinterAdapter;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

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

	protected static final String INDENT = "    ";

	private StringBuilder out = new StringBuilder();

	private int indent = 0;

	private PrinterAdapter adapter;

	public TypeChecker typeChecker;

	private int currentLine = 1;

	private int currentColumn = 0;

	private boolean fillSourceMap = true;

	public SourceMap sourceMap = new SourceMap();

	/**
	 * Creates a new printer.
	 * 
	 * @param logHandler
	 *            the handler that reports logs and problems
	 * @param context
	 *            the scanning context
	 * @param compilationUnit
	 *            the source file to be printed
	 * @param adapter
	 *            the printer adapter
	 * @param fillSourceMap
	 *            tells if printer fills the source map
	 */
	public AbstractTreePrinter(TranspilationHandler logHandler, JSweetContext context,
			JCCompilationUnit compilationUnit, PrinterAdapter adapter, boolean fillSourceMap) {
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
	public AbstractTreePrinter print(JCTree tree) {
		scan(tree);
		return this;
	}

	// /**
	// * Print a given AST.
	// */
	// public AbstractTreePrinter print(ExtendedElement element) {
	// scan(element);
	// return this;
	// }

	/**
	 * Enters the given tree (se {@link #scan(JCTree)}.
	 */
	protected void enter(JCTree tree) {
		super.enter(tree);
		positionStack.push(new Position(getCurrentPosition(), currentLine, currentColumn));
		if (compilationUnit != null && tree.pos >= 0 && inSourceMap(tree)) {
			sourceMap.addEntry(new Position(tree.pos, //
					compilationUnit.lineMap.getLineNumber(tree.pos), //
					compilationUnit.lineMap.getColumnNumber(tree.pos)), positionStack.peek());
		}
	}

	private boolean inSourceMap(JCTree tree) {
		return (tree instanceof JCExpression || tree instanceof JCStatement || tree instanceof JCMethodDecl);
	}

	@Override
	protected void onRollbacked(JCTree target) {
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
		JCTree tree = stack.peek();
		if (compilationUnit != null && tree instanceof JCBlock) {
			int endPos = tree.getEndPosition(diagnosticSource.getEndPosTable());
			sourceMap.addEntry(
					new Position(endPos, //
							compilationUnit.lineMap.getLineNumber(endPos), //
							compilationUnit.lineMap.getColumnNumber(endPos)),
					new Position(getCurrentPosition(), currentLine, currentColumn));
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
	 * Outputs an identifier.
	 */
	public AbstractTreePrinter printIdentifier(Symbol symbol) {
		String adaptedIdentifier = getAdapter().getIdentifier(symbol);
		return print(adaptedIdentifier);
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
	public AbstractTreePrinter printArgList(List<? extends JCTree> args, Consumer<JCTree> printer) {
		for (JCTree arg : args) {
			if (printer != null) {
				printer.accept(arg);
			} else {
				print(arg);
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
	 * Prints an invocation argument list, with type assignment.
	 */
	public AbstractTreePrinter printArgList(JCMethodInvocation inv) {
		for (int i = 0; i < inv.args.size(); i++) {
			JCExpression arg = inv.args.get(i);
			if (inv.meth.type != null) {
				List<Type> argTypes = ((MethodType) inv.meth.type).argtypes;
				Type paramType = i < argTypes.size() ? argTypes.get(i) : argTypes.get(argTypes.size() - 1);
				if (!getAdapter().substituteAssignedExpression(paramType, arg)) {
					print(arg);
				}
			}
			if (i < inv.args.size() - 1) {
				print(", ");
			}
		}
		return this;
	}

	public AbstractTreePrinter printArgList(List<? extends JCTree> args) {
		return printArgList(args, null);
	}

	public abstract AbstractTreePrinter printConstructorArgList(JCNewClass newClass, boolean localClass);

	protected abstract AbstractTreePrinter substituteAndPrintType(JCTree typeTree);

	/**
	 * Prints a comma-separated list of variable names (no types).
	 */
	public AbstractTreePrinter printVarNameList(List<JCVariableDecl> args) {
		for (JCVariableDecl arg : args) {
			print(arg.name.toString());
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
	public AbstractTreePrinter printTypeArgList(List<? extends JCTree> args) {
		for (JCTree arg : args) {
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
	public JCCompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	/**
	 * Tells if this printer tries to preserve the original line numbers of the
	 * Java input.
	 */
	public boolean isFillSourceMap() {
		return fillSourceMap;
	}

	public String getRootRelativeName(Symbol symbol) {
		return context.getRootRelativeName(
				context.useModules ? context.getImportedElements(compilationUnit.getSourceFile().getName()) : null,
				symbol);
	}

	public String getRootRelativeName(Symbol symbol, boolean useJavaNames) {
		return context.getRootRelativeName(
				context.useModules ? context.getImportedElements(compilationUnit.getSourceFile().getName()) : null,
				symbol, useJavaNames);
	}

	public int getIndent() {
		return indent;
	}

}
