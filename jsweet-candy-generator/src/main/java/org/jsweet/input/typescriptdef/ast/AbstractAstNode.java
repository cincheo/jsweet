package org.jsweet.input.typescriptdef.ast;

public abstract class AbstractAstNode implements AstNode {

	private Token token;
	public int nodeTypeId = -1;
	private boolean hidden = false;

	public AbstractAstNode(Token token) {
		super();
		// if(token==null) {
		// throw new RuntimeException("token cannot be null");
		// }
		this.token = token;
	}

	@Override
	public Token getToken() {
		return token;
	}

	@Override
	public String getLocation() {
		if (token != null) {
			return token.getLocation();
		} else {
			return "<unknown location>";
		}
	}

	@Override
	public String toString() {
		return token == null ? "null" : token.toString();
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

}
