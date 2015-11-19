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

import static org.junit.Assert.fail;

import org.jsweet.test.transpiler.source.throwable.InvalidTryCatchTest;
import org.jsweet.test.transpiler.source.throwable.TryCatchFinallyTest;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Assert;
import org.junit.Test;

public class ThrowableTests extends AbstractTest {

	@Test
	public void testTryCatchFinally() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			EvaluationResult r = transpiler.eval(logHandler, getSourceFile(TryCatchFinallyTest.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertNotNull("Test was not executed", r.get("executed"));
			Assert.assertEquals("Expected a message when the catch clause is executed", "test-message", r.get("message"));
			Assert.assertNotNull("Finally was not executed", r.get("finally_executed"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testInvalidTryCatch() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(InvalidTryCatchTest.class));
			Assert.assertEquals("There should be 4 errors", 4, logHandler.reportedProblems.size());
			Assert.assertEquals("Unexpected error", JSweetProblem.TRY_WITH_MULTIPLE_CATCHES, logHandler.reportedProblems.get(0));
			Assert.assertEquals("Unexpected error", JSweetProblem.UNSUPPORTED_TRY_WITH_RESOURCE, logHandler.reportedProblems.get(1));
			Assert.assertEquals("Unexpected error", JSweetProblem.TRY_WITHOUT_CATCH_OR_FINALLY, logHandler.reportedProblems.get(2));
			Assert.assertEquals("Unexpected error", JSweetProblem.JDK_METHOD, logHandler.reportedProblems.get(3));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

}
