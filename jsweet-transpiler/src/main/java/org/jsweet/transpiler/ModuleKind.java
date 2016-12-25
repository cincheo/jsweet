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

/**
 * An enumeration of the support module kinds.
 * 
 * @author Renaud Pawlak
 * @see JSweetTranspiler#setModuleKind(ModuleKind)
 */
public enum ModuleKind {
	/**
	 * The generated code is flat and does not use modules.
	 */
	none,
	/**
	 * The generated code uses <code>commonjs</code> module kind, which can run
	 * under <code>node</code> and be bundled for running in a Web browser.
	 * 
	 * @see JSweetTranspiler#setModuleKind(ModuleKind)
	 */
	commonjs,
	/**
	 * The generated code uses the <code>amd</code> module kind. This can run on
	 * <code>require.js</code> enabled Web browsers.
	 */
	amd,
	/**
	 * The generated code uses the <code>system</code> module kind.
	 */
	system,
	/**
	 * The generated code uses the <code>umd</code> (EcmaScript 6 Universal
	 * Module Definition) module kind.
	 */
	umd
}
