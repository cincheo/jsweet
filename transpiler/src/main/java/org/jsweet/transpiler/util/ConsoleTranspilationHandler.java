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

	/**
	 * Creates a transpilation handled reporting to the console.
	 */
	public ConsoleTranspilationHandler() {
	}

	@Override
	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message) {
		if (sourcePosition == null || sourcePosition.getFile() == null) {
			log(problem.getSeverity(), message);
		} else {
			log(problem.getSeverity(),
					message + " at " + sourcePosition.getFile() + "(" + sourcePosition.getStartLine() + ")");
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
