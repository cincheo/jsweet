package org.jsweet.input.typescriptdef.ast;

import static org.apache.commons.lang3.StringUtils.strip;

import org.apache.commons.lang3.StringUtils;

public class ReferenceDeclaration extends AbstractDeclaration {

	private String referencedName;

	public ReferenceDeclaration(Token token, String alias, String referencedName) {
		super(token, alias);

		this.referencedName = strip(referencedName, "[\"']");
	}

	@Override
	public void accept(Visitor v) {
		v.visitReferenceDeclaration(this);
	}

	@Override
	public Declaration copy() {
		ReferenceDeclaration copy = new ReferenceDeclaration(null, name, referencedName);
		copy.setDocumentation(getDocumentation());
		copy.name = name;
		copy.referencedName = referencedName;
		return copy;
	}

	public String getReferencedName() {
		return referencedName;
	}

	public boolean isImport() {
		return !isExport();
	}

	public boolean isExport() {
		return StringUtils.isBlank(name);
	}

	@Override
	public String toString() {
		if (isExport()) {
			return "export = " + referencedName;
		} else {
			return "import " + name + " = " + referencedName;
		}
	}

	public void setReferencedName(String referencedName) {
		this.referencedName = referencedName;
	}
}
