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
