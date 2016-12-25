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

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * This scanner expands the partial type references to fully qualified type
 * references, so that it is unambiguous to Java. It the case JSWeet is not in
 * JDK replacement mode, it also forces core types in the JSWeet's lang package
 * to reference java.lang package core types.
 * 
 * @author Renaud Pawlak
 */
public class TypeReferenceExpander extends Scanner {

	private int typeCount = 0;

	public TypeReferenceExpander(Context context) {
		super(context);
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		if (typeReference.getName() != null) {
			if (!JSweetDefTranslatorConfig.isJDKReplacementMode() && !"any".equals(typeReference.getName())
					&& Util.coreTypeMap.containsKey(typeReference.getName())) {
				String name = Util.coreTypeMap.get(typeReference.getName());
				typeCount++;
				if (context.verbose) {
					logger.debug("WARNING: rewriting type ref " + typeReference.getName() + " -> " + name + " at "
							+ getCurrentToken().getLocation());
				}
				typeReference.setName(name);
			} else {
				if (typeReference.getName().contains(".")) {
					Type t = lookupType(typeReference, null);
					if (t instanceof TypeDeclaration && !((TypeDeclaration) t).isExternal()) {
						TypeDeclaration typeDeclaration = (TypeDeclaration) t;
						String typeName = context.getTypeName(typeDeclaration);
						if (!typeName.equals(typeReference.getName())) {
							typeCount++;
							if (context.verbose) {
								logger.debug("WARNING: rewriting type ref " + typeReference.getName() + " -> "
										+ typeName + " at " + getCurrentToken().getLocation());
							}
							typeReference.setName(typeName);
						}
					}
				}
			}
		}

		super.visitTypeReference(typeReference);
	}

	@Override
	public void onScanEnded() {
		if (typeCount > 0) {
			logger.debug(typeCount + " type reference(s) rewritten/expanded.");
		}
	}

}
