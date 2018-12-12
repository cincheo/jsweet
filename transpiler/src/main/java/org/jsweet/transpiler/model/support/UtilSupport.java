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
package org.jsweet.transpiler.model.support;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.Util;

import com.sun.tools.javac.code.Type;

/**
 * See {@link Util}.
 * 
 * @author Renaud Pawlak
 */
public class UtilSupport implements Util {

	protected JSweetContext context;

	public UtilSupport(JSweetContext context) {
		this.context = context;
	}

	@Override
	public String getQualifiedName(TypeMirror type) {
		if (type instanceof DeclaredType) {
			Element e = ((DeclaredType) type).asElement();
			if (e instanceof TypeElement) {
				return ((TypeElement) e).getQualifiedName().toString();
			}
		}
		return type.toString();
	}

	@Override
	public TypeMirror getType(Class<?> clazz) {
		switch (clazz.getName()) {
		case "java.lang.annotation.Annotation":
			return context.symtab.annotationType;
		case "java.lang.AssertionError":
			return context.symtab.assertionErrorType;
		case "java.lang.ClassLoader":
			return context.symtab.classLoaderType;
		case "java.lang.AutoCloseable":
			return context.symtab.autoCloseableType;
		case "java.lang.ClassNotFoundException":
			return context.symtab.classNotFoundExceptionType;
		case "java.util.Collections":
			return context.symtab.collectionsType;
		case "java.lang.Comparable":
			return context.symtab.comparableType;
		case "java.lang.IllegalArgumentException":
			return context.symtab.illegalArgumentExceptionType;
		case "java.lang.Error":
			return context.symtab.errorType;
		case "java.lang.StringBuffer":
			return context.symtab.stringBufferType;
		case "java.lang.StringBuilder":
			return context.symtab.stringBuilderType;
		case "java.lang.String":
			return context.symtab.stringType;
		case "java.lang.InterruptedException":
			return context.symtab.interruptedExceptionType;
		case "java.lang.Iterable":
			return context.symtab.iterableType;
		case "java.util.Iterator":
			return context.symtab.iteratorType;
		case "java.lang.RuntimeException":
			return context.symtab.runtimeExceptionType;
		case "java.lang.NoClassDefFoundError":
			return context.symtab.noClassDefFoundErrorType;
		case "java.lang.NoSuchFieldError":
			return context.symtab.noSuchFieldErrorType;
		case "java.lang.Throwable":
			return context.symtab.throwableType;
		case "java.util.List":
			return context.symtab.listType;
		}
		return null;
	}

	@Override
	public boolean isNumber(TypeMirror type) {
		return org.jsweet.transpiler.util.Util.isNumber(type);
	}

	@Override
	public boolean isDeprecated(Element element) {
		return org.jsweet.transpiler.util.Util.isDeprecated(element);
	}

	@Override
	public boolean isCoreType(TypeMirror type) {
		return org.jsweet.transpiler.util.Util.isCoreType(type);
	}

	@Override
	public boolean isIntegral(TypeMirror type) {
		return org.jsweet.transpiler.util.Util.isIntegral((Type) type);
	}

	@Override
	public boolean isSourceElement(Element element) {
		return org.jsweet.transpiler.util.Util.isSourceElement(element);
	}

	@Override
	public String getSourceFilePath(Element element) {
		return org.jsweet.transpiler.util.Util.getSourceFilePath(element);
	}

	@Override
	public String getRelativePath(String fromPath, String toPath) {
		return org.jsweet.transpiler.util.Util.getRelativePath(fromPath, toPath);
	}
	
}
