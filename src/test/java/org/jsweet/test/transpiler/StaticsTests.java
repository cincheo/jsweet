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

import org.junit.Test;

import source.statics.AnonymousClasses;
import source.statics.Classes;
import source.statics.InnerClasses;
import source.statics.StaticInitializer;

import org.junit.Assert;
import org.junit.Ignore;

public class StaticsTests extends AbstractTest {

	@Ignore
	@Test
	public void testInnerClasses() {
		eval((h, r) -> {
			h.assertReportedProblems();
		} , getSourceFile(InnerClasses.class));
	}

	@Test
	public void testAnonymousClasses() {
		eval((h, r) -> {
			h.assertReportedProblems();
			Assert.assertTrue(r.get("m"));
		} , getSourceFile(AnonymousClasses.class));
	}

	@Test
	public void testClasses() {
		eval((h, r) -> {
			h.assertReportedProblems();
			Assert.assertEquals("name", r.get("name1"));
			Assert.assertEquals("name", r.get("name2"));
		} , getSourceFile(Classes.class));
	}

	@Test
	public void testStaticInitializer() {
		eval((h, r) -> {
			h.assertReportedProblems();
			Assert.assertEquals(4, (int) r.get("result"));
		} , getSourceFile(StaticInitializer.class));
	}

}
