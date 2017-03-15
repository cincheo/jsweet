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

import javax.lang.model.element.ExecutableElement;

/**
 * An AST node for a Java method invocation.
 * 
 * @author Renaud Pawlak
 */
public interface MethodInvocationElement extends InvocationElement {

	/**
	 * The invoked method name.
	 */
	String getMethodName();

	/**
	 * The invoked method as an element, if accessible.
	 */
	ExecutableElement getMethod();

	/**
	 * The target expression of the invocation if any, null otherwise.
	 * 
	 * @return for an invocation such as <code>target.name(..)</code>, returns
	 *         <code>target</code>, and returns null when no target is specified
	 *         (<code>target.name(..)</code>).
	 */
	ExtendedElement getTargetExpression();

}
