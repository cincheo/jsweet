package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

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
