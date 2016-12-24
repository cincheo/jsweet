package org.jsweet.input.typescriptdef.ast;


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
		TypeParameterDeclaration copy = new TypeParameterDeclaration(null, getName());
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
