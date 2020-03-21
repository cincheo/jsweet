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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.junit.Assert;
import org.junit.Test;

import source.init.ArrayNew;
import source.init.ClassWithInitializer;
import source.init.Cloning;
import source.init.Constructor;
import source.init.ConstructorField;
import source.init.ConstructorFieldInInterface;
import source.init.ConstructorMethod;
import source.init.ConstructorMethodInInterface;
import source.init.DependentFields;
import source.init.FieldDefaultValues;
import source.init.Initializer;
import source.init.InitializerStatementConditionError;
import source.init.InitializerStatementError;
import source.init.InterfaceRawConstruction;
import source.init.InterfaceWithSuperInterface;
import source.init.MultipleMains;
import source.init.NoOptionalFieldsInClass;
import source.init.OptionalField;
import source.init.OptionalFieldError;
import source.init.ParentInstanceAccess;
import source.init.StaticFieldWithInnerClass;
import source.init.StaticInitializer;
import source.init.UntypedObject;
import source.init.UntypedObjectWrongUses;

public class InitTests extends AbstractTest {

	@Test
	public void testStaticInitializer() {
		eval(ModuleKind.none, (logHandler, result) -> {
			assertEquals(0, logHandler.reportedProblems.size());
			assertEquals(4, result.<Number> get("n").intValue());
			assertEquals("test", result.<String> get("s"));
		}, getSourceFile(StaticInitializer.class));
	}

	@Test
	public void testInitializer() {
		eval(ModuleKind.none, (logHandler, result) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals(4, result.<Number> get("out").intValue());
		}, getSourceFile(Initializer.class));
	}

	@Test
	public void testInitializerStatementError() {
		transpile(logHandler -> {
			assertEquals("There should be 1 problem", 1, logHandler.reportedProblems.size());
			assertTrue("Missing expected error on wrong initializer",
					logHandler.reportedProblems.contains(JSweetProblem.INVALID_INITIALIZER_STATEMENT));
		}, getSourceFile(InitializerStatementError.class));
	}

	@Test
	public void testInitializerStatementWithConditionError() {
		transpile(logHandler -> {
			assertEquals("Missing expected error on non-optional field", 1, logHandler.reportedProblems.size());
			assertEquals("Missing expected error on wrong initializer", JSweetProblem.INVALID_INITIALIZER_STATEMENT,
					logHandler.reportedProblems.get(0));
		}, getSourceFile(InitializerStatementConditionError.class));
	}

	@Test
	public void testOptionalField() {
		transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(OptionalField.class));
	}

	@Test
	public void testOptionalFieldError() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.UNINITIALIZED_FIELD);
		}, getSourceFile(OptionalFieldError.class));
	}

	@Test
	public void testNoOptionalFieldsInClass() {
		transpile(logHandler -> {
			assertEquals("Missing expected warning on non-optional field", 1, logHandler.reportedProblems.size());
			assertTrue("Missing expected warning on non-optional field",
					logHandler.reportedProblems.contains(JSweetProblem.USELESS_OPTIONAL_ANNOTATION));
		}, getSourceFile(NoOptionalFieldsInClass.class));
	}

	@Test
	public void testConstructors() {
		eval(ModuleKind.none, (handler, result) -> {
			handler.assertNoProblems();
			assertEquals("abc", result.get("v1"));
			assertEquals("default", result.get("v2"));
			assertEquals("test", result.get("v3"));
		}, getSourceFile(Constructor.class));
	}

	@Test
	public void testConstructorMethod() {
		transpile(logHandler -> {
			Assert.assertEquals("Missing constructor keyword error", 1, logHandler.reportedProblems.size());
			Assert.assertEquals("Missing constructor keyword error", JSweetProblem.CONSTRUCTOR_MEMBER,
					logHandler.reportedProblems.get(0));
		}, getSourceFile(ConstructorMethod.class));
	}

	@Test
	public void testConstructorField() {
		transpile(logHandler -> {
			Assert.assertEquals("Missing constructor keyword error", 1, logHandler.reportedProblems.size());
			Assert.assertEquals("Missing constructor keyword error", JSweetProblem.CONSTRUCTOR_MEMBER,
					logHandler.reportedProblems.get(0));
		}, getSourceFile(ConstructorField.class));
	}

	@Test
	public void testConstructorFieldInInterface() {
		transpile(logHandler -> {
			assertEquals("There should be 0 problems", 0, logHandler.reportedProblems.size());
		}, getSourceFile(ConstructorFieldInInterface.class));
	}

	@Test
	public void testConstructorMethodInInterface() {
		transpile(logHandler -> {
			Assert.assertEquals("Missing expected error on constructor method", 1, logHandler.reportedProblems.size());
			Assert.assertEquals("Missing expected error on constructor method", JSweetProblem.CONSTRUCTOR_MEMBER,
					logHandler.reportedProblems.get(0));
		}, getSourceFile(ConstructorMethodInInterface.class));
	}

	@Test
	public void testInterfaceRawConstruction() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.UNINITIALIZED_FIELD, JSweetProblem.UNINITIALIZED_FIELD);
		}, getSourceFile(InterfaceRawConstruction.class));
	}

	@Test
	public void testClassWithInitializer() {
		eval(ModuleKind.none, (handler, result) -> {
			handler.assertNoProblems();
		}, getSourceFile(ClassWithInitializer.class));
	}

	@Test
	public void testInterfaceWithSuperInterface() {
		transpile(logHandler -> {
			assertEquals("There should be 0 problems", 0, logHandler.reportedProblems.size());
		}, getSourceFile(InterfaceWithSuperInterface.class));
	}

	@Test
	public void testMultipleMains() {
		eval(ModuleKind.none, (logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());

			assertEquals(0, result.<Number> get("a").intValue());
			assertNull(result.<Number> get("b"));
			assertEquals(1, result.<Number> get("c").intValue());
		}, getSourceFile(MultipleMains.class));
	}

	@Test
	public void testArrayNew() {
		eval(ModuleKind.none, (logHandler, result) -> {
			logHandler.assertNoProblems();
			assertEquals("0,0,0,0,0,0,0,0,0,0,", result.get("result"));
		}, getSourceFile(ArrayNew.class));
	}

	@Test
	public void testUntypedObject() {
		eval(ModuleKind.none, (logHandler, result) -> {
			logHandler.assertNoProblems();

			assertEquals(1, result.<Number> get("a").intValue());
			assertEquals(true, result.<Boolean> get("b"));
		}, getSourceFile(UntypedObject.class));
	}

	@Test
	public void testUntypedObjectWrongUses() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems( //
					JSweetProblem.UNTYPED_OBJECT_ODD_PARAMETER_COUNT, //
					JSweetProblem.UNTYPED_OBJECT_WRONG_KEY, //
					JSweetProblem.UNTYPED_OBJECT_WRONG_KEY, //
					JSweetProblem.UNTYPED_OBJECT_WRONG_KEY);
		}, getSourceFile(UntypedObjectWrongUses.class));
	}

	@Test
	public void testStaticFieldWithInnerClass() {
		eval((h, r) -> {
			h.assertNoProblems();
		}, getSourceFile(StaticFieldWithInnerClass.class));
	}

	@Test
	public void testParentInstanceAccess() {
		eval((logHandler, result) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("test", result.get("field1"));
			assertEquals("test", result.get("field2"));
		}, getSourceFile(ParentInstanceAccess.class));
	}

	@Test
	public void testFieldDefaultValues() {
		eval((logHandler, result) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("value:null", result.get("s1"));
			assertEquals("value:1", result.get("i1"));
			assertEquals("value:0", result.get("j1"));
			assertEquals("value:null", result.get("s2"));
			assertEquals("value:1", result.get("i2"));
			assertEquals("value:0", result.get("j2"));
			assertEquals("value:null", result.get("s3"));
			assertEquals("value:1", result.get("i3"));
			assertEquals("value:0", result.get("j3"));
		}, getSourceFile(FieldDefaultValues.class));
	}

	@Test
	public void testDependentFields() {
		eval((logHandler, result) -> {
			logHandler.assertNoProblems();
			assertEquals("value:1", result.get("i1"));
			assertEquals("value:0", result.get("j1"));
		}, getSourceFile(DependentFields.class));
	}

	@Test
	public void testCloning() {
		eval(ModuleKind.none, (logHandler, result) -> {
			logHandler.assertNoProblems();
			assertEquals("1,2,2,2,1,5,2,6", result.get("result"));
		}, getSourceFile(Cloning.class));
	}
	
}
