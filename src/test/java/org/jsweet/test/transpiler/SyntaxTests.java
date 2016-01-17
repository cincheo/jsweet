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
import static org.junit.Assert.fail;

import org.apache.commons.io.FileUtils;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import source.syntax.AnnotationQualifiedNames;
import source.syntax.DocComments;
import source.syntax.FinalVariables;
import source.syntax.FinalVariablesRuntime;
import source.syntax.GlobalsCastMethod;
import source.syntax.GlobalsInvocation;
import source.syntax.StatementsWithNoBlocks;
import source.syntax.IndexedAccessInStaticScope;
import source.syntax.Keywords;
import source.syntax.Labels;
import source.syntax.QualifiedNames;
import source.syntax.References;
import source.syntax.SpecialFunctions;
import source.syntax.ValidIndexedAccesses;

public class SyntaxTests extends AbstractTest {

	@Test
	public void testReferences() {
		eval((logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("foo", r.get("s"));
			Assert.assertEquals((Number) 5, r.get("i"));
		} , getSourceFile(References.class));
	}

	@Test
	public void testKeywords() {
		transpile((logHandler) -> {
			Assert.assertEquals("There should be no errors", 5, logHandler.reportedProblems.size());
			for (int i = 0; i < 5; i++) {
				Assert.assertEquals("Error #" + i + " is not of the right kind", JSweetProblem.JS_KEYWORD_CONFLICT, logHandler.reportedProblems.get(i));
			}
		} , getSourceFile(Keywords.class));
	}

	@Test
	public void testStatementsWithNoBlocks() {
		transpile((logHandler) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(StatementsWithNoBlocks.class));
	}
	
	@Test
	public void testQualifiedNames() {
		transpile((logHandler) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(QualifiedNames.class));
	}

	@Test
	public void testAnnotationQualifiedNames() {
		transpile((logHandler) -> {
			Assert.assertEquals("Missing expected error", 1, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong type of expected error", JSweetProblem.INVALID_METHOD_BODY_IN_INTERFACE, logHandler.reportedProblems.get(0));
		} , getSourceFile(AnnotationQualifiedNames.class));
	}

	@Test
	public void testGlobalsInvocation() {
		transpile((logHandler) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(GlobalsInvocation.class));
	}

	@Test
	public void testSpecialFunctions() {
		transpile((logHandler) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(SpecialFunctions.class));
	}

	@Test
	public void testLabels() {
		transpile((logHandler) -> {
			Assert.assertEquals("Missing expected errors", 2, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong type of expected error", JSweetProblem.LABELS_ARE_NOT_SUPPORTED, logHandler.reportedProblems.get(0));
			Assert.assertEquals("Wrong type of expected error", JSweetProblem.LABELS_ARE_NOT_SUPPORTED, logHandler.reportedProblems.get(1));
		} , getSourceFile(Labels.class));
	}

	@Test
	public void testFinalVariables() {
		transpile((logHandler) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(FinalVariables.class));
	}

	@Test
	public void testFinalVariablesRuntime() {
		try {
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			EvaluationResult r = transpiler.eval("Java", logHandler, getSourceFile(FinalVariablesRuntime.class));
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong behavior output trace", "11223344", r.get("out").toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
		eval((logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals("Wrong behavior output trace", "11223344", r.get("out").toString());
		} , getSourceFile(FinalVariablesRuntime.class));
		
	}

	@Ignore
	@Test
	public void testIndexedAccessInStaticScope() {
		eval((logHandler, r) -> {
			Assert.assertEquals("Wrong output value", "value", r.get("out_a"));
			Assert.assertNull("Wrong output value", r.get("out_b"));
			Assert.assertNull("var wasn't deleted", r.get("out_c"));
		} , getSourceFile(IndexedAccessInStaticScope.class));
	}

	@Test
	public void testValidIndexedAccesses() {
		eval((logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			
			assertEquals("value", r.get("field1"));
			assertNull(r.get("field2"));
			assertNull(r.get("field3"));
			assertEquals("value4", r.get("field4"));
		} , getSourceFile(ValidIndexedAccesses.class));
	}
	
	@Test
	public void testGlobalCastMethod() {
		transpile((logHandler) -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(GlobalsCastMethod.class));
	}

	@Test
	public void testDocComments() {
		SourceFile f = getSourceFile(DocComments.class);
		transpile(logHandler -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			try {
				String generatedCode = FileUtils.readFileToString(f.getTsFile());
				assertTrue(generatedCode.contains("This is a test of comment."));
				assertTrue(generatedCode.contains("A method, which has some doc comment."));
				assertTrue(generatedCode.contains("This is a constant field."));
			} catch(Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		} , f);
	}
	
}
