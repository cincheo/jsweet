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

import static org.junit.Assert.fail;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Assert;
import org.junit.Test;

import source.typing.ArraysOfLambdas;
import source.typing.ClassTypeAsFunction;
import source.typing.ClassTypeAsTypeOf;
import source.typing.CustomLambdas;
import source.typing.CustomStringTypes;
import source.typing.InvalidIndexedAccesses;
import source.typing.Lambdas;
import source.typing.Numbers;
import source.typing.StringTypesUsage;
import source.typing.Tuples;
import source.typing.Unions;
import source.typing.VoidType;
import source.typing.WrongUnions;

public class TypingTests extends AbstractTest {

	@Test
	public void testNumbers() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(Numbers.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testLambdas() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			EvaluationResult r = transpiler.eval(logHandler, getSourceFile(Lambdas.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("a", r.get("result"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testVoidType() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(VoidType.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testClassTypeAsTypeOf() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(ClassTypeAsTypeOf.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testClassTypeAsFunction() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(ClassTypeAsFunction.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testArraysOfLambdas() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(ArraysOfLambdas.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testStringTypesUsage() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(StringTypesUsage.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testCustomStringTypes() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(CustomStringTypes.class));
			logHandler.assertReportedProblems();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}
	
	@Test
	public void testTuples() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			EvaluationResult r = transpiler.eval(logHandler, getSourceFile(Tuples.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("test", r.get("0_init"));
			Assert.assertEquals(10, r.<Number> get("1_init").intValue());
			Assert.assertEquals("ok", r.get("0_end"));
			Assert.assertEquals(9, r.<Number> get("1_end").intValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testUnions() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			EvaluationResult r = transpiler.eval(logHandler, getSourceFile(Unions.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("test", r.get("union"));
			Assert.assertEquals("test", r.get("s"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testWrongUnions() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(WrongUnions.class));
			Assert.assertEquals("Wrong number of errors", 6, logHandler.reportedProblems.size());
			logHandler.assertReportedProblems(JSweetProblem.UNION_TYPE_MISMATCH, JSweetProblem.UNION_TYPE_MISMATCH, JSweetProblem.UNION_TYPE_MISMATCH,
					JSweetProblem.UNION_TYPE_MISMATCH, JSweetProblem.UNION_TYPE_MISMATCH, JSweetProblem.UNION_TYPE_MISMATCH);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testInvalidIndexedAccesses() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(InvalidIndexedAccesses.class));
			Assert.assertEquals("There should be one error", 1, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong type of expected error", JSweetProblem.INDEXED_SET_TYPE_MISMATCH, logHandler.reportedProblems.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testCustomLambdas() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			Assert.assertEquals("test1", r.get("lambda"));
		} , getSourceFile(CustomLambdas.class));
	}

	
}
