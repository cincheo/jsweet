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

/**
 * A parsing token.
 * 
 * @author Renaud Pawlak
 */
public class Token {

	public int type;

	/**
	 * Creates a new token
	 * 
	 * @param type
	 *            the type as defined in the lexer
	 * @param fileName
	 *            the name of the file from where the token was extracted
	 * @param text
	 *            the text of the token
	 * @param line
	 *            the line number where the token appears in the file
	 * @param charBegin
	 *            the character where it begins in the file
	 * @param charEnd
	 *            the character where it ends in the file
	 */
	public Token(int type, String fileName, String text, int line, int charBegin, int charEnd) {

		this.type = type;
		this.fileName = fileName;
		this.text = text;
		this.line = line;
		this.charBegin = charBegin;
		this.charEnd = charEnd;
	}

	public String getLocation() {
		return "" + fileName + ":" + line + "(" + charBegin + ")";
	}

	String fileName;
	String text;
	int line;
	int charBegin;
	int charEnd;

	// public boolean equals(Object o) {
	// System.err.println("equals("+this+","+o+")");
	// return text.equals(o.toString());
	// }

	public String toString() {
		return text;
	}

	public int getCharBegin() {
		return charBegin;
	}

	public int getCharEnd() {
		return charEnd;
	}

	public int getLine() {
		return line;
	}

	public String getText() {
		return text;
	}

	public String getFileName() {
		return fileName;
	}

	public int getType() {
		return type;
	}

}
