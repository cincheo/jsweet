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
 * Type parameter declarations in source code of TypeScript defintions.
 * 
 * @author Renaud Pawlak
 */
public class TypeParameterDeclaration extends AbstractDeclaration implements Type, TypedDeclaration {

	protected TypeReference upperBound;

	public TypeParameterDeclaration(Token token, String name) {
		super(token, name);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitTypeParameterDeclaration(this);
	}

	public TypeReference getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(TypeReference upperBound) {
		this.upperBound = upperBound;
	}

	@Override
	public TypeParameterDeclaration copy() {
		TypeParameterDeclaration copy = new TypeParameterDeclaration(this.getToken(), getName());
		copy.upperBound = upperBound == null ? null : upperBound.copy();
		return copy;
	}

	@Override
	public void setType(TypeReference type) {
		upperBound = type;
	}

	@Override
	public TypeReference getType() {
		return upperBound;
	}

	@Override
	public boolean isSubtypeOf(Type type) {
		return false;
	}

}
