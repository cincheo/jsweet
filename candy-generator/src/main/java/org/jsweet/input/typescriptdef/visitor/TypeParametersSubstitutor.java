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
package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterizedElement;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.UnionTypeReference;
import org.jsweet.input.typescriptdef.ast.UnionTypeReference.Selected;

/**
 * @author Renaud Pawlak
 */
public class TypeParametersSubstitutor extends Scanner {

	public TypeParametersSubstitutor(Context context) {
		super(context);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		visitTypeParameterizedElement(typeDeclaration);
		super.visitTypeDeclaration(typeDeclaration);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		visitTypeParameterizedElement(functionDeclaration);
		super.visitFunctionDeclaration(functionDeclaration);
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		new PrimitiveTypeSubstitutor(context).scan(typeReference.getTypeArguments());
		if (typeReference.getTypeArguments() != null) {
			Type t = lookupType(typeReference, null);
			if (t != null && (t instanceof TypeDeclaration)) {
				TypeDeclaration typeDecl = (TypeDeclaration) t;
				if (typeDecl.getTypeParameters() != null
						&& typeDecl.getTypeParameters().length == typeReference.getTypeArguments().length) {
					for (int i = 0; i < typeReference.getTypeArguments().length; i++) {
						if ("any".equals(typeReference.getTypeArguments()[i].getName())) {
							TypeReference bound = typeDecl.getTypeParameters()[i].getUpperBound();
							if (bound != null && bound.getName() != null) {
								typeReference.getTypeArguments()[i].setName(bound.getName());
							}
						}
					}
				}
			}
		}
		super.visitTypeReference(typeReference);
	}

	@Override
	public void visitUnionTypeReference(UnionTypeReference unionTypeReference) {
		if (unionTypeReference.getSelected() == Selected.NONE) {
			new PrimitiveTypeSubstitutor(context).scan(unionTypeReference.getTypes());
		}
		super.visitUnionTypeReference(unionTypeReference);
	}

	private void visitTypeParameterizedElement(TypeParameterizedElement element) {
		if (element.getTypeParameters() == null) {
			return;
		}
		for (TypeParameterDeclaration t : element.getTypeParameters()) {
			if (t.getUpperBound() != null && t.getUpperBound().isArray()) {
				t.setUpperBound(t.getUpperBound().getComponentType());
			}
		}
		new PrimitiveTypeSubstitutor(context).scan(element.getTypeParameters());
	}

	private class PrimitiveTypeSubstitutor extends Scanner {

		public PrimitiveTypeSubstitutor(Context context) {
			super(context);
		}

		@Override
		public void visitTypeReference(TypeReference typeReference) {
			String name = typeReference.getName();
			switch (name) {
			case "boolean":
				name = "Boolean";
				break;
			case "number":
				name = "Double";
				break;
			case "void":
				name = "Void";
			}
			typeReference.setName(name);
			super.visitTypeReference(typeReference);
		}
	}

}
