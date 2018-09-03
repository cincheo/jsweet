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

import org.jsweet.test.transpiler.util.TranspilerTestRunner;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.extension.Java2TypeScriptAdapter;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.extension.StringEnumAdapter;
import org.junit.Assert;
import org.junit.Test;

import source.enums.ComplexEnumWithAbstractMethods;
import source.enums.ComplexEnums;
import source.enums.ComplexEnumsWithInterface;
import source.enums.ComplexInnerEnums;
import source.enums.EnumInSamePackage;
import source.enums.EnumWithPropOfSameType;
import source.enums.EnumWithStatics;
import source.enums.Enums;
import source.enums.EnumsImplementingInterfaces;
import source.enums.ErasedEnum;
import source.enums.MyComplexEnum2;
import source.enums.StringEnums;
import source.enums.other.ComplexEnumsAccess;
import source.enums.other.EnumInOtherPackage;

public class EnumTests extends AbstractTest {

	class AddRootFactory extends JSweetFactory {
		@Override
		public Java2TypeScriptAdapter createAdapter(JSweetContext context) {
			return new Java2TypeScriptAdapter(super.createAdapter(context)) {
				{
					context.addAnnotation("@Root", "source.enums");
				}
			};
		}
	}

	class EraseEnumFactory extends JSweetFactory {
		@Override
		public Java2TypeScriptAdapter createAdapter(JSweetContext context) {
			return new Java2TypeScriptAdapter(super.createAdapter(context)) {
				{
					context.addAnnotation("@Erased", "source.enums.ErasedEnum");
				}
			};
		}
	}

	@Test
	public void testEnums() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals(0, ((Number) r.get("value")).intValue());
			Assert.assertEquals("A", r.get("nameOfA"));
			Assert.assertEquals(0, ((Number) r.get("ordinalOfA")).intValue());
			Assert.assertEquals("A", r.get("valueOfA"));
			Assert.assertEquals(2, ((Number) r.get("valueOfC")).intValue());
			Assert.assertEquals("B", r.get("ref"));
			Assert.assertEquals("A", r.get("switch"));
		}, getSourceFile(EnumInSamePackage.class), getSourceFile(EnumInOtherPackage.class), getSourceFile(Enums.class));
	}

	@Test
	public void testComplexEnums() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals(">static,2,--2--,ratio_2_1_5,true,true,true,true,2,2", r.get("trace"));
		}, getSourceFile(ComplexEnums.class));
	}

	@Test
	public void testComplexEnumsAccess() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals(">2,--2--,ratio_2_1_5,true,true,true,true", r.get("trace2"));
			// Assert.assertEquals(">static,2,--2--,ratio_2_1_5,true,true,true,true,2,2",
			// r.get("trace2"));
		}, getSourceFile(MyComplexEnum2.class), getSourceFile(ComplexEnumsAccess.class));
	}

	@Test
	public void testComplexInnerEnums() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(ComplexInnerEnums.class));
	}

	@Test
	public void testComplexEnumWithAbstractMethods() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
			Assert.assertEquals(">ok1,ok2", r.get("trace"));
		}, getSourceFile(ComplexEnumWithAbstractMethods.class));
		transpilerTest().getTranspiler().setBundle(true);
		eval(ModuleKind.none, (logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(">ok1,ok2", r.get("trace"));
		}, getSourceFile(ComplexEnumWithAbstractMethods.class));
		transpilerTest().getTranspiler().setBundle(false);
		
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new AddRootFactory());
		transpilerTest.eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(">ok1,ok2", r.get("trace"));
		}, getSourceFile(ComplexEnumWithAbstractMethods.class));
		transpilerTest.getTranspiler().setBundle(true);
		eval(ModuleKind.none, (logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(">ok1,ok2", r.get("trace"));
		}, getSourceFile(ComplexEnumWithAbstractMethods.class));
	}

	@Test
	public void testErasedEnum() {
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new EraseEnumFactory());
		transpilerTest.transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(ErasedEnum.class));
	}

	@Test
	public void testComplexEnumsWithInterface() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(ComplexEnumsWithInterface.class));
	}

	@Test
	public void testComplexEnumsWithStatics() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(EnumWithStatics.class));
	}

	@Test
	public void testStringEnums() {
		TranspilerTestRunner transpilerTest = new TranspilerTestRunner(getCurrentTestOutDir(), new JSweetFactory() {
			@Override
			public PrinterAdapter createAdapter(JSweetContext context) {
				return new StringEnumAdapter(super.createAdapter(context));
			}
		});
		transpilerTest.eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(StringEnums.class));
	}

	@Test
	public void testEnumsImplementingInterfaces() {
		eval((logHandler, r) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(EnumsImplementingInterfaces.class));
	}

	@Test
	public void testEnumWithPropOfSameType() {
		transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(EnumWithPropOfSameType.class));
	}
	
}
