package org.jsweet.transpiler;

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
 * @param <C>
 *            the JSweet context class
 */
public class JSweetFactory<C extends JSweetContext> {

	/**
	 * An empty constructor is mandatory for all subclasses.
	 */
	public JSweetFactory() {
	}

	/**
	 * Creates the transpiler's context or any subclass.
	 */
	@SuppressWarnings("unchecked")
	public C createContext(JSweetOptions options) {
		return (C) new JSweetContext(options);
	}

	/**
	 * Creates the adapter or any subclass.
	 * 
	 * <p>
	 * For simple extensions such are redirecting invocations and field access,
	 * the programmer should override this method and return a subclass.
	 */
	public Java2TypeScriptAdapter<C> createAdapter(C context) {
		return new Java2TypeScriptAdapter<C>(context);
	}

	/**
	 * Creates the core translator or any subclass.
	 */
	public Java2TypeScriptTranslator<C> createTranslator(Java2TypeScriptAdapter<C> adapter,
			TranspilationHandler transpilationHandler, C context, JCCompilationUnit compilationUnit,
			boolean fillSourceMap) {
		return new Java2TypeScriptTranslator<C>(adapter, transpilationHandler, context, compilationUnit, fillSourceMap);
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
	public GlobalBeforeTranslationScanner<C> createBeforeTranslationScanner(TranspilationHandler transpilationHandler,
			C context) {
		return new GlobalBeforeTranslationScanner<C>(transpilationHandler, context);
	}

}
