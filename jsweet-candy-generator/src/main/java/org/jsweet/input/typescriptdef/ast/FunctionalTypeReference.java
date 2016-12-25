package org.jsweet.input.typescriptdef.ast;

import org.apache.commons.lang3.StringUtils;

public class FunctionalTypeReference extends TypeReference implements TypeParameterizedElement {

	private ParameterDeclaration[] parameters;
	private TypeReference returnType;
	private boolean constructor;
	private TypeParameterDeclaration[] typeParameters;

	public FunctionalTypeReference(Token token, TypeReference returnType, ParameterDeclaration[] parameters,
			TypeParameterDeclaration[] typeParameters) {
		this(token, false, returnType, parameters, typeParameters);
	}

	public FunctionalTypeReference(Token token, boolean constructor, TypeReference returnType,
			ParameterDeclaration[] parameters, TypeParameterDeclaration[] typeParameters) {
		super(token, (String) null, null);
		this.parameters = parameters;
		this.returnType = returnType;
		this.constructor = constructor;
		this.typeParameters = typeParameters;
	}

	@Override
	public String toString() {
		return StringUtils.join(parameters, ",") + "=>" + returnType;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitFunctionalTypeReference(this);
	}

	public ParameterDeclaration[] getParameters() {
		return parameters;
	}

	public void setParameters(ParameterDeclaration[] parameters) {
		this.parameters = parameters;
	}

	public TypeReference getReturnType() {
		return returnType;
	}

	public void setReturnType(TypeReference returnType) {
		this.returnType = returnType;
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		FunctionalTypeReference ft = (FunctionalTypeReference) obj;
		if (!getReturnType().equals(ft.getReturnType())) {
			return false;
		}
		if (parameters.length != ft.parameters.length) {
			return false;
		}
		for (int i = 0; i < ft.parameters.length; i++) {
			if (!parameters[i].getType().equals(ft.parameters[i].getType())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FunctionalTypeReference copy(boolean copyDeclarations) {
		FunctionalTypeReference copy = new FunctionalTypeReference(null, isConstructor(),
				getReturnType().copy(copyDeclarations), DeclarationHelper.copy(getParameters()),
				DeclarationHelper.copy(getTypeParameters()));
		return copy;
	}

	@Override
	public FunctionalTypeReference copy() {
		return copy(false);
	}

	public boolean isConstructor() {
		return constructor;
	}

	public void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}

	@Override
	public TypeParameterDeclaration[] getTypeParameters() {
		return typeParameters;
	}

	@Override
	public void setTypeParameters(TypeParameterDeclaration[] typeParameters) {
		this.typeParameters = typeParameters;
	}
}
