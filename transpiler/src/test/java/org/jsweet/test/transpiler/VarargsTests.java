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

import org.jsweet.transpiler.ModuleKind;
import org.junit.Ignore;
import org.junit.Test;

import source.varargs.VarargsAndOverloads;
import source.varargs.VarargsCalledWithArray;
import source.varargs.VarargsCalledWithArray2;
import source.varargs.VarargsCalledWithoutArray;
import source.varargs.VarargsOnAnonymous;
import source.varargs.VarargsOnApi;
import source.varargs.VarargsOnConstructor;
import source.varargs.VarargsOnField;
import source.varargs.VarargsOnGetter;
import source.varargs.VarargsOnNew;
import source.varargs.VarargsOnStaticMethod;
import source.varargs.VarargsTransmission;

public class VarargsTests extends AbstractTest {
	@Test
	@Ignore
	public void testVarargsOnAnonymous() {
		eval(ModuleKind.none, (ctx, res) -> {
			assertEquals("3", res.get("argsLength").toString());
			assertEquals("called", res.get("firstArg"));
		}, getSourceFile(VarargsOnAnonymous.class));
	}

	@Test
	public void testVarargsOnNew() {
		eval((logHandler, res) -> {
			assertEquals("1", res.get("index").toString());
			assertEquals("3", res.get("argsLength").toString());
			assertEquals("called", res.get("firstArg"));
		}, getSourceFile(VarargsOnNew.class));
	}

	@Test
	public void testVarargsOnField() {
		eval((logHandler, res) -> {
			assertEquals("2", res.get("argsLength").toString());
			assertEquals("field", res.get("firstArg"));
		}, getSourceFile(VarargsOnField.class));
	}

	@Test
	public void testVarargsOnGetter() {
		eval((logHandler, res) -> {
			assertEquals(2, res.<Number> get("index"));
			assertEquals(2, res.<Number> get("argsLength"));
			assertEquals("on", res.get("firstArg"));
		}, getSourceFile(VarargsOnGetter.class));
	}

	@Test
	public void testVarargsCalledWithArray() {
		eval((logHandler, res) -> {
			assertEquals("3", res.get("argsLength_a").toString());
			assertEquals("array", res.get("firstArg_a"));
			assertEquals("3", res.get("argsLength_b").toString());
			assertEquals("array", res.get("firstArg_b"));
		}, getSourceFile(VarargsCalledWithArray.class));
	}

	@Test
	public void testVarargsCalledWithArray2() {
		eval((logHandler, res) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(VarargsCalledWithArray2.class));
	}
	
	@Test
	public void testVarargsCalledWithoutArray() {
		eval((logHandler, res) -> {
			assertEquals("3", res.get("argsLength_a").toString());
			assertEquals("array", res.get("firstArg_a"));
			assertEquals("3", res.get("argsLength_b").toString());
			assertEquals("array", res.get("firstArg_b"));
		}, getSourceFile(VarargsCalledWithoutArray.class));
	}


	@Test
	public void testVarargsOnApi() {
		eval((logHandler, res) -> {
			assertEquals(1, res.<Number> get("out"));
			assertEquals(2, res.<Number> get("d2"));
		}, getSourceFile(VarargsOnApi.class));
	}

	@Test
	public void testVarargsOnStatic() {
		eval((logHandler, res) -> {
			assertEquals("3", res.get("argsLength").toString());
			assertEquals("static", res.get("firstArg"));
		}, getSourceFile(VarargsOnStaticMethod.class));
	}

	@Test
	public void testVarargsTransmission() {
		eval((logHandler, res) -> {
			assertEquals("1", res.get("shouldBe1").toString());
			assertEquals("2", res.get("argsLength").toString());
			assertEquals("transmitted", res.get("firstArg"));
		}, getSourceFile(VarargsTransmission.class));
	}

	@Test
	public void testVarargsOnConstructor() {
		transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(VarargsOnConstructor.class));
	}

	@Test
	public void testVarargsAndOverloads() {
		eval((logHandler, res) -> {
			assertEquals("2.1", res.get("1").toString());
			assertEquals("2.1-a,b", res.get("2").toString());
			assertEquals("a", res.get("3").toString());
			assertEquals("2.1", res.get("4").toString());
			assertEquals("2.1-a", res.get("5").toString());
			assertEquals("a", res.get("6").toString());
			assertEquals("a,b,c", res.get("7").toString());
		}, getSourceFile(VarargsAndOverloads.class));
	}
	
}
