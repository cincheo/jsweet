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
package org.jsweet.transpiler.extension;

import javax.lang.model.element.Element;

import org.jsweet.transpiler.JSweetContext;

/**
 * This class has to be overridden to adapt the way annotations are introspected
 * on the Java AST. Annotation managers should be installed on the JSweet
 * context in order to add or remove annotations (
 * {@link AnnotationManager#manageAnnotation(Element, String)}) and/or customize
 * the values of their properties.
 * 
 * @see JSweetContext#addAnnotationManager(AnnotationAdapter)
 * 
 * @author Renaud Pawlak
 */
public abstract class AnnotationManager {

	/**
	 * The action to be returned by the manager to add or remove annotations.
	 * 
	 * @see AnnotationManager#manageAnnotation(Element, String)
	 */
	public static enum Action {
		/**
		 * To be returned when a manager adds an annotation.
		 */
		ADD,
		/**
		 * To be returned when a manager does not impact the annotation (the
		 * context will delegate to other managers or to the default behavior).
		 */
		VOID,
		/**
		 * To be returned when a manager removes an annotation (it will override
		 * the default behavior or subsequent annotation managers).
		 */
		REMOVE
	}

	/**
	 * Manages the given annotation type, by returning one of the possible
	 * actions (see {@link Action}).
	 * 
	 * @param element
	 *            the AST element where the annotation shall be managed
	 * @param annotationType
	 *            the managed annotation type (fully qualified name)
	 * @return the action to apply on this annotation, which tells if it is
	 *         added, removed, or unchanged by this annotation manager
	 */
	public abstract Action manageAnnotation(Element element, String annotationType);

	/**
	 * Gets the given annotation property value.
	 * 
	 * <p>
	 * This method has to be overridden when annotations containing properties
	 * are being added by this manager with the
	 * {@link AnnotationManager#manageAnnotation(Element, String)} method, or if
	 * this manager wants to tune the values of existing annotations (less
	 * common scenario).
	 * 
	 * <p>
	 * This method returns null by default, which means that it leaves the job
	 * to other managers or to the default behavior.
	 * 
	 * @param element
	 *            the AST element on which the annotation is defined
	 * @param annotationType
	 *            the annotation type (fully qualified name)
	 * @param propertyName
	 *            the annotation's property name (if null, the value property is
	 *            looked up)
	 * @param propertyClass
	 *            the annotation's property class
	 * @param defaultValue
	 *            the default value to be returned if not found
	 * @return the annotation property value (null if the annotation is not
	 *         managed)
	 */
	public <T extends Object> T getAnnotationValue(Element element, String annotationType, String propertyName,
			Class<T> propertyClass, T defaultValue) {
		return null;
	}

}
