/* Copyright 2015 CINCHEO SAS
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

import static org.jsweet.transpiler.JSweetProblem.GLOBAL_CANNOT_BE_INSTANTIATED;
import static org.jsweet.transpiler.JSweetProblem.GLOBAL_CONSTRUCTOR_DEF;
import static org.jsweet.transpiler.JSweetProblem.GLOBAL_DELETE;
import static org.jsweet.transpiler.JSweetProblem.GLOBAL_INDEXER_GET;
import static org.jsweet.transpiler.JSweetProblem.GLOBAL_INDEXER_SET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jsweet.test.transpiler.source.structural.AbstractClass;
import org.jsweet.test.transpiler.source.structural.AutoImportClassesInSamePackage;
import org.jsweet.test.transpiler.source.structural.AutoImportClassesInSamePackageUsed;
import org.jsweet.test.transpiler.source.structural.Enums;
import org.jsweet.test.transpiler.source.structural.ExtendsClassInSameFile;
import org.jsweet.test.transpiler.source.structural.ExtendsObject;
import org.jsweet.test.transpiler.source.structural.GlobalsAccess;
import org.jsweet.test.transpiler.source.structural.Inheritance;
import org.jsweet.test.transpiler.source.structural.InnerClass;
import org.jsweet.test.transpiler.source.structural.NameClashes;
import org.jsweet.test.transpiler.source.structural.NoInstanceofForInterfaces;
import org.jsweet.test.transpiler.source.structural.NoWildcardsInImports;
import org.jsweet.test.transpiler.source.structural.TwoClassesInSameFile;
import org.jsweet.test.transpiler.source.structural.WrongConstructsInEnums;
import org.jsweet.test.transpiler.source.structural.WrongConstructsInInterfaces;
import org.jsweet.test.transpiler.source.structural.globalclasses.Globals;
import org.jsweet.test.transpiler.source.structural.globalclasses.a.GlobalsConstructor;
import org.jsweet.test.transpiler.source.structural.globalclasses.b.GlobalFunctionStaticGetSet;
import org.jsweet.test.transpiler.source.structural.globalclasses.c.GlobalFunctionGetSet;
import org.jsweet.test.transpiler.source.structural.globalclasses.d.GlobalFunctionAccessFromMain;
import org.jsweet.test.transpiler.source.structural.globalclasses.f.GlobalFunctionStaticDelete;
import org.jsweet.test.transpiler.source.structural.globalclasses.g.GlobalFunctionDelete;
import org.jsweet.test.transpiler.source.structural.globalclasses.noroot.a.GlobalsInNoRoot;
import org.jsweet.test.transpiler.source.structural.globalclasses.root.a.GlobalsInRoot;
import org.jsweet.test.transpiler.source.structural.rootparent1.InvalidClassLocation;
import org.jsweet.test.transpiler.source.structural.rootparent1.root.NoClassesInRootParent;
import org.jsweet.test.transpiler.source.structural.rootparent2.root.NoRootInRoot;
import org.jsweet.transpiler.JSweetProblem;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class StructuralTests extends AbstractTest {

	@Test
	public void testFieldMethodNameClashes() {
		transpile(logHandler -> {
			assertEquals("There should be two problems", 2, logHandler.reportedProblems.size());
			assertTrue("Missing expected problem: " + JSweetProblem.FIELD_CONFLICTS_METHOD,
					logHandler.reportedProblems.contains(JSweetProblem.FIELD_CONFLICTS_METHOD));
			assertTrue("Missing expected problem: " + JSweetProblem.METHOD_CONFLICTS_FIELD,
					logHandler.reportedProblems.contains(JSweetProblem.METHOD_CONFLICTS_FIELD));
		} , getSourceFile(NameClashes.class));
	}

	@Test
	public void testTwoClassesInSameFile() {
		transpile(logHandler -> {
			assertEquals("There should be 0 problems", 0, logHandler.reportedProblems.size());
		} , getSourceFile(TwoClassesInSameFile.class));
	}

	@Test
	public void testExtendsClassInSameFile() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(ExtendsClassInSameFile.class));
	}

	@Test
	public void testInnerClass() {
		transpile(logHandler -> {
			assertEquals("There should be 1 problem", 1, logHandler.reportedProblems.size());
			assertTrue("Missing expected problem: " + JSweetProblem.INNER_CLASS, logHandler.reportedProblems.contains(JSweetProblem.INNER_CLASS));
		} , getSourceFile(InnerClass.class));
	}

	@Test
	public void testInheritance() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(Inheritance.class));
	}

	@Test
	public void testWrongConstructsInInterfaces() {
		transpile(logHandler -> {
			assertEquals("There should be 11 errors", 11, logHandler.reportedProblems.size());
			assertEquals("Wrong error type", JSweetProblem.INTERFACE_MUST_BE_ABSTRACT, logHandler.reportedProblems.get(0));
			assertEquals("Wrong error type", JSweetProblem.INVALID_FIELD_INITIALIZER_IN_INTERFACE, logHandler.reportedProblems.get(1));
			assertEquals("Wrong error type", JSweetProblem.INVALID_STATIC_IN_INTERFACE, logHandler.reportedProblems.get(2));
			assertEquals("Wrong error type", JSweetProblem.INVALID_PRIVATE_IN_INTERFACE, logHandler.reportedProblems.get(3));
			assertEquals("Wrong error type", JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, logHandler.reportedProblems.get(4));
			assertEquals("Wrong error type", JSweetProblem.NATIVE_MODIFIER_IS_NOT_ALLOWED, logHandler.reportedProblems.get(5));
			assertEquals("Wrong error type", JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, logHandler.reportedProblems.get(6));
			assertEquals("Wrong error type", JSweetProblem.NATIVE_MODIFIER_IS_NOT_ALLOWED, logHandler.reportedProblems.get(7));
			assertEquals("Wrong error type", JSweetProblem.INVALID_STATIC_IN_INTERFACE, logHandler.reportedProblems.get(8));
			assertEquals("Wrong error type", JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, logHandler.reportedProblems.get(9));
			assertEquals("Wrong error type", JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, logHandler.reportedProblems.get(10));
		} , getSourceFile(WrongConstructsInInterfaces.class));
	}

	@Test
	public void testWrongConstructsInEnums() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.INVALID_FIELD_IN_ENUM, JSweetProblem.INVALID_FIELD_IN_ENUM, JSweetProblem.INVALID_FIELD_IN_ENUM,
					JSweetProblem.INVALID_CONSTRUCTOR_IN_ENUM, JSweetProblem.INVALID_METHOD_IN_ENUM, JSweetProblem.INVALID_METHOD_IN_ENUM,
					JSweetProblem.INVALID_METHOD_IN_ENUM);
		} , getSourceFile(WrongConstructsInEnums.class));
	}

	@Test
	public void testAbstractClass() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(AbstractClass.class));
	}

	@Test
	public void testExtendsObject() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(ExtendsObject.class));
	}

	@Test
	public void testNoInstanceofForInterfaces() {
		transpile(logHandler -> {
			assertEquals("There should be 2 problems", 2, logHandler.reportedProblems.size());
			assertEquals("Missing expected problem: " + JSweetProblem.INVALID_INSTANCEOF_INTERFACE, JSweetProblem.INVALID_INSTANCEOF_INTERFACE,
					logHandler.reportedProblems.get(0));
			assertEquals("Missing expected problem: " + JSweetProblem.INVALID_INSTANCEOF_INTERFACE, JSweetProblem.INVALID_INSTANCEOF_INTERFACE,
					logHandler.reportedProblems.get(1));
		} , getSourceFile(NoInstanceofForInterfaces.class));
	}

	@Test
	public void testEnums() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong enum behavior", 0, ((Number) r.get("value")).intValue());
			Assert.assertEquals("Wrong enum behavior", "A", r.get("nameOfA"));
			Assert.assertEquals("Wrong enum behavior", 0, ((Number) r.get("ordinalOfA")).intValue());
			Assert.assertEquals("Wrong enum behavior", 0, ((Number) r.get("valueOfA")).intValue());
			Assert.assertEquals("Wrong enum behavior", 2, ((Number) r.get("valueOfC")).intValue());
		} , getSourceFile(Enums.class));
	}

	@Test
	public void testNoConstructorInGlobalsClass() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(//
					GLOBAL_CONSTRUCTOR_DEF, //
					GLOBAL_CANNOT_BE_INSTANTIATED);
		} , getSourceFile(GlobalsConstructor.class));
	}

	@Test
	public void testNoGetSetInGlobalFunction() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(GLOBAL_INDEXER_GET, GLOBAL_INDEXER_SET, GLOBAL_INDEXER_GET, GLOBAL_INDEXER_SET);
		} , getSourceFile(GlobalFunctionGetSet.class));
	}

	@Test
	public void testNoStaticGetSetInGlobalFunction() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(GLOBAL_INDEXER_GET, GLOBAL_INDEXER_SET);
		} , getSourceFile(GlobalFunctionStaticGetSet.class));
	}

	@Test
	public void testNoStaticDeleteInGlobalFunction() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(GLOBAL_DELETE);
		} , getSourceFile(GlobalFunctionStaticDelete.class));
	}

	@Test
	public void testNoDeleteInGlobalFunction() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(GLOBAL_DELETE);
		} , getSourceFile(GlobalFunctionDelete.class));
	}

	@Test
	public void testGlobalFunctionAccessFromMain() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(true, r.get("mainInvoked"));
			Assert.assertEquals("invoked", r.get("test"));
			Assert.assertEquals("invoked1_2", r.get("Static"));
			Assert.assertEquals("invoked1_2", r.get("test2"));
		} , getSourceFile(Globals.class), getSourceFile(org.jsweet.test.transpiler.source.structural.globalclasses.e.Globals.class),
				getSourceFile(GlobalFunctionAccessFromMain.class));
	}

	@Test
	public void testGlobalsInRoot() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(true, r.get("m1"));
			Assert.assertEquals(true, r.get("m2"));
		} , getSourceFile(GlobalsInRoot.class));
	}

	@Test
	public void testGlobalsInNoRoot() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(true, r.get("m1"));
			Assert.assertEquals(true, r.get("m2"));
		} , getSourceFile(GlobalsInNoRoot.class));
	}

	@Ignore
	@Test
	public void testNoClassesInRootParent() {
		transpile((logHandler) -> {
			assertEquals(1, logHandler.reportedProblems.size());
		} , getSourceFile(InvalidClassLocation.class), getSourceFile(NoClassesInRootParent.class));
	}

	@Ignore
	@Test
	public void testNoRootInRoot() {
		transpile((logHandler) -> {
			assertEquals(1, logHandler.reportedProblems.size());
		} , getSourceFile(NoRootInRoot.class));
	}

	@Test
	public void testWildcardsInImports() {
		transpile((logHandler) -> {
			logHandler.assertReportedProblems(JSweetProblem.WILDCARD_IMPORT, JSweetProblem.WILDCARD_IMPORT);
		} , getSourceFile(NoWildcardsInImports.class));
	}

	@Test
	public void testAutoImportClassesInSamePackage() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("A method was not executed as expected", true, r.get("m1"));
			Assert.assertEquals("A method was not executed as expected", true, r.get("m2"));
			Assert.assertEquals("A method was not executed as expected", true, r.get("sm1"));
			Assert.assertEquals("A method was not executed as expected", true, r.get("sm2"));
		} , getSourceFile(AutoImportClassesInSamePackageUsed.class), getSourceFile(AutoImportClassesInSamePackage.class));
	}

	@Test
	public void testGlobalsAccess() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("Renaud Pawlak", r.get("result"));
		} , getSourceFile(GlobalsAccess.class));
	}

}
