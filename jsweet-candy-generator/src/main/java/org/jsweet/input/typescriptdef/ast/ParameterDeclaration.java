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

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A parameter declaration in the TypeScript source code.
 * 
 * @author Renaud Pawlak
 */
public class ParameterDeclaration extends VariableDeclaration {

	private boolean varargs = false;

	public ParameterDeclaration(Token token, String name, TypeReference type, boolean optional, boolean varargs) {
		super(token, name, type, optional, false);
		this.varargs = varargs;
	}

	@Override
	public void accept(Visitor v) {
		v.visitParameterDeclaration(this);
	}

	public boolean isVarargs() {
		return varargs;
	}

	public void setVarargs(boolean varargs) {
		this.varargs = varargs;
	}

	@Override
	public ParameterDeclaration copy() {
		ParameterDeclaration copy = new ParameterDeclaration(this.getToken(), name, getType().copy(), isOptional(),
				isVarargs());
		copy.setModifiers(this.getModifiers() == null ? null : new HashSet<String>(this.getModifiers()));
		copy.setStringAnnotations(
				this.getStringAnnotations() == null ? null : new ArrayList<String>(getStringAnnotations()));
		copy.setDocumentation(this.getDocumentation());
		return copy;
	}

}
