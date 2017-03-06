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
 * This package contains the basic extensions of the JSweet transpiler, as
 * printer adapters.
 * 
 * <p>
 * A printer adapter is an object that can override the JSweet's default
 * printer. The programmer needs to subclass the
 * {@link org.jsweet.transpiler.extension.PrinterAdapter} class and use a
 * factory that creates an instance of this subclass. Printer adapters shall
 * avoid using the javac API in order to rely on public and stable interfaces,
 * such as the {@link javax.lang.model} and {@link org.jsweet.transpiler.model}.
 * 
 * <p>
 * Printer adapters are chainable (decorator pattern) and the default behavior
 * defined in {@link org.jsweet.transpiler.extension.PrinterAdapter} is to
 * delegate to the parent adapter if any. To enhance reuse of adapters,
 * programmers should use delegation rather than subclassing.
 * 
 * @author Renaud Pawlak
 */
package org.jsweet.transpiler.extension;
