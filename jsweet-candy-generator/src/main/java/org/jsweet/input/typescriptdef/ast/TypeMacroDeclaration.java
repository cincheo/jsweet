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

import java.util.HashSet;

/**
 * Type macro declarations in source code of TypeScript defintions.
 * 
 * @author Renaud Pawlak
 */
public class TypeMacroDeclaration extends TypeDeclaration implements TypedDeclaration {

	private TypeReference type;

	public TypeMacroDeclaration(Token token, String aliasName, TypeParameterDeclaration[] typeParameters, TypeReference type) {
		super(token, "type", aliasName, typeParameters, null, null);
		this.type = type;
	}

	@Override
	public void accept(Visitor v) {
		v.visitTypeMacro(this);
	}

	@Override
	public TypeMacroDeclaration copy() {
		TypeMacroDeclaration copy = new TypeMacroDeclaration(null, getName(), getTypeParameters(), getType());
		copy.setDocumentation(getDocumentation());
		copy.setModifiers(this.getModifiers() == null ? null : new HashSet<String>(getModifiers()));
		return copy;
	}

	@Override
	public boolean isSubtypeOf(Type type) {
		if (type == null) {
			return false;
		}

		return type.isSubtypeOf(getType().getDeclaration());
	}

	@Override
	public TypeReference getType() {
		return type;
	}

	@Override
	public void setType(TypeReference type) {
		this.type = type;
	}
}
