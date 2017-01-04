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
package org.jsweet.transpiler;

import org.apache.log4j.Logger;

/**
 * Objects implementing this interface handle transpilation errors and warnings.
 * 
 * @author Renaud Pawlak
 */
public interface TranspilationHandler {

	Logger OUTPUT_LOGGER = Logger.getLogger("output");

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