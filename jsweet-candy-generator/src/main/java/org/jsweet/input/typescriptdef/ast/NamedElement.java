package org.jsweet.input.typescriptdef.ast;

public interface NamedElement {

	boolean isAnonymous();

	String getName();

	void setName(String name);

	String getOriginalName();

	void setOriginalName(String name);

}
