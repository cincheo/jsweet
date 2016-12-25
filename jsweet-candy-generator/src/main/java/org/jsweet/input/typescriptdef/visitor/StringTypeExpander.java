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
