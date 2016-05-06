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

import source.overload.Overload;
import source.overload.OverloadInInnerClass;
import source.overload.OverloadWithStaticAndInstanceMethods;
import source.overload.WrongOverload;
import source.overload.WrongOverloadWithArraysAndObjects;
import source.overload.WrongOverloadWithGenerics;
import source.overload.WrongOverloadWithInheritance;
import source.overload.WrongOverloads;
import source.overload.WrongOverloadsWithDefaultMethods;

public class OverloadTests extends AbstractTest {

	@Test
	public void testOverload() {
		eval((logHandler, result) -> {
			logHandler.assertReportedProblems();
			assertEquals("default1", result.<String> get("res1"));
			assertEquals("s11", result.<String> get("res2"));
			assertEquals("s22", result.<String> get("res3"));
		} , getSourceFile(Overload.class));
	}

	@Test
	public void testWrongOverload() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,2,3,4,5,6,7", r.get("trace"));
		} , getSourceFile(WrongOverload.class));
	}

	@Test
	public void testWrongOverloads() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,5,2,3,2,4,test5,tutu,2,4,1,tutu,6", r.get("trace"));
		} , getSourceFile(WrongOverloads.class));
	}

	@Test
	public void testWrongOverloadsWithDefaultMethods() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("draw0,draw1", r.get("trace"));
		} , getSourceFile(WrongOverloadsWithDefaultMethods.class));
	}
	
	@Test
	public void testOverloadInInnerClass() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			//assertEquals("1,5,2,3,2,4,2,4,6", r.get("trace"));
		} , getSourceFile(OverloadInInnerClass.class));
	}

	@Test
	public void testWrongOverloadWithArraysAndObjects() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(WrongOverloadWithArraysAndObjects.class));
	}

	@Test
	public void testWrongOverloadWithGenerics() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,2,3,4", r.get("trace"));
		} , getSourceFile(WrongOverloadWithGenerics.class));
	}
	
	@Test
	public void testWrongOverloadWithInheritance() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("0-88,0-99,1-s1,m2,2-99-s2,3-true,m1,4,5-5,5-6", r.get("trace"));
		} , getSourceFile(WrongOverloadWithInheritance.class));
	}

	@Test
	public void testOverloadWithStaticAndInstanceMethods() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals(true, r.get("static"));
			assertEquals(true, r.get("instance"));
		} , getSourceFile(OverloadWithStaticAndInstanceMethods.class));
	}
	
}
