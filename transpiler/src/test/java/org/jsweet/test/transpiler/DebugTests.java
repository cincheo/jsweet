package org.jsweet.test.transpiler;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.junit.Test;

import source.debug.Simple;

public class DebugTests extends AbstractTest {

	@Test
	public void testSimple() {
		transpilerTest().getTranspiler().setDebugMode(true);
		transpile(ModuleKind.none, handler -> {
			handler.assertReportedProblems(JSweetProblem.MAPPED_TSC_ERROR, JSweetProblem.MAPPED_TSC_ERROR,
					JSweetProblem.MAPPED_TSC_ERROR, JSweetProblem.MAPPED_TSC_ERROR);
		}, getSourceFile(Simple.class));
	}

}