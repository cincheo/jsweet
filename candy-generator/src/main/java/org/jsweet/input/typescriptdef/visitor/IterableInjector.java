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

import org.apache.commons.lang3.ArrayUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * This scanner injects the java.lang.Iterable interface when possible on
 * indexed types (it also injects the $set method when not found).
 * 
 * @author Renaud Pawlak
 */
public class IterableInjector extends Scanner {

	public IterableInjector(Context context) {
		super(context);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		for (FunctionDeclaration getFunction : typeDeclaration
				.findFunctions(JSweetDefTranslatorConfig.INDEXED_GET_FUCTION_NAME)) {
			if (getFunction.isHidden()) {
				continue;
			}

			// inject the Iterable interface if needed
			VariableDeclaration var = typeDeclaration.findVariable("length");
			if (var == null) {
				return;
			}
			if (getFunction.getParameters().length == 1
					&& "number".equals(getFunction.getParameters()[0].getType().getName())) {
				// skip if already in a super type
				if (typeDeclaration.getSuperTypes() != null) {
					for (TypeReference t : typeDeclaration.getSuperTypes()) {
						FunctionDeclaration alreadyInSupertype = lookupFunctionDeclaration(t,
								JSweetDefTranslatorConfig.INDEXED_GET_FUCTION_NAME,
								new TypeReference(null, "number", null));
						if (alreadyInSupertype != null) {
							return;
						}
					}
				}

				if ("class".equals(typeDeclaration.getKind()) || "interface".equals(typeDeclaration.getKind())) {
					TypeReference iterableType = new TypeReference(null, "Iterable",
							new TypeReference[] { Util.wrapTypeReferences(getFunction.getType().copy()) });
					if (!ArrayUtils.contains(typeDeclaration.getSuperTypes(), iterableType)) {
						logger.trace("add " + getFunction.getType() + " iterator to "
								+ context.getTypeName(typeDeclaration));
						typeDeclaration.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_SYNTACTIC_ITERABLE);
						typeDeclaration.setSuperTypes(ArrayUtils.add(typeDeclaration.getSuperTypes(), iterableType));
						if (!"interface".equals(typeDeclaration.getKind())) {
							FunctionDeclaration iterator = new FunctionDeclaration(null, "iterator",
									new TypeReference(null, "java.util.Iterator",
											new TypeReference[] {
													Util.wrapTypeReferences(getFunction.getType().copy()) }),
									new ParameterDeclaration[0], null);
							iterator.setDocumentation(
									"/** From Iterable, to allow foreach loop (do not use directly). */");
							iterator.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_ERASED);

							typeDeclaration.addMember(iterator);
						}
					}
				}
			}
		}
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
