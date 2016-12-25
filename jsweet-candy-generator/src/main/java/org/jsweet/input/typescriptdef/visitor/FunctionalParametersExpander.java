package org.jsweet.input.typescriptdef.visitor;

import org.apache.commons.lang3.ArrayUtils;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.FunctionalTypeReference;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

public class FunctionalParametersExpander extends Scanner {

	public FunctionalParametersExpander(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		super.visitFunctionDeclaration(functionDeclaration);
		DeclarationContainer container = getParent(DeclarationContainer.class);
		expandFunctionalParameters(container, functionDeclaration);
	}

	private void expandFunctionalParameters(DeclarationContainer container, FunctionDeclaration functionDeclaration) {
		for (int i = functionDeclaration.getParameters().length - 1; i >= 0; i--) {
			if (functionDeclaration.getParameters()[i].getType() instanceof FunctionalTypeReference) {
				FunctionalTypeReference p = (FunctionalTypeReference) functionDeclaration.getParameters()[i].getType();
				if ("any".equals(p.getReturnType().getName())) {
					FunctionDeclaration newFunction = functionDeclaration.copy();
					((FunctionalTypeReference) newFunction.getParameters()[i].getType()).getReturnType().setName("void");
					if (!ArrayUtils.contains(container.getMembers(), newFunction)) {
						container.addMember(newFunction);
						expandFunctionalParameters(container, newFunction);
					}
				}
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
