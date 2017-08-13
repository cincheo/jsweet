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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.jsweet.transpiler.util.Util;

/**
 * This simple adapter renames non-public members by adding two underscores as a
 * prefix.
 * 
 * <p>
 * Note that this could be dangerous to use for protected fields if wanting to
 * access them from subclasses declared in other JSweet projects.
 * 
 * @author Renaud Pawlak
 */

public class AddPrefixToNonPublicMembersAdapter extends PrinterAdapter {

	/**
	 * Builds the adapter and delegate to the given parent.
	 * 
	 * <p>
	 * This adapter just uses an annotation manager that will install
	 * <code>@Name</code> annotations when the fields are not public.
	 */
	public AddPrefixToNonPublicMembersAdapter(PrinterAdapter parentAdapter) {
		super(parentAdapter);
		addAnnotationManager(new AnnotationManager() {

			@Override
			public Action manageAnnotation(Element element, String annotationType) {
				return "jsweet.lang.Name".equals(annotationType) && isNonPublicMember(element) ? Action.ADD
						: Action.VOID;
			}

			@Override
			public <T> T getAnnotationValue(Element element, String annotationType, String propertyName,
					Class<T> propertyClass, T defaultValue) {
				if ("jsweet.lang.Name".equals(annotationType) && isNonPublicMember(element)) {
					return propertyClass.cast("__" + element.getSimpleName());
				} else {
					return null;
				}
			}

			private boolean isNonPublicMember(Element element) {
				return (element instanceof VariableElement || element instanceof ExecutableElement)
						&& element.getEnclosingElement() instanceof TypeElement
						&& !element.getModifiers().contains(Modifier.PUBLIC) && Util.isSourceElement(element);

			}
		});
	}

}
