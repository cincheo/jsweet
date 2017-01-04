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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * @author Renaud Pawlak
 */
public class SuperTypesMerger extends Scanner {

	public SuperTypesMerger(Context context) {
		super(context);
	}

	private int injectedTypeCount = 0;

	private class TypeParameterSubstitutor extends Scanner {

		Map<String, String> typeArgs;

		public TypeParameterSubstitutor(Context context, Map<String, String> typeArgs) {
			super(context);
			this.typeArgs = typeArgs;
		}

		@Override
		public void visitTypeReference(TypeReference typeReference) {
			if (typeArgs.containsKey(typeReference.getName())) {
				typeReference.setName(typeArgs.get(typeReference.getName()));
			}
			super.visitTypeReference(typeReference);
		}

	}

	private void injectSuperTypes(TypeDeclaration target) {
		if (target.getSuperTypes() != null && target.getSuperTypes().length > 1) {
			TypeReference[] superTypes = ArrayUtils.subarray(target.getSuperTypes(), 1, target.getSuperTypes().length);
			for (TypeReference superType : superTypes) {
				TypeDeclaration source = (TypeDeclaration) lookupType(superType, null);
				if (source != null) {
					if (target == source) {
						// this can happen when lower-casing module names
						// (classes get merged)
						// this is a sort of potential bug, so we should support
						// it at some point
						// example: Express interfaces
						context.reportWarning("ignore injecting supertype that resolves to the same target type: "
								+ context.getTypeName(target));
						continue;
					}
					injectSuperTypes(source);
					target.addMergedSuperType(superType);
					injectedTypeCount++;
					if (context.verbose) {
						logger.debug("injecting supertype " + superType + " into " + target);
					}
					Map<String, String> typeArgs = new HashMap<String, String>();
					if (source.getTypeParameters() != null) {
						for (int i = 0; i < source.getTypeParameters().length; i++) {
							Type t = null;
							if (superType.getTypeArguments() != null && i < superType.getTypeArguments().length) {
								t = lookupType(superType.getTypeArguments()[i], null);
							}
							typeArgs.put(source.getTypeParameters()[i].getName(),
									t == null ? "java.lang.Object" : t.getName());
						}
					}
					for (Declaration declaration : source.getMembers()) {
						if (declaration.isAnonymous()
								|| declaration.isHidden()
								|| ((declaration instanceof FunctionDeclaration) && ((FunctionDeclaration) declaration)
										.isConstructor())) {
							continue;
						}
						if ((declaration instanceof VariableDeclaration)
								&& target.findVariable(declaration.getName()) != null) {
							continue;
						}
						Declaration d = declaration.copy();
						target.addMember(d);
						new TypeParameterSubstitutor(context, typeArgs).scan(d);
					}
				}
			}
			target.setSuperTypes(new TypeReference[] { target.getSuperTypes()[0] });
		}
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		injectSuperTypes(typeDeclaration);
	}

	@Override
	public void visitTypeParameterDeclaration(TypeParameterDeclaration typeParameterDeclaration) {
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	@Override
	public void onScanEnded() {
		if (injectedTypeCount > 0) {
			logger.debug(injectedTypeCount + " supertype(s) injected.");
		}
	}

}
