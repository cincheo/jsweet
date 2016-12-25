package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

public class FunctionTypeOfReplacer extends Scanner {

	public FunctionTypeOfReplacer(Context context) {
		super(context);
	}

	public FunctionTypeOfReplacer(Scanner parentScanner) {
		super(parentScanner);
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
		if (variableDeclaration.getType().isTypeOf()) {
			QualifiedDeclaration<FunctionDeclaration> function = lookupFunctionDeclaration(
					variableDeclaration.getType().getName());
			if (function != null) {
				DeclarationContainer container = getParent(DeclarationContainer.class);
				container.removeMember(variableDeclaration);
				FunctionDeclaration f = function.getDeclaration().copy();
				f.setName(variableDeclaration.getName());
				container.addMember(f);
			}
		}
		super.visitVariableDeclaration(variableDeclaration);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
	}

}
