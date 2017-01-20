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

import org.junit.Assert;
import org.junit.Test;

import source.enums.ComplexEnumWithAbstractMethods;
import source.enums.ComplexEnums;
import source.enums.EnumInSamePackage;
import source.enums.Enums;
import source.enums.LengthUnit;
import source.enums.other.EnumInOtherPackage;

public class EnumTests extends AbstractTest {

	@Test
	public void testEnums() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
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
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(">static,2,--2--,ratio_2_1_5,true,true,true,true", r.get("trace"));
		}, getSourceFile(ComplexEnums.class));
	}

	@Test
	public void testComplexEnumWithAbstractMethods() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(">ok1,ok2", r.get("trace"));
		}, getSourceFile(ComplexEnumWithAbstractMethods.class));
	}

}
