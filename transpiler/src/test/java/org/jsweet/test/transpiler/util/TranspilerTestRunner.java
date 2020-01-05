package org.jsweet.test.transpiler.util;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.lang.model.util.Types;

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

		String classPath = System.getProperty("java.class.path");

		// adds classloader URLs to class path because java.class.path property doesn't
		// include every artifacts :o/
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader instanceof URLClassLoader) {
			URL[] urls = ((URLClassLoader) classLoader).getURLs();
			classPath += File.pathSeparator + Stream.of(urls).map(url -> {
				try {
					return new File(url.toURI()).getCanonicalPath();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).collect(joining(File.pathSeparator));
		}

		String modulePath = System.getProperty("jdk.module.path");
		logger.info("classPath: " + classPath);
		logger.info("modulePath: " + modulePath);

		transpiler = new JSweetTranspiler( //
				configurationFile, //
				factory, //
				null, //
				baseTsOutputDir, //
				null, //
				new File(JSweetTranspiler.TMP_WORKING_DIR_NAME + "/candies/js"), //
				classPath);
		transpiler.setEcmaTargetVersion(EcmaScriptComplianceLevel.ES6);
		transpiler.setEncoding("UTF-8");
		transpiler.setSkipTypeScriptChecks(true);
		transpiler.setIgnoreAssertions(false);
		transpiler.setGenerateSourceMaps(false);
		transpiler.setUseTsserver(true);
		transpiler.setVerbose(verbose);

		if (verbose) {
			logger.info("remove transpiler working dir: " + transpiler.getWorkingDirectory());
		}
		FileUtils.deleteQuietly(transpiler.getWorkingDirectory());
	}

	public JSweetTranspiler getTranspiler() {
		return transpiler;
	}

	public Types types() {
		return transpiler.getContext().types;
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
			File tsOutputDir = getTsOutputDir(moduleKind, transpiler.isBundle());
			transpiler.setTsOutputDir(tsOutputDir);

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

	public void close() throws Exception {
		transpiler.close();
	}

}
