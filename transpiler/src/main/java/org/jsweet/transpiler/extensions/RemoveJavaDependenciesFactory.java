package org.jsweet.transpiler.extensions;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.typescript.Java2TypeScriptAdapter;

/**
 * Tunes the transpiler to avoid using Java APIs as much as possible (JDK).
 * 
 * <p>
 * By default, the transpiler generates lists, maps and other Java elements as
 * is. It expects the J4TS runtime and assumes a default implementation at
 * runtime.
 * 
 * <p>
 * When this extension is selected, the transpiler will try to translate Java
 * APIs to native JavaScript APIs. It will report transpilation errors when a
 * Java element cannot be translated to a JavaScript element.
 */
public class RemoveJavaDependenciesFactory<C extends JSweetContext> extends JSweetFactory<C> {

	@Override
	public Java2TypeScriptAdapter<C> createAdapter(C context) {
		return new RemoveJavaDependenciesAdapter<C>(context);
	}

}
