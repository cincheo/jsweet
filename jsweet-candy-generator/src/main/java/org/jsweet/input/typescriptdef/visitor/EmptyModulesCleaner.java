package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.ReferenceDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner hides the module that don't have visible members.
 * 
 * @author Renaud Pawlak
 */
public class EmptyModulesCleaner extends Scanner {

	public EmptyModulesCleaner(Context context) {
		super(context);
	}

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		for (Declaration d : moduleDeclaration.getMembers()) {
			if (!d.isHidden() && !(d instanceof ReferenceDeclaration)) {
				super.visitModuleDeclaration(moduleDeclaration);
				return;
			}
		}
		moduleDeclaration.setHidden(true);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}
	
	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}
	
	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
	}
	
	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
	}
	
}
