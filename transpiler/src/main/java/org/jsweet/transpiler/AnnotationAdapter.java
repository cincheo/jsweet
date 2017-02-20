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

import com.sun.tools.javac.code.Symbol;

/**
 * This interface has to be implemented to adapt the way annotations are
 * introspected on the Java AST. Adapter should be installed on the JSweet
 * context in order to add or remove annotations.
 * 
 * @see JSweetContext#addAnnotationAdapter(AnnotationAdapter)
 * 
 * @author Renaud Pawlak
 */
public interface AnnotationAdapter {

	/**
	 * The annotation state from an adapter's point of view.
	 */
	public enum AnnotationState {
		/**
		 * Used when an annotation is added by an adapter.
		 */
		ADDED,
		/**
		 * Used when an annotation is not handled by an adapter (the context
		 * will delegate to other adapters or to the default behavior).
		 */
		UNCHANGED,
		/**
		 * Used when an annotation is added by an adapter (it will override the
		 * default behavior or subsequent annotation adapters).
		 */
		REMOVED
	}

	/**
	 * Gets the state of the given annotation type, as defined by this
	 * annotation adapter.
	 * 
	 * @param symbol
	 *            the AST element where to lookup the annotation
	 * @param annotationType
	 *            the looked up annotation type (fully qualified name)
	 * @return the state of this annotation, which states if it is added,
	 *         removed, or unchanged by this annotation adapter
	 */
	AnnotationState getAnnotationState(Symbol symbol, String annotationType);

	/**
	 * Gets the given annotation property value.
	 * 
	 * @param symbol
	 *            the AST element on which the annotation is defined
	 * @param annotationType
	 *            the annotation type (fully qualified name)
	 * @param propertyName
	 *            the annotation's property name (if null, the value property is
	 *            looked up)
	 * @param defaultValue
	 *            the default value to be returned if not found
	 * @return the annotation property value
	 */
	String getAnnotationValue(Symbol symbol, String annotationType, String propertyName, String defaultValue);

}
