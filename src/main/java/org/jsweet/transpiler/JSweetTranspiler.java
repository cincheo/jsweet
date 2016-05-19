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
package org.jsweet.transpiler;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.jsweet.transpiler.util.Util.toJavaFileObjects;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.candies.CandiesProcessor;
import org.jsweet.transpiler.typescript.Java2TypeScriptTranslator;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import org.jsweet.transpiler.util.DirectedGraph;
import org.jsweet.transpiler.util.DirectedGraph.Node;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.EvaluationResult;
import org.jsweet.transpiler.util.Position;
import org.jsweet.transpiler.util.ProcessUtil;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.BasicDiagnosticFormatter;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.JavacMessages;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Log.WriterKind;
import com.sun.tools.javac.util.Options;

/**
 * The actual JSweet transpiler.
 * 
 * <p>
 * Instantiate this class to transpile Java to TypeScript/JavaScript.
 * 
 * @author Renaud Pawlak
 */
public class JSweetTranspiler implements JSweetOptions {

	static {
		JSweetConfig.initClassPath(null);
	}

	/**
	 * The constant for the name of the directory that stores temporary files.
	 */
	public static final String TMP_WORKING_DIR_NAME = ".jsweet";

	/**
	 * A constant that is used for exporting variables.
	 * 
	 * @see TraceBasedEvaluationResult
	 * @see #eval(TranspilationHandler, SourceFile...)
	 */
	public static final String EXPORTED_VAR_BEGIN = "EXPORT ";
	/**
	 * A constant that is used for exporting variables.
	 * 
	 * @see TraceBasedEvaluationResult
	 * @see #eval(TranspilationHandler, SourceFile...)
	 */
	public static final String EXPORTED_VAR_END = ";";
	private static Pattern exportedVarRE = Pattern.compile(EXPORTED_VAR_BEGIN + "(\\w*)=(.*)" + EXPORTED_VAR_END);

	private final static Logger logger = Logger.getLogger(JSweetTranspiler.class);

	/**
	 * The name of the file generated in the root package to avoid the
	 * TypeScript compiler to skip empty directories.
	 */
	public final static String TSCROOTFILE = ".tsc-rootfile.ts";

	private long transpilationStartTimestamp;
	private ArrayList<File> auxiliaryTsModuleFiles = new ArrayList<>();
	private JSweetContext context;
	private Options options;
	private JavaFileManager fileManager;
	private JavaCompiler compiler;
	private Log log;
	private CandiesProcessor candiesProcessor;
	private boolean preserveSourceLineNumbers = false;
	private File workingDir;
	private File tsOutputDir;
	private File jsOutputDir;
	private String classPath;
	private boolean generateJsFiles = true;
	private boolean tscWatchMode = false;
	private File[] tsDefDirs = {};
	private ModuleKind moduleKind = ModuleKind.none;
	private EcmaScriptComplianceLevel ecmaTargetVersion = EcmaScriptComplianceLevel.ES3;
	private boolean bundle = false;
	private File bundlesDirectory;
	private String encoding = null;
	private boolean noRootDirectories = false;
	private boolean ignoreAssertions = false;
	private boolean ignoreJavaFileNameError = false;
	private boolean generateDeclarations = false;
	private File declarationsOutputDir;
	private boolean jdkAllowed = true;

	/**
	 * Creates a JSweet transpiler, with the default values.
	 * 
	 * <p>
	 * TypeScript and JavaScript output directories are set to
	 * <code>System.getProperty("java.io.tmpdir")</code>. The classpath is set
	 * to <code>System.getProperty("java.class.path")</code>.
	 */
	public JSweetTranspiler() {
		this(new File(System.getProperty("java.io.tmpdir")), null, null, System.getProperty("java.class.path"));
	}

	/**
	 * Creates a JSweet transpiler.
	 * 
	 * @param tsOutputDir
	 *            the directory where TypeScript files are written
	 * @param jsOutputDir
	 *            the directory where JavaScript files are written
	 * @param extractedCandiesJavascriptDir
	 *            see {@link #getExtractedCandyJavascriptDir()}
	 * @param classPath
	 *            the classpath as a string (check out system-specific
	 *            requirements for Java classpathes)
	 */
	public JSweetTranspiler(File tsOutputDir, File jsOutputDir, File extractedCandiesJavascriptDir, String classPath) {
		this(new File(TMP_WORKING_DIR_NAME), tsOutputDir, jsOutputDir, extractedCandiesJavascriptDir, classPath);
	}

	/**
	 * Creates a JSweet transpiler.
	 * 
	 * @param workingDir
	 *            the working directory
	 * @param tsOutputDir
	 *            the directory where TypeScript files are written
	 * @param jsOutputDir
	 *            the directory where JavaScript files are written
	 * @param extractedCandiesJavascriptDir
	 *            see {@link #getExtractedCandyJavascriptDir()}
	 * @param classPath
	 *            the classpath as a string (check out system-specific
	 *            requirements for Java classpaths)
	 */
	public JSweetTranspiler(File workingDir, File tsOutputDir, File jsOutputDir, File extractedCandiesJavascriptDir, String classPath) {
		this.workingDir = workingDir.getAbsoluteFile();
		this.extractedCandyJavascriptDir = extractedCandiesJavascriptDir;
		try {
			tsOutputDir.mkdirs();
			this.tsOutputDir = tsOutputDir.getCanonicalFile();
			if (jsOutputDir != null) {
				jsOutputDir.mkdirs();
				this.jsOutputDir = jsOutputDir.getCanonicalFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("cannot locate output dirs", e);
		}
		this.classPath = classPath == null ? System.getProperty("java.class.path") : classPath;
		logger.info("creating transpiler version " + JSweetConfig.getVersionNumber() + " (build date: " + JSweetConfig.getBuildDate() + ")");
		logger.info("curent dir: " + new File(".").getAbsolutePath());
		logger.info("tsOut: " + tsOutputDir + (tsOutputDir == null ? "" : " - " + tsOutputDir.getAbsolutePath()));
		logger.info("jsOut: " + jsOutputDir + (jsOutputDir == null ? "" : " - " + jsOutputDir.getAbsolutePath()));
		logger.debug("compile classpath: " + classPath);
		logger.debug("runtime classpath: " + System.getProperty("java.class.path"));
		this.candiesProcessor = new CandiesProcessor(workingDir, classPath, extractedCandyJavascriptDir);
	}

	/**
	 * Gets this transpiler working directory (where the temporary files are
	 * stored).
	 */
	public File getWorkingDirectory() {
		return this.workingDir;
	}

	private void initNode(TranspilationHandler transpilationHandler) throws Exception {
		ProcessUtil.initNode();
		logger.debug("extra path: " + ProcessUtil.EXTRA_PATH);
		File initFile = new File(workingDir, ".node-init");
		boolean initialized = initFile.exists();
		if (!initialized) {
			ProcessUtil.runCommand(ProcessUtil.NODE_COMMAND, null, () -> {
				transpilationHandler.report(JSweetProblem.NODE_CANNOT_START, null, JSweetProblem.NODE_CANNOT_START.getMessage());
				throw new RuntimeException("cannot find node.js");
			} , "--version");
			initFile.mkdirs();
			initFile.createNewFile();
		}
	}

	private void initNodeCommands(TranspilationHandler transpilationHandler) throws Exception {
		if (!ProcessUtil.isInstalledWithNpm("tsc")) {
			ProcessUtil.installNodePackage("typescript", true);
		}
		if (!ProcessUtil.isInstalledWithNpm("browserify")) {
			ProcessUtil.installNodePackage("browserify", true);
		}
	}

	/**
	 * Sets one or more directories that contain TypeScript definition files
	 * (sub-directories are scanned recursively to find all .d.ts files).
	 * 
	 * @param tsDefDirs
	 *            a list of directories to scan for .d.ts files
	 */
	public void setTsDefDirs(File... tsDefDirs) {
		this.tsDefDirs = tsDefDirs;
	}

	/**
	 * Adds a directory that contains TypeScript definition files
	 * (sub-directories are scanned recursively to find all .d.ts files).
	 * 
	 * @param tsDefDir
	 *            a directory to scan for .d.ts files
	 */
	public void addTsDefDir(File tsDefDir) {
		if (!ArrayUtils.contains(tsDefDirs, tsDefDir)) {
			tsDefDirs = ArrayUtils.add(tsDefDirs, tsDefDir);
		}
	}

	/**
	 * Undo previous calls to {@link #setTsDefDirs(File...)} and
	 * {@link #addTsDefDir(File)}.
	 */
	public void clearTsDefDirs() {
		tsDefDirs = new File[0];
	}

	private void initJavac(final TranspilationHandler transpilationHandler) {
		context = new JSweetContext(this);
		options = Options.instance(context);
		if (classPath != null) {
			options.put(Option.CLASSPATH, classPath);
			for (String s : classPath.split(File.pathSeparator)) {
				if (s.contains(JSweetConfig.MAVEN_JAVA_OVERRIDE_ARTIFACT)) {
					context.strictMode = true;
					options.put(Option.BOOTCLASSPATH, s);
				}
			}
		}
		logger.debug("classpath: " + options.get(Option.CLASSPATH));
		logger.debug("bootclasspath: " + options.get(Option.BOOTCLASSPATH));
		logger.debug("strict mode: " + context.strictMode);
		options.put(Option.XLINT, "path");
		JavacFileManager.preRegister(context);
		fileManager = context.get(JavaFileManager.class);

		compiler = JavaCompiler.instance(context);
		compiler.attrParseOnly = true;
		compiler.verbose = false;
		compiler.genEndPos = true;
		compiler.keepComments = true;

		log = Log.instance(context);

		log.dumpOnError = false;
		log.emitWarnings = false;

		Writer w = new StringWriter() {
			@Override
			public void write(String str) {
				// TranspilationHandler.OUTPUT_LOGGER.error(getBuffer());
				// getBuffer().delete(0, getBuffer().length());
			}

		};

		log.setWriter(WriterKind.ERROR, new PrintWriter(w));
		log.setDiagnosticFormatter(new BasicDiagnosticFormatter(JavacMessages.instance(context)) {
			@Override
			public String format(JCDiagnostic diagnostic, Locale locale) {
				if (diagnostic.getKind() == Kind.ERROR) {
					if (!(ignoreJavaFileNameError && "compiler.err.class.public.should.be.in.file".equals(diagnostic.getCode()))) {
						transpilationHandler.report(JSweetProblem.INTERNAL_JAVA_ERROR, new SourcePosition(new File(diagnostic.getSource().getName()), null,
								(int) diagnostic.getLineNumber(), (int) diagnostic.getColumnNumber()), diagnostic.getMessage(locale));
					}
				}
				if (diagnostic.getSource() != null) {
					return diagnostic.getMessage(locale) + " at " + diagnostic.getSource().getName() + "(" + diagnostic.getLineNumber() + ")";
				} else {
					return diagnostic.getMessage(locale);
				}
			}
		});

		if (encoding != null) {
			compiler.encoding = encoding;
		}
	}

	private boolean areAllTranspiled(SourceFile... sourceFiles) {
		for (SourceFile file : sourceFiles) {
			if (file.getJsFile() == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Evaluates the given Java source files with the default JavaScript engine
	 * (Nashorn).
	 * <p>
	 * This function automatically transpile the source files if needed.
	 * 
	 * @param transpilationHandler
	 *            the transpilation handler
	 * @param sourceFiles
	 *            the source files to be evaluated
	 * @return an object that holds the evaluation result
	 * @throws Exception
	 *             when an internal error occurs
	 */
	public EvaluationResult eval(TranspilationHandler transpilationHandler, SourceFile... sourceFiles) throws Exception {
		return eval("JavaScript", transpilationHandler, sourceFiles);
	}

	private static class MainMethodFinder extends TreeScanner {
		public MethodSymbol mainMethod;

		public void visitMethodDef(JCMethodDecl methodDecl) {
			MethodSymbol method = methodDecl.sym;
			if ("main(java.lang.String[])".equals(method.toString())) {
				if (method.isStatic()) {
					mainMethod = method;
					throw new RuntimeException();
				}
			}
		}
	};

	private void initExportedVarMap() throws Exception {
		Field f = Thread.currentThread().getContextClassLoader().loadClass("jsweet.util.Globals").getDeclaredField("EXPORTED_VARS");
		f.setAccessible(true);
		@SuppressWarnings("unchecked")
		ThreadLocal<Map<String, Object>> exportedVars = (ThreadLocal<Map<String, Object>>) f.get(null);
		exportedVars.set(new HashMap<>());
	}

	private Map<String, Object> getExportedVarMap() throws Exception {
		Field f = Thread.currentThread().getContextClassLoader().loadClass("jsweet.util.Globals").getDeclaredField("EXPORTED_VARS");
		f.setAccessible(true);
		@SuppressWarnings("unchecked")
		ThreadLocal<Map<String, Object>> exportedVars = (ThreadLocal<Map<String, Object>>) f.get(null);
		return new HashMap<>(exportedVars.get());
	}

	/**
	 * Evaluates the given source files with the given evaluation engine.
	 * <p>
	 * If given engine name is "Java", this function looks up for the classes in
	 * the classpath and run the main methods when found.
	 * 
	 * @param engineName
	 *            the engine name: either "Java" or any valid and installed
	 *            JavaScript engine.
	 * @param transpilationHandler
	 *            the log handler
	 * @param sourceFiles
	 *            the source files to be evaluated (transpiled first if needed)
	 * @return the evaluation result
	 * @throws Exception
	 *             when an internal error occurs
	 */
	public EvaluationResult eval(String engineName, TranspilationHandler transpilationHandler, SourceFile... sourceFiles) throws Exception {
		logger.info("[" + engineName + " engine] eval files: " + Arrays.asList(sourceFiles));
		if ("Java".equals(engineName)) {
			// search for main functions
			JSweetContext context = new JSweetContext(this);
			Options options = Options.instance(context);
			if (classPath != null) {
				options.put(Option.CLASSPATH, classPath);
			}
			options.put(Option.XLINT, "path");
			JavacFileManager.preRegister(context);
			JavaFileManager fileManager = context.get(JavaFileManager.class);

			List<JavaFileObject> fileObjects = toJavaFileObjects(fileManager, Arrays.asList(SourceFile.toFiles(sourceFiles)));

			JavaCompiler compiler = JavaCompiler.instance(context);
			compiler.attrParseOnly = true;
			compiler.verbose = true;
			compiler.genEndPos = false;

			logger.info("parsing: " + fileObjects);
			List<JCCompilationUnit> compilationUnits = compiler.enterTrees(compiler.parseFiles(fileObjects));
			MainMethodFinder mainMethodFinder = new MainMethodFinder();
			try {
				for (JCCompilationUnit cu : compilationUnits) {
					cu.accept(mainMethodFinder);
				}
			} catch (Exception e) {
				// swallow on purpose
			}
			if (mainMethodFinder.mainMethod != null) {
				try {
					initExportedVarMap();
					Class<?> c = Class.forName(mainMethodFinder.mainMethod.getEnclosingElement().getQualifiedName().toString());
					c.getMethod("main", String[].class).invoke(null, (Object) null);
				} catch (Exception e) {
					throw new Exception("evalution error", e);
				}
			}

			final Map<String, Object> map = getExportedVarMap();
			return new EvaluationResult() {

				@SuppressWarnings("unchecked")
				@Override
				public <T> T get(String variableName) {
					return (T) map.get("_exportedVar_" + variableName);
				}

				@Override
				public String toString() {
					return map.toString();
				}

				@Override
				public String getExecutionTrace() {
					return "<not available>";
				}
			};
		} else {
			if (!areAllTranspiled(sourceFiles)) {
				ErrorCountTranspilationHandler errorHandler = new ErrorCountTranspilationHandler(transpilationHandler);
				transpile(errorHandler, sourceFiles);
				if (errorHandler.getErrorCount() > 0) {
					throw new Exception("unable to evaluate: transpilation errors remain");
				}
			}

			StringWriter trace = new StringWriter();

			Process runProcess;
			if (context.useModules) {
				File f = sourceFiles[sourceFiles.length - 1].getJsFile();
				logger.debug("eval file: " + f);
				runProcess = ProcessUtil.runCommand(ProcessUtil.NODE_COMMAND, line -> trace.append(line + "\n"), null, f.getPath());
			} else {
				File tmpFile = new File(new File(TMP_WORKING_DIR_NAME), "eval.tmp.js");
				FileUtils.deleteQuietly(tmpFile);
				for (SourceFile sourceFile : sourceFiles) {
					String script = FileUtils.readFileToString(sourceFile.getJsFile());
					FileUtils.write(tmpFile, script + "\n", true);
				}
				logger.debug("eval file: " + tmpFile);
				runProcess = ProcessUtil.runCommand(ProcessUtil.NODE_COMMAND, line -> trace.append(line + "\n"), null, tmpFile.getPath());
			}

			int returnCode = runProcess.exitValue();
			logger.info("return code=" + returnCode);
			if (returnCode != 0) {
				throw new Exception("evaluation error (code=" + returnCode + ") - trace=" + trace);
			}
			return new TraceBasedEvaluationResult(trace.getBuffer().toString());
		}
	}

	static private class TraceBasedEvaluationResult implements EvaluationResult {
		private String trace;

		public TraceBasedEvaluationResult(String trace) {
			super();
			this.trace = trace;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T get(String variableName) {
			String[] var = null;
			Matcher matcher = exportedVarRE.matcher(trace);
			int index = 0;
			boolean match = true;
			while (match) {
				match = matcher.find(index);
				if (match) {
					if (variableName.equals(matcher.group(1))) {
						var = new String[] { matcher.group(1), matcher.group(2) };
						match = false;

					}
					index = matcher.end() - 1;
				}
			}
			if (var == null) {
				return null;
			} else {
				String stringValue = var[1];
				try {
					return (T) (Integer) Integer.parseInt(stringValue);
				} catch (Exception e1) {
					try {
						return (T) (Double) Double.parseDouble(stringValue);
					} catch (Exception e2) {
						if ("true".equals(stringValue)) {
							return (T) Boolean.TRUE;
						}
						if ("false".equals(stringValue)) {
							return (T) Boolean.FALSE;
						}
						if ("undefined".equals(stringValue)) {
							return null;
						}
					}
				}
				return (T) stringValue;
			}
		}

		@Override
		public String getExecutionTrace() {
			return trace;
		}
	}

	/**
	 * Transpiles the given Java source files. When the transpiler is in watch
	 * mode ({@link #setTscWatchMode(boolean)}), the first invocation to this
	 * method determines the files to be watched by the Tsc process.
	 * 
	 * @param transpilationHandler
	 *            the log handler
	 * @param files
	 *            the files to be transpiled
	 * @throws IOException
	 */
	synchronized public void transpile(TranspilationHandler transpilationHandler, SourceFile... files) throws IOException {
		transpilationStartTimestamp = System.currentTimeMillis();
		try {
			initNode(transpilationHandler);
			initNodeCommands(transpilationHandler);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return;
		}
		candiesProcessor.processCandies(transpilationHandler);
		addTsDefDir(candiesProcessor.getCandiesTsdefsDir());
		if (classPath != null && !ArrayUtils.contains(classPath.split(File.pathSeparator), candiesProcessor.getCandiesProcessedDir().getPath())) {
			classPath = candiesProcessor.getCandiesProcessedDir() + File.pathSeparator + classPath;
			logger.debug("updated classpath: " + classPath);
		}

		ErrorCountTranspilationHandler errorHandler = new ErrorCountTranspilationHandler(transpilationHandler);
		Collection<SourceFile> jsweetSources = asList(files).stream() //
				.filter(source -> source.getJavaFile() != null).collect(toList());
		java2ts(errorHandler, jsweetSources.toArray(new SourceFile[0]));
		auxiliaryTsModuleFiles.clear();
		createAuxiliaryModuleFiles(tsOutputDir);

		if (errorHandler.getErrorCount() == 0 && generateJsFiles) {
			Collection<SourceFile> tsSources = asList(files).stream() //
					.filter(source -> source.getTsFile() != null).collect(toList());
			ts2js(errorHandler, tsSources.toArray(new SourceFile[0]));
		}

		generateBundles(errorHandler, files);
		logger.info("transpilation process finished in " + (System.currentTimeMillis() - transpilationStartTimestamp) + " ms");
	}

	private void generateBundles(ErrorCountTranspilationHandler errorHandler, SourceFile... files) {
		if (bundle && context.useModules && errorHandler.getErrorCount() == 0) {
			if (moduleKind != ModuleKind.commonjs) {
				errorHandler.report(JSweetProblem.BUNDLE_WITH_COMMONJS, null, JSweetProblem.BUNDLE_WITH_COMMONJS.getMessage());
			}
			context.packageDependencies.topologicalSort(node -> {
				if (errorHandler.getErrorCount() == 0) {
					errorHandler.report(JSweetProblem.BUNDLE_HAS_CYCLE, null,
							JSweetProblem.BUNDLE_HAS_CYCLE.getMessage(context.packageDependencies.toString()));
				}
			});
			if (errorHandler.getErrorCount() > 0) {
				return;
			}

			logger.info("checking for used modules: " + context.getUsedModules());
			for (String module : context.getUsedModules()) {
				if (module.endsWith(JSweetConfig.MODULE_FILE_NAME)) {
					continue;
				}
				logger.debug("cheking for module " + module);
				if (!ProcessUtil.isNodePackageInstalled(module)) {
					logger.debug("installing " + module + "...");
					// TODO: error reporting
					ProcessUtil.installNodePackage(module, false);
				}
			}

			Set<String> entries = new HashSet<>();
			for (SourceFile f : files) {
				if (context.entryFiles.contains(f.getJavaFile())) {
					entries.add(f.jsFile.getAbsolutePath());
				}
			}

			if (entries.isEmpty()) {
				errorHandler.report(JSweetProblem.BUNDLE_HAS_NO_ENTRIES, null, JSweetProblem.BUNDLE_HAS_NO_ENTRIES.getMessage());
			}

			for (String entry : entries) {
				String[] args = { entry };
				File entryDir = new File(entry).getParentFile();
				File bundleDirectory = bundlesDirectory != null ? bundlesDirectory : entryDir;
				if (!bundleDirectory.exists()) {
					bundleDirectory.mkdirs();
				}
				String bundleName = "bundle-" + entryDir.getName() + ".js";
				args = ArrayUtils.addAll(args, "-o", new File(bundleDirectory, bundleName).getAbsolutePath());
				logger.info("creating bundle file with browserify, args: " + StringUtils.join(args, ' '));
				// TODO: keep original ts files sourcemaps:
				// http://stackoverflow.com/questions/23453160/keep-original-typescript-source-maps-after-using-browserify
				ProcessUtil.runCommand("browserify", ProcessUtil.USER_HOME_DIR, false, null, null, null, args);
			}
		}
	}

	private void createAuxiliaryModuleFiles(File rootDir) throws IOException {
		if (context.useModules) {
			// export-import submodules
			File moduleFile = new File(rootDir, JSweetConfig.MODULE_FILE_NAME + ".ts");
			boolean createModuleFile = !moduleFile.exists();
			if (!createModuleFile) {
				boolean isTsFromSourceFile = false;
				for (SourceFile sourceFile : context.sourceFiles) {
					if (sourceFile.getTsFile() != null && moduleFile.getAbsolutePath().equals(sourceFile.getTsFile().getAbsolutePath())) {
						isTsFromSourceFile = true;
						break;
					}
				}
				createModuleFile = !isTsFromSourceFile;
			}

			if (createModuleFile) {
				FileUtils.deleteQuietly(moduleFile);
				auxiliaryTsModuleFiles.add(moduleFile);
			}
			for (File f : rootDir.listFiles()) {
				if (f.isDirectory() && !f.getName().startsWith(".")) {
					if (createModuleFile) {
						logger.debug("create auxiliary module file: " + moduleFile);
						FileUtils.write(moduleFile,
								"export import " + f.getName() + " = require('./" + f.getName() + "/" + JSweetConfig.MODULE_FILE_NAME + "');\n", true);
					}
					createAuxiliaryModuleFiles(f);
				}
			}
		}
	}

	private void java2ts(ErrorCountTranspilationHandler transpilationHandler, SourceFile[] files) throws IOException {

		initJavac(transpilationHandler);
		List<JavaFileObject> fileObjects = toJavaFileObjects(fileManager, Arrays.asList(SourceFile.toFiles(files)));

		logger.info("parsing: " + fileObjects);
		List<JCCompilationUnit> compilationUnits = compiler.enterTrees(compiler.parseFiles(fileObjects));
		if (transpilationHandler.getErrorCount() > 0) {
			return;
		}
		logger.info("attribution phase");
		compiler.attribute(compiler.todo);

		if (transpilationHandler.getErrorCount() > 0) {
			return;
		}
		context.useModules = isUsingModules();
		context.sourceFiles = files;

		new GlobalBeforeTranslationScanner(transpilationHandler, context).process(compilationUnits);

		if (context.useModules) {
			generateTsModules(transpilationHandler, files, compilationUnits);
		} else {
			if (bundle) {
				generateTsBundle(transpilationHandler, files, compilationUnits);
			} else {
				generateTsFiles(transpilationHandler, files, compilationUnits);
			}
		}
		log.flush();
		getOrCreateTscRootFile();
	}

	private boolean isPackageDefinition(JCCompilationUnit cu) {
		return cu.packge.getQualifiedName().toString().startsWith("def.") && cu.getSourceFile().getName().endsWith("package-info.java");
	}

	private void generateTsFiles(ErrorCountTranspilationHandler transpilationHandler, SourceFile[] files, List<JCCompilationUnit> compilationUnits)
			throws IOException {
		// regular file-to-file generation
		new OverloadScanner(transpilationHandler, context).process(compilationUnits);
		for (int i = 0; i < compilationUnits.length(); i++) {
			JCCompilationUnit cu = compilationUnits.get(i);
			if (isPackageDefinition(cu)) {
				continue;
			}
			logger.info("scanning " + cu.sourcefile.getName() + "...");
			AbstractTreePrinter printer = new Java2TypeScriptTranslator(transpilationHandler, context, cu, preserveSourceLineNumbers);
			printer.print(cu);
			String[] s = cu.getSourceFile().getName().split(File.separator.equals("\\") ? "\\\\" : File.separator);
			String cuName = s[s.length - 1];
			s = cuName.split("\\.");
			cuName = s[0];
			String javaSourceFileRelativeFullName = (cu.packge.getQualifiedName().toString().replace(".", File.separator) + File.separator + cuName + ".java");
			files[i].javaSourceDirRelativeFile = new File(javaSourceFileRelativeFullName);
			files[i].javaSourceDir = new File(
					cu.getSourceFile().getName().substring(0, cu.getSourceFile().getName().length() - javaSourceFileRelativeFullName.length()));
			String packageName = isNoRootDirectories() ? Util.getRootRelativeJavaName(cu.packge) : cu.packge.getQualifiedName().toString();
			String outputFileRelativePathNoExt = packageName.replace(".", File.separator) + File.separator + cuName;
			String outputFileRelativePath = outputFileRelativePathNoExt + printer.getTargetFilesExtension();
			logger.info("output file: " + outputFileRelativePath);
			File outputFile = new File(tsOutputDir, outputFileRelativePath);
			outputFile.getParentFile().mkdirs();
			String outputFilePath = outputFile.getPath();
			PrintWriter out = new PrintWriter(outputFilePath);
			try {
				out.println(printer.getResult());
				out.print(context.poolFooterStatements());
			} finally {
				out.close();
			}
			files[i].tsFile = outputFile;
			files[i].javaFileLastTranspiled = files[i].getJavaFile().lastModified();
			files[i].sourceMap = printer.sourceMap;
			logger.info("created " + outputFilePath);
		}
	}

	private void generateTsModules(ErrorCountTranspilationHandler transpilationHandler, SourceFile[] files, List<JCCompilationUnit> compilationUnits)
			throws IOException {
		// when using modules, all classes of the same package are folded to
		// one module file
		Map<PackageSymbol, StringBuilder> modules = new HashMap<>();
		Map<PackageSymbol, StringBuilder> moduleFooters = new HashMap<>();
		Map<PackageSymbol, ArrayList<Integer>> fileIndexes = new HashMap<>();
		StaticInitilializerAnalyzer analizer = new StaticInitilializerAnalyzer(context);
		analizer.process(compilationUnits);
		java.util.List<JCCompilationUnit> orderedCompilationUnits = new ArrayList<>();
		for (Entry<PackageSymbol, DirectedGraph<JCCompilationUnit>> pkgCus : analizer.staticInitializersDependencies.entrySet()) {
			orderedCompilationUnits.addAll(pkgCus.getValue().topologicalSort(n -> {
				transpilationHandler.report(JSweetProblem.CYCLE_IN_STATIC_INITIALIZER_DEPENDENCIES, null,
						JSweetProblem.CYCLE_IN_STATIC_INITIALIZER_DEPENDENCIES.getMessage(n.element.sourcefile.getName()));
			}));
		}
		logger.debug("ordered compilation units: " + orderedCompilationUnits.stream().map(cu -> {
			return cu.sourcefile.getName();
		}).collect(Collectors.toList()));
		int[] permutation = new int[orderedCompilationUnits.size()];
		StringBuilder permutationString = new StringBuilder();
		for (int i = 0; i < orderedCompilationUnits.size(); i++) {
			permutation[i] = compilationUnits.indexOf(orderedCompilationUnits.get(i));
			permutationString.append("" + i + "=" + permutation[i] + ";");
		}
		logger.debug("permutation: " + permutationString.toString());
		new OverloadScanner(transpilationHandler, context).process(orderedCompilationUnits);
		StringBuilder packageDefinitions = new StringBuilder();
		for (int i = 0; i < orderedCompilationUnits.size(); i++) {
			JCCompilationUnit cu = orderedCompilationUnits.get(i);
			logger.info("scanning " + cu.sourcefile.getName() + "...");
			AbstractTreePrinter printer = new Java2TypeScriptTranslator(transpilationHandler, context, cu, preserveSourceLineNumbers);
			printer.print(cu);
			if (isPackageDefinition(cu)) {
				packageDefinitions.append(printer.getOutput());
				continue;
			}
			StringBuilder sb = modules.get(cu.packge);
			if (sb == null) {
				sb = new StringBuilder();
				modules.put(cu.packge, sb);
			}
			StringBuilder sb2 = moduleFooters.get(cu.packge);
			if (sb2 == null) {
				sb2 = new StringBuilder();
				moduleFooters.put(cu.packge, sb2);
			}
			ArrayList<Integer> indexes = fileIndexes.get(cu.packge);
			if (indexes == null) {
				indexes = new ArrayList<Integer>();
				fileIndexes.put(cu.packge, indexes);
			}
			indexes.add(i);
			files[permutation[i]].sourceMap = printer.sourceMap;
			files[permutation[i]].sourceMap.shiftOutputPositions(StringUtils.countMatches(sb, "\n"));
			sb.append(printer.getOutput());
			sb2.append(context.poolFooterStatements());
		}
		for (Entry<PackageSymbol, StringBuilder> e : modules.entrySet()) {
			String outputFileRelativePathNoExt = Util.getRootRelativeJavaName(e.getKey()).replace(".", File.separator) + File.separator
					+ JSweetConfig.MODULE_FILE_NAME;
			String outputFileRelativePath = outputFileRelativePathNoExt + ".ts";
			logger.info("output file: " + outputFileRelativePath);
			File outputFile = new File(tsOutputDir, outputFileRelativePath);
			outputFile.getParentFile().mkdirs();
			String outputFilePath = outputFile.getPath();
			PrintWriter out = new PrintWriter(outputFilePath);
			try {
				out.println(e.getValue().toString());
				out.print(moduleFooters.get(e.getKey()));
			} finally {
				out.close();
			}
			for (Integer i : fileIndexes.get(e.getKey())) {
				files[permutation[i]].tsFile = outputFile;
				files[permutation[i]].javaFileLastTranspiled = files[permutation[i]].getJavaFile().lastModified();
			}
			logger.info("created " + outputFilePath);
		}
		if (packageDefinitions.length() > 0) {
			FileUtils.write(new File(tsOutputDir, "modules.d.ts"), packageDefinitions);
			logger.info("created module definitions");
		}
	}

	private void generateTsBundle(ErrorCountTranspilationHandler transpilationHandler, SourceFile[] files, List<JCCompilationUnit> compilationUnits)
			throws IOException {
		if (context.useModules) {
			return;
		}
		StaticInitilializerAnalyzer analizer = new StaticInitilializerAnalyzer(context);
		analizer.process(compilationUnits);
		ArrayList<Node<JCCompilationUnit>> sourcesInCycle = new ArrayList<>();
		java.util.List<JCCompilationUnit> orderedCompilationUnits = analizer.globalStaticInitializersDependencies.topologicalSort(n -> {
			sourcesInCycle.add(n);
		});
		if (!sourcesInCycle.isEmpty()) {
			transpilationHandler.report(JSweetProblem.CYCLE_IN_STATIC_INITIALIZER_DEPENDENCIES, null, JSweetProblem.CYCLE_IN_STATIC_INITIALIZER_DEPENDENCIES
					.getMessage(sourcesInCycle.stream().map(n -> n.element.sourcefile.getName()).collect(Collectors.toList())));

			DirectedGraph.dumpCycles(sourcesInCycle, u -> u.sourcefile.getName());

			return;
		}

		new OverloadScanner(transpilationHandler, context).process(orderedCompilationUnits);

		logger.debug("ordered compilation units: " + orderedCompilationUnits.stream().map(cu -> {
			return cu.sourcefile.getName();
		}).collect(Collectors.toList()));
		logger.debug("count: " + compilationUnits.size() + " (initial), " + orderedCompilationUnits.size() + " (ordered)");
		int[] permutation = new int[orderedCompilationUnits.size()];
		StringBuilder permutationString = new StringBuilder();
		for (int i = 0; i < orderedCompilationUnits.size(); i++) {
			permutation[i] = compilationUnits.indexOf(orderedCompilationUnits.get(i));
			permutationString.append("" + i + "=" + permutation[i] + ";");
		}
		logger.debug("permutation: " + permutationString.toString());
		context.expandTypeNames = true;
		StringBuilder sb = new StringBuilder();
		int lineCount = 0;
		for (int i = 0; i < orderedCompilationUnits.size(); i++) {
			JCCompilationUnit cu = orderedCompilationUnits.get(i);
			if (isPackageDefinition(cu)) {
				continue;
			}
			logger.info("scanning " + cu.sourcefile.getName() + "...");
			AbstractTreePrinter printer = new Java2TypeScriptTranslator(transpilationHandler, context, cu, preserveSourceLineNumbers);
			printer.print(cu);
			files[permutation[i]].sourceMap = printer.sourceMap;
			files[permutation[i]].sourceMap.shiftOutputPositions(lineCount);
			sb.append(printer.getOutput());
			lineCount += printer.getCurrentLine();
		}

		context.expandTypeNames = false;

		File bundleDirectory = tsOutputDir;
		if (!bundleDirectory.exists()) {
			bundleDirectory.mkdirs();
		}
		String bundleName = "bundle.ts";

		File outputFile = new File(bundleDirectory, bundleName);

		logger.info("creating bundle file: " + outputFile);
		outputFile.getParentFile().mkdirs();
		String outputFilePath = outputFile.getPath();
		PrintWriter out = new PrintWriter(outputFilePath);
		try {
			out.println(sb.toString());
			out.print(context.poolFooterStatements());
		} finally {
			out.close();
		}
		for (int i = 0; i < orderedCompilationUnits.size(); i++) {
			files[permutation[i]].tsFile = outputFile;
			files[permutation[i]].javaFileLastTranspiled = files[permutation[i]].getJavaFile().lastModified();
		}
		logger.info("created " + outputFilePath);

	}

	private File getOrCreateTscRootFile() throws IOException {
		File tscRootFile = new File(tsOutputDir, TSCROOTFILE);

		if (!tscRootFile.exists()) {
			FileUtils.write(tscRootFile, "// Root empty file generated by JSweet to avoid tsc behavior, which\n"
					+ "// does not preserve the entire file hierarchy for empty directories.", false);
		}
		return tscRootFile;
	}

	private static class TscOutput {
		public SourcePosition position;
		public String message;

		@Override
		public String toString() {
			return message + " - " + position;
		}

		public SourcePosition findOriginalPosition(Collection<SourceFile> sourceFiles) {
			for (SourceFile sourceFile : sourceFiles) {
				if (sourceFile.tsFile != null && sourceFile.tsFile.getAbsolutePath().endsWith(position.getFile().getPath())) {
					if (sourceFile.sourceMap != null) {
						Position inputPosition = sourceFile.sourceMap.findInputPosition(position.getStartLine(), position.getStartColumn());
						if (inputPosition != null) {
							return new SourcePosition(sourceFile.getJavaFile(), null, inputPosition);
						}
					}
				}
			}
			return null;
		}

	}

	private static Pattern errorRE = Pattern.compile("(.*)\\((.*)\\): error TS[0-9]+: (.*)");

	private static TscOutput parseTscOutput(String outputString) {
		Matcher m = errorRE.matcher(outputString);
		TscOutput error = new TscOutput();
		if (m.matches()) {
			String[] pos = m.group(2).split(",");
			error.position = new SourcePosition(new File(m.group(1)), null, Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
			StringBuilder sb = new StringBuilder(m.group(3));
			sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
			if (sb.charAt(sb.length() - 1) == '.') {
				sb.deleteCharAt(sb.length() - 1);
			}
			error.message = sb.toString();
		} else {
			error.message = outputString;
		}
		return error;
	}

	private Process tsCompilationProcess;
	private SourceFile[] watchedFiles;

	private File extractedCandyJavascriptDir;

	private Path relativizeTsFile(File file) {
		try {
			return getTsOutputDir().getAbsoluteFile().getCanonicalFile().toPath().relativize(file.getAbsoluteFile().getCanonicalFile().toPath());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the watched files when the transpiler is in watch mode. See
	 * {@link #setTscWatchMode(boolean)}. The watched file list corresponds to
	 * the one given at the first invocation of
	 * {@link #transpile(TranspilationHandler, SourceFile...)} after the
	 * transpiler was set to watch mode. All subsequent invocations of
	 * {@link #transpile(TranspilationHandler, SourceFile...)} will not change
	 * the initial watched files. In order to change the watch files, invoke
	 * {@link #resetTscWatchMode()} and call
	 * {@link #transpile(TranspilationHandler, SourceFile...)} with a new file
	 * list.
	 */
	synchronized public SourceFile[] getWatchedFiles() {
		return watchedFiles;
	}

	/**
	 * Gets the watched files that corresponds to the given Java file. See
	 * {@link #setTscWatchMode(boolean)}.
	 */
	synchronized public SourceFile getWatchedFile(File javaFile) {
		if (watchedFiles != null) {
			for (SourceFile f : watchedFiles) {
				if (f.getJavaFile().getAbsoluteFile().equals(javaFile.getAbsoluteFile())) {
					return f;
				}
			}
		}
		return null;
	}

	private void ts2js(ErrorCountTranspilationHandler transpilationHandler, SourceFile[] files) throws IOException {
		if (tsCompilationProcess != null && isTscWatchMode()) {
			return;
		}
		if (isTscWatchMode()) {
			watchedFiles = files;
		}

		logger.debug("ts2js: " + Arrays.asList(files));
		LinkedList<String> args = new LinkedList<>();
		if (System.getProperty("os.name").startsWith("Windows")) {
			args.addAll(asList("--target", ecmaTargetVersion.name()));
		} else {
			args.addAll(asList("--target", ecmaTargetVersion.name()));
		}

		if (isUsingModules()) {
			if (ecmaTargetVersion.higherThan(EcmaScriptComplianceLevel.ES5)) {
				logger.warn("cannot use old fashionned modules with ES>5 target");
			} else {
				args.add("--module");
				args.add(moduleKind.toString());
			}
		}

		if (isTscWatchMode()) {
			args.add("--watch");
		}
		if (isPreserveSourceLineNumbers()) {
			args.add("--sourceMap");
		}
		if (isGenerateDeclarations()) {
			args.add("--declaration");
		}
		args.addAll(asList("--rootDir", tsOutputDir.getAbsolutePath()));
		// args.addAll(asList("--sourceRoot", tsOutputDir.toString()));

		if (jsOutputDir != null) {
			args.addAll(asList("--outDir", jsOutputDir.getAbsolutePath()));
		}
		File tscRootFile = getOrCreateTscRootFile();
		if (tscRootFile.exists()) {
			args.add(relativizeTsFile(tscRootFile).toString());
		}
		for (SourceFile sourceFile : files) {
			String filePath = relativizeTsFile(sourceFile.getTsFile()).toString();
			if (!args.contains(filePath)) {
				args.add(filePath);
			}
		}
		// this may not be necessary because tsc seems to add required modules
		// automatically
		for (File f : auxiliaryTsModuleFiles) {
			String filePath = relativizeTsFile(f).toString();
			if (!args.contains(filePath)) {
				args.add(filePath);
			}
		}

		for (File dir : tsDefDirs) {
			LinkedList<File> tsDefFiles = new LinkedList<>();
			Util.addFiles(".d.ts", dir, tsDefFiles);
			for (File f : tsDefFiles) {
				args.add(relativizeTsFile(f).toString());
			}
		}

		LinkedList<File> tsDefFiles = new LinkedList<>();
		Util.addFiles(".d.ts", tsOutputDir, tsDefFiles);
		for (File f : tsDefFiles) {
			args.add(relativizeTsFile(f).toString());
		}

		try {
			logger.info("launching tsc with args: " + args);

			boolean[] fullPass = { true };

			tsCompilationProcess = ProcessUtil.runCommand("tsc", getTsOutputDir(), isTscWatchMode(), line -> {
				logger.info(line);
				TscOutput output = parseTscOutput(line);
				if (output.position != null) {
					SourcePosition position = output.findOriginalPosition(Arrays.asList(files));
					if (position == null) {
						transpilationHandler.report(JSweetProblem.INTERNAL_TSC_ERROR, output.position, output.message);
					} else {
						transpilationHandler.report(JSweetProblem.MAPPED_TSC_ERROR, position, output.message);
					}
				} else {
					if (output.message.startsWith("message TS6042:")) {
						onTsTranspilationCompleted(fullPass[0], transpilationHandler, files);
						fullPass[0] = false;
					} else {
						// TODO enhance tsc feedbacks support: some
						// messages are swallowed here: for instance
						// error TS1204: Cannot compile modules into
						// 'commonjs', 'amd', 'system' or 'umd' when
						// targeting 'ES6' or higher.
					}
				}
			} , process -> {
				tsCompilationProcess = null;
				onTsTranspilationCompleted(fullPass[0], transpilationHandler, files);
				fullPass[0] = false;
			} , () -> {
				if (transpilationHandler.getProblemCount() == 0) {
					transpilationHandler.report(JSweetProblem.INTERNAL_TSC_ERROR, null, "Unknown tsc error");
				}
			} , args.toArray(new String[0]));

			// tsCompilationProcess.waitFor();
			// if (tsCompilationProcess != null &&
			// tsCompilationProcess.exitValue() == 1) {
			// transpilationHandler.report(JSweetProblem.TSC_CANNOT_START, null,
			// JSweetProblem.TSC_CANNOT_START.getMessage());
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onTsTranspilationCompleted(boolean fullPass, ErrorCountTranspilationHandler handler, SourceFile[] files) {
		try {
			if (isGenerateDeclarations()) {
				if (getDeclarationsOutputDir() != null) {
					logger.info("moving d.ts files to " + getDeclarationsOutputDir());
					LinkedList<File> dtsFiles = new LinkedList<File>();
					File rootDir = jsOutputDir == null ? tsOutputDir : jsOutputDir;
					Util.addFiles(".d.ts", rootDir, dtsFiles);
					for (File dtsFile : dtsFiles) {
						String relativePath = Util.getRelativePath(rootDir.getAbsolutePath(), dtsFile.getAbsolutePath());
						File targetFile = new File(getDeclarationsOutputDir(), relativePath);
						logger.info("moving " + dtsFile + " to " + targetFile);
						if (targetFile.exists()) {
							FileUtils.deleteQuietly(targetFile);
						}
						try {
							FileUtils.moveFile(dtsFile, targetFile);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
			if (handler.getErrorCount() == 0) {
				Set<File> handledFiles = new HashSet<>();
				for (SourceFile sourceFile : files) {
					if (!sourceFile.getTsFile().getAbsolutePath().startsWith(tsOutputDir.getAbsolutePath())) {
						throw new RuntimeException("ts directory isn't configured properly, please use setTsDir");
					}
					String outputFileRelativePath = sourceFile.getTsFile().getAbsolutePath().substring(tsOutputDir.getAbsolutePath().length());
					File outputFile = new File(jsOutputDir == null ? tsOutputDir : jsOutputDir, Util.removeExtension(outputFileRelativePath) + ".js");
					sourceFile.jsFile = outputFile;
					if (outputFile.lastModified() > sourceFile.jsFileLastTranspiled) {
						if (handledFiles.contains(outputFile)) {
							continue;
						}
						handledFiles.add(outputFile);
						logger.info("js output file: " + outputFile);
						File mapFile = new File(outputFile.getAbsolutePath() + ".map");
						if (mapFile.exists() && preserveSourceLineNumbers) {
							sourceFile.jsMapFile = mapFile;
							logger.info("redirecting map file: " + mapFile);
							String map = FileUtils.readFileToString(mapFile);
							try {
								if (sourceFile.javaSourceDir != null) {
									map = StringUtils.replacePattern(map, "\"sourceRoot\":\"\"", "\"sourceRoot\":\"" + sourceFile.getJsFile().getParentFile()
											.getCanonicalFile().toPath().relativize(sourceFile.javaSourceDir.getCanonicalFile().toPath()) + "/\"");
									map = StringUtils.replacePattern(map, "\"sources\":\\[\".*\"\\]",
											"\"sources\":\\[\"" + sourceFile.javaSourceDirRelativeFile.getPath() + "\"\\]");
								}
							} catch (Exception e) {
								logger.warn("cannot resolve path to source file for .map", e);
							}
							FileUtils.writeStringToFile(mapFile, map);
							// mapFile.setLastModified(sourceFile)
							sourceFile.jsFileLastTranspiled = outputFile.lastModified();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			handler.onCompleted(this, fullPass, files);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#isPreserveSourceLineNumbers()
	 */
	@Override
	public boolean isPreserveSourceLineNumbers() {
		return preserveSourceLineNumbers;
	}

	/**
	 * Sets the flag that tells if the transpiler preserves the generated
	 * TypeScript source line numbers wrt the Java original source file (allows
	 * for Java debugging through js.map files).
	 */
	public void setPreserveSourceLineNumbers(boolean preserveSourceLineNumbers) {
		this.preserveSourceLineNumbers = preserveSourceLineNumbers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#getTsOutputDir()
	 */
	@Override
	public File getTsOutputDir() {
		return tsOutputDir;
	}

	/**
	 * Sets the current TypeScript output directory.
	 */
	public void setTsOutputDir(File tsOutputDir) {
		this.tsOutputDir = tsOutputDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#getJsOutputDir()
	 */
	@Override
	public File getJsOutputDir() {
		return jsOutputDir;
	}

	/**
	 * Sets the current JavaScript output directory.
	 */
	public void setJsOutputDir(File jsOutputDir) {
		this.jsOutputDir = jsOutputDir;
	}

	/**
	 * Tells if the JavaScript generation is enabled/disabled.
	 */
	public boolean isGenerateJsFiles() {
		return generateJsFiles;
	}

	/**
	 * Sets the flag to enable/disable JavaScript generation.
	 */
	public void setGenerateJsFiles(boolean generateJsFiles) {
		this.generateJsFiles = generateJsFiles;
	}

	/**
	 * Tells if this transpiler is using a Tsc watch process to automatically
	 * regenerate the javascript when one of the source file changes.
	 */
	synchronized public boolean isTscWatchMode() {
		return tscWatchMode;
	}

	/**
	 * Enables or disable this transpiler watch mode. When watch mode is
	 * enabled, the first invocation to
	 * {@link #transpile(TranspilationHandler, SourceFile...)} will start the
	 * Tsc watch process, which regenerates the JavaScript files when one of the
	 * input file changes.
	 * 
	 * @param tscWatchMode
	 *            true: enables the watch mode (do nothing is already enabled),
	 *            false: disables the watch mode and stops the current Tsc
	 *            watching process
	 * @see #getWatchedFile(File)
	 */
	synchronized public void setTscWatchMode(boolean tscWatchMode) {
		this.tscWatchMode = tscWatchMode;
		if (!tscWatchMode) {
			if (tsCompilationProcess != null) {
				tsCompilationProcess.destroyForcibly();
				while (tsCompilationProcess != null && tsCompilationProcess.isAlive()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
					logger.error("tsc did not terminate");
				}
				try {
					if (tsCompilationProcess != null) {
						tsCompilationProcess.waitFor();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tsCompilationProcess = null;
				watchedFiles = null;
			}
		}
	}

	/**
	 * Resets the watch mode (clears the watched files and restarts the Tsc
	 * process on the next invocation of
	 * {@link #transpile(TranspilationHandler, SourceFile...)}).
	 */
	synchronized public void resetTscWatchMode() {
		setTscWatchMode(false);
		setTscWatchMode(true);
	}

	/**
	 * Gets the candies processor.
	 */
	public CandiesProcessor getCandiesProcessor() {
		return candiesProcessor;
	}

	/**
	 * Sets target ECMA script version for generated JavaScript
	 * 
	 * @param ecmaTargetVersion
	 *            The target version
	 */
	public void setEcmaTargetVersion(EcmaScriptComplianceLevel ecmaTargetVersion) {
		this.ecmaTargetVersion = ecmaTargetVersion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#getModuleKind()
	 */
	@Override
	public ModuleKind getModuleKind() {
		return moduleKind;
	}

	/**
	 * Sets the module kind when transpiling to code using JavaScript modules.
	 */
	public void setModuleKind(ModuleKind moduleKind) {
		this.moduleKind = moduleKind;
	}

	/**
	 * Tells if this transpiler transpiles to code using JavaScript modules.
	 */
	public boolean isUsingModules() {
		return moduleKind != null && moduleKind != ModuleKind.none;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#getBundlesDirectory()
	 */
	@Override
	public File getBundlesDirectory() {
		return bundlesDirectory;
	}

	/**
	 * Sets the directory where JavaScript bundles are generated when the bundle
	 * option is activated.
	 */
	public void setBundlesDirectory(File bundlesDirectory) {
		this.bundlesDirectory = bundlesDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#isBundle()
	 */
	@Override
	public boolean isBundle() {
		return bundle;
	}

	/**
	 * Sets this transpiler to generate JavaScript bundles for running in a Web
	 * browser.
	 */
	public void setBundle(boolean bundle) {
		this.bundle = bundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#getEncoding()
	 */
	@Override
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the expected Java source code encoding.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#isNoRootDirectories()
	 */
	@Override
	public boolean isNoRootDirectories() {
		return noRootDirectories;
	}

	/**
	 * Sets this transpiler to skip the root directories (packages annotated
	 * with @jsweet.lang.Root) so that the generated file hierarchy starts at
	 * the root directories rather than including the entire directory
	 * structure.
	 */
	public void setNoRootDirectories(boolean noRootDirectories) {
		this.noRootDirectories = noRootDirectories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#isIgnoreAssertions()
	 */
	@Override
	public boolean isIgnoreAssertions() {
		return ignoreAssertions;
	}

	/**
	 * Sets the transpiler to ignore the 'assert' statements or generate
	 * appropriate code.
	 */
	public void setIgnoreAssertions(boolean ignoreAssertions) {
		this.ignoreAssertions = ignoreAssertions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsweet.transpiler.JSweetOptions#isIgnoreJavaFileNameError()
	 */
	@Override
	public boolean isIgnoreJavaFileNameError() {
		return ignoreJavaFileNameError;
	}

	public void setIgnoreJavaFileNameError(boolean ignoreJavaFileNameError) {
		this.ignoreJavaFileNameError = ignoreJavaFileNameError;
	}

	@Override
	public boolean isGenerateDeclarations() {
		return generateDeclarations;
	}

	public void setGenerateDeclarations(boolean generateDeclarations) {
		this.generateDeclarations = generateDeclarations;
	}

	@Override
	public File getDeclarationsOutputDir() {
		return declarationsOutputDir;
	}

	public void setDeclarationsOutputDir(File declarationsOutputDir) {
		this.declarationsOutputDir = declarationsOutputDir;
	}

	@Override
	public File getExtractedCandyJavascriptDir() {
		return extractedCandyJavascriptDir;
	}

	@Override
	public boolean isJDKAllowed() {
		return jdkAllowed;
	}
}
