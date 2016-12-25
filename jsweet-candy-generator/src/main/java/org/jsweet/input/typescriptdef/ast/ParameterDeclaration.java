package org.jsweet.input.typescriptdef.ast;

import java.util.ArrayList;
import java.util.HashSet;

public class ParameterDeclaration extends VariableDeclaration {

	private boolean varargs = false;

	public ParameterDeclaration(Token token, String name, TypeReference type, boolean optional, boolean varargs) {
		super(token, name, type, optional, false);
		this.varargs = varargs;
	}

	@Override
	public void accept(Visitor v) {
		v.visitParameterDeclaration(this);
	}

	public boolean isVarargs() {
		return varargs;
	}

	public void setVarargs(boolean varargs) {
		this.varargs = varargs;
	}

	@Override
	public ParameterDeclaration copy() {
		ParameterDeclaration copy = new ParameterDeclaration(null, name, getType().copy(), isOptional(), isVarargs());
		copy.setModifiers(this.getModifiers() == null ? null : new HashSet<String>(this.getModifiers()));
		copy.setStringAnnotations(this.getStringAnnotations() == null ? null : new ArrayList<String>(
				getStringAnnotations()));
		copy.setDocumentation(this.getDocumentation());
		return copy;
	}

}
