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
