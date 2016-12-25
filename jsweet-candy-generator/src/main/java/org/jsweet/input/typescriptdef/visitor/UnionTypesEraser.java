/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
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
