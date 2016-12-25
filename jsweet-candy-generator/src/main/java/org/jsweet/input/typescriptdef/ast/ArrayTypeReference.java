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
 * A reference to array types.
 * 
 * @author Renaud Pawlak
 */
public class ArrayTypeReference extends TypeReference {

	TypeReference componentType;
	boolean disableArray = false;

	public ArrayTypeReference(Token token, TypeReference componentType) {
		super(token, (String) null, null);
		this.componentType = componentType;
	}

	@Override
	public String toString() {
		if (componentType == null) {
			return "any[]";
		}
		return componentType.toString() + "[]";
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitArrayTypeReference(this);
	}

	public TypeReference getComponentType() {
		return componentType;
	}

	public void setComponentType(TypeReference componentType) {
		this.componentType = componentType;
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		ArrayTypeReference tr = (ArrayTypeReference) obj;
		return tr.getComponentType().equals(getComponentType());
	}

	public boolean isDisableArray() {
		return disableArray;
	}

	public void setDisableArray(boolean disableArray) {
		this.disableArray = disableArray;
	}

	@Override
	public ArrayTypeReference copy(boolean copyDeclarations) {
		ArrayTypeReference copy = new ArrayTypeReference(null,
				getComponentType() == null ? null : getComponentType().copy(copyDeclarations));
		copy.disableArray = disableArray;
		return copy;
	}

	@Override
	public ArrayTypeReference copy() {
		return copy(false);
	}

	@Override
	public boolean isArray() {
		return true && !disableArray;
	}

	@Override
	public boolean isSubtypeOf(TypeReference type) {
		if (type.isArray()) {
			return componentType.isSubtypeOf(type.getComponentType());
		} else {
			return false;
		}
	}

	@Override
	public boolean substituteTypeReference(TypeReference targetType, TypeReference newType) {
		if (componentType == targetType) {
			componentType = newType;
			return true;
		}
		return false;
	}

}
