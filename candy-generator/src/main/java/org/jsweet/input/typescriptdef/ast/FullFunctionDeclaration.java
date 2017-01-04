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
package org.jsweet.input.typescriptdef.ast;

/**
 * A class that gathers all information about a function declaration.
 * 
 * @author Renaud Pawlak
 */
public class FullFunctionDeclaration {

	public FullFunctionDeclaration(ModuleDeclaration declaringModule, TypeDeclaration declaringType, FunctionDeclaration function) {
		super();
		this.declaringType = declaringType;
		this.function = function;
	}

	public TypeDeclaration declaringType;
	public ModuleDeclaration declaringModule;
	public FunctionDeclaration function;

	@Override
	public String toString() {
		return (declaringModule == null ? "" : declaringModule.getName() + ".") + (declaringType == null ? "" : declaringType.getName() + ".") + function;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FullFunctionDeclaration)) {
			return false;
		}
		FullFunctionDeclaration d = (FullFunctionDeclaration) obj;
		return this.function == d.function;
	}

	@Override
	public int hashCode() {
		return function.hashCode();
	}

}
