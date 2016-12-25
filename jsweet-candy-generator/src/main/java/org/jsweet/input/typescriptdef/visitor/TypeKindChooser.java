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
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner decides whether a type should be translated as a Java class or a
 * Java interface.
 * 
 * @author Renaud Pawlak
 */
public class TypeKindChooser extends Scanner {

	public TypeKindChooser(Context context) {
		super(context);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		if (!typeDeclaration.isAnonymous() && !"enum".equals(typeDeclaration.getKind())
				&& !typeDeclaration.isExternal()) {
			if (!(typeDeclaration.getKind().equals("interface")
					&& (typeDeclaration.getSuperTypes() == null || typeDeclaration.getSuperTypes().length == 0)
					&& typeDeclaration.getMembers().length == 1 && ((typeDeclaration.getMembers()[0] instanceof FunctionDeclaration) && ((FunctionDeclaration) typeDeclaration
					.getMembers()[0]).isAnonymous()))) {
				if (typeDeclaration.getKind().equals("interface")) {
					typeDeclaration.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_INTERFACE);
					typeDeclaration.addModifier("abstract");
				}
				typeDeclaration.setKind("class");
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
