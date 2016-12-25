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
 * This package contains the candies processing implementation.
 * 
 * <p>
 * The candies processor extract the candies found in the classpath. It then
 * merges the bytecode of classes that are declared as mixins. Candies jar files
 * contains the sources (for Javadoc), bytecode (for Java compiling), and the
 * original TypeScript source code for compiling with <code>tsc</code>.
 */
package org.jsweet.transpiler.candies;
