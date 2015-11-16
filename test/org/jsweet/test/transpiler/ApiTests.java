/* Copyright 2015 CINCHEO SAS
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
import static org.junit.Assert.fail;

import org.jsweet.test.transpiler.source.api.CastMethods;
import org.jsweet.test.transpiler.source.api.ForeachIteration;
import org.jsweet.test.transpiler.source.api.JdkInvocations;
import org.jsweet.test.transpiler.source.api.PrimitiveInstantiation;
import org.jsweet.test.transpiler.source.api.QualifiedInstantiation;
import org.jsweet.test.transpiler.source.api.WrongJdkInvocations;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Assert;
import org.junit.Test;

public class ApiTests extends AbstractTest {

	@Test
	public void testWrongJdkInvocations() {
		transpile(logHandler -> {
			Assert.assertEquals("There should be 7 errors", 7, logHandler.reportedProblems.size());
			Assert.assertEquals("Unexpected error", JSweetProblem.JDK_TYPE, logHandler.reportedProblems.get(0));
			Assert.assertEquals("Unexpected error", JSweetProblem.JDK_METHOD, logHandler.reportedProblems.get(1));
			Assert.assertEquals("Unexpected error", JSweetProblem.JDK_TYPE, logHandler.reportedProblems.get(2));
			Assert.assertEquals("Unexpected error", JSweetProblem.ERASED_METHOD, logHandler.reportedProblems.get(3));
			Assert.assertEquals("Unexpected error", JSweetProblem.JDK_METHOD, logHandler.reportedProblems.get(4));
			Assert.assertEquals("Unexpected error", JSweetProblem.JDK_METHOD, logHandler.reportedProblems.get(5));
			Assert.assertEquals("Unexpected error", JSweetProblem.JDK_METHOD, logHandler.reportedProblems.get(6));
		}, getSourceFile(WrongJdkInvocations.class));
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
		}, getSourceFile(JdkInvocations.class));
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

}
