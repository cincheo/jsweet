package org.jsweet.test.transpiler;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import source.debug.Simple;

public class DebugTests extends AbstractTest {

	@BeforeClass
	public static void start() {
		transpiler.setDebugMode(true);
	}

	@AfterClass
	public static void end() {
		transpiler.setDebugMode(false);
	}

	@Test
	public void testSimple() {
		transpile(ModuleKind.none, handler -> {
			handler.assertReportedProblems(JSweetProblem.MAPPED_TSC_ERROR, JSweetProblem.MAPPED_TSC_ERROR,
					JSweetProblem.MAPPED_TSC_ERROR, JSweetProblem.MAPPED_TSC_ERROR);
		}, getSourceFile(Simple.class));
	}

}