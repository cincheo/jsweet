package org.jsweet.input.typescriptdef.ast;

import java.util.HashSet;

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
