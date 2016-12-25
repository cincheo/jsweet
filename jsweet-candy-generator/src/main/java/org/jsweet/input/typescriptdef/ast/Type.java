package org.jsweet.input.typescriptdef.ast;

public interface Type {

	String getName();

	boolean isSubtypeOf(Type type);
	
}
