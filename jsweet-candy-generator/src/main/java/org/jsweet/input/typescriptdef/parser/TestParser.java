package org.jsweet.input.typescriptdef.parser;

import java.io.File;

public class TestParser {

	public static void main(String[] args) throws Exception {
		TypescriptDefParser.parseFile(new File("test.d.ts"));
	}
	
}
