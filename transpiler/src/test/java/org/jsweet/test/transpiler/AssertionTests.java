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

import org.jsweet.transpiler.ModuleKind;
import org.junit.Assert;
import org.junit.Test;

import source.assertion.FailingAssertions;
import source.assertion.PassingAssertion;

public class AssertionTests extends AbstractTest {

	@Test
	public void testPassingAssertion() {
		eval(ModuleKind.none, (logHandler, r) -> {
			assertEquals(0, logHandler.reportedProblems.size());
			Assert.assertEquals(true, r.get("assertion"));
		}, getSourceFile(PassingAssertion.class));
	}

	@Test
	public void testFailingAssertions() {
		eval(ModuleKind.none, (logHandler, r) -> {
			assertEquals(0, logHandler.reportedProblems.size());
			Assert.assertNull(r.get("assertion1"));
			Assert.assertFalse(r.get("assertion2"));
		}, getSourceFile(FailingAssertions.class));
	}

	@Test
	public void testIgnoreAssertions() {
		try {
			transpilerTest().getTranspiler().setIgnoreAssertions(true);
			eval(ModuleKind.none, (logHandler, r) -> {
				Assert.assertTrue(r.get("assertion1"));
				Assert.assertNull(r.get("assertion2"));
			}, getSourceFile(FailingAssertions.class));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

}
