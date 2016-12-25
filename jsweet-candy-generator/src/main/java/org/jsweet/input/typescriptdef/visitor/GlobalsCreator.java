package org.jsweet.input.typescriptdef.visitor;

import static org.jsweet.input.typescriptdef.util.Util.getOrCreateGlobalsType;

import org.apache.commons.lang3.StringUtils;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

public class GlobalsCreator extends Scanner {

	public GlobalsCreator(Context context) {
		super(context);
	}

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		for (Declaration declaration : moduleDeclaration.getMembers()) {
			if (declaration.isHidden()) {
				continue;
			}
			if ((declaration instanceof VariableDeclaration) || (declaration instanceof FunctionDeclaration)) {
				TypeDeclaration globalsClass = getOrCreateGlobalsType(context, moduleDeclaration,
						getParent(ModuleDeclaration.class));
				if (DeclarationHelper.JS_OBJECT_METHOD_NAMES.contains(declaration.getName())) {
					declaration.setName(StringUtils.capitalize(declaration.getName()));
				}
				moduleDeclaration.removeMember(declaration);
				if (declaration instanceof VariableDeclaration) {
					VariableDeclaration existing = globalsClass.findVariable(declaration.getName());
					if (existing != null && !existing.isHidden()) {
						context.reportWarning("skip variable " + declaration + " - already exists in "
								+ moduleDeclaration + " (" + existing + ")");
						// variable already exists
						continue;
					}
				}
				globalsClass.addMember(declaration);
				declaration.addModifier("static");
				if (declaration instanceof VariableDeclaration) {
					VariableDeclaration varDecl = (VariableDeclaration) declaration;
					Type t = lookupType(varDecl.getType(), null);
					if (t instanceof TypeDeclaration) {
						final TypeDeclaration typeDeclaration = (TypeDeclaration) t;
						if (!typeDeclaration.isExternal() && typeDeclaration.isStatic()) {
							globalsClass.removeMember(declaration);
						}
					}
				}
			}
		}
		super.visitModuleDeclaration(moduleDeclaration);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}
}
