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

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.junit.Assert;
import org.junit.Test;

import def.test.Globals;
import def.test.JQuery;
import def.test2.ExtendedJQuery;
import source.typing.ArraysOfLambdas;
import source.typing.CastConversions;
import source.typing.ClassType;
import source.typing.ClassTypeAsFunction;
import source.typing.ClassTypeAsTypeOf;
import source.typing.CustomLambdas;
import source.typing.CustomStringTypes;
import source.typing.InvalidIndexedAccesses;
import source.typing.Lambdas;
import source.typing.MixinsWithDefs;
import source.typing.MixinsWithDefsAndOtherName;
import source.typing.Numbers;
import source.typing.StringTypesUsage;
import source.typing.Tuples;
import source.typing.Unions;
import source.typing.VoidType;
import source.typing.WrongUnions;
import source.typing.root.MixinsWithAmbient;

public class TypingTests extends AbstractTest {

	@Test
	public void testNumbers() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(Numbers.class));
	}

	@Test
	public void testLambdas() {
		eval(ModuleKind.none, (logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("a", r.get("result"));
			Assert.assertEquals("1:0,1:1,2:2,3:0", r.get("trace"));
		}, getSourceFile(Lambdas.class));
	}

	@Test
	public void testVoidType() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(VoidType.class));
	}

	@Test
	public void testClassTypeAsTypeOf() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(ClassTypeAsTypeOf.class));
	}

	@Test
	public void testClassTypeAsFunction() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(ClassTypeAsFunction.class));
	}

	@Test
	public void testClassType() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(ClassType.class));
	}
	
	@Test
	public void testArraysOfLambdas() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(ArraysOfLambdas.class));
	}

	@Test
	public void testStringTypesUsage() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(StringTypesUsage.class));
	}

	@Test
	public void testCustomStringTypes() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(CustomStringTypes.class));
	}

	@Test
	public void testTuples() {
		eval(ModuleKind.none, (logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("test", r.get("0_init"));
			Assert.assertEquals(10, r.<Number> get("1_init").intValue());
			Assert.assertEquals("ok", r.get("0_end"));
			Assert.assertEquals(9, r.<Number> get("1_end").intValue());
		}, getSourceFile(Tuples.class));
	}

	@Test
	public void testCastConversions() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(CastConversions.class));
	}
	
	@Test
	public void testUnions() {
		eval(ModuleKind.none, (logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("test", r.get("union"));
			Assert.assertEquals("test", r.get("s"));
		}, getSourceFile(Unions.class));
	}

	@Test
	public void testWrongUnions() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.UNION_TYPE_MISMATCH, JSweetProblem.UNION_TYPE_MISMATCH,
					JSweetProblem.UNION_TYPE_MISMATCH, JSweetProblem.UNION_TYPE_MISMATCH,
					JSweetProblem.UNION_TYPE_MISMATCH, JSweetProblem.UNION_TYPE_MISMATCH,
					JSweetProblem.UNION_TYPE_MISMATCH, JSweetProblem.UNION_TYPE_MISMATCH);
		}, getSourceFile(WrongUnions.class));
	}

	@Test
	public void testInvalidIndexedAccesses() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertEquals("There should be one error", 1, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong type of expected error", JSweetProblem.INDEXED_SET_TYPE_MISMATCH,
					logHandler.reportedProblems.get(0));
		}, getSourceFile(InvalidIndexedAccesses.class));
	}

	@Test
	public void testCustomLambdas() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals("test1", r.get("lambda"));
		}, getSourceFile(CustomLambdas.class));
	}

	@Test
	public void testMixinsWithAmbient() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(MixinsWithAmbient.class));
	}

	@Test
	public void testMixinsWithDefs() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(MixinsWithDefs.class), getSourceFile(Globals.class), getSourceFile(JQuery.class));
	}

	@Test
	public void testWrongMixins() {
		transpile(ModuleKind.none, logHandler -> {
			Assert.assertTrue(logHandler.reportedProblems.contains(JSweetProblem.WRONG_MIXIN_NAME)
					&& logHandler.reportedProblems.contains(JSweetProblem.SELF_MIXIN_TARGET));
		}, getSourceFile(MixinsWithDefsAndOtherName.class), getSourceFile(def.test2.Globals.class),
				getSourceFile(ExtendedJQuery.class), getSourceFile(def.test3.JQuery.class));
	}

}
