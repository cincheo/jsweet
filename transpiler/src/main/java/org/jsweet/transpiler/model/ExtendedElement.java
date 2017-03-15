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
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.extension.PrinterAdapter;

/**
 * The root class for AST nodes that represent Java program elements
 * (expressions and statements) that are not accessible through the regular
 * {@link Element} API.
 * 
 * <p>
 * This class wraps Javac tree nodes to allow the JSweet printer adapters to use
 * an API that is independent from the Javac API.
 * 
 * @author Renaud Pawlak
 * @see PrinterAdapter
 */
public interface ExtendedElement {

	/**
	 * Gets the type that corresponds to this element, if any.
	 * 
	 * <p>
	 * Not all elements have a type. For instance, statements such as if, case,
	 * for, and so on do not have a type, while expressions have a type.
	 * 
	 * <p>
	 * To return the corresponding element rather than the type mirror, use
	 * {@link #getTypeAsElement()} instead.
	 * 
	 * see {@link #getTypeAsElement()}
	 */
	TypeMirror getType();

	/**
	 * Gets the standard element that corresponds to the type (if any).
	 * 
	 * @see #getType()
	 */
	Element getTypeAsElement();

	/**
	 * Tells if this extended element is a constant expression.
	 */
	boolean isConstant();

	/**
	 * Tells if this extended element is a string literal.
	 */
	boolean isStringLiteral();

}
