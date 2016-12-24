package org.jsweet.input.typescriptdef.ast;

import org.jsweet.input.typescriptdef.util.Util;

public class QualifiedDeclaration<T extends Declaration> {
	private T declaration;
	private String qualifiedDeclarationName;

	public QualifiedDeclaration(T declaration, String qualifiedDeclarationName) {
		this.declaration = declaration;
		this.qualifiedDeclarationName = qualifiedDeclarationName;
	}

	public T getDeclaration() {
		return declaration;
	}

	public String getQualifiedDeclarationName() {
		return qualifiedDeclarationName;
	}

	@Override
	public String toString() {
		return qualifiedDeclarationName;
	}

	public String getQualifier() {
		return Util.getQualifier(qualifiedDeclarationName);
	}

	public String getSimpleName() {
		return Util.getSimpleName(qualifiedDeclarationName);
	}

}
