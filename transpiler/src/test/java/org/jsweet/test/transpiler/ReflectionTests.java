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

import org.junit.Test;

import source.reflection.*;

public class ReflectionTests extends AbstractTest {

	@Test
	public void testBasicBeanReflection() {
		eval((h, r) -> {
			h.assertNoProblems();
		}, getSourceFile(BasicBeanReflection.class));
	}

	@Test
	public void testFieldAccess() {
		eval((h, r) -> {
			h.assertNoProblems();
		}, getSourceFile(FieldAccess.class));
	}
	
	@Test
	public void testArrayMethods() {
		eval((h, r) -> {
			h.assertNoProblems();
		}, getSourceFile(ArrayMethods.class));
	}
	
	@Test
	public void testGetClass() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("source.reflection.AClass1", r.get("name1"));
			assertEquals("source.reflection.AClass1", r.get("name2"));
			assertEquals("source.reflection.AClass1", r.get("name3"));
			assertEquals("source.reflection.Functions", r.get("name4"));
			assertEquals("source.reflection.Functions", r.get("name5"));
			assertEquals("AClass1", r.get("simplename1"));
			assertEquals("AClass1", r.get("simplename2"));
			assertEquals("AClass1", r.get("simplename3"));
			assertEquals("Functions", r.get("simplename4"));
			assertEquals("Functions", r.get("simplename5"));
			assertEquals("String", r.get("string"));
			assertEquals("Number", r.get("number"));
		}, getSourceFile(GetClass.class));
	}

	@Test
	public void testClassMethods() {
		eval((handler, result) -> {
			handler.assertNoProblems();
		}, getSourceFile(ClassMethods.class));
	}
}
