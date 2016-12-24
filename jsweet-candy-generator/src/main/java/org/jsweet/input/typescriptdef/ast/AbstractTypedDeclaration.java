package org.jsweet.input.typescriptdef.ast;

public abstract class AbstractTypedDeclaration extends AbstractDeclaration implements TypedDeclaration {

	private TypeReference type;

	public AbstractTypedDeclaration(Token token, String name, TypeReference type) {
		super(token, name);
		if (type == null) {
			this.type = new TypeReference(null, "any", null);
		} else {
			this.type = type;
		}
	}

	@Override
	public String toString() {
		return super.toString() + ":" + type;
	}

	public TypeReference getType() {
		return type;
	}

	public void setType(TypeReference type) {
		this.type = type;
	}

}
