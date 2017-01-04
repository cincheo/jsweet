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
import org.jsweet.input.typescriptdef.ast.FunctionalTypeReference;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner makes functional type parameters and return optional in
 * functions.
 * 
 * <p>
 * For instance: <code>f(p:A,B=>C)</code> will generate:
 * 
 * <ul>
 * <li><code>f(p:(a:A,b:B)=>C)</code></li>
 * <li><code>f(p:(a:A,b:B)=>void)</code></li>
 * <li><code>f(p:()=>C)</code></li>
 * <li><code>f(p:()=>void)</code></li>
 * </ul>
 * 
 * @author Renaud Pawlak
 */
public class OptionalLambdasParametersExpander extends Scanner {

	int param = 0;

	public OptionalLambdasParametersExpander(Context context) {
		super(context);
	}

	public OptionalLambdasParametersExpander(Scanner parent, int param) {
		super(parent);
		this.param = param;
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		super.visitFunctionDeclaration(functionDeclaration);
		DeclarationContainer container = getParent(DeclarationContainer.class);
		for (int i = param; i < functionDeclaration.getParameters().length; i++) {
			if (functionDeclaration.getParameters()[i].getType() instanceof FunctionalTypeReference) {
				FunctionalTypeReference functionalType = (FunctionalTypeReference) functionDeclaration
						.getParameters()[i].getType();
				FunctionDeclaration voidFunction = null;
				FunctionalTypeReference voidFunctionalType = null;
				if (!"void".equals(functionalType.getReturnType().getName())) {
					voidFunctionalType = functionalType.copy();
					voidFunctionalType.setReturnType(new TypeReference(null, "void", null));
					voidFunction = createFunction(container, functionDeclaration, voidFunctionalType, i);
					new OptionalLambdasParametersExpander(this, param + 1).visitFunctionDeclaration(voidFunction);
				}
				if (functionalType.getParameters().length > 0) {
					FunctionalTypeReference truncatedFunctionalType = functionalType.copy();
					truncatedFunctionalType.setParameters(new ParameterDeclaration[0]);
					FunctionDeclaration truncatedFunction = createFunction(container, functionDeclaration,
							truncatedFunctionalType, i);
					new OptionalLambdasParametersExpander(this, param + 1).visitFunctionDeclaration(truncatedFunction);
					if (voidFunction != null) {
						truncatedFunctionalType = voidFunctionalType.copy();
						truncatedFunctionalType.setParameters(new ParameterDeclaration[0]);
						truncatedFunction = createFunction(container, functionDeclaration, truncatedFunctionalType, i);
						new OptionalLambdasParametersExpander(this, param + 1)
								.visitFunctionDeclaration(truncatedFunction);
					}
				}
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	private FunctionDeclaration createFunction(DeclarationContainer container, FunctionDeclaration functionDeclaration,
			FunctionalTypeReference newParamType, int param) {
		FunctionDeclaration newFunction = new FunctionDeclaration(functionDeclaration.getToken(),
				functionDeclaration.getName(),
				functionDeclaration.getType() == null ? null : functionDeclaration.getType().copy(),
				DeclarationHelper.copy(functionDeclaration.getParameters()),
				DeclarationHelper.copy(functionDeclaration.getTypeParameters()));
		newFunction.getParameters()[param].setType(newParamType);
		newFunction.setDocumentation(functionDeclaration.getDocumentation());
		newFunction.setModifiers(functionDeclaration.getModifiers() == null ? null
				: new HashSet<String>(functionDeclaration.getModifiers()));
		if (functionDeclaration.getStringAnnotations() != null) {
			newFunction.setStringAnnotations(new ArrayList<>(functionDeclaration.getStringAnnotations()));
		}
		if (!ArrayUtils.contains(container.getMembers(), newFunction)) {
			container.addMember(newFunction);
		}
		return newFunction;

	}

}
