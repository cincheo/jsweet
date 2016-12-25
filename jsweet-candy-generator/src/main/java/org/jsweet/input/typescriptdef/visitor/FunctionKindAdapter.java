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
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * @author Renaud Pawlak
 */
public class FunctionKindAdapter extends Scanner {

	public FunctionKindAdapter(Context context) {
		super(context);
	}

	boolean isInInterface = false;

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		isInInterface = "interface".equals(typeDeclaration.getKind())
				|| typeDeclaration.hasStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_INTERFACE);
		super.visitTypeDeclaration(typeDeclaration);
		isInInterface = false;
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		if (!(getParent() instanceof TypeDeclaration)) {
			return;
		}
		final TypeDeclaration parent = (TypeDeclaration) getParent();
		if (functionDeclaration.isConstructor() && isInInterface) {
			functionDeclaration.addModifier("native");
			functionDeclaration.setName(JSweetDefTranslatorConfig.NEW_FUNCTION_NAME);
			if (functionDeclaration.getType() == null) {
				functionDeclaration.setType(new TypeReference(null, parent, null));
			}
		} else if (JSweetDefTranslatorConfig.ANONYMOUS_FUNCTION_NAME.equals(functionDeclaration.getName())
				&& functionDeclaration.hasModifier("static")) {
			functionDeclaration.setName(JSweetDefTranslatorConfig.ANONYMOUS_STATIC_FUNCTION_NAME);
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
