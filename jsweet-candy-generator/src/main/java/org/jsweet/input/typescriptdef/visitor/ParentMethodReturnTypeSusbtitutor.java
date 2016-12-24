package org.jsweet.input.typescriptdef.visitor;

import java.util.function.BiConsumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner substitutes void return types of parent methods with Object.
 * 
 * <p>
 * In Typescript, one can override a void method with a method that returns an
 * object. In Java, it is not possible. So this scanner make sure that the
 * parent method with actually return an object to avoid Java compile errors.
 * 
 * @author Renaud Pawlak
 */
public class ParentMethodReturnTypeSusbtitutor extends Scanner {

	public ParentMethodReturnTypeSusbtitutor(Context context) {
		super(context);
	}

	private void applyToParentMethod(TypeDeclaration declaringType, FunctionDeclaration childFunction,
			TypeDeclaration parentType, BiConsumer<TypeDeclaration, FunctionDeclaration> apply) {
		int index = -1;
		if (declaringType != parentType) {
			index = ArrayUtils.indexOf(parentType.getMembers(), childFunction);
		}
		if (index != -1) {
			apply.accept(parentType, (FunctionDeclaration) parentType.getMembers()[index]);
		} else {
			if (parentType.getSuperTypes() != null && parentType.getSuperTypes().length > 0) {
				for (TypeReference ref : parentType.getSuperTypes()) {
					Type decl = lookupType(ref, null);
					if (decl instanceof TypeDeclaration) {
						applyToParentMethod(declaringType, childFunction, (TypeDeclaration) decl, apply);
					}
				}
			} else if (!JSweetDefTranslatorConfig.getObjectClassName().equals(context.getTypeName(parentType))) {
				TypeDeclaration decl = context.getTypeDeclaration(JSweetDefTranslatorConfig.getObjectClassName());
				if (decl != null) {
					applyToParentMethod(declaringType, childFunction, (TypeDeclaration) decl, apply);
				}
			}
		}
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		TypeDeclaration declaringType = getParent(TypeDeclaration.class);
		if (declaringType != null && functionDeclaration.getType() != null
				&& functionDeclaration.getType().getName() != null
				&& !functionDeclaration.getType().getName().equals("void")) {
			applyToParentMethod(declaringType, functionDeclaration, declaringType, (parentType, parentFunction) -> {
				if (functionDeclaration.getType().isPrimitive() && !parentFunction.getType().isPrimitive()) {
					functionDeclaration.getType()
							.setName(StringUtils.capitalize(functionDeclaration.getType().getName()));
				} else {
					if ("void".equals(parentFunction.getType().getName())) {

						if (context.isInDependency(parentType)) {
							// if parent belongs to a dependency, we cant change
							// it
							logger.info("modify return type of " + context.getTypeName(declaringType) + "."
									+ functionDeclaration + ": " + functionDeclaration.getType() + " -> "
									+ parentFunction.getType());
							functionDeclaration.setType(parentFunction.getType());
						} else {
							logger.info(
									"modify return type of " + context.getTypeName(parentType) + "." + parentFunction
											+ ": " + parentFunction.getType() + " -> " + functionDeclaration.getType());
							parentFunction.setType(functionDeclaration.getType());
						}
					}
				}
			});
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
