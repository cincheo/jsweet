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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.junit.Assert;
import org.junit.Test;

import source.ambient.GlobalsAccess;
import source.ambient.LibAccess;
import source.ambient.LibAccessSubModule;
import source.ambient.WrongUseOfAmbientAnnotations;
import source.ambient.lib.Base;
import source.ambient.lib.Extension;
import source.ambient.lib.sub.C;
import source.ambient.three.Globals;

public class AmbientTests extends AbstractTest {

	@Test
	public void testLibAccess() throws Exception {
		File target = new File(transpiler.getTsOutputDir(), "lib.js");
		FileUtils.deleteQuietly(target);
		FileUtils.copyFile(new File(TEST_DIRECTORY_NAME + "/source/ambient/lib.js"), target);
		System.out.println("copied to " + target);

		SourceFile libJs = new SourceFile(null) {
			{
				setJsFile(target);
			}

			@Override
			public String toString() {
				return target.toString();
			}

			@Override
			public void touch() {
			}
		};

		// TODO: also test with modules (we need a requirable lib and a way to
		// specify modules in ambients)
		eval(ModuleKind.none, false, (logHandler, result) -> {
			Assert.assertTrue("test was not executed", result.get("baseExecuted"));
			Assert.assertTrue("extension was not executed", result.get("extensionExecuted"));
		}, libJs, getSourceFile(LibAccess.class));
	}

	@Test
	public void testLibAccessSubModule() throws Exception {
		File target = new File(transpiler.getTsOutputDir(), "libsub.js");
		FileUtils.deleteQuietly(target);
		FileUtils.copyFile(new File(TEST_DIRECTORY_NAME + "/source/ambient/libsub.js"), target);
		System.out.println("copied to " + target);

		SourceFile libJs = new SourceFile(null) {
			{
				setJsFile(target);
			}

			@Override
			public String toString() {
				return target.toString();
			}

			@Override
			public void touch() {
			}
		};

		// TODO: also test with modules (we need a requirable lib and a way to
		// specify modules in ambients)
		eval(ModuleKind.none, false, (logHandler, result) -> {
			Assert.assertTrue("test was not executed", result.get("baseExecuted"));
			Assert.assertTrue("extension was not executed", result.get("extensionExecuted"));
		}, libJs, getSourceFile(LibAccessSubModule.class), getSourceFile(Base.class), getSourceFile(Extension.class),
				getSourceFile(C.class));
	}

	@Test
	public void testGlobalsAccess() {
		transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(GlobalsAccess.class), getSourceFile(Globals.class));
	}

	@Test
	public void testWrongUseOfAmbientAnnotations() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.WRONG_USE_OF_AMBIENT, JSweetProblem.WRONG_USE_OF_AMBIENT);
		}, getSourceFile(WrongUseOfAmbientAnnotations.class));
	}

}
