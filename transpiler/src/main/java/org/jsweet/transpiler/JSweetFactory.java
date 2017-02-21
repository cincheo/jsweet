package org.jsweet.transpiler;

import org.apache.log4j.Logger;
import org.jsweet.transpiler.extensions.RemoveJavaDependenciesAdapter;
import org.jsweet.transpiler.typescript.Java2TypeScriptAdapter;
import org.jsweet.transpiler.typescript.Java2TypeScriptTranslator;

import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

/**
 * The factory object is the one creating instances for key JSweet transpilation
 * elements. By subclassing the factory, programmers can extend the transpiler
 * behavior.
 * 
 * @author Renaud Pawlak
 *
 */
public class JSweetFactory {

	protected final static Logger logger = Logger.getLogger(JSweetFactory.class);

	/**
	 * An empty constructor is mandatory for all subclasses.
	 */
	public JSweetFactory() {
	}

	/**
	 * Creates the transpiler's context or any subclass.
	 */
	public JSweetContext createContext(JSweetOptions options) {
		return new JSweetContext(options);
	}

	/**
	 * Creates the adapter or any subclass.
	 * 
	 * <p>
	 * For simple extensions such are redirecting invocations and field access,
	 * the programmer should override this method and return a subclass.
	 */
	public Java2TypeScriptAdapter createAdapter(JSweetContext context) {
		if (context.isUsingJavaRuntime()) {
			return new Java2TypeScriptAdapter(context);
		} else {
			return new RemoveJavaDependenciesAdapter(context);
		}
	}

	/**
	 * Creates the core translator or any subclass.
	 */
	public Java2TypeScriptTranslator createTranslator(Java2TypeScriptAdapter adapter,
			TranspilationHandler transpilationHandler, JSweetContext context, JCCompilationUnit compilationUnit,
			boolean fillSourceMap) {
		return new Java2TypeScriptTranslator(adapter, transpilationHandler, context, compilationUnit, fillSourceMap);
	}

	/**
	 * Creates the scanner which is called before translating the program.
	 * 
	 * <p>
	 * A typical use would be to return a subclass of
	 * {@link GlobalBeforeTranslationScanner} in order to fill the context with
	 * some specific global analysis results that can be used later on by the
	 * translator.
	 */
	public GlobalBeforeTranslationScanner createBeforeTranslationScanner(TranspilationHandler transpilationHandler,
			JSweetContext context) {
		return new GlobalBeforeTranslationScanner(transpilationHandler, context);
	}

	/**
	 * Creates a diagnostic handler (responsible for reporting Java
	 * errors/warnings).
	 * 
	 * <p>
	 * One can override with a subclass to tune how JSweet reports Java
	 * messages.
	 */
	public JSweetDiagnosticHandler createDiagnosticHandler(TranspilationHandler transpilationHandler,
			JSweetContext context) {
		return new JSweetDiagnosticHandler(transpilationHandler, context);
	}

}
