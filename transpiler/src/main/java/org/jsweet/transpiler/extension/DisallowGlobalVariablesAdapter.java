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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.jsweet.transpiler.JSweetProblem;

/**
 * This simple adapter reports errors when global variables are found.
 * 
 * @author Renaud Pawlak
 */
public class DisallowGlobalVariablesAdapter extends PrinterAdapter {

	/**
	 * Builds an adapter that will delegate to the given parent.
	 */
	public DisallowGlobalVariablesAdapter(PrinterAdapter parentAdapter) {
		super(parentAdapter);
	}

	/**
	 * Reports an error when static non-final variables are found in Globals
	 * classes (except when Globals are part of a def.* package).
	 */
	@Override
	public void afterType(TypeElement type) {
		// we only check for static variables that are in a Globals class but
		// this could be generalized to any static variable
		if (!type.getQualifiedName().toString().startsWith("def.")
				&& type.getSimpleName().toString().equals("Globals")) {
			for (Element member : type.getEnclosedElements()) {
				if (member.getKind() == ElementKind.FIELD) {
					VariableElement field = (VariableElement) member;
					// only non-final static variable have side effect
					if (field.getModifiers().contains(Modifier.STATIC)
							&& !field.getModifiers().contains(Modifier.FINAL)) {
						report(field, JSweetProblem.USER_ERROR, "global variables are not allowed");
					}
				}
			}
		}
		super.afterType(type);
	}

}
