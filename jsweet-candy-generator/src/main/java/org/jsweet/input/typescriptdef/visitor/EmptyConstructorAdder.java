package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner adds an empty protected constructor in classes that have
 * non-empty constructors, so that subclasses can define constructors without
 * having to explicitly invoke a superclass constructor.
 * 
 * @author Renaud Pawlak
 */
public class EmptyConstructorAdder extends Scanner {

	public EmptyConstructorAdder(Context context) {
		super(context);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		boolean hasEmptyConstructor = false;
		boolean hasNonEmptyConstructor = false;
		for (FunctionDeclaration func : typeDeclaration.findConstructors()) {
			if (func.isHidden()) {
				continue;
			}
			if (func.getParameters().length == 0) {
				hasEmptyConstructor = true;
			} else {
				hasNonEmptyConstructor = true;
			}
		}
		if (hasNonEmptyConstructor && !hasEmptyConstructor) {
			FunctionDeclaration constructor = new FunctionDeclaration(null,
					FunctionDeclaration.NEW_FUNCTION_RESERVED_NAME, null, new ParameterDeclaration[0], null);
			constructor.addModifier("protected");
			typeDeclaration.addMember(constructor);
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
