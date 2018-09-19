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

import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Assert;
import org.junit.Test;

import source.calculus.Chars;
import source.calculus.Integers;
import source.calculus.Longs;
import source.calculus.MathApi;
import source.calculus.Null;
import source.calculus.Numbers;
import source.calculus.Operators;
import source.calculus.Strings;

public class CalculusTests extends AbstractTest {

	@Test
	public void testIntegers() {
		try {
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			EvaluationResult r = transpilerTest().getTranspiler().eval("Java", logHandler,
					getSourceFile(Integers.class));
			logHandler.assertNoProblems();
			Assert.assertEquals("3", r.get("i").toString());
			Assert.assertEquals((Integer) 1, r.get("i1"));
			Assert.assertEquals((Integer) 1, r.get("i2"));
			Assert.assertEquals((Double) 1.5, r.get("f1"));
			Assert.assertEquals((Double) 1.5, r.get("f2"));
			Assert.assertEquals((Double) 7.5, r.get("f3"));
			Assert.assertEquals((Integer) 7, r.get("i3"));
			Assert.assertEquals((Integer) 7, r.get("i4"));
			Assert.assertEquals((Integer) 1, r.get("j"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals("3", r.get("i").toString());
			Assert.assertEquals((Integer) 1, r.get("i1"));
			Assert.assertEquals((Integer) 1, r.get("i2"));
			Assert.assertEquals((Double) 1.5, r.get("f1"));
			Assert.assertEquals((Double) 1.5, r.get("f2"));
			Assert.assertEquals((Double) 7.5, r.get("f3"));
			Assert.assertEquals((Integer) 7, r.get("i3"));
			Assert.assertEquals((Integer) 7, r.get("i4"));
			Assert.assertEquals((Integer) 1, r.get("j"));
		}, getSourceFile(Integers.class));
	}

	@Test
	public void testLongs() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals(r.get("t1").toString(), r.get("t2").toString());
			Assert.assertEquals(0, (int) r.get("l"));
			Assert.assertTrue((int) r.get("c") < 0);
		}, getSourceFile(Longs.class));
	}

	@Test
	public void testMathApi() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();

			Assert.assertEquals(Math.E, (double) r.get("E"), 0.00001);
			Assert.assertEquals(Math.PI, (double) r.get("PI"), 0.00001);
			Assert.assertEquals(Math.abs(-123), (int) r.get("abs_123"), 0.00001);
			Assert.assertEquals(Math.acos(0.1), (double) r.get("acos0_1"), 0.00001);
			Assert.assertEquals(Math.floor(3.3), (int) r.get("floor3_3"), 0.00001);
			Assert.assertEquals(Math.cbrt(2), (double) r.get("cbrt2"), 0.00001);
			Assert.assertEquals(Math.signum(-2342), (int) r.get("signum_2342"), 0.00001);
			Assert.assertEquals(Math.scalb(1.2, 2), (double) r.get("scalb1_2__2"), 0.00001);
			Assert.assertEquals(Math.toDegrees(0.5), (double) r.get("toDegres0_5"), 0.00001);

			Assert.assertEquals(Math.E, (double) r.get("strict_E"), 0.00001);
			Assert.assertEquals(Math.PI, (double) r.get("strict_PI"), 0.00001);
			Assert.assertEquals(Math.abs(-123), (int) r.get("strict_abs_123"), 0.00001);
			Assert.assertEquals(Math.acos(0.1), (double) r.get("strict_acos0_1"), 0.00001);
			Assert.assertEquals(Math.floor(3.3), (int) r.get("strict_floor3_3"), 0.00001);
			Assert.assertEquals(Math.cbrt(2), (double) r.get("strict_cbrt2"), 0.00001);
			Assert.assertEquals(Math.signum(-2342), (int) r.get("strict_signum_2342"), 0.00001);
			Assert.assertEquals(Math.scalb(1.2, 2), (double) r.get("strict_scalb1_2__2"), 0.00001);
			Assert.assertEquals(Math.toDegrees(0.5), (double) r.get("strict_toDegres0_5"), 0.00001);

			Assert.assertEquals(Math.abs(-1) + Math.abs(-1), (int) r.get("2"), 0.00001);
			Assert.assertEquals(Math.cbrt(2), (double) r.get("3"), 0.00001);
			Assert.assertEquals(Math.cbrt(2), (double) r.get("4"), 0.00001);
			Assert.assertEquals(Math.cbrt(2), (double) r.get("4"), 0.00001);

			Assert.assertTrue(Math.ulp(956.294) == 1.1368683772161603E-13);
			Assert.assertTrue(Math.ulp(123.1) == 1.4210854715202004E-14);
		}, getSourceFile(MathApi.class));
	}

	@Test
	public void testOperators() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();

			assertEquals(1L, r.<Number>get("bitwise_or_assign").longValue());
			assertEquals(2L, r.<Number>get("bitwise_leftshift").longValue());
			assertEquals(4L, r.<Number>get("bitwise_leftshift_assign").longValue());
			System.out.println("last shift: " + r.get("bitwise_leftshift_assign"));

			assertEquals(12L, r.<Number>get("bitwise_leftshift_char").longValue());

			assertEquals(15L, r.<Number>get("bitwise_or_assign_char").longValue());

			assertEquals(3L, r.<Number>get("bitwise_and_assign_char").longValue());
			assertEquals(24L, r.<Number>get("bitwise_lshift_assign_char").longValue());
			assertEquals(3L, r.<Number>get("bitwise_rshift_assign_char").longValue());
			assertEquals(1L, r.<Number>get("bitwise_div_assign_char").longValue());
			assertEquals(-2L, r.<Number>get("bitwise_minus_assign_char").longValue());
			assertEquals(-2L, r.<Number>get("bitwise_modulo_assign_char").longValue());
			assertEquals(-6L, r.<Number>get("bitwise_multiply_assign_char").longValue());
			assertEquals(-3L, r.<Number>get("bitwise_plus_assign_char").longValue());
			assertEquals(-2L, r.<Number>get("bitwise_xor_assign_char").longValue());

			assertEquals("A", r.get("bitwise_add_to_char"));
			assertEquals("B", r.get("bitwise_multiply_assign_char_to_char"));

		}, getSourceFile(Operators.class));
	}

	@Test
	public void testChars() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals("" + (char) (0x0030 + ((int) (5 * 10))), r.get("result"));
		}, getSourceFile(Chars.class));
	}

	@Test
	public void testNull() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(Null.class));
	}

	@Test
	public void testNumbers() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertTrue(r.get("NaN_test"));
			assertEquals(2.5, (double) r.get("Numbers_f3"), 0.00001);
		}, getSourceFile(Numbers.class));
	}

	@Test
	public void testStrings() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals("a", r.get("str_plus_char_casted_int").toString());
			Assert.assertEquals("97", r.get("str_plus_int").toString());
			Assert.assertEquals("d", r.get("str_plus_char").toString());
			Assert.assertEquals("a", r.get("str_plus_equal_casted_int").toString());
			Assert.assertEquals("97", r.get("str_plus_equal_int").toString());
		}, getSourceFile(Strings.class));
	}
}
