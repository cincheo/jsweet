package org.jsweet.transpiler.eval;

import static java.util.Arrays.asList;

import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.JavaCompilationComponents;
import org.jsweet.transpiler.JavaCompilationComponents.Options;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.EvaluationResult;
import org.jsweet.transpiler.util.MainMethodFinder;
import org.jsweet.transpiler.util.MainMethodFinder.MainMethodFoundSignal;

import com.sun.source.tree.CompilationUnitTree;

public class JavaEval extends RuntimeEval {

	private JSweetTranspiler transpiler;

	public JavaEval(JSweetTranspiler transpiler, EvalOptions options) {
		this.transpiler = transpiler;
	}

	public EvaluationResult performEval(SourceFile[] sourceFiles) throws Exception {
		JSweetContext context = new JSweetContext(transpiler);
		JavaCompilationComponents compilationComponents = JavaCompilationComponents.prepareFor( //
				asList(SourceFile.toFiles(sourceFiles)), //
				context, //
				transpiler.getFactory(), //
				new Options() {
					{
						classPath = transpiler.getClassPath();
						encoding = transpiler.getEncoding();
					}
				});
		
		logger.info("parsing: " + compilationComponents.getSourceFileObjects());
		ExecutableElement mainMethod = null;
		Iterable<? extends CompilationUnitTree> compilationUnits = compilationComponents.getTask().parse();
		MainMethodFinder mainMethodFinder = new MainMethodFinder();
		try {
			mainMethodFinder.scan(compilationUnits, context.trees);
		} catch (MainMethodFoundSignal foundSignal) {
			mainMethod = foundSignal.mainMethod;
		}
		if (mainMethod != null) {
			try {
				initExportedVarMap();
				Class<?> c = Class
						.forName(((TypeElement) mainMethod.getEnclosingElement()).getQualifiedName().toString());
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
