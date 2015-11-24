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
import static org.junit.Assert.fail;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Test;

import source.overload.Overload;
import source.overload.WrongOverload;
import source.overload.WrongOverloads;

public class OverloadTests extends AbstractTest {

	@Test
	public void testOverload() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			EvaluationResult result = transpiler.eval(logHandler, getSourceFile(Overload.class));
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("default1", result.<String> get("res1"));
			assertEquals("s11", result.<String> get("res2"));
			assertEquals("s22", result.<String> get("res3"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Test
	public void testWrongOverload() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(WrongOverload.class));
			logHandler.assertReportedProblems(JSweetProblem.INVALID_OVERLOAD, JSweetProblem.INVALID_OVERLOAD);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}
	
	@Test
	public void testWrongOverloads() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			transpiler.transpile(logHandler, getSourceFile(WrongOverloads.class));
			logHandler.assertReportedProblems(JSweetProblem.INVALID_OVERLOAD, JSweetProblem.INVALID_OVERLOAD, JSweetProblem.INVALID_OVERLOAD,
					JSweetProblem.INVALID_OVERLOAD, JSweetProblem.INVALID_OVERLOAD, JSweetProblem.INVALID_OVERLOAD);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

}
