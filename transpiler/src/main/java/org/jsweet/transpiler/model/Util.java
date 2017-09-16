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
	
	/**
	 * Tells if the given element is part of the transpiled sources.
	 */
	boolean isSourceElement(Element element);

	/**
	 * Gets the source file path if any.
	 * 
	 * @see #isSourceElement(Element)
	 */
	String getSourceFilePath(Element element);

	/**
	 * Gets the relative path that links the two given paths.
	 * 
	 * <pre>
	 * assertEquals("../c", getRelativePath("/a/b", "/a/c"));
	 * assertEquals("..", getRelativePath("/a/b", "/a"));
	 * assertEquals("../e", getRelativePath("/a/b/c", "/a/b/e"));
	 * assertEquals("d", getRelativePath("/a/b/c", "/a/b/c/d"));
	 * assertEquals("d/e", getRelativePath("/a/b/c", "/a/b/c/d/e"));
	 * assertEquals("../../../d/e/f", getRelativePath("/a/b/c", "/d/e/f"));
	 * assertEquals("../..", getRelativePath("/a/b/c", "/a"));
	 * assertEquals("..", getRelativePath("/a/b/c", "/a/b"));
	 * </pre>
	 * 
	 * <p>
	 * Thanks to:
	 * http://mrpmorris.blogspot.com/2007/05/convert-absolute-path-to-relative-
	 * path.html
	 * 
	 * <p>
	 * Bug fix: Renaud Pawlak
	 * 
	 * @param fromPath
	 *            the path to start from
	 * @param toPath
	 *            the path to reach
	 */
	String getRelativePath(String fromPath, String toPath);
	
	
}
