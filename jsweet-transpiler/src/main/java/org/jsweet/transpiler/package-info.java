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
/**
 * This package contains the JSweet Java to JavaScript transpiler
 * implementation.
 * 
 * <p>
 * The entry point is {@link org.jsweet.transpiler.JSweetTranspiler} with the
 * {@link org.jsweet.transpiler.JSweetTranspiler#transpile(TranspilationHandler, SourceFile...)}
 * function.
 * 
 * <p>
 * The JSweet transpiler transpiles to TypeScript and uses <code>tsc</code> to
 * transpile to JavaScript.
 * 
 * @author Renaud Pawlak
 */
package org.jsweet.transpiler;
