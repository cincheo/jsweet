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

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.junit.Assert;
import org.junit.Test;

import source.api.CastMethods;
import source.api.ErasingJava;
import source.api.ForeachIteration;
import source.api.J4TSInvocations;
import source.api.JdkInvocations;
import source.api.PrimitiveInstantiation;
import source.api.QualifiedInstantiation;
import source.api.WrongJdkInvocations;

public class ApiTests extends AbstractTest {

	@Test
	public void testWrongJdkInvocations() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.MAPPED_TSC_ERROR, JSweetProblem.MAPPED_TSC_ERROR, JSweetProblem.MAPPED_TSC_ERROR,
					JSweetProblem.MAPPED_TSC_ERROR, JSweetProblem.MAPPED_TSC_ERROR);
		} , getSourceFile(J4TSInvocations.class), getSourceFile(WrongJdkInvocations.class));
	}

	@Test
	public void testJ4TSInvocations() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(J4TSInvocations.class));
	}

	@Test
	public void testJdkInvocations() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("test", result.<String> get("s1"));
			assertEquals("m1", result.<String> get("s2"));
			assertEquals("e", result.<String> get("s3"));
			assertEquals("testc", result.<String> get("s4"));
			assertEquals(2, result.<Number> get("i1").intValue());
			assertEquals(-1, result.<Number> get("i2").intValue());
			assertEquals(4, result.<Number> get("l").intValue());
			assertEquals("t1st", result.<String> get("r"));
		} , getSourceFile(JdkInvocations.class));
	}

	@Test
	public void testForeachIteration() {
		eval((logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong behavior output trace", "abc", r.get("out"));
		} , getSourceFile(ForeachIteration.class));
	}

	@Test
	public void testPrimitiveInstantiation() {
		transpile(logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(PrimitiveInstantiation.class));
	}

	@Test
	public void testQualifiedInstantiation() {
		transpile(logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(QualifiedInstantiation.class));
	}

	@Test
	public void testCastMethods() {
		transpile(logHandler -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(CastMethods.class));
	}

	@Test
	public void testErasingJava() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(ErasingJava.class));
	}

}
