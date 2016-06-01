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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.LinkedList;

import org.jsweet.JSweetCommandLineLauncher;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ProcessUtil;
import org.jsweet.transpiler.util.Util;
import org.junit.Ignore;
import org.junit.Test;

import source.overload.Overload;
import source.structural.AbstractClass;

public class TranspilerTests extends AbstractTest {

	@Ignore
	@Test
	public void testTranspilerWatchMode() {
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			File overload = getSourceFile(Overload.class).getJavaFile();
			File abstractClass = getSourceFile(AbstractClass.class).getJavaFile();
			JSweetTranspiler transpiler = new JSweetTranspiler();
			transpiler.setTscWatchMode(true);
			transpiler.setPreserveSourceLineNumbers(true);
			long t = System.currentTimeMillis();
			transpiler.transpile(logHandler, new SourceFile(overload), new SourceFile(abstractClass));
			t = System.currentTimeMillis() - t;
			assertEquals("There should be no problems", 0, logHandler.reportedProblems.size());
			assertTrue("Wrong transpiler state", transpiler.isTscWatchMode());
			assertEquals("Wrong transpiler state", 2, transpiler.getWatchedFiles().length);
			assertEquals("Wrong transpiler state", overload, transpiler.getWatchedFile(overload).getJavaFile());

			Thread.sleep(Math.max(4000, t * 5));

			assertTrue("File not generated", transpiler.getWatchedFile(overload).getJsFile().exists());

			long ts1 = transpiler.getWatchedFile(overload).getJsFileLastTranspiled();

			transpiler.getWatchedFile(overload).getTsFile().setLastModified(System.currentTimeMillis());

			Thread.sleep(Math.max(2000, t * 4));

			assertTrue("File not regenerated", transpiler.getWatchedFile(overload).getJsFileLastTranspiled() != ts1);

			transpiler.transpile(logHandler, new SourceFile(overload));
			assertEquals("There should be no problems", 0, logHandler.reportedProblems.size());
			assertTrue("Wrong transpiler state", transpiler.isTscWatchMode());
			assertEquals("Wrong transpiler state", 2, transpiler.getWatchedFiles().length);
			assertEquals("Wrong transpiler state", overload, transpiler.getWatchedFile(overload).getJavaFile());

			Thread.sleep(Math.max(2000, t * 4));

			assertTrue("File not regenerated", transpiler.getWatchedFile(overload).getJsFileLastTranspiled() != ts1);

			transpiler.resetTscWatchMode();

			transpiler.transpile(logHandler, new SourceFile(overload));
			assertEquals("There should be no problems", 0, logHandler.reportedProblems.size());
			assertTrue("Wrong transpiler state", transpiler.isTscWatchMode());
			assertEquals("Wrong transpiler state", 1, transpiler.getWatchedFiles().length);
			assertEquals("Wrong transpiler state", overload, transpiler.getWatchedFile(overload).getJavaFile());

			Thread.sleep(Math.max(1000, t * 2));

			assertTrue("File not regenerated", transpiler.getWatchedFile(overload).getJsFileLastTranspiled() != ts1);

			transpiler.setTscWatchMode(false);
			SourceFile sf = new SourceFile(overload);
			transpiler.transpile(logHandler, sf);
			assertEquals("There should be no problems", 0, logHandler.reportedProblems.size());
			assertTrue("Wrong transpiler state", !transpiler.isTscWatchMode());
			assertTrue("Wrong transpiler state", transpiler.getWatchedFiles() == null);

			ts1 = sf.getJsFileLastTranspiled();

			sf.getTsFile().setLastModified(System.currentTimeMillis());

			Thread.sleep(Math.max(1000, t * 2));

			assertTrue("File regenerated", sf.getJsFileLastTranspiled() == ts1);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
	}

	@Ignore
	@Test
	public void testTscInstallation() throws Exception {
		ProcessUtil.uninstallNodePackage("typescript", true);
		assertFalse(ProcessUtil.isInstalledWithNpm("tsc"));
		globalSetUp();
		//transpiler.cleanWorkingDirectory();
		transpile(ModuleKind.none, h -> h.assertReportedProblems(), getSourceFile(Overload.class));
	}

	@Test
	public void testCommandLine() throws Throwable {
		File outDir = new File(new File(TMPOUT_DIR), getCurrentTestName() + "/" + ModuleKind.none);

		Process process = ProcessUtil.runCommand("java", line -> {
			System.out.println(line);
		} , null, "-cp", System.getProperty("java.class.path"), //
				JSweetCommandLineLauncher.class.getName(), //
				"--tsout", outDir.getPath(), //
				"--jsout", outDir.getPath(), //
				"--sourceMap", //
				"-i", TEST_DIRECTORY_NAME + "/org/jsweet/test/transpiler/source/blocksgame");

		assertTrue(process.exitValue() == 0);
		LinkedList<File> files = new LinkedList<>();
		Util.addFiles(".ts", outDir, files);
		assertTrue(!files.isEmpty());
		Util.addFiles(".js", outDir, files);
		assertTrue(!files.isEmpty());
		Util.addFiles(".js.map", outDir, files);
		assertTrue(!files.isEmpty());

	}

}
