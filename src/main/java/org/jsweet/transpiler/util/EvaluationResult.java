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
	 * the jsweet.util.Globals.$export function).
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
}
