package org.jsweet.transpiler;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsweet.transpiler.extension.Java2TypeScriptAdapter;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.extension.RemoveJavaDependenciesAdapter;

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

	/**
	 * A logger to be used for internal messages.
	 */
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
	 * Creates the printer adapter or any subclass.
	 * 
	 * <p>
	 * This is the method to be overridden to create composable extensions.
	 * Adapters are chainable (decorator pattern) and new adapters will delegate
	 * to parent adapters when not overriding the parent behavior.
	 * 
	 * <p>
	 * For instance, here my own adapter will override the needed behavior and
	 * delegate to the default adapter chain:
	 * 
	 * <pre>
	 * &#64;Override
	 * public PrinterAdapter createAdapter(JSweetContext context) {
	 * 	return new MyOwnAdapter(super.createAdapter(context));
	 * }
	 * </pre>
	 */
	public PrinterAdapter createAdapter(JSweetContext context) {
		if (context.options.getConfiguration() != null && context.options.getConfiguration().containsKey("adapters")) {
			// generically creates the adapter chain from the "adapters"
			// configuration entry
			logger.info("constructing adapters: " + context.options.getConfiguration().get("adapters"));
			try {
				Class<?> adapterClass;
				PrinterAdapter adapter = null;
				List<?> adapters = (List<?>) context.options.getConfiguration().get("adapters");
				for (int i = adapters.size() - 1; i >= 0; i--) {
					if (adapters.get(i) instanceof String) {
						adapterClass = PrinterAdapter.class.getClassLoader().loadClass((String) adapters.get(i));
						if (i == adapters.size() - 1) {
							Constructor<?> constructor = null;
							try {
								constructor = adapterClass.getConstructor(JSweetContext.class);
							} catch (Exception e) {
								// swallow
							}
							if (constructor == null) {
								logger.debug("constructing default adapter");
								adapter = context.isUsingJavaRuntime() ? new Java2TypeScriptAdapter(context)
										: new RemoveJavaDependenciesAdapter(context);
								try {
									constructor = adapterClass.getConstructor(PrinterAdapter.class);
								} catch (Exception e) {
									// swallow
								}
								if (constructor != null) {
									adapter = (PrinterAdapter) constructor.newInstance(adapter);
								} else {
									throw new RuntimeException("wrong adapter class " + adapterClass.getName()
											+ ": the last adapter must be chainable or be the root adapter (see the PrinterAdapter API)");
								}

							} else {
								adapter = (PrinterAdapter) constructor.newInstance(context);
							}
						} else {
							Constructor<?> constructor = adapterClass.getConstructor(PrinterAdapter.class);
							if (constructor == null) {
								throw new RuntimeException("wrong adapter class " + adapterClass.getName()
										+ ": a chainable adapter must define a constructor accepting a parent adapter");
							} else {
								adapter = (PrinterAdapter) constructor.newInstance(adapter);
							}
						}
					}
				}
				return adapter;
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			if (context.isUsingJavaRuntime()) {
				return new Java2TypeScriptAdapter(context);
			} else {
				return new RemoveJavaDependenciesAdapter(context);
			}
		}
	}

	/**
	 * Creates the core translator or any subclass.
	 */
	public Java2TypeScriptTranslator createTranslator(PrinterAdapter adapter, TranspilationHandler transpilationHandler,
			JSweetContext context, JCCompilationUnit compilationUnit, boolean fillSourceMap) {
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
