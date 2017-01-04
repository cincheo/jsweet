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

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.input.typescriptdef.TypescriptDef2Java;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * This scanner replaces all references to TypeMacro with the referenced type
 * 
 * @author Louis Grignon
 */
public class TypeMacroReplacer extends Scanner {

	private int referencesCount = 0;

	public TypeMacroReplacer(Context context) {
		super(context);
	}

	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
		String aliasQualifiedName = getCurrentContainerName();
		TypeReference actualType = typeMacroDeclaration.getType();

		logger.debug("replacing type macro " + aliasQualifiedName + " with type " + actualType);

		// type macros need to be looked up first for cross-namespace usages...
		new Scanner(this) {
			public void visitTypeReference(TypeReference typeReference) {
				if (typeReference.isStringType()) {
					return;
				}
				lookupType(typeReference, null, TypescriptDef2Java.generateMissingTypes, true);
				super.visitTypeReference(typeReference);
			}
		}.scan(typeMacroDeclaration.getType());
		TypeReferenceRedirector r = new TypeReferenceRedirector(context, typeMacroDeclaration, aliasQualifiedName);
		r.scan(context.compilationUnits);

		referencesCount++;

		super.visitTypeMacro(typeMacroDeclaration);
	}

	/**
	 * This scanner reset TypeReferences which still reference type copied from
	 * other packages even though this class has been copied
	 */
	// TODO : extract base type reference substitutor
	private static class TypeReferenceRedirector extends Scanner {

		private String aliasQualifiedName;
		TypeMacroDeclaration typeMacroDeclaration;

		public TypeReferenceRedirector(Context context, TypeMacroDeclaration typeMacroDeclaration,
				String aliasQualifiedName) {
			super(context);
			this.typeMacroDeclaration = typeMacroDeclaration;
			this.aliasQualifiedName = aliasQualifiedName;
		}

		@Override
		public void visitTypeReference(TypeReference typeReference) {
			if (isTypeMacro(typeReference)) {
				TypedDeclaration parent = getParent(TypedDeclaration.class);
				logger.trace("subtituting " + typeReference + " with " + typeMacroDeclaration.getType());
				TypeReference ref = typeMacroDeclaration.getType().copy(true);
				if (typeReference.isQualified()) {
					new Scanner(context) {
						public void visitTypeReference(TypeReference typeReference) {
							if (typeReference.getDeclaration() instanceof TypeDeclaration) {
								typeReference
										.setName(context.getTypeName((TypeDeclaration) typeReference.getDeclaration()));
							}
							super.visitTypeReference(typeReference);
						}
					}.scan(ref);
				}
				Util.substituteTypeReference(this, parent, typeReference, ref);
				// substitute macro's type parameters if any...
				Util.findTypeParameters(this.context, typeMacroDeclaration, parent, (typeRef, typeParameter) -> {
					int tparamIndex = ArrayUtils.indexOf(typeMacroDeclaration.getTypeParameters(), typeParameter);
					if (tparamIndex < 0 || tparamIndex >= typeReference.getTypeArguments().length) {
						logger.error("cannot find type parameter corresponding type argument for type macro: " + typeRef
								+ " for " + typeParameter);
						return;
					}
					TypeReference targ = typeReference.getTypeArguments()[tparamIndex];
					if (targ == null) {
						logger.error("type argument is null: " + tparamIndex + " in "
								+ Arrays.asList(typeReference.getTypeArguments()));
						return;
					}
					typeRef.setDeclaration(targ.getDeclaration());
					typeRef.setName(targ.getName());
					typeRef.setTypeArguments(targ.getTypeArguments());
				});
			} else {
				super.visitTypeReference(typeReference);
			}
		}

		private boolean isTypeMacro(TypeReference type) {
			if (type != null && StringUtils.equals(type.getSimpleName(), typeMacroDeclaration.getName())) {
				QualifiedDeclaration<TypeDeclaration> decl = lookupTypeDeclaration(type.getName());
				if (decl != null && (decl.getDeclaration() instanceof TypeMacroDeclaration)
						&& decl.getQualifiedDeclarationName().equals(aliasQualifiedName)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void onScanEnded() {
		if (referencesCount > 0) {
			logger.debug(referencesCount + " type macros redirected.");
			logger.info("===> " + context.findDeclarations(VariableDeclaration.class, "*.fieldWithUnion"));
		}
	}

}
