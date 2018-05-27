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

import org.jsweet.transpiler.JSweetTranspiler;

/**
 * An evaluation result of a JSweet program.
 * 
 * @author Renaud Pawlak
 * @see JSweetTranspiler#eval(org.jsweet.transpiler.TranspilationHandler,
 *      org.jsweet.transpiler.SourceFile...)
 * @see JSweetTranspiler#eval(String,
 *      org.jsweet.transpiler.TranspilationHandler,
 *      org.jsweet.transpiler.SourceFile...)
 */
public interface EvaluationResult {
	/**
	 * Get access to the value of an exported variable (exported with a call to
	 * the jsweet.util.Lang.$export function).
	 * 
	 * @param variableName
	 *            the variable to access
	 * @return the value as it was exported during the program execution
	 */
	<T> T get(String variableName);

	/**
	 * Gets the execution trace of the program execution.
	 */
	String getExecutionTrace();
	
	public static final EvaluationResult VOID = new EvaluationResult() {
		
		@Override
		public String getExecutionTrace() {
			return "";
		}
		
		@Override
		public <T> T get(String variableName) {
			return null;
		}
	};
}
