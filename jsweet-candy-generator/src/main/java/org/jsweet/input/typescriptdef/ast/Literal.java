package org.jsweet.input.typescriptdef.ast;


public class Literal extends AbstractAstNode {

	public Literal(Token token, String value) {
		super(token);
		this.value = value;
	}

	protected String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitLiteral(this);
	}

	public Literal copy() {
		return new Literal(null, this.value);
	}

}
