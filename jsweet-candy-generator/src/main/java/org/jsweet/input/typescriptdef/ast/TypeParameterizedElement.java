package org.jsweet.input.typescriptdef.ast;

public interface TypeParameterizedElement extends AstNode {

	TypeParameterDeclaration[] getTypeParameters();

	void setTypeParameters(TypeParameterDeclaration[] typeParameters);

}
