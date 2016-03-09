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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.TypeChecker;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

/**
 * A tree printer is a kind of tree scanner specialized in pretty printing the
 * scanned AST of a compilation unit (source file).
 * 
 * @author Renaud Pawlak
 */
public abstract class AbstractTreePrinter extends AbstractTreeScanner {

	private static final Logger logger = Logger.getLogger(AbstractTreePrinter.class);

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
	 * This method must be overridden to define the printer output files
	 * extension.
	 */
	public abstract String getTargetFilesExtension();

	private static final String INDENT = "    ";

	private StringBuilder out = new StringBuilder();

	private int indent = 0;

	private AbstractPrinterAdapter adapter;

	public TypeChecker typeChecker;

	private int currentLine = 1;

	private int currentColumn = 0;

	private boolean preserveSourceLineNumbers = true;

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
	 * @param preserveSourceLineNumbers
	 *            tells if the output source code should try to preserve the
	 *            line numbers of the original Java code
	 */
	public AbstractTreePrinter(TranspilationHandler logHandler, JSweetContext context, JCCompilationUnit compilationUnit, AbstractPrinterAdapter adapter,
			boolean preserveSourceLineNumbers) {
		super(logHandler, context, compilationUnit);
		this.typeChecker = new TypeChecker(this);
		this.adapter = adapter;
		this.adapter.setPrinter(this);
		this.preserveSourceLineNumbers = preserveSourceLineNumbers;
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

	/**
	 * Enters the given tree (se {@link #scan(JCTree)}.
	 */
	protected void enter(JCTree tree) {
		super.enter(tree);
		if (this.preserveSourceLineNumbers && !stack.isEmpty()) {
			int line = compilationUnit.lineMap.getLineNumber(stack.peek().pos);
			// adjusting line...
			while (currentLine < line) {
				out.append("\n");
				currentColumn = 0;
				currentLine++;
			}
			while (currentLine != 1 && currentLine > line && out.charAt(out.length() - 1) == '\n') {
				out.deleteCharAt(out.length() - 1);
				currentColumn = 0;
				currentLine--;
			}
			if (currentLine != line) {
				logger.warn("cannot adjust line for: " + tree.getClass() + " at line " + line);
			}
			// adjusting columns... (TODO: does not work)
			// int column =
			// compilationUnit.lineMap.getColumnNumber(stack.peek().pos);
			// while (currentColumn < column) {
			// // System.out.println("adding a column on "+tree.getClass());
			// out.append(" ");
			// currentColumn++;
			// }
			// while (currentColumn > column
			// && ((currentColumn == 1 && out.charAt(out.length() - 1) == ' ')
			// || (currentColumn > 1 && out.charAt(out.length() - 2) == ' '))) {
			// out.deleteCharAt(out.length() - 1);
			// currentColumn--;
			// }
			// if (currentColumn != column) {
			// System.out.println("cannot adjust column for: " + tree.getClass()
			// + " at position " + line + ", " + column + " - " + (currentColumn
			// - column));
			// }
		}
		positionStack.push(new Position(getCurrentPosition(), currentLine, currentColumn));
		sourceMap.addEntry(new Position(tree.pos, //
				compilationUnit.lineMap.getLineNumber(tree.pos), //
				compilationUnit.lineMap.getColumnNumber(tree.pos)), positionStack.peek());
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
	 * Prints an indentation for the current indentation value.
	 */
	public AbstractTreePrinter printIndent() {
		for (int i = 0; i < indent; i++) {
			out.append(INDENT);
		}
		return this;
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
	 * Outputs a string.
	 */
	public AbstractTreePrinter print(String string) {
		out.append(string);
		currentColumn += string.length();
		return this;
	}

	/**
	 * Outputs an identifier.
	 */
	public AbstractTreePrinter printIdentifier(String identifier) {
		String adaptedIdentifier = getAdapter().getIdentifier(identifier);
		out.append(adaptedIdentifier);
		currentColumn += adaptedIdentifier.length();
		return this;
	}

	/**
	 * Adds a space to the output.
	 */
	public AbstractTreePrinter space() {
		print(" ");
		return this;
	}

	/**
	 * Removes the last output character.
	 */
	public AbstractTreePrinter removeLastChar() {
		out.deleteCharAt(out.length() - 1);
		currentColumn--;
		return this;
	}

	/**
	 * Removes the last output characters.
	 */
	public AbstractTreePrinter removeLastChars(int count) {
		if (count > 0) {
			out.delete(out.length() - count, out.length());
		}
		currentColumn -= count;
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
		if (this.preserveSourceLineNumbers) {
			out.append(" ");
			currentColumn++;
			return this;
		}
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
	public AbstractPrinterAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Sets the adapter attached to this printer.
	 */
	public void setAdapter(AbstractPrinterAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Print a comma-separated list of subtrees.
	 */
	public AbstractTreePrinter printArgList(List<? extends JCTree> args) {
		for (JCTree arg : args) {
			print(arg);
			print(", ");
		}
		if (!args.isEmpty()) {
			removeLastChars(2);
		}
		return this;
	}

	/**
	 * Print a comma-separated list of type subtrees.
	 */
	public AbstractTreePrinter printTypeArgList(List<? extends JCTree> args) {
		for (JCTree arg : args) {
			getAdapter().substituteAndPrintType(arg);
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
	public boolean isPreserveLineNumbers() {
		return preserveSourceLineNumbers;
	}

	/**
	 * Gets the Javadoc attached to the given element if any.
	 */
	protected String getJavaDoc(JCTree element) {
		String javaDoc = compilationUnit.docComments.getCommentText(element);
		return javaDoc;
	}

	/**
	 * Prints the Javadoc attached to the given element if any.
	 */
	protected AbstractTreePrinter printJavaDoc(JCTree element) {
		String doc = getJavaDoc(element);
		if (!isBlank(doc)) {
			print("/**");
			println();
			print(" * ");
			print(join(doc.split("\n"), "\n * "));
			println();
			print(" */");
			println();
		}

		return this;
	}
}
