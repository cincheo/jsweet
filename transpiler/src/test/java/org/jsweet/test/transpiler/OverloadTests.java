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
import static org.junit.Assert.assertTrue;

import org.jsweet.transpiler.ModuleKind;
import org.junit.Test;

import def.test.AmbientWithOverload;
import source.genericinterfaceperf.ImplementationA1;
import source.genericinterfaceperf.ImplementationA10;
import source.genericinterfaceperf.ImplementationA11;
import source.genericinterfaceperf.ImplementationA12;
import source.genericinterfaceperf.ImplementationA13;
import source.genericinterfaceperf.ImplementationA14;
import source.genericinterfaceperf.ImplementationA15;
import source.genericinterfaceperf.ImplementationA2;
import source.genericinterfaceperf.ImplementationA3;
import source.genericinterfaceperf.ImplementationA4;
import source.genericinterfaceperf.ImplementationA5;
import source.genericinterfaceperf.ImplementationA6;
import source.genericinterfaceperf.ImplementationA7;
import source.genericinterfaceperf.ImplementationA8;
import source.genericinterfaceperf.ImplementationA9;
import source.genericinterfaceperf.ImplementationB1;
import source.genericinterfaceperf.ImplementationB10;
import source.genericinterfaceperf.ImplementationB11;
import source.genericinterfaceperf.ImplementationB12;
import source.genericinterfaceperf.ImplementationB13;
import source.genericinterfaceperf.ImplementationB14;
import source.genericinterfaceperf.ImplementationB15;
import source.genericinterfaceperf.ImplementationB2;
import source.genericinterfaceperf.ImplementationB3;
import source.genericinterfaceperf.ImplementationB4;
import source.genericinterfaceperf.ImplementationB5;
import source.genericinterfaceperf.ImplementationB6;
import source.genericinterfaceperf.ImplementationB7;
import source.genericinterfaceperf.ImplementationB8;
import source.genericinterfaceperf.ImplementationB9;
import source.genericinterfaceperf.InterfaceA;
import source.genericinterfaceperf.InterfaceB;
import source.overload.AbstractMethodOverloadInAnonymousClass;
import source.overload.BasicOverride;
import source.overload.ConstructorOverLoadWithArray;
import source.overload.ConstructorOverloadWithFieldInitializer;
import source.overload.InterfaceInheritance;
import source.overload.LocalVariablesNameCollision;
import source.overload.NonPublicRootMethod;
import source.overload.OverLoadWithClassParam;
import source.overload.Overload;
import source.overload.OverloadInInnerClass;
import source.overload.OverloadWithAbstractClass;
import source.overload.OverloadWithEnums;
import source.overload.OverloadWithInterfaces;
import source.overload.OverloadWithStaticAndInstanceMethods;
import source.overload.OverloadWithSuperclass;
import source.overload.WithAmbients;
import source.overload.WrongOverload;
import source.overload.WrongOverloadConstructor;
import source.overload.WrongOverloadConstructor2;
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
import source.overload.visitor.A1;
import source.overload.visitor.A2;
import source.overload.visitor.A3;
import source.overload.visitor.F;
import source.overload.visitor.F1;

public class OverloadTests extends AbstractTest {

	@Test
	public void testOverload() {
		eval((logHandler, result) -> {
			logHandler.assertNoProblems();
			assertEquals("default1", result.<String>get("res1"));
			assertEquals("s11", result.<String>get("res2"));
			assertEquals("s22", result.<String>get("res3"));
		}, getSourceFile(Overload.class));
	}

	@Test
	public void testOverloadWithSuperclass() {
		eval((logHandler, result) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(OverloadWithSuperclass.class));
	}

	@Test
	public void testWrongOverload() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("1,2,3,4,5,6,7", r.get("trace"));
		}, getSourceFile(WrongOverload.class));
	}

	@Test
	public void testWrongOverloads() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("1,5,2,3,2,4,test5,tutu,2,4,1,tutu,6", r.get("trace"));
		}, getSourceFile(WrongOverloads.class));
	}

	@Test
	public void testWrongOverloadsWithDefaultMethods() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("draw0,draw1", r.get("trace"));
		}, getSourceFile(WrongOverloadsWithDefaultMethods.class));
	}

	@Test
	public void testWrongOverloadsWithNonCoreMethod() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("draw0,draw1,double1,float1", r.get("trace"));
		}, getSourceFile(WrongOverloadsWithNonCoreMethod.class));
	}

	@Test
	public void testOverloadInInnerClass() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(OverloadInInnerClass.class));
	}

	@Test
	public void testWrongOverloadInInnerClass() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(WrongOverloadInInnerClass.class));
	}

	@Test
	public void testWrongOverloadWithArraysAndObjects() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(WrongOverloadWithArraysAndObjects.class));
	}

	@Test
	public void testWrongOverloadWithGenerics() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("1,2,3,4", r.get("trace"));
		}, getSourceFile(WrongOverloadWithGenerics.class));
	}

	@Test
	public void testWrongOverloadWithInheritance() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("0-88,0-99,1-s1,m2,2-99-s2,3-true,m1,4,5-5,5-6,m3,6,7,test2,test1", r.get("trace"));
		}, getSourceFile(WrongOverloadWithInheritance.class));
	}

	@Test
	public void testOverloadWithStaticAndInstanceMethods() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals(true, r.get("static"));
			assertEquals(true, r.get("instance"));
		}, getSourceFile(OverloadWithStaticAndInstanceMethods.class));
	}

	@Test
	public void testWrongOverloadFrom2Interfaces() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("remove1: abc,remove2: 1", r.get("trace"));
		}, getSourceFile(WrongOverloadFrom2Interfaces.class));
	}

	@Test
	public void testInterfaceInheritance() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(InterfaceInheritance.class));
	}

	@Test
	public void testNonPublicRootMethod() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(NonPublicRootMethod.class));
	}

	@Test
	public void testBasicOverride() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("1-1-X,1-1-0,1-2-X,1-2-0,1-3-X,1-3-0,2-1-X,2-1-0,2-2-X,2-2-0,2-3-X,2-3-0,0-3-X",
					r.get("trace"));
		}, getSourceFile(BasicOverride.class));
	}

	@Test
	public void testOverloadWithAbstractClass() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(OverloadWithAbstractClass.class));
	}

	@Test
	public void testWrongOverloadWithSpecialParameters() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("m,m,m,read1,read2", r.get("trace"));
		}, getSourceFile(WrongOverloadWithSpecialParameters.class));
	}

	@Test
	public void testWrongOverloadConstructorWithVarargs() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("c11,t12,c13,t14", r.get("trace"));
		}, getSourceFile(WrongOverloadConstructorWithVarargs.class));
	}

	@Test
	public void testWrongOverloadConstructorWithParamNameCollision() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("1,2,3,4,5,6,7", r.get("trace"));
		}, getSourceFile(WrongOverloadConstructorWithParamNameCollision.class));
	}

	@Test
	public void testConstructorOverloadWithFieldInitializer() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("test,1", r.get("trace"));
		}, getSourceFile(ConstructorOverloadWithFieldInitializer.class));
	}

	@Test
	public void testConstructorOverloadWithArray() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertTrue("1,2,3,4,3".equals(r.get("trace")) || "1,2,3,4,4".equals(r.get("trace")));
		}, getSourceFile(ConstructorOverLoadWithArray.class));
	}

	@Test
	public void testLocalVariablesNameCollision() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(LocalVariablesNameCollision.class));
	}

	@Test
	public void testOverloadWithEnums() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("1,2,3,4", r.get("trace"));
		}, getSourceFile(OverloadWithEnums.class));
	}

	@Test
	public void testOverloadWithInterfaces() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("1,2,3,3", r.get("trace"));
		}, getSourceFile(OverloadWithInterfaces.class));
	}

	@Test
	public void testOverloadWithClassParam() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("ctor_overload_class;OverLoadWithClassParam;0;0," //
					+ "ctor_overload_class;OverLoadWithClassParam;4;0," //
					+ "ctor_overload_class;OverLoadWithClassParam;10;100," //
					+ "m_overload_class;OverLoadWithClassParam;0;0," //
					+ "m_overload_class;OverLoadWithClassParam;4;0," //
					+ "m_overload_class;OverLoadWithClassParam;10;100", r.get("trace"));
		}, getSourceFile(OverLoadWithClassParam.class));
	}

	@Test
	public void testWithAmbients() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(WithAmbients.class), getSourceFile(AmbientWithOverload.class));
	}

	@Test
	public void testAbstractMethodOverloadInAnonymousClass() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(AbstractMethodOverloadInAnonymousClass.class));
	}

	@Test
	public void testVisitor() {
		transpile((logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(A1.class), getSourceFile(A2.class), getSourceFile(A3.class), getSourceFile(F1.class),
				getSourceFile(F.class));
	}

	@Test
	public void testVisitor2() {
		transpile((logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(source.overload.visitor2.b.A1.class), getSourceFile(source.overload.visitor2.c.A2.class),
				getSourceFile(source.overload.visitor2.c.A3.class), getSourceFile(source.overload.visitor2.a.F1.class),
				getSourceFile(source.overload.visitor2.a.F.class));
	}

	@Test
	public void testWrongOverloadConstructor() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("1.1(24,7):1.2(6):1.3(true):2.1(24,7):2.2(6):2.3(true):3.1(24,7):3.2(6):3.3(true)",
					r.get("trace"));
		}, getSourceFile(WrongOverloadConstructor.class));
	}

	@Test
	public void testWrongOverloadConstructor2() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(WrongOverloadConstructor2.class));
	}

	@Test
	public void testGenericInterfacePerf() {
		eval(ModuleKind.commonjs, (logHandler, result) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(InterfaceA.class), //
				getSourceFile(InterfaceB.class), //
				getSourceFile(ImplementationA1.class), //
				getSourceFile(ImplementationA2.class), //
				getSourceFile(ImplementationA3.class), //
				getSourceFile(ImplementationA4.class), //
				getSourceFile(ImplementationA5.class), //
				getSourceFile(ImplementationA6.class), //
				getSourceFile(ImplementationA7.class), //
				getSourceFile(ImplementationA8.class), //
				getSourceFile(ImplementationA9.class), //
				getSourceFile(ImplementationA10.class), //
				getSourceFile(ImplementationA11.class), //
				getSourceFile(ImplementationA12.class), //
				getSourceFile(ImplementationA13.class), //
				getSourceFile(ImplementationA14.class), //
				getSourceFile(ImplementationA15.class), //
				getSourceFile(ImplementationB1.class), //
				getSourceFile(ImplementationB2.class), //
				getSourceFile(ImplementationB3.class), //
				getSourceFile(ImplementationB4.class), //
				getSourceFile(ImplementationB5.class), //
				getSourceFile(ImplementationB6.class), //
				getSourceFile(ImplementationB7.class), //
				getSourceFile(ImplementationB8.class), //
				getSourceFile(ImplementationB9.class), //
				getSourceFile(ImplementationB10.class), //
				getSourceFile(ImplementationB11.class), //
				getSourceFile(ImplementationB12.class), //
				getSourceFile(ImplementationB13.class), //
				getSourceFile(ImplementationB14.class), //
				getSourceFile(ImplementationB15.class));
	}

}
