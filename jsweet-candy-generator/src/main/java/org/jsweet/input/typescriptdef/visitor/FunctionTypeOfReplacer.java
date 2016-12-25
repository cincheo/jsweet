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
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * @author Renaud Pawlak
 */
public class FunctionTypeOfReplacer extends Scanner {

	public FunctionTypeOfReplacer(Context context) {
		super(context);
	}

	public FunctionTypeOfReplacer(Scanner parentScanner) {
		super(parentScanner);
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
		if (variableDeclaration.getType().isTypeOf()) {
			QualifiedDeclaration<FunctionDeclaration> function = lookupFunctionDeclaration(
					variableDeclaration.getType().getName());
			if (function != null) {
				DeclarationContainer container = getParent(DeclarationContainer.class);
				container.removeMember(variableDeclaration);
				FunctionDeclaration f = function.getDeclaration().copy();
				f.setName(variableDeclaration.getName());
				container.addMember(f);
			}
		}
		super.visitVariableDeclaration(variableDeclaration);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
	}

}
