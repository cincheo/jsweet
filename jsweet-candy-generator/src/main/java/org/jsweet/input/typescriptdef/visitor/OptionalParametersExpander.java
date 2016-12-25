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
