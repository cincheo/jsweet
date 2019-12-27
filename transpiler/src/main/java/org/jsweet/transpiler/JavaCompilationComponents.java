package org.jsweet.transpiler;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.Util;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;

public class JavaCompilationComponents implements AutoCloseable {

	private final static Logger logger = Logger.getLogger(JavaCompilationComponents.class);

	private final StandardJavaFileManager fileManager;
	private final JavaCompiler compiler;
	private final JavacTask task;
	private final List<JavaFileObject> sourceFileObjects;

	private JavaCompilationComponents(StandardJavaFileManager fileManager, JavaCompiler compiler,
			List<JavaFileObject> sourceFileObjects, JavacTask task) {
		this.fileManager = fileManager;
		this.compiler = compiler;
		this.sourceFileObjects = sourceFileObjects;
		this.task = task;
	}

	public static class JavaCompilerOptions {
		protected List<String> optionsAsList = new ArrayList<>();

		public void put(String name, String value) {
			optionsAsList.add(name);
			optionsAsList.add(value);
		}
		
		public void put(String singleArgument) {
			optionsAsList.add(singleArgument);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + optionsAsList;
		}
	}

	public static class Options {
		public String classPath;
		public String encoding;
		public TranspilationHandler transpilationHandler = new ConsoleTranspilationHandler();
	}

	public static JavaCompilationComponents prepareFor( //
			List<File> sourceFiles, //
			JSweetContext context, //
			JSweetFactory factory, //
			Options options //
	) {
		String encoding = isBlank(options.encoding) ? Charset.defaultCharset().name() : options.encoding;
		String classPath = isBlank(options.classPath) ? System.getProperty("java.class.path") : options.classPath;
		TranspilationHandler transpilationHandler = options.transpilationHandler != null ? options.transpilationHandler
				: new ConsoleTranspilationHandler();

		JavaCompilerOptions compilerOptions = new JavaCompilerOptions();
		if (classPath != null) {
			compilerOptions.put("--module-path", classPath);
			compilerOptions.put("-cp", classPath);
			for (String s : classPath.split(File.pathSeparator)) {
				if (s.contains(JSweetConfig.MAVEN_JAVA_OVERRIDE_ARTIFACT)) {
					context.strictMode = true;
					compilerOptions.put("-bootclasspath", s);
				}
			}
		}

		compilerOptions.put("-Xlint:path");

		Charset charset = null;
		if (encoding != null) {
			compilerOptions.put("-encoding", encoding);
			try {
				charset = Charset.forName(encoding);
			} catch (Exception e) {
				logger.warn("cannot use charset " + encoding, e);
			}
		}
		if (encoding == null) {
			charset = Charset.forName("UTF-8");
		}
		logger.debug("charset: " + charset);
		logger.debug("strict mode: " + context.strictMode);

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, context.locale, charset);

		List<JavaFileObject> sourceFileObjects = context.util.toJavaFileObjects(fileManager, sourceFiles);

		JSweetDiagnosticHandler diagnosticHandler = factory.createDiagnosticHandler(transpilationHandler, context);

		compilerOptions = factory.finalizeJavaCompilerOptions(compilerOptions);
		
		logger.info("creating JavaCompiler task with options: " + compilerOptions);
		JavacTask task = (JavacTask) compiler.getTask(null, fileManager, diagnosticHandler,
				compilerOptions.optionsAsList, null, sourceFileObjects);

		context.trees = Trees.instance(task);
		context.elements = task.getElements();
		context.types = task.getTypes();
		context.util = new Util(context);

		return new JavaCompilationComponents(fileManager, compiler, sourceFileObjects, task);
	}

	public JavaCompiler getCompiler() {
		return compiler;
	}

	public StandardJavaFileManager getFileManager() {
		return fileManager;
	}

	public JavacTask getTask() {
		return task;
	}

	public Iterable<JavaFileObject> getSourceFileObjects() {
		return sourceFileObjects;
	}

	@Override
	public void close() throws IOException {
		getFileManager().close();
	}
}