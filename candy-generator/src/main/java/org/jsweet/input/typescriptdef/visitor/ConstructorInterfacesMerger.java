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

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * Gathers constructors in the same type.
 * 
 * @author Renaud Pawlak
 */
public class ConstructorInterfacesMerger extends Scanner {

	public ConstructorInterfacesMerger(Context context) {
		super(context);
	}

	protected void makeAllMembersStatic(TypeDeclaration typeDeclaration) {
		for (Declaration member : typeDeclaration.getMembers()) {
			member.addModifier("static");
		}
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		// TODO
		// if a variable V exists in the same module with the same name then
		// -- if V is of the type of typeDeclaration then
		// ---- if there is no constructor in typeDeclaration (or supertypes)
		// then
		// ------ typeDeclaration moves all its functions and variables to
		// static ones
		// ---- end
		// -- else if V is of a type T (can be anonymous - object type) that
		// declares a constructor then
		// ---- 1. typeDeclaration is not abstract anymore
		// ---- 2. all the functions and variables (including constructors) of T
		// are injected into typeDeclaration
		// ---- 3. all the functions of typeDeclaration are not abstract anymore
		// ---- 4. V is deleted from the current modules' members
		// ---- 5. T is deleted only if T is not anonymous
		// -- end
		// end
		VariableDeclaration v = getParent(DeclarationContainer.class).findVariable(typeDeclaration.getName());
		if (v != null) {
			Type t = lookupType(v.getType(), null);
			if (t instanceof TypeDeclaration) {
				TypeDeclaration variableType = (TypeDeclaration) t;
				if (typeDeclaration.equals(variableType)) {
					if (typeDeclaration.findFirstConstructor() == null) {
						makeAllMembersStatic(typeDeclaration);
						typeDeclaration.removeStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_OBJECT_TYPE);
						v.setHidden(true);
					}
				} else {
					for (Declaration declaration : variableType.getMembers()) {
						if ((declaration instanceof FunctionDeclaration)
								&& ((FunctionDeclaration) declaration).isConstructor()) {
							continue;
							// declaration.setName("constructor");
						} else {
							declaration.addModifier("static");
						}
					}
					typeDeclaration.removeStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_INTERFACE);
					typeDeclaration.addMembers(variableType.getMembers());
					typeDeclaration.removeModifier("abstract");
					v.setHidden(true);
					if (!variableType.isAnonymous()) {
						context.mergedContructors.put(variableType, typeDeclaration);
						variableType.setHidden(true);
					}
				}
			}
		}
		// super.visitTypeDeclaration(typeDeclaration);
	}

}
