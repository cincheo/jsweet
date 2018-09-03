/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.test.transpiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import source.structural.globalclasses.Globals;
import source.tscomparison.AbstractClasses;
import source.tscomparison.ActualScoping;
import source.tscomparison.CompileTimeWarnings;
import source.tscomparison.OtherThisExample;
import source.tscomparison.SaferVarargs;
import source.tscomparison.StrongerTyping;
import source.tscomparison.ThisIsThis;

public class TsComparisonTest extends AbstractTest {

	@Ignore
	@Test
	public void strongerTypingTest() {
		// jsweet part
		SourceFile file = getSourceFile(StrongerTyping.class);
		eval(ModuleKind.none, null, file);

		// ts part
		evalTs(getTsSourceFile(file));
	}

	@Ignore
	@Test
	public void compileTimeWarningsTest() {
		// jsweet part
		SourceFile file = getSourceFile(CompileTimeWarnings.class);
		eval(ModuleKind.none, null, file);

		// ts part
		evalTs(getTsSourceFile(file));
	}

	@Test
	public void abstractClassesTest() {
		// jsweet part
		SourceFile file = getSourceFile(AbstractClasses.class);
		eval(ModuleKind.none, null, file);

		// ts part
		evalTs(getTsSourceFile(file));
	}

	@Ignore
	@Test
	public void actualScopingTest() {
		// jsweet part
		SourceFile file = getSourceFile(ActualScoping.class);
		eval(ModuleKind.none, null, file);

		// ts part
		evalTs(getTsSourceFile(file));
	}

	@Ignore
	@Test
	public void thisIsThisTest() {
		// jsweet part
		SourceFile file = getSourceFile(ThisIsThis.class);
		eval(ModuleKind.none, null, file);

		// ts part
		evalTs(getTsSourceFile(file));
	}

	@Test
	public void otherThisExampleTest() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			System.out.println("" + r.get("results"));
			Assert.assertEquals("3,4,5", "" + r.get("results"));
		}, getSourceFile(Globals.class), getSourceFile(OtherThisExample.class));

	}

	@Ignore
	@Test
	public void saferVarargsTest() {
		// jsweet part

		SourceFile file = getSourceFile(SaferVarargs.class);
		eval(ModuleKind.none, (ctx, result) -> {
			assertEquals("foo", result.get("firstArg"));
		}, file);

		// ts part
		EvaluationResult result = evalTs(getTsSourceFile(file));

		assertTrue(result.get("firstArg").getClass().isArray());
		Object[] res = (Object[]) result.get("firstArg");
		assertEquals("blah", res[0]);
		assertEquals("bluh", res[1]);
	}

	private TsSourceFile getTsSourceFile(SourceFile jsweetSourceFile) {
		String javaTestFilePath = jsweetSourceFile.getJavaFile().getAbsolutePath();
		File tsFile = new File(javaTestFilePath.substring(0, javaTestFilePath.length() - 5) + ".ts");
		TsSourceFile tsSourceFile = new TsSourceFile(tsFile);
		return tsSourceFile;
	}

	private EvaluationResult evalTs(TsSourceFile sourceFile) {
		return evalTs(sourceFile, false);
	}

	private EvaluationResult evalTs(TsSourceFile sourceFile, boolean expectErrors) {
		try {
			System.out.println("running tsc: " + sourceFile);
			TestTranspilationHandler logHandler = new TestTranspilationHandler();

			transpilerTest().getTranspiler().setTsOutputDir(sourceFile.getTsFile().getParentFile());
			EvaluationResult result = transpilerTest().getTranspiler().eval(logHandler, sourceFile);
			FileUtils.deleteQuietly(sourceFile.getJsFile());

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot compile Typescript file: " + sourceFile);
			return null;
		}
	}

	private class TsSourceFile extends SourceFile {
		public TsSourceFile(File tsFile) {
			super(null);
			this.setTsFile(tsFile);
		}

		@Override
		public String toString() {
			return getTsFile().toString();
		}
	}
}
