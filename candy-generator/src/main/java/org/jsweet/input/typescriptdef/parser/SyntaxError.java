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
package org.jsweet.input.typescriptdef.parser;

import java.io.PrintStream;

import org.jsweet.input.typescriptdef.ast.Token;

public class SyntaxError {

	public Token origin;
	public String message;
	public Exception exception;

	public SyntaxError(Token origin, String message) {
		super();
		this.origin = origin;
		this.message = message;
		this.exception = new Exception();
		//this.exception.printStackTrace();
	}

	@Override
	public String toString() {
		return "SYNTAX ERROR"
				+ ": "
				+ message
				+ (origin != null ? " at '" + origin.toString() + "'" + " "
						+ origin.getLocation() : "");
	}

	public void printStackTrace(PrintStream stream) {
		exception.printStackTrace(stream);
	}
	
}
