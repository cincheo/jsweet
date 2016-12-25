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

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.ArrayUtils;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * @author Renaud Pawlak
 */
public class OptionalParametersExpander extends Scanner {

	public OptionalParametersExpander(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		super.visitFunctionDeclaration(functionDeclaration);
		DeclarationContainer container = getParent(DeclarationContainer.class);
		for (int i = functionDeclaration.getParameters().length - 1; i >= 0; i--) {
			if (functionDeclaration.getParameters()[i].isOptional()) {
				FunctionDeclaration newFunction = new FunctionDeclaration(functionDeclaration.getToken(),
						functionDeclaration.getName(),
						functionDeclaration.getType() == null ? null : functionDeclaration.getType().copy(),
						DeclarationHelper.copy(ArrayUtils.subarray(functionDeclaration.getParameters(), 0, i)),
						DeclarationHelper.copy(functionDeclaration.getTypeParameters()));
				newFunction.setDocumentation(functionDeclaration.getDocumentation());
				newFunction.setModifiers(functionDeclaration.getModifiers() == null ? null
						: new HashSet<String>(functionDeclaration.getModifiers()));
				if (functionDeclaration.getStringAnnotations() != null) {
					newFunction.setStringAnnotations(new ArrayList<>(functionDeclaration.getStringAnnotations()));
				}
				if (!ArrayUtils.contains(container.getMembers(), newFunction)) {
					container.addMember(newFunction);
				}
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
