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
import org.junit.Test;

import source.overload.BasicOverride;
import source.overload.ConstructorOverloadWithFieldInitializer;
import source.overload.InterfaceInheritance;
import source.overload.LocalVariablesNameCollision;
import source.overload.NonPublicRootMethod;
import source.overload.ConstructorOverLoadWithArray;
import source.overload.Overload;
import source.overload.OverloadInInnerClass;
import source.overload.OverloadWithEnums;
import source.overload.OverloadWithInterfaces;
import source.overload.OverloadWithStaticAndInstanceMethods;
import source.overload.WrongOverload;
import source.overload.WrongOverloadConstructorWithParamNameCollision;
import source.overload.WrongOverloadConstructorWithVarargs;
import source.overload.WrongOverloadFrom2Interfaces;
import source.overload.WrongOverloadInInnerClass;
import source.overload.WrongOverloadWithArraysAndObjects;
import source.overload.WrongOverloadWithGenerics;
import source.overload.WrongOverloadWithInheritance;
import source.overload.WrongOverloadWithSpecialParameters;
import source.overload.WrongOverloads;
import source.overload.WrongOverloadsWithDefaultMethods;
import source.overload.WrongOverloadsWithNonCoreMethod;

public class OverloadTests extends AbstractTest {

	@Test
	public void testOverload() {
		eval((logHandler, result) -> {
			logHandler.assertReportedProblems();
			assertEquals("default1", result.<String> get("res1"));
			assertEquals("s11", result.<String> get("res2"));
			assertEquals("s22", result.<String> get("res3"));
		}, getSourceFile(Overload.class));
	}

	@Test
	public void testWrongOverload() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,2,3,4,5,6,7", r.get("trace"));
		}, getSourceFile(WrongOverload.class));
	}

	@Test
	public void testWrongOverloads() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,5,2,3,2,4,test5,tutu,2,4,1,tutu,6", r.get("trace"));
		}, getSourceFile(WrongOverloads.class));
	}

	@Test
	public void testWrongOverloadsWithDefaultMethods() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("draw0,draw1", r.get("trace"));
		}, getSourceFile(WrongOverloadsWithDefaultMethods.class));
	}

	@Test
	public void testWrongOverloadsWithNonCoreMethod() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("draw0,draw1", r.get("trace"));
		}, getSourceFile(WrongOverloadsWithNonCoreMethod.class));
	}

	@Test
	public void testOverloadInInnerClass() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertReportedProblems();
		}, getSourceFile(OverloadInInnerClass.class));
	}

	@Test
	public void testWrongOverloadInInnerClass() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertReportedProblems();
		}, getSourceFile(WrongOverloadInInnerClass.class));
	}

	@Test
	public void testWrongOverloadWithArraysAndObjects() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
		}, getSourceFile(WrongOverloadWithArraysAndObjects.class));
	}

	@Test
	public void testWrongOverloadWithGenerics() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,2,3,4", r.get("trace"));
		}, getSourceFile(WrongOverloadWithGenerics.class));
	}

	@Test
	public void testWrongOverloadWithInheritance() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("0-88,0-99,1-s1,m2,2-99-s2,3-true,m1,4,5-5,5-6", r.get("trace"));
		}, getSourceFile(WrongOverloadWithInheritance.class));
	}

	@Test
	public void testOverloadWithStaticAndInstanceMethods() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals(true, r.get("static"));
			assertEquals(true, r.get("instance"));
		}, getSourceFile(OverloadWithStaticAndInstanceMethods.class));
	}

	@Test
	public void testWrongOverloadFrom2Interfaces() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("remove1: abc,remove2: 1", r.get("trace"));
		}, getSourceFile(WrongOverloadFrom2Interfaces.class));
	}

	@Test
	public void testInterfaceInheritance() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertReportedProblems();
		}, getSourceFile(InterfaceInheritance.class));
	}

	@Test
	public void testNonPublicRootMethod() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertReportedProblems();
		}, getSourceFile(NonPublicRootMethod.class));
	}

	@Test
	public void testBasicOverride() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1-1-X,1-1-0,1-2-X,1-2-0,1-3-X,1-3-0,2-1-X,2-1-0,2-2-X,2-2-0,2-3-X,2-3-0,0-3-X", r.get("trace"));
		}, getSourceFile(BasicOverride.class));
	}

	@Test
	public void testWrongOverloadWithSpecialParameters() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("m,m,read1,read2", r.get("trace"));
		}, getSourceFile(WrongOverloadWithSpecialParameters.class));
	}

	@Test
	public void testWrongOverloadConstructorWithVarargs() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("c11,t12,c13,t14", r.get("trace"));
		}, getSourceFile(WrongOverloadConstructorWithVarargs.class));
	}

	@Test
	public void testWrongOverloadConstructorWithParamNameCollision() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,2,3,4,5,6,7", r.get("trace"));
		}, getSourceFile(WrongOverloadConstructorWithParamNameCollision.class));
	}

	@Test
	public void testConstructorOverloadWithFieldInitializer() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("test,1", r.get("trace"));
		}, getSourceFile(ConstructorOverloadWithFieldInitializer.class));
	}

	@Test
	public void testConstructorOverloadWithArray() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,2", r.get("trace"));
		}, getSourceFile(ConstructorOverLoadWithArray.class));
	}
	
	@Test
	public void testLocalVariablesNameCollision() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
		}, getSourceFile(LocalVariablesNameCollision.class));
	}

	@Test
	public void testOverloadWithEnums() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,2,3,4", r.get("trace"));
		}, getSourceFile(OverloadWithEnums.class));
	}

	@Test
	public void testOverloadWithInterfaces() {
		eval((logHandler, r) -> {
			logHandler.assertReportedProblems();
			assertEquals("1,2,3,3", r.get("trace"));
		}, getSourceFile(OverloadWithInterfaces.class));
	}
	
}
