package org.jsweet.input.typescriptdef.parser;

public class GenParser {

	public static void main(String[] args) throws Exception {
		java_cup.Main.main(new String[] { "-expect", "0", "-package", "org.jsweet.input.typescriptdef.parser", "-expect", "0", "-parser", "TypescriptDefParser",
				"typescriptdef.cup" });
		JFlex.Main.main(new String[] { "typescriptdef.lex" });
	}
	
}
