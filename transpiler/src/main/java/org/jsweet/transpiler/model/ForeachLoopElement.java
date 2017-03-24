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
package org.jsweet.transpiler.model;

import javax.lang.model.element.VariableElement;

/**
 * An AST node for a Java for each loop statement, of the form
 * <code>for(var : iterable) body</code>.
 * 
 * @author Renaud Pawlak
 */
public interface ForeachLoopElement extends ExtendedElement {

	/**
	 * The expression returning the iterable (or array) being looped over.
	 */
	ExtendedElement getIterableExpression();

	/**
	 * The iteration local variable.
	 */
	VariableElement getIterationVariable();

	/**
	 * The body of the foreach loop.
	 */
	ExtendedElement getBody();

}
