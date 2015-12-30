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
package org.jsweet.transpiler;

import java.io.File;

import org.apache.log4j.Logger;

import com.sun.tools.javac.tree.JCTree;

/**
 * Objects implementing this interface handle transpilation errors and warnings.
 * 
 * @author Renaud Pawlak
 */
public interface TranspilationHandler {

	Logger OUTPUT_LOGGER = Logger.getLogger("output");

	/**
	 * A position in a source file.
	 * 
	 * @author Renaud Pawlak
	 */
	static class SourcePosition {
		public SourcePosition(File file, JCTree sourceElement, int startPosition, int endPosition, int startLine, int startColumn, int endLine, int endColumn) {
			super();
			this.file = file;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
			this.sourceElement = sourceElement;
		}

		private File file;
		private int startPosition;
		private int endPosition;
		private int startLine;
		private int startColumn;
		private int endLine;
		private int endColumn;
		private JCTree sourceElement;

		public File getFile() {
			return file;
		}

		public int getStartPosition() {
			return startPosition;
		}

		public int getEndPosition() {
			return endPosition;
		}

		public int getStartLine() {
			return startLine;
		}

		public int getStartColumn() {
			return startColumn;
		}

		public int getEndLine() {
			return endLine;
		}

		public int getEndColumn() {
			return endColumn;
		}

		public JCTree getSourceElement() {
			return sourceElement;
		}

		@Override
		public String toString() {
			return "" + file + "(" + getStartLine() + "," + getStartColumn() + ")";
		}
	}

	/**
	 * This method is called by the transpiler when a problem needs to be
	 * reported.
	 * 
	 * @param problem
	 *            the reported problem
	 * @param sourcePosition
	 *            the position in the source file
	 * @param message
	 *            the reported message
	 */
	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message);

	/**
	 * This method is invoked when the tranpilation process ends.
	 * 
	 * @param transpiler
	 *            the transpiler that generates this event
	 * @param fullPass
	 *            true for a full transpilation (i.e. a non-watch mode
	 *            transpilation or first pass of a watch mode), false for an
	 *            incremental transpilation in the watch mode
	 * @param files
	 *            the files that were transpiled (can be different from
	 *            <code>transpiler.getWatchedFiles()</code> in a non-full pass)
	 */
	public void onCompleted(JSweetTranspiler transpiler, boolean fullPass, SourceFile[] files);

}
