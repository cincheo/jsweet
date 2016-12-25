package org.jsweet.input.typescriptdef.visitor;

import java.util.List;

import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.TypedDeclaration;
import org.jsweet.input.typescriptdef.ast.UnionTypeReference;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * Remove too big union types because it makes Java APIs look awfull.
 * 
 * @author Renaud Pawlak
 */
public class UnionTypesEraser extends Scanner {

	public UnionTypesEraser(Context context) {
		super(context);
	}

	@Override
	public void visitUnionTypeReference(UnionTypeReference unionTypeReference) {

		boolean substituted = false;
		List<TypeReference> possibleTypes = unionTypeReference.getTypes();
		if (possibleTypes.size() > 3) {
			logger.trace("replace union in " + getCurrentContainerName() + "." + getParent() + " types: "
					+ possibleTypes);
			TypeReference any = new TypeReference(null, "any", null);
			substituted = Util.substituteTypeReference(this, getParent(TypedDeclaration.class), unionTypeReference,
					any);

			if (!substituted) {
				logger.warn("cannot replace: " + getCurrentContainerName() + "." + getParent() + " ===> union::"
						+ possibleTypes);
			}
		}

		if (!substituted) {
			super.visitUnionTypeReference(unionTypeReference);
		}
	}

}
