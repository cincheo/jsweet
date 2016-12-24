package org.jsweet.input.typescriptdef.visitor;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeReference;

/**
 * @author Renaud Pawlak
 */
public class ConstructorTypeReferenceReplacer extends Scanner {

	public ConstructorTypeReferenceReplacer(Context context) {
		super(context);
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		if (context.mergedContructors.containsKey(typeReference.getDeclaration())) {
			// TODO: not very well typed but otherwise would require method
			// disambiguation... better typing could be quite easily achieved

			// typeReference.setTypeArguments(new TypeReference[] { new
			// TypeReference(null,
			// context.getTypeName(context.mergedContructors.get(typeReference.getDeclaration())),
			// null) });
			typeReference.setTypeArguments(new TypeReference[] { new TypeReference(null, "any", null) });
			typeReference.setName(Class.class.getName());
			typeReference.setDeclaration(context.getTypeDeclaration(Class.class.getName()));
		} else {
			super.visitTypeReference(typeReference);
		}
	}

}
