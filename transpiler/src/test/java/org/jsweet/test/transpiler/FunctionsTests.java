package org.jsweet.test.transpiler;

import org.junit.Test;

import source.functions.BasicFunctions;

public class FunctionsTests extends AbstractTest {

	@Test
	public void testBasicFunctions() {
		eval((h, r) -> {
			h.assertNoProblems();
		}, getSourceFile(BasicFunctions.class));
	}
	
	
}
