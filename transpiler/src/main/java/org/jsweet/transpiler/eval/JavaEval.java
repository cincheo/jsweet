package org.jsweet.transpiler.eval;

import static org.jsweet.transpiler.util.Util.toJavaFileObjects;

import java.util.Arrays;
import java.util.Map;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.EvaluationResult;
import org.jsweet.transpiler.util.MainMethodFinder;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;

public class JavaEval extends RuntimeEval {

	private JSweetTranspiler transpiler;

	public JavaEval(JSweetTranspiler transpiler, EvalOptions options) {
		this.transpiler = transpiler;
	}

	public EvaluationResult performEval(SourceFile[] sourceFiles) throws Exception {
		// search for main functions
		JSweetContext context = new JSweetContext(transpiler);
		Options options = Options.instance(context);
		if (transpiler.getClassPath() != null) {
			options.put(Option.CLASSPATH, transpiler.getClassPath());
		}
		options.put(Option.XLINT, "path");
		if (transpiler.getEncoding() != null) {
			options.put(Option.ENCODING, transpiler.getEncoding());
		}

		JavacFileManager.preRegister(context);
		JavaFileManager fileManager = context.get(JavaFileManager.class);

		List<JavaFileObject> fileObjects = toJavaFileObjects(fileManager,
				Arrays.asList(SourceFile.toFiles(sourceFiles)));

		JavaCompiler compiler = JavaCompiler.instance(context);
		compiler.attrParseOnly = true;
		compiler.verbose = true;
		compiler.genEndPos = false;
		compiler.encoding = transpiler.getEncoding();

		Log log = Log.instance(context);
		log.dumpOnError = false;
		log.emitWarnings = false;

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
				Class<?> c = Class
						.forName(mainMethodFinder.mainMethod.getEnclosingElement().getQualifiedName().toString());
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
	}
}
