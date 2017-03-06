package org.jsweet.transpiler.model;

import javax.lang.model.type.TypeMirror;

/**
 * This interface defines utilities on the Java model.
 * 
 * @author Renaud Pawlak
 */
public interface Util {

	String getQualifiedName(TypeMirror type);

	/**
	 * Gets the type from an existing runtime class when possible (return null
	 * when the type cannot be found in the compiler's symbol table).
	 */
	TypeMirror getType(Class<?> clazz);

}
