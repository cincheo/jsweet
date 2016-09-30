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

import source.generics.AbstractClassAndOverride;
import source.generics.AddThisOnGenericMethods;
import source.generics.GenericObjectStructure;
import source.generics.InnerClassNotStatic;
import source.generics.InstantiationWithGenerics;
import source.generics.RawTypes;
import source.generics.StaticAnonymousClass;
import source.generics.Wildcards;

public class GenericsTests extends AbstractTest {

	@Test
	public void testInstantiationWithGenerics() {
		// TODO: qualified names do not work with modules :(
		transpile(ModuleKind.none, logHandler -> {
			assertEquals(0, logHandler.getReportedProblems().size());
		} , getSourceFile(InstantiationWithGenerics.class));
	}

	@Test
	public void testWildcards() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(Wildcards.class));
	}

	@Test
	public void testGenericObjectStructure() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(GenericObjectStructure.class));
	}

	@Test
	public void testAddThisOnGenericMethods() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(AddThisOnGenericMethods.class));
	}

	@Test
	public void testInnerClassNotStatic() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(InnerClassNotStatic.class));
	}

	@Test
	public void testRawTypes() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(RawTypes.class));
	}

	@Test
	public void testAbstractClassAndOverride() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(AbstractClassAndOverride.class));
	}

	@Test
	public void testStaticAnonymousClass() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(StaticAnonymousClass.class));
	}
	
}
