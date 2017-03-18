package org.jsweet.transpiler.model;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * This interface defines utilities on the Java model.
 * 
 * @author Renaud Pawlak
 */
public interface Util {

	/**
	 * Gets the qualified name for the given type.
	 */
	String getQualifiedName(TypeMirror type);

	/**
	 * Gets the type from an existing runtime class when possible (return null
	 * when the type cannot be found in the compiler's symbol table).
	 */
	TypeMirror getType(Class<?> clazz);

	/**
	 * Tells if the given type is a number.
	 */
	boolean isNumber(TypeMirror type);

	/**
	 * Tells if the given element is deprecated.
	 */
	boolean isDeprecated(Element element);

	/**
	 * Tells if the given type is a core type.
	 */
	boolean isCoreType(TypeMirror type);

	/**
	 * Tells if the given type is an integral type (int, long, byte).
	 */
	boolean isIntegral(TypeMirror type);
	
}
