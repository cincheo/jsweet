/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.input.typescriptdef.ast;

import org.apache.commons.lang3.StringUtils;

/**
 * A reference to a functional type.
 * 
 * @author Renaud Pawlak
 */
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
