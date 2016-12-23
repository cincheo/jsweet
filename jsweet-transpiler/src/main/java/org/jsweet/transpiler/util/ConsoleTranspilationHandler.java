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

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.Severity;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.SourcePosition;
import org.jsweet.transpiler.TranspilationHandler;

/**
 * This is a simple transpilation handler that reports problems to the default
 * output streams.
 * 
 * @author Renaud Pawlak
 */
public class ConsoleTranspilationHandler implements TranspilationHandler {

	@Override
	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message) {
		if (sourcePosition == null || sourcePosition.getFile() == null) {
			log(problem.getSeverity(), message);
		} else {
			log(problem.getSeverity(), message + " at " + sourcePosition.getFile() + "(" + sourcePosition.getStartLine() + ")");
		}
	}

	private void log(Severity severity, String message) {
		switch (severity) {
		case ERROR:
			OUTPUT_LOGGER.error(message);
			break;
		case WARNING:
			OUTPUT_LOGGER.warn(message);
			break;
		case MESSAGE:
			OUTPUT_LOGGER.info(message);
			break;
		}
	}
	
	@Override
	public void onCompleted(JSweetTranspiler transpiler, boolean fullPass, SourceFile[] files) {
	}
	
}
