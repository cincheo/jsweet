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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import source.statics.AnonymousClasses;
import source.statics.Classes;
import source.statics.DefaultValues;
import source.statics.InnerClasses;
import source.statics.StaticInitializer;
import source.statics.StaticInitializerWithNoFields;
import source.statics.StaticsInInterfaces;
import source.statics.static_accesses.definitions.AClass;
import source.statics.static_accesses.definitions.AnInterface;
import source.statics.static_accesses.definitions.TestClassStaticAccess;
import source.statics.static_accesses.definitions.TestEnumStaticAccess;
import source.statics.static_accesses.uses.AccessThroughInstance;
import source.statics.static_accesses.uses.FullPathAccess;
import source.statics.static_accesses.uses.Using;

public class StaticsTests extends AbstractTest {

	@Ignore
	@Test
	public void testInnerClasses() {
		eval((h, r) -> {
			h.assertNoProblems();
		}, getSourceFile(InnerClasses.class));
	}

	@Test
	public void testAnonymousClasses() {
		eval((h, r) -> {
			h.assertNoProblems();
			Assert.assertTrue(r.get("m"));
		}, getSourceFile(AnonymousClasses.class));
	}

	@Test
	public void testClasses() {
		eval((h, r) -> {
			h.assertNoProblems();
			Assert.assertEquals("name", r.get("name1"));
			Assert.assertEquals("name", r.get("name2"));
		}, getSourceFile(Classes.class));
	}

	@Test
	public void testStaticInitializer() {
		eval((h, r) -> {
			h.assertNoProblems();
			Assert.assertEquals(4, (int) r.get("result"));
		}, getSourceFile(StaticInitializer.class));
	}

	@Test
	public void testStaticInitializerWithNoFields() {
		eval((h, r) -> {
			h.assertNoProblems();
			Assert.assertTrue(r.get("ok"));
		}, getSourceFile(StaticInitializerWithNoFields.class));
	}

	@Test
	public void testStaticsInInterfaces() {
		eval((h, r) -> {
			h.assertNoProblems();
			Assert.assertEquals(1, (int) r.get("c1"));
			Assert.assertEquals(2, (int) r.get("c2"));
		}, getSourceFile(StaticsInInterfaces.class));
	}

	@Test
	public void testDefaultValues() {
		eval((h, r) -> {
			h.assertNoProblems();
		}, getSourceFile(DefaultValues.class));
	}

	@Test
	public void testImportInterface() {
		transpile(TestTranspilationHandler::assertNoProblems, getSourceFile(AClass.class),
				getSourceFile(AnInterface.class), getSourceFile(Using.class));
	}

	@Test
	public void testFullPathStaticAccess() {
		transpile(TestTranspilationHandler::assertNoProblems, getSourceFile(TestClassStaticAccess.class),
				getSourceFile(TestEnumStaticAccess.class), getSourceFile(FullPathAccess.class));
	}
	
	@Test
	public void testAccessThroughInstance() {
		eval((h, r) -> {
			h.assertNoProblems();
		}, getSourceFile(TestClassStaticAccess.class), getSourceFile(AccessThroughInstance.class));
	}

	
}
