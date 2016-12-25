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
import org.jsweet.input.typescriptdef.ast.ArrayTypeReference;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * 
 * 
 * @author Renaud Pawlak
 */
public class DynamicTypeParametersExpander extends Scanner {

	private int expandedParameterCount = 0;

	public DynamicTypeParametersExpander(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		super.visitFunctionDeclaration(functionDeclaration);
		DeclarationContainer container = getParent(DeclarationContainer.class);
		if (container instanceof TypeDeclaration && ((TypeDeclaration) container).isFunctionalInterface()) {
			// do not expand method signature if container is a functional
			// interface
			return;
		}

		for (int i = functionDeclaration.getParameters().length - 1; i >= 0; i--) {
			if (functionDeclaration.getParameters()[i].getType().getTypeArguments() != null
					&& functionDeclaration.getParameters()[i].getType().getTypeArguments().length == 1
					&& context.arrayTypes.contains(lookupType(functionDeclaration.getParameters()[i].getType(), null))) {
				expandedParameterCount++;
				if (context.verbose) {
					logger.debug("expanding dynamic type for: " + functionDeclaration);
				}
				FunctionDeclaration newFunction = new FunctionDeclaration(functionDeclaration.getToken(),
						functionDeclaration.getName(), functionDeclaration.getType(),
						DeclarationHelper.copy(functionDeclaration.getParameters()),
						functionDeclaration.getTypeParameters());
				newFunction.setDocumentation(functionDeclaration.getDocumentation());
				newFunction.setModifiers(functionDeclaration.getModifiers());
				newFunction.getParameters()[i] = new ParameterDeclaration(null,
						functionDeclaration.getParameters()[i].getName(), new ArrayTypeReference(null,
								functionDeclaration.getParameters()[i].getType().getTypeArguments()[0]),
						functionDeclaration.getParameters()[i].isOptional(),
						functionDeclaration.getParameters()[i].isVarargs());
				if (!ArrayUtils.contains(container.getMembers(), newFunction)) {
					container.addMember(newFunction);
				}
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	@Override
	public void onScanEnded() {
		if (expandedParameterCount > 0) {
			logger.debug(expandedParameterCount + " parameter(s) expanded.");
		}
	}

}
