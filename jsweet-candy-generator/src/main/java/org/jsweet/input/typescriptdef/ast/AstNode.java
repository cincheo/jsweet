package org.jsweet.input.typescriptdef.ast;

public interface AstNode extends Visitable {

	static String toString2() {
		return null;
	}

	/**
	 * Returns the corresponding parser token.
	 */
	Token getToken();

	/**
	 * Returns the location in the parsed file.
	 */
	String getLocation();
	
	// String toString();

	boolean equals(Object object);

}
