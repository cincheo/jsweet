package org.jsweet.input.typescriptdef.ast;

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
