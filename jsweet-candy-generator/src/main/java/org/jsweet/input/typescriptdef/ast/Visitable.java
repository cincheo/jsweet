package org.jsweet.input.typescriptdef.ast;


public interface Visitable {

	void setHidden(boolean hidden);

	boolean isHidden();

	void accept(Visitor visitor);
	
}
