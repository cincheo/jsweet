package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;

public class DeclarationBinder extends Scanner {

	public DeclarationBinder(Context context) {
		super(context);
	}

	public DeclarationBinder(Scanner parentScanner) {
		super(parentScanner);
	}

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		context.registerModule(getCurrentContainerName(), moduleDeclaration);
		super.visitModuleDeclaration(moduleDeclaration);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		if (!typeDeclaration.isAnonymous()) {
			context.registerType(getCurrentContainerName(), typeDeclaration);
		}
		super.visitTypeDeclaration(typeDeclaration);
	}

	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
		context.registerType(getCurrentContainerName(), typeMacroDeclaration);
		super.visitTypeMacro(typeMacroDeclaration);
	}
}
