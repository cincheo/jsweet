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

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * An AST node for a Java variable/field access (of the form
 * <code>targetExpression.variableName</code> or <code>variableName</code>).
 * 
 * @author Renaud Pawlak
 */
public interface VariableAccessElement extends ExtendedElement {

	/**
	 * Gets the target expression of the access (the part before the dot), if
	 * any.
	 * 
	 * @return the part before the dot
	 */
	ExtendedElement getTargetExpression();

	/**
	 * Gets the name of the accessed variable.
	 */
	String getVariableName();

	/**
	 * Returns the target element holding the variable declaration (can be a
	 * type or an executable in case of a local variable).
	 */
	Element getTargetElement();

	/**
	 * Gets the accessed variable element .
	 */
	VariableElement getVariable();

}
