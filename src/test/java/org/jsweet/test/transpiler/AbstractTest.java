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

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

public class AbstractTest {

	protected static final String TEST_DIRECTORY_NAME = "src/test/java";

	protected static final String JSWEET_TEST_DIRECTORY_NAME = "src/test/jsweet";

	protected static final Logger staticLogger = Logger.getLogger(AbstractTest.class);

	protected final Logger logger = Logger.getLogger(getClass());

	private static boolean testSuiteInitialized = false;

	@Rule
	public final TestName testNameRule = new TestName();

	protected File getTestFile(String name) {
		return new File(TEST_DIRECTORY_NAME + "/" + getClass().getPackage().getName().replace('.', '/'), name + ".d.ts");
	}

	protected final String getCurrentTestName() {
		return getClass().getSimpleName() + "." + testNameRule.getMethodName();
	}

	public static int runTsc(String... files) throws IOException {
		String[] args;
		if (System.getProperty("os.name").startsWith("Windows")) {
			args = new String[] { "cmd", "/c", "tsc --target ES3" };
		} else {
			args = new String[] { "tsc", "--target", "ES3" };
		}
		args = ArrayUtils.addAll(args, files);
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Process process = pb.start();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}

			return process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return -1;
	}

	protected static JSweetTranspiler transpiler;
	protected static final String TMPOUT_DIR = "tempOut";

	@BeforeClass
	public static void globalSetUp() throws Exception {
		File outDir = new File(TMPOUT_DIR);
		if (!testSuiteInitialized) {
			staticLogger.info("*** test suite initialization ***");
			FileUtils.deleteQuietly(outDir);
			staticLogger.info("*** create transpiler ***");
			transpiler = new JSweetTranspiler(outDir, null, new File(JSweetTranspiler.TMP_WORKING_DIR_NAME + "/candies/js"),
					System.getProperty("java.class.path"));
			transpiler.setModuleKind(ModuleKind.none);
			transpiler.setPreserveSourceLineNumbers(true);
			FileUtils.deleteQuietly(transpiler.getWorkingDirectory());
			transpiler.getCandiesProcessor().touch();
			testSuiteInitialized = true;
		}
	}

	private void initOutputDir() {
		transpiler.setTsOutputDir(new File(new File(TMPOUT_DIR), getCurrentTestName() + "/" + transpiler.getModuleKind()));
	}

	@Before
	public void setUp() {
		initOutputDir();
		logger.info("*********************** " + getCurrentTestName() + " ***********************");
	}

	public AbstractTest() {
		super();
	}

	protected SourceFile getSourceFile(Class<?> mainClass) {
		return new SourceFile(new File(TEST_DIRECTORY_NAME + "/" + mainClass.getName().replace(".", "/") + ".java"));
	}

	protected SourceFile getJSweetSourceFile(String className) {
		return new SourceFile(new File(JSWEET_TEST_DIRECTORY_NAME + "/" + className.replace(".", "/") + ".java"));
	}

	protected EvaluationResult eval(SourceFile sourceFile, JSweetProblem... expectedProblems) {
		EvaluationResult res = null;
		TestTranspilationHandler logHandler = new TestTranspilationHandler();
		try {
			res = transpiler.eval(logHandler, sourceFile);
			logHandler.assertReportedProblems(expectedProblems);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test - errors=" + logHandler.reportedProblems);
		}
		return res;
	}

	protected void transpile(Consumer<TestTranspilationHandler> assertions, SourceFile... files) {
		transpile(new ModuleKind[] { ModuleKind.none, ModuleKind.commonjs }, assertions, files);
	}

	protected void transpile(ModuleKind[] moduleKinds, Consumer<TestTranspilationHandler> assertions, SourceFile... files) {
		for (ModuleKind moduleKind : moduleKinds) {
			transpile(moduleKind, assertions, files);
		}
	}

	protected void transpile(ModuleKind moduleKind, Consumer<TestTranspilationHandler> assertions, SourceFile... files) {
		ModuleKind initialModuleKind = transpiler.getModuleKind();
		File initialOutputDir = transpiler.getTsOutputDir();
		try {
			logger.info("*** module kind: " + moduleKind + " ***");
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			transpiler.setModuleKind(moduleKind);
			// if (moduleKind.equals(ModuleKind.commonjs)) {
			// transpiler.setBundle(true);
			// }
			initOutputDir();
			transpiler.transpile(logHandler, files);
			assertions.accept(logHandler);
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception occured while running test " + getCurrentTestName() + " with module kind " + moduleKind);
		} finally {
			transpiler.setModuleKind(initialModuleKind);
			transpiler.setTsOutputDir(initialOutputDir);
		}
	}

	protected void eval(BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		eval(new ModuleKind[] { ModuleKind.none, ModuleKind.commonjs }, assertions, files);
	}

	protected void eval(ModuleKind[] moduleKinds, BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		for (ModuleKind moduleKind : moduleKinds) {
			eval(moduleKind, assertions, files);
		}
	}

	protected void eval(ModuleKind moduleKind, BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		ModuleKind initialModuleKind = transpiler.getModuleKind();
		File initialOutputDir = transpiler.getTsOutputDir();
		try {
			logger.info("*** module kind: " + moduleKind + " ***");
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			EvaluationResult res = null;
			transpiler.setModuleKind(moduleKind);
			// touch will force the transpilation even if the files were already
			// transpiled
			SourceFile.touch(files);
			initOutputDir();
			res = transpiler.eval(logHandler, files);
			assertions.accept(logHandler, res);
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception occured while running test " + getCurrentTestName() + " with module kind " + moduleKind);
		} finally {
			transpiler.setModuleKind(initialModuleKind);
			transpiler.setTsOutputDir(initialOutputDir);
		}
	}

}