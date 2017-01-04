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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Default abstract implementation for TypeScript declarations.
 * 
 * @author Renaud Pawlak
 */
public abstract class AbstractDeclaration extends AbstractAstNode implements Declaration {
	protected String name;
	protected String originalName;
	protected String documentation;
	private Set<String> modifiers;
	private Map<Class<? extends Annotation>, Annotation[]> annotationMap;
	private List<String> stringAnnotations;
	protected boolean quotedName = false;

	public AbstractDeclaration(Token token, String name) {
		super(token);
		setName(name);
	}

	@Override
	public Set<String> getModifiers() {
		return modifiers;
	}

	@Override
	public void setModifiers(Set<String> modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		if (name != null && (name.contains("\"") || name.contains("'"))) {
			this.name = name.replace("\"", "").replace("'", "");
			setQuotedName(true);
		} else {
			this.name = name;
		}
	}

	@Override
	public String getDocumentation() {
		return documentation;
	}

	@Override
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	@Override
	public void addModifier(String modifier) {
		if (modifiers == null) {
			modifiers = new HashSet<String>();
		}
		if (modifier.contains(" ")) {
			for (String s : modifier.split(" ")) {
				modifiers.add(s);
			}
		} else {
			modifiers.add(modifier);
		}
	}

	@Override
	public void removeModifier(String modifier) {
		if (modifiers != null) {
			modifiers.remove(modifier);
		}
	}

	@Override
	public boolean hasModifier(String modifier) {
		if (modifiers != null) {
			return modifiers.contains(modifier);
		}
		return false;
	}

	@Override
	public boolean isAnonymous() {
		return name == null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!o.getClass().equals(getClass())) {
			return false;
		}
		Declaration d = (Declaration) o;
		if (d.getName() == null && getName() != null) {
			return false;
		}
		if (!d.getName().equals(getName())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isInputAnnotatedWith(String annotation) {
		return getDocumentation() != null && getDocumentation().contains("@" + annotation);
	}

	@Override
	public void addStringAnnotation(String annotation) {
		if (stringAnnotations == null) {
			stringAnnotations = new ArrayList<String>();
		}
		stringAnnotations.add(annotation);
	}

	@Override
	public void removeStringAnnotation(String annotation) {
		if (stringAnnotations == null) {
			return;
		}
		for (String a : new ArrayList<>(stringAnnotations)) {
			if (a.startsWith(annotation)) {
				stringAnnotations.remove(a);
			}
		}
	}

	@Override
	public List<String> getStringAnnotations() {
		return stringAnnotations;
	}

	@Override
	public boolean hasStringAnnotation(String annotation) {
		if (stringAnnotations == null) {
			return false;
		}
		for (String a : stringAnnotations) {
			if (a.startsWith(annotation)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getStringAnnotation(String annotation) {
		if (stringAnnotations == null) {
			return null;
		}
		for (String a : stringAnnotations) {
			if (a.startsWith(annotation)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public void addAnnotation(Annotation annotation) {
		if (annotationMap == null) {
			annotationMap = new HashMap<Class<? extends Annotation>, Annotation[]>();
		}
		Class<? extends Annotation> annotationClass = annotation.annotationType();
		Annotation[] annotations = annotationMap.get(annotationClass);
		if (annotations == null) {
			annotations = new Annotation[0];
		}
		annotations = ArrayUtils.add(annotations, annotation);
		annotationMap.put(annotationClass, annotations);
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		if (annotationMap == null) {
			return null;
		}
		Annotation[] annotations = annotationMap.get(annotationClass);
		if (annotations != null && annotations.length > 0) {
			@SuppressWarnings("unchecked")
			T annotation = (T) annotations[0];
			return annotation;
		} else {
			return null;
		}
	}

	@Override
	public <T extends Annotation> T removeAnnotation(Class<T> annotationClass) {
		if (annotationMap == null) {
			return null;
		}
		Annotation[] annotations = annotationMap.get(annotationClass);
		if (annotations != null && annotations.length > 0) {
			@SuppressWarnings("unchecked")
			T annotation = (T) annotations[0];

			annotations = ArrayUtils.removeElement(annotations, annotation);
			annotationMap.put(annotationClass, annotations);

			return annotation;
		} else {
			return null;
		}
	}

	@Override
	public Annotation[] getAnnotations() {
		if (annotationMap == null) {
			return new Annotation[0];
		}
		List<Annotation> l = new ArrayList<Annotation>();
		for (Annotation[] annotations : annotationMap.values()) {
			l.addAll(Arrays.asList(annotations));
		}
		return l.toArray(new Annotation[0]);
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return getAnnotations();
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		if (annotationMap == null) {
			return false;
		}
		return annotationMap.containsKey(annotationClass);
	}

	@Override
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		if (annotationMap == null || !annotationMap.containsKey(annotationClass)) {
			@SuppressWarnings("unchecked")
			T[] r = (T[]) new Annotation[0];
			return r;
		}
		@SuppressWarnings("unchecked")
		T[] r = (T[]) annotationMap.get(annotationClass);
		return r;
	}

	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return getAnnotation(annotationClass);
	}

	@Override
	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return getAnnotationsByType(annotationClass);
	}

	@Override
	public void setStringAnnotations(List<String> stringAnnotations) {
		this.stringAnnotations = stringAnnotations;
	}

	@Override
	public boolean isQuotedName() {
		return quotedName;
	}

	@Override
	public void setQuotedName(boolean quotedName) {
		this.quotedName = quotedName;
	}

	@Override
	public String getOriginalName() {
		if (originalName == null) {
			return name;
		}
		return originalName;
	}

	@Override
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

}
