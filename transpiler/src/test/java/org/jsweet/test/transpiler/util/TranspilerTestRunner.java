package org.jsweet.test.transpiler.util;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.jsweet.test.transpiler.TestTranspilationHandler;
import org.jsweet.transpiler.EcmaScriptComplianceLevel;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.EvaluationResult;

import com.sun.tools.javac.model.JavacTypes;

/**
 * Wraps a transpiler setup for one test run
 * 
 * @author Louis Grignon
 */
public class TranspilerTestRunner {

	private final static Logger logger = Logger.getLogger(TranspilerTestRunner.class);

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

	private final JSweetTranspiler transpiler;
	private final File baseTsOutputDir;

	public TranspilerTestRunner(File tsOutputDir) {
		this(tsOutputDir, new JSweetFactory());
	}

	public TranspilerTestRunner( //
			File tsOutputDir, //
			JSweetFactory factory) {
		this(null, tsOutputDir, factory);
	}

	public TranspilerTestRunner( //
			File configurationFile, //
			File baseTsOutputDir, //
			JSweetFactory factory) {
		this.baseTsOutputDir = baseTsOutputDir;
		
		boolean verbose = System.getenv("JSWEET_VERBOSE") == null
				|| BooleanUtils.toBoolean(System.getenv("JSWEET_VERBOSE"));

		transpiler = new JSweetTranspiler( //
				configurationFile, //
				factory, //
				null, //
				baseTsOutputDir, //
				null, //
				new File(JSweetTranspiler.TMP_WORKING_DIR_NAME + "/candies/js"), //
				System.getProperty("java.class.path"));
		transpiler.setEcmaTargetVersion(EcmaScriptComplianceLevel.ES6);
		transpiler.setEncoding("UTF-8");
		transpiler.setSkipTypeScriptChecks(true);
		transpiler.setIgnoreAssertions(false);
		transpiler.setGenerateSourceMaps(false);
		transpiler.setUseTsserver(true);
		transpiler.setVerbose(verbose);

		FileUtils.deleteQuietly(transpiler.getWorkingDirectory());
	}

	public JSweetTranspiler getTranspiler() {
		return transpiler;
	}

	public JavacTypes types() {
		return com.sun.tools.javac.model.JavacTypes.instance(transpiler.getContext());
	}

	public void transpile(Consumer<TestTranspilationHandler> assertions, SourceFile... files) {
		transpile(new ModuleKind[] { ModuleKind.none, ModuleKind.commonjs }, assertions, files);
	}

	public void transpile(ModuleKind[] moduleKinds, Consumer<TestTranspilationHandler> assertions,
			SourceFile... files) {
		for (ModuleKind moduleKind : moduleKinds) {
			transpile(moduleKind, assertions, files);
		}
	}

	public void transpile(ModuleKind moduleKind, Consumer<TestTranspilationHandler> assertions, SourceFile... files) {
		ModuleKind initialModuleKind = transpiler.getModuleKind();
		File initialOutputDir = transpiler.getTsOutputDir();
		try {
			logger.info("*** module kind: " + moduleKind + (transpiler.isBundle() ? " (with bundle)" : "") + " ***");
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			transpiler.setModuleKind(moduleKind);
			transpiler.setTsOutputDir(getTsOutputDir(moduleKind, transpiler.isBundle()));
			transpiler.transpile(logHandler, files);
			if (assertions != null) {
				assertions.accept(logHandler);
			}
		} catch (Exception e) {
			String message = "exception occured while running test for " + baseTsOutputDir;
			logger.error(message, e);
			fail(message);
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

	public File getTsOutputDir(ModuleKind moduleKind, boolean isBundle) {
		return new File(baseTsOutputDir, moduleKind + (isBundle ? "_bundle" : ""));
	}

	public void eval(BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		eval(new ModuleKind[] { ModuleKind.none, ModuleKind.commonjs }, assertions, files);
	}

	public void eval(ModuleKind[] moduleKinds, BiConsumer<TestTranspilationHandler, EvaluationResult> assertions,
			SourceFile... files) {
		for (ModuleKind moduleKind : moduleKinds) {
			eval(moduleKind, assertions, files);
		}
	}

	public void eval(ModuleKind moduleKind, BiConsumer<TestTranspilationHandler, EvaluationResult> assertions,
			SourceFile... files) {
		eval(moduleKind, true, assertions, files);
	}

	public void eval(ModuleKind moduleKind, boolean testBundle,
			BiConsumer<TestTranspilationHandler, EvaluationResult> assertions, SourceFile... files) {
		ModuleKind initialModuleKind = transpiler.getModuleKind();
		File initialOutputDir = transpiler.getTsOutputDir();
		try {
			logger.info("*** module kind: " + moduleKind + (transpiler.isBundle() ? " (with bundle)" : "") + " ***");
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			EvaluationResult res = null;
			transpiler.setModuleKind(moduleKind);

			// touch will force the transpilation even if the files were already transpiled
			SourceFile.touch(files);

			transpiler.setTsOutputDir(getTsOutputDir(moduleKind, transpiler.isBundle()));

			res = transpiler.eval(logHandler, files);
			logger.trace(baseTsOutputDir + " -- result=\n" + res.getExecutionTrace());
			if (assertions != null) {
				assertions.accept(logHandler, res);
			}
		} catch (Exception e) {
			String message = "exception occured while running test for " + baseTsOutputDir;
			logger.error(message, e);
			fail(message);
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
