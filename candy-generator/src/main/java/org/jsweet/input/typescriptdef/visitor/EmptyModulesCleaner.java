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
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.ReferenceDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner hides the module that don't have visible members.
 * 
 * @author Renaud Pawlak
 */
public class EmptyModulesCleaner extends Scanner {

	public EmptyModulesCleaner(Context context) {
		super(context);
	}

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		for (Declaration d : moduleDeclaration.getMembers()) {
			if (!d.isHidden() && !(d instanceof ReferenceDeclaration)) {
				super.visitModuleDeclaration(moduleDeclaration);
				return;
			}
		}
		moduleDeclaration.setHidden(true);
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}
	
	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}
	
	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
	}
	
	@Override
	public void visitTypeMacro(TypeMacroDeclaration typeMacroDeclaration) {
	}
	
}
