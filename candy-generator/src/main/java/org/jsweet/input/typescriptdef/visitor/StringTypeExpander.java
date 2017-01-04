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
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * @author Renaud Pawlak
 */
public class StringTypeExpander extends Scanner {

	public StringTypeExpander(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		super.visitFunctionDeclaration(functionDeclaration);
		redirectStringType(functionDeclaration);
		DeclarationContainer container = getParent(DeclarationContainer.class);
		expandStringParameters(container, functionDeclaration);
	}

	private void expandStringParameters(DeclarationContainer container, FunctionDeclaration functionDeclaration) {
		for (int i = functionDeclaration.getParameters().length - 1; i >= 0; i--) {
			TypeReference t = functionDeclaration.getParameters()[i].getType();
			if ("string".equals(t.getName()) || "String".equals(t.getName())) {
				redirectStringType(functionDeclaration.getParameters()[i]);
				FunctionDeclaration newFunction = functionDeclaration.copy();
				newFunction.getParameters()[i].getType().setName(String.class.getName());
				if (!ArrayUtils.contains(container.getMembers(), newFunction)) {
					container.addMember(newFunction);
					expandStringParameters(container, newFunction);
				}
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
		redirectStringType(variableDeclaration);
	}

	private void redirectStringType(TypedDeclaration declaration) {
		if (declaration.getType() != null && ("string".equals(declaration.getType().getName()) || "String".equals(declaration.getType().getName()))) {
			declaration.getType().setName(JSweetDefTranslatorConfig.LANG_PACKAGE + ".String");
		}
	}

}
