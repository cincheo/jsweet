package org.jsweet.input.typescriptdef.parser;

import java.io.PrintStream;

import org.jsweet.input.typescriptdef.ast.Token;

public class SyntaxError {

	public Token origin;
	public String message;
	public Exception exception;

	public SyntaxError(Token origin, String message) {
		super();
		this.origin = origin;
		this.message = message;
		this.exception = new Exception();
		//this.exception.printStackTrace();
	}

	@Override
	public String toString() {
		return "SYNTAX ERROR"
				+ ": "
				+ message
				+ (origin != null ? " at '" + origin.toString() + "'" + " "
						+ origin.getLocation() : "");
	}

	public void printStackTrace(PrintStream stream) {
		exception.printStackTrace(stream);
	}
	
}
