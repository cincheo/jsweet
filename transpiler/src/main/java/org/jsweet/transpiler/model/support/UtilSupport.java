package org.jsweet.transpiler.model.support;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.Util;

public class UtilSupport implements Util {

	JSweetContext context;

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
		}
		return null;
	}

}
