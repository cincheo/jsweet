package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner decides whether a type should be translated as a Java class or a
 * Java interface.
 * 
 * @author Renaud Pawlak
 */
public class TypeKindChooser extends Scanner {

	public TypeKindChooser(Context context) {
		super(context);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		if (!typeDeclaration.isAnonymous() && !"enum".equals(typeDeclaration.getKind())
				&& !typeDeclaration.isExternal()) {
			if (!(typeDeclaration.getKind().equals("interface")
					&& (typeDeclaration.getSuperTypes() == null || typeDeclaration.getSuperTypes().length == 0)
					&& typeDeclaration.getMembers().length == 1 && ((typeDeclaration.getMembers()[0] instanceof FunctionDeclaration) && ((FunctionDeclaration) typeDeclaration
					.getMembers()[0]).isAnonymous()))) {
				if (typeDeclaration.getKind().equals("interface")) {
					typeDeclaration.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_INTERFACE);
					typeDeclaration.addModifier("abstract");
				}
				typeDeclaration.setKind("class");
			}
		}
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
