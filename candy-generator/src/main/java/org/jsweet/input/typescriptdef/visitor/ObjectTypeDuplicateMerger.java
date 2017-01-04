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

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * Merges explicit Java object types in case several ones have been created.
 * 
 * @author Renaud Pawlak
 */
public class ObjectTypeDuplicateMerger extends Scanner {

	public ObjectTypeDuplicateMerger(Context context) {
		super(context);
	}

	Map<String, TypeDeclaration> duplicatesToReplace;

	@Override
	public void visitCompilationUnit(CompilationUnit compilationUnit) {
		DuplicateCollector collector = new DuplicateCollector(context);
		compilationUnit.accept(collector);
		duplicatesToReplace = collector.replacements;
		if (duplicatesToReplace.isEmpty()) {
			return;
		}

		logger.debug(compilationUnit + " object types replacements: " + duplicatesToReplace);

		super.visitCompilationUnit(compilationUnit);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		String qualifiedName = getCurrentContainerName();
		TypeDeclaration replacement = duplicatesToReplace.get(qualifiedName);
		if (replacement != null) {
			DeclarationContainer container = getParent(DeclarationContainer.class);
			logger.trace("replace " + qualifiedName + " with " + context.getTypeName(replacement) + " in " + container);
			container.removeMember(typeDeclaration);
		} else {
			super.visitTypeDeclaration(typeDeclaration);
		}
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		String qualifiedName = getCurrentContainerName() + "." + typeReference.getName();
		TypeDeclaration replacement = duplicatesToReplace.get(qualifiedName);
		if (replacement == null) {
			replacement = duplicatesToReplace.get(qualifiedName = typeReference.getName());
		}

		if (replacement != null) {
			logger.trace("replace reference " + qualifiedName + " with " + replacement);
			TypedDeclaration parentDeclaration = getParent(TypedDeclaration.class);
			Util.substituteTypeReference(this, parentDeclaration, typeReference,
					new TypeReference(null, replacement, typeReference.getTypeArguments()));
		}
		super.visitTypeReference(typeReference);
	}

	private class DuplicateCollector extends Scanner {
		public DuplicateCollector(Context context) {
			super(context);
		}

		protected Map<String, TypeDeclaration> replacements = new HashMap<>();

		@Override
		public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
			super.visitTypeDeclaration(typeDeclaration);

			// TODO : this only a POC, how to handle duplicates within
			// moduleDeclaration ????
			List<TypeDeclaration> objectTypes = context.generatedObjectTypes.get(typeDeclaration);
			if (objectTypes != null) {
				for (TypeDeclaration objectType : objectTypes) {
					String qualifiedName = getCurrentContainerName() + "." + objectType;
					logger.trace("lookup duplicate of " + qualifiedName);
					TypeDeclaration match = searchTopMostDuplicateInParentHierarchy(typeDeclaration, objectType);
					if (match != null) {
						replacements.put(qualifiedName, match);
						logger.trace("most relevant match: " + match);
					}
				}
			}
		}

		private TypeDeclaration searchTopMostDuplicateInParentHierarchy(TypeDeclaration owner,
				TypeDeclaration objectType) {
			if (objectType.getMembers() == null || objectType.getMembers().length == 0) {
				return null;
			}

			TypeDeclaration match = null;

			List<TypeDeclaration> objectTypeMatches = context.generatedObjectTypes.get(owner);
			if (objectTypeMatches != null) {
				for (TypeDeclaration matchInCurrent : objectTypeMatches) {
					if (matchInCurrent != objectType && areObjectTypesEqual(matchInCurrent, objectType)) {
						match = matchInCurrent;
					}
				}
			}

			TypeReference[] superRefs = ((TypeDeclaration) owner).getSuperTypes();
			if (superRefs != null) {
				for (TypeReference superRef : superRefs) {
					Type superType = lookupType(superRef, context.getTypeModule(((TypeDeclaration) owner)));
					if (superType instanceof TypeDeclaration) {
						TypeDeclaration matchInSuperType = searchTopMostDuplicateInParentHierarchy(
								(TypeDeclaration) superType, objectType);
						if (matchInSuperType != null) {
							match = matchInSuperType;
						}
					}
				}
			}

			return match;
		}

		// TODO : externalize ???
		private boolean areObjectTypesEqual(TypeDeclaration left, TypeDeclaration right) {
			if (!defaultString(left.getName()).equals(defaultString(right.getName()))) {
				return false;
			}

			int leftTypeParametersCount = left.getTypeParameters() == null ? 0 : left.getTypeParameters().length;
			int rightTypeParametersCount = right.getTypeParameters() == null ? 0 : right.getTypeParameters().length;
			if (leftTypeParametersCount != rightTypeParametersCount) {
				return false;
			}

			// TODO : TypeParamDecl equals AND TypedDeclaration equals are not
			// correctly implemented .... (only name is checked)
			if (leftTypeParametersCount > 0
					&& !DeclarationHelper.areDeclarationsEqual(left.getTypeParameters(), right.getTypeParameters())) {
				return false;
			}

			if (left.getMembers() == null || right.getMembers() == null) {
				return false;
			}

			if (left.getMembers().length != right.getMembers().length) {
				return false;
			}

			return DeclarationHelper.areDeclarationsEqual(left.getMembers(), right.getMembers());
		}

	}
}