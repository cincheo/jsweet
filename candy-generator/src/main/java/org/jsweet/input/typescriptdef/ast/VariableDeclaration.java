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
 * A variable declaration in the source code of TypeScript definitions.
 * 
 * @author Renaud Pawlak
 */
public class VariableDeclaration extends AbstractTypedDeclaration {

	private boolean optional = false;
	private boolean readonly = false;
	private Literal initializer = null;

	public VariableDeclaration(Token token, String name, TypeReference type, boolean optional, boolean readonly) {
		super(token, name, type);
		this.optional = optional;
		this.readonly = readonly;
	}

	@Override
	public void accept(Visitor v) {
		v.visitVariableDeclaration(this);
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	@Override
	public VariableDeclaration copy() {
		VariableDeclaration copy = new VariableDeclaration(this.getToken(), name, getType().copy(), optional, readonly);
		copy.setModifiers(this.getModifiers() == null ? null : new HashSet<String>(this.getModifiers()));
		copy.setStringAnnotations(
				this.getStringAnnotations() == null ? null : new ArrayList<String>(getStringAnnotations()));
		copy.setDocumentation(this.getDocumentation());
		copy.setInitializer(initializer == null ? null : initializer.copy());
		return copy;
	}

	public Literal getInitializer() {
		return initializer;
	}

	public void setInitializer(Literal initializer) {
		this.initializer = initializer;
	}

	public final boolean isReadonly() {
		return readonly;
	}

	public final void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

}
