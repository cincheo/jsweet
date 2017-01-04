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

import java.util.LinkedList;
import java.util.List;

/**
 * A type reference defined as a union type.
 * 
 * @author Renaud Pawlak
 */
public class UnionTypeReference extends TypeReference {

	public enum Selected {
		NONE, PENDING, LEFT, RIGHT;
		public Selected inverse() {
			switch (this) {
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			default:
				return NONE;
			}
		}
	}

	private TypeReference leftType;
	private TypeReference rightType;
	private Selected selected = Selected.NONE;
	private boolean intersection = false;

	public UnionTypeReference(Token token, TypeReference leftType, TypeReference rightType) {
		this(token, leftType, rightType, false);
	}

	public UnionTypeReference(Token token, TypeReference leftType, TypeReference rightType, boolean intersection) {
		super(token, intersection ? "&" : "|", null);
		setLeftType(leftType);
		setRightType(rightType);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitUnionTypeReference(this);
	}

	public TypeReference getLeftType() {
		return leftType;
	}

	public void setLeftType(TypeReference leftType) {
		this.leftType = leftType;
		if (leftType != null && leftType.getName() != null && leftType.getName().equals("void")) {
			selected = Selected.RIGHT;
		}
	}

	public TypeReference getRightType() {
		return rightType;
	}

	public void setRightType(TypeReference rightType) {
		this.rightType = rightType;
		if (rightType != null && rightType.getName() != null && rightType.getName().equals("void")) {
			selected = Selected.LEFT;
		}
	}

	@Override
	public UnionTypeReference copy(boolean copyDeclarations) {
		UnionTypeReference copy = new UnionTypeReference(null, leftType.copy(copyDeclarations),
				rightType.copy(copyDeclarations));
		copy.typeArguments = DeclarationHelper.copyReferences(typeArguments, copyDeclarations);
		copy.selected = this.selected;
		return copy;
	}

	@Override
	public UnionTypeReference copy() {
		return copy(false);
	}

	@Override
	public String toString() {
		switch (selected) {
		case LEFT:
			return leftType.toString();
		case RIGHT:
			return rightType.toString();
		case PENDING:
			return leftType.toString() + " |? " + rightType.toString();
		default:
			return leftType.toString() + " | " + rightType.toString();
		}
	}

	public Selected getSelected() {
		// if (leftT)

		return selected;
	}

	public void setSelected(Selected selected) {
		this.selected = selected;
	}

	public TypeReference getOperand(Selected selected) {
		switch (selected) {
		case LEFT:
			return leftType;
		case RIGHT:
			return rightType;
		default:
			return null;
		}
	}

	public TypeReference getSelectedType() {
		switch (selected) {
		case LEFT:
			return leftType;
		case RIGHT:
			return rightType;
		default:
			return null;
		}
	}

	public List<TypeReference> getTypes() {
		List<TypeReference> types = new LinkedList<>();
		TypeReference selectedType = getSelectedType();
		if (selectedType != null) {
			if (selectedType instanceof UnionTypeReference) {
				types.addAll(((UnionTypeReference) selectedType).getTypes());
			} else {
				types.add(selectedType);
			}
		} else {
			// add both types if union hasn't been resolved
			if (leftType instanceof UnionTypeReference) {
				types.addAll(((UnionTypeReference) leftType).getTypes());
			} else {
				types.add(leftType);
			}

			if (rightType instanceof UnionTypeReference) {
				types.addAll(((UnionTypeReference) rightType).getTypes());
			} else {
				types.add(rightType);
			}
		}

		return types;
	}

	@Override
	public Type getDeclaration() {
		switch (selected) {
		case LEFT:
			return leftType.getDeclaration();
		case RIGHT:
			return rightType.getDeclaration();
		default:
			return super.getDeclaration();
		}
	}

	@Override
	public String getName() {
		switch (selected) {
		case LEFT:
			return leftType.getName();
		case RIGHT:
			return rightType.getName();
		default:
			return super.getName();
		}
	}

	@Override
	public String getSimpleName() {
		switch (selected) {
		case LEFT:
			return leftType.getSimpleName();
		case RIGHT:
			return rightType.getSimpleName();
		default:
			return super.getSimpleName();
		}
	}

	@Override
	public TypeDeclaration getObjectType() {
		switch (selected) {
		case LEFT:
			return leftType.getObjectType();
		case RIGHT:
			return rightType.getObjectType();
		default:
			return super.getObjectType();
		}
	}

	@Override
	public TypeReference[] getTypeArguments() {
		switch (selected) {
		case LEFT:
			return leftType.getTypeArguments();
		case RIGHT:
			return rightType.getTypeArguments();
		default:
			return super.getTypeArguments();
		}
	}

	@Override
	public String getWrappingTypeName() {
		switch (selected) {
		case LEFT:
			return leftType.getWrappingTypeName();
		case RIGHT:
			return rightType.getWrappingTypeName();
		default:
			return super.getWrappingTypeName();
		}
	}

	@Override
	public boolean isAnonymous() {
		switch (selected) {
		case LEFT:
			return leftType.isAnonymous();
		case RIGHT:
			return rightType.isAnonymous();
		default:
			return super.isAnonymous();
		}
	}

	@Override
	public boolean isHidden() {
		switch (selected) {
		case LEFT:
			return leftType.isHidden();
		case RIGHT:
			return rightType.isHidden();
		default:
			return super.isHidden();
		}
	}

	@Override
	public boolean isObjectType() {
		switch (selected) {
		case LEFT:
			return leftType.isObjectType();
		case RIGHT:
			return rightType.isObjectType();
		default:
			return super.isObjectType();
		}
	}

	@Override
	public boolean isPrimitive() {
		switch (selected) {
		case LEFT:
			return leftType.isPrimitive();
		case RIGHT:
			return rightType.isPrimitive();
		default:
			return super.isPrimitive();
		}
	}

	@Override
	public boolean isStringType() {
		switch (selected) {
		case LEFT:
			return leftType.isStringType();
		case RIGHT:
			return rightType.isStringType();
		default:
			return super.isStringType();
		}
	}

	@Override
	public boolean isArray() {
		switch (selected) {
		case LEFT:
			return leftType.isArray();
		case RIGHT:
			return rightType.isArray();
		default:
			return super.isArray();
		}
	}

	@Override
	public TypeReference getComponentType() {
		switch (selected) {
		case LEFT:
			return leftType.getComponentType();
		case RIGHT:
			return rightType.getComponentType();
		default:
			return super.getComponentType();
		}
	}

	@Override
	public boolean isSubtypeOf(TypeReference type) {
		switch (selected) {
		case LEFT:
			return leftType.isSubtypeOf(type);
		case RIGHT:
			return rightType.isSubtypeOf(type);
		default:
			return super.isSubtypeOf(type);
		}
	}

	@Override
	public boolean substituteTypeReference(TypeReference targetType, TypeReference newType) {
		if (getLeftType() == targetType) {
			setLeftType(newType);
			return true;
		}
		if (getRightType() == targetType) {
			setRightType(newType);
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		switch (selected) {
		case LEFT:
			return leftType.equals(obj);
		case RIGHT:
			return rightType.equals(obj);
		default:
			return super.equals(obj);
		}
	}

	public boolean isIntersection() {
		return intersection;
	}

	public void setIntersection(boolean intersection) {
		this.intersection = intersection;
	}

}
