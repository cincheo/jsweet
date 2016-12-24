package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner makes sure that classes that are sub-types of a serializable
 * class do not trigger useless compilation warnings.
 * 
 * @author Renaud Pawlak
 */
public class SerializableHandler extends Scanner {

	public SerializableHandler(Context context) {
		super(context);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		if (typeDeclaration.isSubtypeOf(context.getTypeDeclaration(JSweetDefTranslatorConfig.LANG_PACKAGE + "."
				+ RuntimeException.class.getSimpleName()))) {
			typeDeclaration.addStringAnnotation(SuppressWarnings.class.getName() + "(\"serial\")");
		}
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
	}

}
