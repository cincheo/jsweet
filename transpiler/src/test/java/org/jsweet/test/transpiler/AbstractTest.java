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
import org.jsweet.transpiler.EcmaScriptComplianceLevel;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class AbstractTest {

	protected static final String TEST_DIRECTORY_NAME = "src/test/java";

	protected static final Logger staticLogger = Logger.getLogger(AbstractTest.class);

	protected final Logger logger = Logger.getLogger(getClass());

	private static boolean testSuiteInitialized = false;

	private static int testCount;

	@Rule
	public final TestName testNameRule = new TestName();

	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			logger.info("*********************** " + description.getMethodName() + " #" + (++testCount)
					+ " ***********************");
		}
	};

	protected File getTestFile(String name) {
		return new File(TEST_DIRECTORY_NAME + "/" + getClass().getPackage().getName().replace('.', '/'),
				name + ".d.ts");
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

	protected static void createTranspiler(JSweetFactory factory) {
		createTranspiler(null, factory);
	}

	protected static void createTranspiler(File configurationFile, JSweetFactory factory) {
		transpiler = new JSweetTranspiler(configurationFile, factory, null, new File(TMPOUT_DIR), null,
				new File(JSweetTranspiler.TMP_WORKING_DIR_NAME + "/candies/js"), System.getProperty("java.class.path"));
		transpiler.setEcmaTargetVersion(EcmaScriptComplianceLevel.ES6);
		transpiler.setEncoding("UTF-8");
		transpiler.setSkipTypeScriptChecks(true);
		transpiler.setIgnoreAssertions(false);
		transpiler.setGenerateSourceMaps(false);
		transpiler.setUseTsserver(true);
	}

	@BeforeClass
	public static void globalSetUp() throws Exception {
		if (!testSuiteInitialized) {
			staticLogger.info("*** test suite initialization ***");
			FileUtils.deleteQuietly(new File(TMPOUT_DIR));
			staticLogger.info("*** create transpiler ***");
			createTranspiler(new JSweetFactory());
			FileUtils.deleteQuietly(transpiler.getWorkingDirectory());
			transpiler.getCandiesProcessor().touch();
			testSuiteInitialized = true;
		}
	}

	private void initOutputDir() {
		transpiler.setTsOutputDir(new File(new File(TMPOUT_DIR),
				getCurrentTestName() + "/" + transpiler.getModuleKind() + (transpiler.isBundle() ? "_bundle" : "")));
	}

	@Before
	public void setUp() {
		initOutputDir();
	}

	public AbstractTest() {
		super();
	}

	protected SourceFile getSourceFile(Class<?> mainClass) {
		return new SourceFile(new File(TEST_DIRECTORY_NAME + "/" + mainClass.getName().replace(".", "/") + ".java"));
	}

	protected SourceFile getSourceFile(int testDirIndex, String className) {
		return new SourceFile(
				new File(TEST_DIRECTORY_NAME + testDirIndex + "/" + className.replace(".", "/") + ".java"));
	}

	protected void transpile(Consumer<TestTranspilationHandler> assertions, SourceFile... files) {
		transpile(new ModuleKind[] { ModuleKind.none, ModuleKind.commonjs }, assertions, files);
	}

	protected void transpile(ModuleKind[] moduleKinds, Consumer<TestTranspilationHandler> assertions,
			SourceFile... files) {
		for (ModuleKind moduleKind : moduleKinds) {
			transpile(moduleKind, assertions, files);
		}
	}

	protected void transpile(ModuleKind moduleKind, Consumer<TestTranspilationHandler> assertions,
			SourceFile... files) {
		ModuleKind initialModuleKind = transpiler.getModuleKind();
		File initialOutputDir = transpiler.getTsOutputDir();
		try {
			logger.info("*** module kind: " + moduleKind + (transpiler.isBundle() ? " (with bundle)" : "") + " ***");
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			transpiler.setModuleKind(moduleKind);
			initOutputDir();
			transpiler.transpile(logHandler, files);
			if (assertions != null) {
				assertions.accept(logHandler);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception occured while running test " + getCurrentTestName() + " with module kind " + moduleKind);
		} finally {
			transpiler.setModuleKind(initialModuleKind);
			transpiler.setTsOutputDir(initialOutputDir);
		}
		if (moduleKind == ModuleKind.none && !transpiler.isBundle() && files.length > 1) {
			ArrayUtils.reverse(files);
			transpiler.setBundle(true);
			try {
				transpile(moduleKind, assertions, files);
			} finally {
				transpiler.setBundle(false);
				ArrayUtils.reverse(files);
			}
		}
	}

	protected void eval(BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		eval(new ModuleKind[] { ModuleKind.none, ModuleKind.commonjs }, assertions, files);
	}

	protected void eval(ModuleKind[] moduleKinds, BiConsumer<TestTranspilationHandler, EvaluationResult> assertions,
			SourceFile... files) {
		for (ModuleKind moduleKind : moduleKinds) {
			eval(moduleKind, assertions, files);
		}
	}

	protected void eval(ModuleKind moduleKind, BiConsumer<TestTranspilationHandler, EvaluationResult> assertions,
			SourceFile... files) {
		eval(moduleKind, true, assertions, files);
	}

	protected void eval(ModuleKind moduleKind, boolean testBundle,
			BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		ModuleKind initialModuleKind = transpiler.getModuleKind();
		File initialOutputDir = transpiler.getTsOutputDir();
		try {
			logger.info("*** module kind: " + moduleKind + (transpiler.isBundle() ? " (with bundle)" : "") + " ***");
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			EvaluationResult res = null;
			transpiler.setModuleKind(moduleKind);

			// touch will force the transpilation even if the files were
			// already
			// transpiled
			SourceFile.touch(files);
			initOutputDir();
			res = transpiler.eval(logHandler, files);
			logger.trace(getCurrentTestName() + " -- result=" + res.getExecutionTrace());
			if (assertions != null) {
				assertions.accept(logHandler, res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception occured while running test " + getCurrentTestName() + " with module kind " + moduleKind);
		} finally {
			transpiler.setModuleKind(initialModuleKind);
			transpiler.setTsOutputDir(initialOutputDir);
		}
		if (testBundle && moduleKind == ModuleKind.none && !transpiler.isBundle() && files.length > 1) {
			ArrayUtils.reverse(files);
			transpiler.setBundle(true);
			try {
				eval(moduleKind, assertions, files);
			} finally {
				transpiler.setBundle(false);
				ArrayUtils.reverse(files);
			}
		}

	}

}