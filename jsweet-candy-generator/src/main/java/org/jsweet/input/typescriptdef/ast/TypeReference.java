package org.jsweet.input.typescriptdef.ast;

import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.util.Util;

public class TypeReference extends AbstractAstNode implements NamedElement {

	protected TypeReference[] typeArguments;
	protected String name;
	protected TypeDeclaration objectType;
	// cache to the declaration
	transient private Type declaration;
	private boolean typeOf = false;

	public boolean isTypeOf() {
		return typeOf;
	}

	public void setTypeOf(boolean typeOf) {
		this.typeOf = typeOf;
	}

	public TypeReference(Token token, String name, TypeReference[] typeArguments) {
		super(token);
		if ("$tuple$".equals(name)) {
			name = JSweetDefTranslatorConfig.TUPLE_CLASSES_PACKAGE + "."
					+ JSweetDefTranslatorConfig.TUPLE_CLASSES_PREFIX + typeArguments.length;
		}
		this.name = name;
		this.typeArguments = typeArguments;
	}

	public TypeReference(Token token, Type type, TypeReference[] typeArguments) {
		super(token);
		this.name = type.getName();
		this.typeArguments = typeArguments;
		this.declaration = type;
	}

	public TypeReference(Token token, Declaration[] members) {
		super(token);
		this.objectType = new TypeDeclaration(token, "object_type", null, null, null, members);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (objectType != null) {
			sb.append(objectType.toString());
		} else {
			sb.append(name);
			if (typeArguments != null && typeArguments.length > 0) {
				sb.append("<");
				for (TypeReference t : typeArguments) {
					sb.append(t);
					sb.append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(">");
			}
		}
		return sb.toString();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitTypeReference(this);
	}

	public TypeReference[] getTypeArguments() {
		return this.typeArguments;
	}

	public void setTypeArguments(TypeReference[] typeArguments) {
		this.typeArguments = typeArguments;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public boolean isObjectType() {
		return objectType != null;
	}

	public TypeDeclaration getObjectType() {
		return objectType;
	}

	@Override
	public boolean isAnonymous() {
		return name == null;
	}

	public boolean isStringType() {
		return name != null && (name.startsWith("\"") && name.endsWith("\""));
	}

	public boolean isPrimitive() {
		return DeclarationHelper.isPrimitiveType(name);
	}

	public String getWrappingTypeName() {
		if ("any".equals(name)) {
			return "java.lang.Object";
		} else if (isPrimitive()) {
			return StringUtils.capitalize(name);
		} else {
			return name;
		}
	}

	public String getSimpleName() {
		return Util.getSimpleName(name);
	}

	public String getQualifier() {
		return Util.getQualifier(name);
	}

	public boolean isQualified() {
		return Util.isQualified(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		TypeReference tr = (TypeReference) obj;
		if (name == null && tr.name == null) {
			if (objectType != null && tr.objectType != null) {
				return DeclarationHelper.areDeclarationsEqual(objectType.getMembers(), tr.objectType.getMembers());
			}
			return false;
		}

		if (name == null && tr.name != null) {
			return false;
		}
		if (!name.equals(tr.name)) {
			return false;
		}
		return true;
	}

	public TypeReference copy(boolean copyDeclarations) {
		TypeReference copy = new TypeReference(null, getName(), DeclarationHelper.copyReferences(typeArguments, copyDeclarations));
		if (objectType != null) {
			copy.objectType = objectType.copy();
		}
		if (copyDeclarations) {
			copy.declaration = declaration;
		}
		return copy;
	}

	public TypeReference copy() {
		return copy(false);
	}

	public Type getDeclaration() {
		return declaration;
	}

	public void setDeclaration(Type type) {
		this.declaration = type;
	}

	public boolean isArray() {
		return false;
	}

	public TypeReference getComponentType() {
		return this;
	}

	public boolean isSubtypeOf(TypeReference type) {
		if (declaration == null) {
			throw new RuntimeException("unattributed type reference: " + this);
		}
		if (!type.isArray() && type.declaration == null) {
			throw new RuntimeException("unattributed type reference: " + type);
		}
		return declaration.isSubtypeOf(type.declaration);
	}

	/**
	 * Substitutes a type reference if found in the node.
	 * 
	 * @param targetType
	 *            the type reference to be substituted, if found in the node
	 * @param newType
	 *            the new type reference that will be substituted to target type
	 * @return true if the target type was found and substituted
	 */
	public boolean substituteTypeReference(TypeReference targetType, TypeReference newType) {
		if (typeArguments != null) {
			for (int i = 0; i < typeArguments.length; i++) {
				if (typeArguments[i] == targetType) {
					typeArguments[i] = newType;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getOriginalName() {
		return name;
	}

	@Override
	public void setOriginalName(String name) {
		this.name = name;
	}

	public boolean isTuple() {
		return name != null && name.startsWith(
				JSweetDefTranslatorConfig.TUPLE_CLASSES_PACKAGE + "." + JSweetDefTranslatorConfig.TUPLE_CLASSES_PREFIX);
	}

}
