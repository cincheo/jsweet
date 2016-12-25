package org.jsweet.input.typescriptdef.ast;

/**
 * @author Renaud Pawlak
 */
public class Token {

	public int type;

	/**
	 * Creates a new token
	 * 
	 * @param type
	 *            the type as defined in the lexer
	 * @param fileName
	 *            the name of the file from where the token was extracted
	 * @param text
	 *            the text of the token
	 * @param line
	 *            the line number where the token appears in the file
	 * @param charBegin
	 *            the character where it begins in the file
	 * @param charEnd
	 *            the character where it ends in the file
	 */
	public Token(int type, String fileName, String text, int line,
			int charBegin, int charEnd) {

		this.type = type;
		this.fileName = fileName;
		this.text = text;
		this.line = line;
		this.charBegin = charBegin;
		this.charEnd = charEnd;
	}

	public String getLocation() {
		return "" + fileName + ":" + line + "(" + charBegin + ")";
	}

	String fileName;
	String text;
	int line;
	int charBegin;
	int charEnd;

	// public boolean equals(Object o) {
	// System.err.println("equals("+this+","+o+")");
	// return text.equals(o.toString());
	// }

	public String toString() {
		return text;
	}

	public int getCharBegin() {
		return charBegin;
	}

	public int getCharEnd() {
		return charEnd;
	}

	public int getLine() {
		return line;
	}

	public String getText() {
		return text;
	}

	public String getFileName() {
		return fileName;
	}

	public int getType() {
		return type;
	}

}
