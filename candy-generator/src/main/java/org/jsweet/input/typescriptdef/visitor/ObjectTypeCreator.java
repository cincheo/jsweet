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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * Creates explicit Java object types from implicit TypeScript ones.
 * 
 * @author Renaud Pawlak
 */
public class ObjectTypeCreator extends Scanner {

	public ObjectTypeCreator(Context context) {
		super(context);
	}

	private class NameConflictFinder extends Scanner {
		private String name;
		public boolean conflict = false;

		public NameConflictFinder(Context context, String name) {
			super(context);
			this.name = name;
		}

		@Override
		public void visitTypeReference(TypeReference typeReference) {
			if (name.equals(typeReference.getName())) {
				conflict = true;
			}
			super.visitTypeReference(typeReference);
		}

		@Override
		public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
			if (name.equals(typeMacroDeclaration.getName())) {
				conflict = true;
			}
			super.visitTypeMacro(typeMacroDeclaration);
		}
	};

	private static String[] suffixes = { "Object", "Data", "Dto", "Structure", "Info" };

	private static String getSuffix(int i) {
		if (i < suffixes.length) {
			return suffixes[i];
		} else {
			return "" + (i + 1 - suffixes.length);
		}
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		if (typeReference.isObjectType()) {
			DeclarationContainer container = getParent(t -> {
				return (t instanceof DeclarationContainer) && !(t instanceof TypeMacroDeclaration);
			});
			TypedDeclaration parentDeclaration = getParent(TypedDeclaration.class);
			if (typeReference.getObjectType().getMembers().length == 0) {
				Util.substituteTypeReference(this, parentDeclaration, typeReference,
						new TypeReference(null, Object.class.getName(), null));
			} else {

				String rootName = StringUtils.capitalize(parentDeclaration.getName());
				String name = rootName;
				int i = 1;

				TypeParameterDeclaration[] typeParameters = Util.findTypeParameters(this, typeReference);
				for (TypeParameterDeclaration p : typeParameters) {
					if (Util.containsAstNode(typeReference, p)) {
						typeParameters = ArrayUtils.remove(typeParameters, ArrayUtils.indexOf(typeParameters, p));
					}
				}
				if (typeParameters.length == 0) {
					typeParameters = null;
				}
				TypeDeclaration newClass = null;

				while (newClass == null) {
					// first check that no type of the same level has the same
					// name
					TypeDeclaration conflictingType = container.findType(name);
					boolean conflict = false;
					if (conflictingType != null) {
						for (Declaration member : typeReference.getObjectType().getMembers()) {
							if (!ArrayUtils.contains(conflictingType.getMembers(), member)) {
								conflict = true;
								break;
							}
						}
						if (!conflict) {
							newClass = conflictingType;
						} else {
							name = rootName + getSuffix(i++);
						}
					} else {
						// check enclosing types names (in Java, enclosed types
						// cannot be named like an enclosing one)
						List<TypeDeclaration> parents = getParents(TypeDeclaration.class);
						for (TypeDeclaration parent : parents) {
							if (name.equals(parent.getName())) {
								conflict = true;
							}
						}
						if (!conflict) {
							NameConflictFinder finder = new NameConflictFinder(context, name);
							container.accept(finder);
							conflict = finder.conflict;
						}
						if (conflict) {
							name = rootName + getSuffix(i++);
						} else {
							// finally, no conflict, we create the class
							newClass = new TypeDeclaration(null, "class", name, DeclarationHelper.copy(typeParameters),
									null, null);
							newClass.setDocumentation(
									"/** This is an automatically generated object type (see the source definition). */");
							if (container instanceof TypeDeclaration) {
								newClass.addModifier("static");
							}
							newClass.addMembers(typeReference.getObjectType().getMembers());
							newClass.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_OBJECT_TYPE);
							container.addMember(newClass);
						}
					}
				}

				String objectTypeFullName = getCurrentContainerName() + "." + newClass.getName();

				logger.trace("registering object type: " + objectTypeFullName + " in container " + container + " ("
						+ container.getClass().getSimpleName() + ")");
				List<TypeDeclaration> containerGeneratedTypes = context.generatedObjectTypes.get(container);
				if (containerGeneratedTypes == null) {
					context.generatedObjectTypes.put(container,
							containerGeneratedTypes = new LinkedList<TypeDeclaration>());
				}
				containerGeneratedTypes.add(newClass);

				// parentDeclaration.setType(new TypeReference(null,
				// newClass.getName(), DeclarationHelper
				// .toTypeArguments(typeParameters)));

				Util.substituteTypeReference(this, parentDeclaration, typeReference,
						new TypeReference(null, newClass.getName(), DeclarationHelper.toTypeArguments(typeParameters)));
				context.registerType(objectTypeFullName, newClass);
				// recursively scan the newly created class
				scan(newClass);
			}
		}
		scan(typeReference.getTypeArguments());
	}

}
