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

import java.util.ArrayList;
import java.util.HashSet;

import org.jsweet.JSweetDefTranslatorConfig;

/**
 * A function declaration in the TypeScript source code.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class FunctionDeclaration extends AbstractTypedDeclaration implements TypeParameterizedElement {

	public static final String NEW_FUNCTION_RESERVED_NAME = JSweetDefTranslatorConfig.NEW_FUNCTION_NAME;
	public static final String ANONYMOUS_FUNCTION_RESERVED_NAME = JSweetDefTranslatorConfig.ANONYMOUS_FUNCTION_NAME;
	public static final String INDEXSIG_RESERVED_NAME = JSweetDefTranslatorConfig.INDEXED_GET_FUCTION_NAME;

	ParameterDeclaration[] parameters;
	TypeParameterDeclaration[] typeParameters;

	public FunctionDeclaration(Token token, String name, TypeReference type, ParameterDeclaration[] parameters,
			TypeParameterDeclaration[] typeParameters) {
		super(token, name, type);
		this.parameters = parameters;
		this.typeParameters = typeParameters;
	}

	public boolean isConstructor() {
		return "new".equals(name) || "constructor".equals(name) || NEW_FUNCTION_RESERVED_NAME.equals(name);
	}

	public boolean isIndexSignature() {
		return INDEXSIG_RESERVED_NAME.equals(name);
	}

	@Override
	public boolean isAnonymous() {
		return super.isAnonymous() || ANONYMOUS_FUNCTION_RESERVED_NAME.equals(name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("(");
		for (ParameterDeclaration p : parameters) {
			sb.append(p.name);
			sb.append(":");
			sb.append(p.getType().toString());
			sb.append(",");
		}
		if (parameters.length > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public void accept(Visitor v) {
		v.visitFunctionDeclaration(this);
	}

	public ParameterDeclaration[] getParameters() {
		return parameters;
	}

	public void setParameters(ParameterDeclaration[] parameters) {
		this.parameters = parameters;
	}

	@Override
	public TypeParameterDeclaration[] getTypeParameters() {
		return this.typeParameters;
	}

	@Override
	public void setTypeParameters(TypeParameterDeclaration[] typeParameters) {
		this.typeParameters = typeParameters;
	}

	@Override
	public boolean equals(Object o) {
		if (!o.getClass().equals(getClass())) {
			return false;
		}
		FunctionDeclaration fd = (FunctionDeclaration) o;
		String name1 = getName();
		if ("constructor".equals(name1)) {
			name1 = "new";
		}
		String name2 = fd.getName();
		if ("constructor".equals(name2)) {
			name2 = "new";
		}
		if (name2 == null && name1 != null) {
			return false;
		}
		if (!name2.equals(name1)) {
			return false;
		}
		if (fd.parameters.length != parameters.length) {
			return false;
		}
		for (int i = 0; i < fd.parameters.length; i++) {
			if (!parameters[i].getType().equals(fd.parameters[i].getType())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FunctionDeclaration copy() {
		FunctionDeclaration copy = new FunctionDeclaration(this.getToken(), name,
				getType() == null ? null : getType().copy(), DeclarationHelper.copy(parameters),
				DeclarationHelper.copy(typeParameters));
		copy.setDocumentation(getDocumentation());
		copy.setModifiers(this.getModifiers() == null ? null : new HashSet<String>(getModifiers()));
		copy.setStringAnnotations(
				this.getStringAnnotations() == null ? null : new ArrayList<String>(getStringAnnotations()));
		return copy;
	}
}
