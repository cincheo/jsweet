package org.jsweet.input.typescriptdef.ast;

import java.util.ArrayList;
import java.util.HashSet;

public class VariableDeclaration extends AbstractTypedDeclaration {

	private boolean optional = false;
	private boolean readonly = false;
	private Literal initializer = null;

	public VariableDeclaration(Token token, String name, TypeReference type, boolean optional, boolean readonly) {
		super(token, name, type);
		this.optional = optional;
		this.readonly = readonly;
		// System.out.println("new VariableDeclaration: " + this);
	}

	@Override
	public void accept(Visitor v) {
		v.visitVariableDeclaration(this);
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	@Override
	public VariableDeclaration copy() {
		VariableDeclaration copy = new VariableDeclaration(null, name, getType().copy(), optional, readonly);
		copy.setModifiers(this.getModifiers() == null ? null : new HashSet<String>(this.getModifiers()));
		copy.setStringAnnotations(
				this.getStringAnnotations() == null ? null : new ArrayList<String>(getStringAnnotations()));
		copy.setDocumentation(this.getDocumentation());
		copy.setInitializer(initializer == null ? null : initializer.copy());
		return copy;
	}

	public Literal getInitializer() {
		return initializer;
	}

	public void setInitializer(Literal initializer) {
		this.initializer = initializer;
	}

	public final boolean isReadonly() {
		return readonly;
	}

	public final void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

}
