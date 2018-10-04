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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jsweet.transpiler.ModuleKind;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import source.api.AccessStaticMethod;
import source.api.ArrayBuffers;
import source.api.Booleans;
import source.api.CastMethods;
import source.api.Characters;
import source.api.Equals;
import source.api.ErasingJava;
import source.api.ExpressionBuilderTest;
import source.api.ExpressionBuilderTest2;
import source.api.ForeachIteration;
import source.api.J4TSInvocations;
import source.api.JdkInvocations;
import source.api.Numbers;
import source.api.PrimitiveInstantiation;
import source.api.PromisesAsyncAwait;
import source.api.QualifiedInstantiation;
import source.api.Strings;
import source.api.ThreadLocalFake;
import source.api.WrongJdkInvocations;

public class ApiTests extends AbstractTest {

	// J4TS messes up with forbidden invocations...
	@Ignore
	@Test
	public void testWrongJdkInvocations() {
		transpile(logHandler -> {
			// assertEquals(11, logHandler.reportedProblems.size());
			assertEquals(19, logHandler.reportedSourcePositions.get(0).getStartLine());
			assertEquals(39, logHandler.reportedSourcePositions.get(1).getStartLine());
			// assertEquals(41,
			// logHandler.reportedSourcePositions.get(2).getStartLine());
			assertEquals(48, logHandler.reportedSourcePositions.get(3).getStartLine());
			assertEquals(52, logHandler.reportedSourcePositions.get(4).getStartLine());
			assertEquals(72, logHandler.reportedSourcePositions.get(5).getStartLine());
			// assertEquals(78,
			// logHandler.reportedSourcePositions.get(6).getStartLine());
			// assertEquals(83,
			// logHandler.reportedSourcePositions.get(7).getStartLine());
			assertEquals(87, logHandler.reportedSourcePositions.get(8).getStartLine());
			assertEquals(97, logHandler.reportedSourcePositions.get(9).getStartLine());
			assertEquals(118, logHandler.reportedSourcePositions.get(10).getStartLine());
			// assertEquals(120,
			// logHandler.reportedSourcePositions.get(11).getStartLine());
			assertEquals(127, logHandler.reportedSourcePositions.get(12).getStartLine());
			assertEquals(131, logHandler.reportedSourcePositions.get(13).getStartLine());
		}, getSourceFile(J4TSInvocations.class), getSourceFile(WrongJdkInvocations.class));
	}

	@Test
	public void testJ4TSInvocations() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(J4TSInvocations.class));
	}

	@Test
	public void testJdkInvocations() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("test", result.<String>get("s1"));
			assertEquals("m1", result.<String>get("s2"));
			assertEquals("e", result.<String>get("s3"));
			assertEquals("testc", result.<String>get("s4"));
			assertEquals(2, result.<Number>get("i1").intValue());
			assertEquals(-1, result.<Number>get("i2").intValue());
			assertEquals(4, result.<Number>get("l").intValue());
			assertEquals("t1st", result.<String>get("r"));
		}, getSourceFile(JdkInvocations.class));
	}

	@Test
	public void testThreadLocal() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("coucou", result.get("out"));
			assertEquals("nakach", result.get("out2"));
			assertEquals("nakach", result.get("out3"));
		}, getSourceFile(ThreadLocalFake.class));
	}

	@Test
	public void testPromisesAsyncAwaits() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());

			// async await style
			assertNotNull(result.get("t0"));
			assertNotNull(result.get("t1"));
			assertNotNull(result.get("t2"));

			long t0 = result.<Number>get("t0").longValue();
			long t1 = result.<Number>get("t1").longValue();
			long t2 = result.<Number>get("t2").longValue();

			assertTrue(t1 - t0 >= PromisesAsyncAwait.WAIT_BETWEEN_STEPS_MS);
			assertTrue(t2 - t1 >= PromisesAsyncAwait.WAIT_BETWEEN_STEPS_MS);

			assertEquals(new Integer(42), result.get("r1"));
			assertEquals("my answer", result.get("r2"));
			assertEquals("supermessage", result.get("e"));

			// promise style
			assertNotNull(result.get("2_t0"));
			assertNotNull(result.get("2_t1"));
			assertNotNull(result.get("2_t2"));

			t0 = result.<Number>get("2_t0").longValue();
			t1 = result.<Number>get("2_t1").longValue();
			t2 = result.<Number>get("2_t2").longValue();

			assertTrue(t1 - t0 >= PromisesAsyncAwait.WAIT_BETWEEN_STEPS_MS);
			assertTrue(t2 - t1 >= PromisesAsyncAwait.WAIT_BETWEEN_STEPS_MS);

			assertEquals(new Integer(42), result.get("2_r1"));
			assertEquals("my answer", result.get("2_r2"));
			assertEquals("supermessage", result.get("2_e"));
		}, getSourceFile(PromisesAsyncAwait.class));
	}

	@Test
	public void testForeachIteration() {
		eval((logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong behavior output trace", "abc", r.get("out"));
		}, getSourceFile(ForeachIteration.class));
	}

	@Test
	public void testPrimitiveInstantiation() {
		transpile(logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(PrimitiveInstantiation.class));
	}

	@Test
	public void testAccessStaticMethod() {
		transpile(logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(AccessStaticMethod.class));
	}

	@Test
	public void testQualifiedInstantiation() {
		transpile(logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(QualifiedInstantiation.class));
	}

	@Test
	public void testCastMethods() {
		transpile(logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(CastMethods.class));
	}

	@Test
	public void testErasingJava() {
		transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(ErasingJava.class));
	}

	@Test
	public void testStrings() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals(
					"b,bc,c,bc,3,true,ab,32,b,0,false,true,source.api.Strings,Strings,abc,cdcdcd,true,false,true,false,true,true,false,a,aa",
					r.get("trace"));
		}, getSourceFile(Strings.class));
	}

	@Test
	public void testCharacters() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();

			assertEquals(true, r.get("switch_int"));
			assertEquals(true, r.get("switch_char"));
			assertEquals(true, r.get("switch_char_cast_int"));
			assertEquals(true, r.get("switch_char_cast_char"));
			assertEquals(true, r.get("switch_int_cast_int"));
			assertEquals(true, r.get("switch_int_cast_char"));

		}, getSourceFile(Characters.class));
	}

	@Test
	public void testNumbers() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(Numbers.class));
	}

	@Test
	public void testBooleans() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(Booleans.class));
	}

	@Test
	public void testArrayBuffers() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals("0,0,1", r.get("trace"));
		}, getSourceFile(ArrayBuffers.class));
	}

	@Test
	public void testExpressionBuilder() {
		// evalWithJavaSupport((logHandler, r) -> {
		// logHandler.assertNoProblems();
		// Assert.assertEquals(30, (int) r.get("result"));
		// Assert.assertEquals(30, (int) r.get("result2"));
		// }, getSourceFile(ExpressionBuilderTest.class),
		// getSourceFile(ExpressionBuilderTest2.class));
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals(30, (int) r.get("result"));
		}, getSourceFile(ExpressionBuilderTest.class));
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals(30, (int) r.get("result2"));
		}, getSourceFile(ExpressionBuilderTest2.class));
	}

	@Test
	public void testEquals() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals("false,true,false,true,false,true,false,false,true", r.get("trace"));
		}, getSourceFile(Equals.class));
	}

}
