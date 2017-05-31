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
package org.jsweet;

import java.io.File;
import java.util.List;

/**
 * This interface generically defines an already-configured, ready-to-run
 * transpilation task.
 * 
 * @author Renaud Pawlak
 */
public interface TranspilationTask {

	/**
	 * Runs the transpilation task.
	 */
	void run() throws Exception;

	/**
	 * Gets the list of input directories this transpilation task works on.
	 */
	List<File> getInputDirList();

}
