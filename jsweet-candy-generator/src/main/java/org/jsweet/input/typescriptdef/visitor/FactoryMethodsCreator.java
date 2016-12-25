package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner creates factory methods out of anonymous ones when they return a
 * different type than the declaring type (otherwise, they are just
 * constructors).
 * 
 * @author Renaud Pawlak
 */
public class FactoryMethodsCreator extends Scanner {

	private int createdMethodCount = 0;

	public FactoryMethodsCreator(Context context) {
		super(context);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		if (!functionDeclaration.isAnonymous()) {
			return;
		}
		TypeDeclaration declaringType = getParent(TypeDeclaration.class);
		if (declaringType != null) {
			if (!"any".equals(functionDeclaration.getType().getName())) {
				Type t = lookupType(functionDeclaration.getType(), null);
				if (t != declaringType) {
					createdMethodCount++;
					if (context.verbose) {
						String location = functionDeclaration.getToken() == null ? "?" : functionDeclaration.getToken()
								.getLocation();
						logger.debug("creating factory method at " + location);
						functionDeclaration.setName("_create");
					}
				}
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	@Override
	public void onScanEnded() {
		if (createdMethodCount > 0) {
			logger.debug(createdMethodCount + " factory method(s) created.");
		}
	}

}
