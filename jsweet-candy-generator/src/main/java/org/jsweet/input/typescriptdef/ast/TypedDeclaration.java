package org.jsweet.input.typescriptdef.ast;

public interface TypedDeclaration extends Declaration {

	TypeReference getType();

	void setType(TypeReference type);
	
	
}
