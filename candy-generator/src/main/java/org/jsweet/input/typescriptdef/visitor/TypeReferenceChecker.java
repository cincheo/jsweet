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

import org.jsweet.input.typescriptdef.TypescriptDef2Java;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeReference;

/**
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class TypeReferenceChecker extends Scanner {

	public TypeReferenceChecker(Context context) {
		super(context);
	}
	
	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		if (context.isInDependency(moduleDeclaration)) {
			return;
		}
		
		super.visitModuleDeclaration(moduleDeclaration);
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		if (!"this".equals(typeReference.getName()) && !typeReference.isTypeOf()) {
			lookupType(typeReference, null, TypescriptDef2Java.generateMissingTypes, true);
		}
		super.visitTypeReference(typeReference);
	}

}
