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

import source.candies.Angular;
import source.candies.ExpressLib;
import source.candies.GlobalsImport;
import source.candies.JQuery;
import source.candies.QualifiedNames;

public class CandiesTests extends AbstractTest {

	@Test
	public void testGlobalsImport() {
//		transpile(ModuleKind.none, logHandler -> {
//			logHandler.assertReportedProblems(JSweetProblem.INTERNAL_TSC_ERROR, //
//					JSweetProblem.INTERNAL_TSC_ERROR, JSweetProblem.INTERNAL_TSC_ERROR, //
//					JSweetProblem.INTERNAL_TSC_ERROR, JSweetProblem.INTERNAL_TSC_ERROR, //
//					JSweetProblem.INTERNAL_TSC_ERROR, JSweetProblem.INTERNAL_TSC_ERROR, //
//					JSweetProblem.INTERNAL_TSC_ERROR, JSweetProblem.INTERNAL_TSC_ERROR, //
//					JSweetProblem.INTERNAL_TSC_ERROR, JSweetProblem.INTERNAL_TSC_ERROR);
//		} , getSourceFile(GlobalsImport.class));
		transpile(ModuleKind.commonjs, logHandler -> {
			assertEquals(0, logHandler.getReportedProblems().size());
		} , getSourceFile(GlobalsImport.class));
	}

	@Test
	public void testQualifiedNames() {
		transpile(ModuleKind.commonjs, logHandler -> {
			assertEquals(0, logHandler.getReportedProblems().size());
		} , getSourceFile(QualifiedNames.class));
	}

	@Test
	public void testAngular() {
		transpile(logHandler -> {
			assertEquals(0, logHandler.getReportedProblems().size());
		} , getSourceFile(Angular.class));
	}

	@Test
	public void testJQuery() {
		transpile(logHandler -> {
			assertEquals(0, logHandler.getReportedProblems().size());
		} , getSourceFile(JQuery.class));
	}
	
	@Test
	public void testExpressLib() {
		transpile(ModuleKind.commonjs, logHandler -> {
			assertEquals(0, logHandler.getReportedProblems().size());
		} , getSourceFile(ExpressLib.class));
	}

}
