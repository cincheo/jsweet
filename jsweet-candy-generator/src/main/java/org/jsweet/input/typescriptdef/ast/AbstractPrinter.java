/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
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
package org.jsweet.input.typescriptdef.ast;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.annotation.Annotation;

import org.jsweet.input.typescriptdef.util.Util;

/**
 * Default abstract implementation for printing an AST.
 * 
 * @author Renaud Pawlak
 */
public abstract class AbstractPrinter extends Scanner {

	private static final String INDENT = "    ";

	private StringBuilder out = new StringBuilder();

	private int indent = 0;

	private int currentLine = 1;

	private int currentColumn = 0;

	public AbstractPrinter(Context context) {
		super(context);
	}

	protected void clearOutput() {
		out = new StringBuilder();
	}

	public AbstractPrinter print(Visitable element) {
		scan(element);
		return this;
	}

	protected String getIndent() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			sb.append(INDENT);
		}
		return sb.toString();
	}

	public AbstractPrinter printIndent() {
		return print(getIndent());
	}

	public AbstractPrinter startIndent() {
		indent++;
		return this;
	}

	public AbstractPrinter endIndent() {
		indent--;
		return this;
	}

	public AbstractPrinter print(CharSequence string) {
		out.append(string);
		currentColumn += string.length();
		return this;
	}

	public AbstractPrinter space() {
		return print(" ");
	}

	public AbstractPrinter removeLastChar() {
		out.deleteCharAt(out.length() - 1);
		currentColumn--;
		return this;
	}

	public AbstractPrinter removeLastChars(int count) {
		if (count > 0) {
			out.delete(out.length() - count, out.length());
		}
		currentColumn -= count;
		return this;
	}

	public AbstractPrinter removeLastIndent() {
		removeLastChars(INDENT.length());
		return this;
	}

	public AbstractPrinter println() {
		out.append("\n");
		currentLine++;
		currentColumn = 0;
		return this;
	}

	public String getResult() {
		return out.toString();
	}

	public int getCurrentLine() {
		return currentLine;
	}

	public int getCurrentColumn() {
		return currentColumn;
	}

	public AbstractPrinter printAnnotations(Declaration declaration) {
		CharSequence annos = annotationsToString(declaration);
		if (!isBlank(annos)) {
			print(annos);
		}
		return this;
	}

	public CharSequence annotationsToString(Declaration declaration) {
		StringBuilder annosDecls = new StringBuilder(); 
		for (Annotation annotation : declaration.getAnnotations()) {
			annosDecls.append(getIndent());
			annosDecls.append(Util.toString(annotation) + "\n");
		}
		if (declaration.getStringAnnotations() != null) {
			for (String annotation : declaration.getStringAnnotations()) {
				annosDecls.append(getIndent());
				annosDecls.append("@" + annotation + "\n");
			}
		}
		
		return annosDecls;
	}
}
