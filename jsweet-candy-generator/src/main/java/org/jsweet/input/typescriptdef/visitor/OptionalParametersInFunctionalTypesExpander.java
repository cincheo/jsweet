package org.jsweet.input.typescriptdef.visitor;

import org.apache.commons.lang3.ArrayUtils;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.FunctionalTypeReference;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * Expands optional parameters in functional types.
 * 
 * @author Renaud Pawlak
 */
public class OptionalParametersInFunctionalTypesExpander extends Scanner {

	public OptionalParametersInFunctionalTypesExpander(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		super.visitFunctionDeclaration(functionDeclaration);
		DeclarationContainer container = getParent(DeclarationContainer.class);
		for (int i = functionDeclaration.getParameters().length - 1; i >= 0; i--) {
			if (functionDeclaration.getParameters()[i].getType() instanceof FunctionalTypeReference) {
				FunctionalTypeReference f = (FunctionalTypeReference) functionDeclaration.getParameters()[i].getType();
				for (int j = f.getParameters().length - 1; j >= 0; j--) {
					if (f.getParameters()[j].isOptional()) {
						FunctionalTypeReference newFunctionalType = new FunctionalTypeReference(null,
								f.getReturnType(), ArrayUtils.subarray(f.getParameters(), 0, j), null);
						FunctionDeclaration newFunction = new FunctionDeclaration(functionDeclaration.getToken(),
								functionDeclaration.getName(), functionDeclaration.getType(),
								DeclarationHelper.copy(functionDeclaration.getParameters()),
								functionDeclaration.getTypeParameters());
						newFunction.getParameters()[i].setType(newFunctionalType);
						newFunction.setDocumentation(functionDeclaration.getDocumentation());
						newFunction.setModifiers(functionDeclaration.getModifiers());
						if (!ArrayUtils.contains(container.getMembers(), newFunction)) {
							container.addMember(newFunction);
						}
					}
				}
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
