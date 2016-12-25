/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
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
package org.jsweet.input.typescriptdef.ast;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Set;

/**
 * A common interface for TypeScript declarations.
 * 
 * @author Renaud Pawlak
 */
public interface Declaration extends AstNode, NamedElement, AnnotatedElement {

	String getDocumentation();

	void setDocumentation(String documentation);

	void addModifier(String modifier);

	void removeModifier(String modifier);

	boolean hasModifier(String modifier);

	Set<String> getModifiers();

	void setModifiers(Set<String> modifiers);

	Declaration copy();

	boolean isInputAnnotatedWith(String annotation);

	void addAnnotation(Annotation annotation);

	<T extends Annotation> T removeAnnotation(Class<T> annotationClass);

	void addStringAnnotation(String annotation);

	void removeStringAnnotation(String annotation);

	List<String> getStringAnnotations();

	void setStringAnnotations(List<String> stringAnnotations);

	boolean hasStringAnnotation(String annotation);

	String getStringAnnotation(String annotation);

	boolean isQuotedName();

	void setQuotedName(boolean quotedName);
	
}
