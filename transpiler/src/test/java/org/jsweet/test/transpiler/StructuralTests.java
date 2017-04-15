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

import static org.jsweet.transpiler.JSweetProblem.GLOBAL_DELETE;
import static org.jsweet.transpiler.JSweetProblem.GLOBAL_INDEXER_GET;
import static org.jsweet.transpiler.JSweetProblem.GLOBAL_INDEXER_SET;
import static org.junit.Assert.assertEquals;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetOptions;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.extension.Java2TypeScriptAdapter;
import org.junit.Assert;
import org.junit.Test;

import source.structural.AbstractClass;
import source.structural.AnonymousClass;
import source.structural.AnonymousClassForLambda;
import source.structural.AutoImportClassesInSamePackage;
import source.structural.AutoImportClassesInSamePackageUsed;
import source.structural.DefaultMethods;
import source.structural.DefaultMethodsConsumer;
import source.structural.EndsWithGlobals;
import source.structural.ExtendsClassInSameFile;
import source.structural.ExtendsObject;
import source.structural.GetClass;
import source.structural.GlobalsAccess;
import source.structural.Inheritance;
import source.structural.InnerClass;
import source.structural.InnerClassNotStatic;
import source.structural.InnerClassUse;
import source.structural.InstanceOf;
import source.structural.InstanceofForInterfaces;
import source.structural.InterfaceInheritance;
import source.structural.JSNI;
import source.structural.LocalClasses;
import source.structural.Name;
import source.structural.NameClashesWithMethodInvocations;
import source.structural.NoNameClashesWithFields;
import source.structural.NoWildcardsInImports;
import source.structural.ObjectTypes;
import source.structural.StaticMembersInInterfaces;
import source.structural.TwoClassesInSameFile;
import source.structural.ReplaceAnnotation;
import source.structural.WrongConstructsInInterfaces;
import source.structural.WrongThisAccessOnStatic;
import source.structural.globalclasses.Globals;
import source.structural.globalclasses.a.ClassWithStaticMethod;
import source.structural.globalclasses.a.GlobalsConstructor;
import source.structural.globalclasses.c.ClassUsingStaticMethod;
import source.structural.globalclasses.c.GlobalFunctionGetSetDelete;
import source.structural.globalclasses.d.GlobalFunctionAccessFromMain;
import source.structural.globalclasses.f.InvalidGlobalSetGetDelete;
import source.structural.other.Wrapping;

public class StructuralTests extends AbstractTest {

	@Test
	public void testNoNameClashesWithFields() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals(2, (int) r.get("v1"));
			assertEquals(2, (int) r.get("v2"));
			assertEquals(3, (int) r.get("v3"));
			assertEquals("hello", (String) r.get("v4"));
			assertEquals("hello", (String) r.get("v5"));
		}, getSourceFile(NoNameClashesWithFields.class));
	}

	@Test
	public void testVariableMethodNameClashes() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.HIDDEN_INVOCATION, JSweetProblem.HIDDEN_INVOCATION,
					JSweetProblem.HIDDEN_INVOCATION);
		}, getSourceFile(NameClashesWithMethodInvocations.class));
	}

	@Test
	public void testTwoClassesInSameFile() {
		transpile(logHandler -> {
			assertEquals("There should be 0 problems", 0, logHandler.reportedProblems.size());
		}, getSourceFile(TwoClassesInSameFile.class));
	}

	@Test
	public void testExtendsClassInSameFile() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(ExtendsClassInSameFile.class));
	}

	@Test
	public void testInnerClass() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("test1", r.get("value1"));
			assertEquals("test2", r.get("value2"));
			assertEquals("test3", r.get("value3"));
			assertEquals("test4", r.get("value4"));
			assertEquals("abc", r.get("value5"));
		}, getSourceFile(InnerClass.class));
	}

	@Test
	public void testInnerClassUse() {
		transpile(TestTranspilationHandler::assertNoProblems, getSourceFile(InnerClass.class),
				getSourceFile(Wrapping.class), getSourceFile(InnerClassUse.class));
	}

	@Test
	public void testInnerClassNotStatic() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("22abc,22a,22ABC,22a,22b,22c,22ABC,test22a,staticMethod,1", r.get("trace"));
		}, getSourceFile(InnerClassNotStatic.class));
	}

	@Test
	public void testAnonymousClasses() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("test,22abcfinal", r.get("trace"));
		}, getSourceFile(AnonymousClass.class));
	}

	@Test
	public void testAnonymousClassForLambda() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(AnonymousClassForLambda.class));
	}

	@Test
	public void testInheritance() {
		eval((logHandler, r) -> { // TODO
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals(true, r.<Boolean> get("X"));
			assertEquals(true, r.<Boolean> get("Y"));
			assertEquals("s1", r.<Boolean> get("s1b"));
			assertEquals("s2", r.<Boolean> get("s2b"));
			assertEquals(false, r.<Boolean> get("itfo"));
			assertEquals("s1", r.<Boolean> get("s1o"));
			assertEquals("s2", r.<Boolean> get("s2o"));
		}, getSourceFile(Inheritance.class));
	}

	@Test
	public void testWrongConstructsInInterfaces() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems( //
					JSweetProblem.INVALID_FIELD_INITIALIZER_IN_INTERFACE, //
					// JSweetProblem.INVALID_STATIC_IN_INTERFACE, //
					JSweetProblem.INVALID_PRIVATE_IN_INTERFACE, //
					JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, //
					JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, //
					// JSweetProblem.INVALID_STATIC_IN_INTERFACE, //
					JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE, //
					JSweetProblem.INVALID_INITIALIZER_IN_INTERFACE);
		}, getSourceFile(WrongConstructsInInterfaces.class));
	}

	@Test
	public void testAbstractClass() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(AbstractClass.class));
	}

	@Test
	public void testExtendsObject() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(ExtendsObject.class));
	}

	@Test
	public void testInterfaceInheritance() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(InterfaceInheritance.class));
	}

	@Test
	public void testInstanceofForInterfaces() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals("1,2,1,3,4,5,6", r.get("trace"));
		}, getSourceFile(InstanceofForInterfaces.class));
	}

	@Test
	public void testNoConstructorInGlobalsClass() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(//
					JSweetProblem.GLOBAL_CONSTRUCTOR_DEF, //
					JSweetProblem.GLOBAL_CANNOT_BE_INSTANTIATED, //
					JSweetProblem.GLOBAL_CANNOT_BE_INSTANTIATED, //
					JSweetProblem.GLOBALS_CLASS_CANNOT_BE_SUBCLASSED);
		}, getSourceFile(GlobalsConstructor.class));
	}

	@Test
	public void testNoGetSetInGlobalFunction() {
		transpile(TestTranspilationHandler::assertNoProblems, getSourceFile(GlobalFunctionGetSetDelete.class));
	}

	@Test
	public void testNoStaticDeleteInGlobalFunction() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(GLOBAL_INDEXER_GET, GLOBAL_INDEXER_SET, GLOBAL_DELETE);
		}, getSourceFile(InvalidGlobalSetGetDelete.class));
	}

	@Test
	public void testGlobalFunctionAccessFromMain() {
		// TODO: make it work with modules
		eval(ModuleKind.none, (logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(true, r.get("mainInvoked"));
			Assert.assertEquals("invoked", r.get("test"));
			// Assert.assertEquals("invoked1_2", r.get("Static"));
			Assert.assertEquals("invoked1_2", r.get("Ok"));
			Assert.assertEquals("invoked1_2", r.get("test2"));
		}, getSourceFile(Globals.class), getSourceFile(source.structural.globalclasses.e.Globals.class),
				getSourceFile(GlobalFunctionAccessFromMain.class));
	}

	@Test
	public void testWildcardsInImports() {
		transpile((logHandler) -> {
			logHandler.assertReportedProblems(JSweetProblem.WILDCARD_IMPORT);
		}, getSourceFile(NoWildcardsInImports.class));
	}

	@Test
	public void testName() {
		transpile(TestTranspilationHandler::assertNoProblems, getSourceFile(Name.class));
	}

	@Test
	public void testAutoImportClassesInSamePackage() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("A method was not executed as expected", true, r.get("m1"));
			Assert.assertEquals("A method was not executed as expected", true, r.get("m2"));
			Assert.assertEquals("A method was not executed as expected", true, r.get("sm1"));
			Assert.assertEquals("A method was not executed as expected", true, r.get("sm2"));
		}, getSourceFile(AutoImportClassesInSamePackageUsed.class),
				getSourceFile(AutoImportClassesInSamePackage.class));
	}

	@Test
	public void testGlobalsAccess() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("Renaud Pawlak", r.get("result"));
		}, getSourceFile(GlobalsAccess.class));
	}

	@Test
	public void testWrongGlobals() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.GLOBALS_CLASS_CANNOT_HAVE_SUPERCLASS,
					JSweetProblem.GLOBAL_CONSTRUCTOR_DEF, JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS,
					JSweetProblem.GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS);
		}, getSourceFile(source.structural.wrongglobals.Globals.class));
	}

	@Test
	public void testEndsWithGlobals() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(EndsWithGlobals.class));
	}
	
	@Test
	public void testObjectTypes() {
		transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(ObjectTypes.class));
	}

	@Test
	public void testWrongThisAccessOnStatic() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.CANNOT_ACCESS_STATIC_MEMBER_ON_THIS,
					JSweetProblem.CANNOT_ACCESS_STATIC_MEMBER_ON_THIS);
		}, getSourceFile(WrongThisAccessOnStatic.class));
	}

	@Test
	public void testInstanceOf() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(InstanceOf.class));
	}

	@Test
	public void testJSNI() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(JSNI.class));
	}

	@Test
	public void testReplaceAnnotation() {
		createTranspiler(new JSweetFactory() {
			@Override
			public Java2TypeScriptAdapter createAdapter(JSweetContext context) {
				return new Java2TypeScriptAdapter(context) {
					{
						context.addAnnotation("@Replace('return (this.i + 2)')",
								"source.structural.ReplaceAnnotation.m2()");
						context.addAnnotation("@Replace('let result = (() => { #BODY# })(); return (result + '#METHODNAME#'.length);')",
								"source.structural.ReplaceAnnotation.m4()");
					}
				};
			}
		});
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals(2, (int) r.get("test1"));
			assertEquals(3, (int) r.get("test2"));
			assertEquals(1, (int) r.get("test3"));
			assertEquals(3, (int) r.get("test4"));
		}, getSourceFile(ReplaceAnnotation.class));
		createTranspiler(new JSweetFactory());
	}

	@Test
	public void testDefaultMethods() {
		// TODO: make it work with modules
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("m,m1,m2-overriden", r.get("trace"));
		}, getSourceFile(ClassWithStaticMethod.class), getSourceFile(DefaultMethods.class),
				getSourceFile(DefaultMethodsConsumer.class));
	}

	@Test
	public void testStaticMembersInInterfaces() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("test1", r.get("value1"));
			assertEquals("test2", r.get("value2"));
			assertEquals("test3", r.get("value3"));
			assertEquals("test4", r.get("value4"));
		}, getSourceFile(StaticMembersInInterfaces.class));
		createTranspiler(new JSweetFactory() {
			@Override
			public JSweetContext createContext(JSweetOptions options) {
				return new JSweetContext(options) {
					{
						addAnnotation("@Root", "source.structural");
					}
				};
			}
		});
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("test1", r.get("value1"));
			assertEquals("test2", r.get("value2"));
			assertEquals("test3", r.get("value3"));
			assertEquals("test4", r.get("value4"));
		}, getSourceFile(StaticMembersInInterfaces.class));
		createTranspiler(new JSweetFactory());
	}

	@Test
	public void testStaticImport() {
		// TODO: support with modules
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals(true, r.get("m"));
		}, getSourceFile(ClassWithStaticMethod.class), getSourceFile(ClassUsingStaticMethod.class));
	}

	@Test
	public void testGetClass() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			assertEquals("source.structural.AClass1", r.get("name1"));
			assertEquals("source.structural.AClass1", r.get("name2"));
			assertEquals("source.structural.AClass1", r.get("name3"));
			assertEquals("source.structural.Functions", r.get("name4"));
			assertEquals("source.structural.Functions", r.get("name5"));
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
	public void testLocalClasses() {
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		}, getSourceFile(LocalClasses.class));
	}

}
