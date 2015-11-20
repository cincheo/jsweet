/* Copyright 2015 CINCHEO SAS
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
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.TranspilationHandler;

/**
 * An error count decorator for a transpilation handler.
 * 
 * @author Renaud Pawlak
 */
public class ErrorCountTranspilationHandler implements TranspilationHandler {

	private TranspilationHandler delegate;
	private int errorCount = 0;
	private int warningCount = 0;
	private int problemCount = 0;

	/**
	 * Decorates the given transpilation handler.
	 */
	public ErrorCountTranspilationHandler(TranspilationHandler delegate) {
		this.delegate = delegate;
	}

	/**
	 * Count the problems and delegates to the decorated transpilation handler.
	 */
	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message) {
		switch (problem.getSeverity()) {
		case ERROR:
			problemCount++;
			errorCount++;
			break;
		case WARNING:
			problemCount++;
			warningCount++;
			break;
		default:
			problemCount++;
		}
		delegate.report(problem, sourcePosition, message);
	}

	/**
	 * Returns the error count.
	 */
	public int getErrorCount() {
		return errorCount;
	}

	@Override
	public void onCompleted(JSweetTranspiler transpiler, boolean fullPass, SourceFile[] files) {
		delegate.onCompleted(transpiler, fullPass, files);
	}

	/**
	 * Returns the warning count.
	 */
	public int getWarningCount() {
		return warningCount;
	}

	/**
	 * Returns the problem count (error + warning count).
	 */
	public int getProblemCount() {
		return problemCount;
	}

	@Override
	public void reportSilentError() {
		errorCount++;
		problemCount++;
		delegate.reportSilentError();
	}

}
