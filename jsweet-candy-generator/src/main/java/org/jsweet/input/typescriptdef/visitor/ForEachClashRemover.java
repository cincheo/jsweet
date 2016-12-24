package org.jsweet.input.typescriptdef.visitor;

import java.util.function.Consumer;

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * Very specific case for JavaScript Array. Will not be necessary if we switch
 * to Peter's technique.
 * 
 * @author Renaud Pawlak
 */
public class ForEachClashRemover extends Scanner {

	public ForEachClashRemover(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		if ("forEach".equals(functionDeclaration.getName()) && functionDeclaration.getParameters().length == 1) {
			Type t = functionDeclaration.getParameters()[0].getType().getDeclaration();
			if (t instanceof TypeDeclaration
					&& Consumer.class.getName().equals(context.getTypeName((TypeDeclaration) t))) {
				functionDeclaration.getParameters()[0].getType()
						.setName(JSweetDefTranslatorConfig.FUNCTION_CLASSES_PACKAGE + ".Consumer");
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
	}

}
