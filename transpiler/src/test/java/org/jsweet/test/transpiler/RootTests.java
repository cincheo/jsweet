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

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.junit.Assert;
import org.junit.Test;

import source.root.noroot.a.B;
import source.root.noroot.a.GlobalsInNoRoot;
import source.root.root.a.GlobalsInRoot;
import source.root.root2.AccessFromClassInRoot;
import source.root.root2.a.A;
import source.root.rootparent1.InvalidClassLocation;
import source.root.rootparent1.root.NoClassesInRootParent;
import source.root.rootparent2.root.NoRootInRoot;

public class RootTests extends AbstractTest {

	@Test
	public void testGlobalsInRoot() {
		eval(ModuleKind.commonjs, (logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(true, r.get("m1"));
			Assert.assertEquals(true, r.get("m2"));
		}, getSourceFile(GlobalsInRoot.class));
	}

	@Test
	public void testGlobalsInNoRoot() {
		eval((logHandler, r) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertEquals(true, r.get("m1"));
			Assert.assertEquals(true, r.get("m2"));
		}, getSourceFile(GlobalsInNoRoot.class));
	}

	@Test
	public void testNoClassesInRootParent() {
		transpile((logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(InvalidClassLocation.class), getSourceFile(NoClassesInRootParent.class));
transpilerTest().getTranspiler().setNoRootDirectories(true);
		transpile((logHandler) -> {
			if (!transpilerTest().getTranspiler().isBundle()) {
				logHandler.assertReportedProblems(JSweetProblem.CLASS_OUT_OF_ROOT_PACKAGE_SCOPE);
			}
		}, getSourceFile(InvalidClassLocation.class), getSourceFile(NoClassesInRootParent.class));
	}

	@Test
	public void testNoRootInRoot() {
		transpile((logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(NoRootInRoot.class));
		transpilerTest().getTranspiler().setNoRootDirectories(true);
		transpile((logHandler) -> {
			logHandler.assertReportedProblems(JSweetProblem.ENCLOSED_ROOT_PACKAGES);
		}, getSourceFile(NoRootInRoot.class));
	}

	@Test
	public void testAccessFromClassInRoot() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertNoProblems();
		}, getSourceFile(AccessFromClassInRoot.class), getSourceFile(A.class), getSourceFile(B.class));
		transpile(ModuleKind.commonjs, (logHandler) -> {
			logHandler.assertReportedProblems(/*JSweetProblem.MULTIPLE_ROOT_PACKAGES_NOT_ALLOWED_WITH_MODULES*/);
		}, getSourceFile(AccessFromClassInRoot.class), getSourceFile(A.class), getSourceFile(B.class));
	}

}
