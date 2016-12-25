package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.input.typescriptdef.TypescriptDef2Java;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeReference;

public class TypeReferenceChecker extends Scanner {

	public TypeReferenceChecker(Context context) {
		super(context);
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		if (!"this".equals(typeReference.getName()) && !typeReference.isTypeOf()) {
			lookupType(typeReference, null, TypescriptDef2Java.generateMissingTypes, true);
		}
		super.visitTypeReference(typeReference);
	}

}
