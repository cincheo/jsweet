package org.jsweet.input.typescriptdef.ast;


public class FullFunctionDeclaration {

	public FullFunctionDeclaration(ModuleDeclaration declaringModule, TypeDeclaration declaringType, FunctionDeclaration function) {
		super();
		this.declaringType = declaringType;
		this.function = function;
	}

	public TypeDeclaration declaringType;
	public ModuleDeclaration declaringModule;
	public FunctionDeclaration function;

	@Override
	public String toString() {
		return (declaringModule == null ? "" : declaringModule.getName() + ".") + (declaringType == null ? "" : declaringType.getName() + ".") + function;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FullFunctionDeclaration)) {
			return false;
		}
		FullFunctionDeclaration d = (FullFunctionDeclaration) obj;
		return this.function == d.function;
	}

	@Override
	public int hashCode() {
		return function.hashCode();
	}

}
