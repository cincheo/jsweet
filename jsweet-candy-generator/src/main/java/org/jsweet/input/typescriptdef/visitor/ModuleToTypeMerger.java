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
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * @author Renaud Pawlak
 */
public class ModuleToTypeMerger extends Scanner {

	public ModuleToTypeMerger(Context context) {
		super(context);
	}

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		if (moduleDeclaration.getName().contains("-")) {
			context.reportWarning("invalid module name " + moduleDeclaration.getName() + " at "
					+ moduleDeclaration.getToken().getLocation());
			moduleDeclaration.setHidden(true);
			return;
		}
		String fullName = getCurrentModuleName();
		TypeDeclaration type = context.getTypeDeclaration(fullName);
		boolean hideType = false;
		boolean hideModule = true;
		if (type != null) {
//				for (Declaration declaration : moduleDeclaration.getMembers()) {
//					if (!(declaration instanceof VariableDeclaration || declaration instanceof FunctionDeclaration
//							|| (declaration instanceof TypeDeclaration))) {
//						hideType = true;
//						logger.warn("submodule '" + declaration.getName()
//								+ "' found while merging module into class... erase class and keep modules at "
//								+ declaration.getToken().getLocation());
//						moduleDeclaration.addMembers(type.getMembers());
//						type.setHidden(true);
//						context.unregisterType(type);
//						hideType = true;
//						break;
//					}
//				}
			if (!hideType) {
				for (Declaration declaration : moduleDeclaration.getMembers()) {
					if (declaration instanceof VariableDeclaration || declaration instanceof FunctionDeclaration
							/*|| (declaration instanceof TypeDeclaration)*/) {
						type.addMember(declaration);
						moduleDeclaration.removeMember(declaration);
						if(!(declaration instanceof TypeDeclaration)) {
							declaration.addModifier("static");
						}
					} else {
						logger.warn("cannot merge declaration '" + declaration.getName()
						+ "' "
						+ declaration.getToken().getLocation());
						hideModule = false;
					}
				}
				if(hideModule) {
					moduleDeclaration.setHidden(true);
				} else {
					context.setTypeClashingWithModule(context.getTypeName(type));
				}
			}
		}
		super.visitModuleDeclaration(moduleDeclaration);
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
	}

}
