package org.jsweet.transpiler.util;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.util.TreePathScanner;
import standalone.com.sun.source.util.Trees;

/**
 * Finds the first main method in a tree.
 * 
 * Triggers a {@link MainMethodFoundSignal} exception (containing main method) when found 
 * 
 * @author Louis Grignon
 *
 */
public class MainMethodFinder extends TreePathScanner<Void, Trees> {
	private ExecutableElement mainMethod;

	public static class MainMethodFoundSignal extends RuntimeException {
		public final ExecutableElement mainMethod;

		public MainMethodFoundSignal(ExecutableElement mainMethod) {
			this.mainMethod = mainMethod;
		}

		private static final long serialVersionUID = 1L;
	}

	@Override
	public Void visitMethod(MethodTree methodTree, Trees trees) {
		ExecutableElement method = (ExecutableElement) trees.getElement(getCurrentPath());
		if ("main(java.lang.String[])".equals(method.toString())) {
			if (method.getModifiers().contains(Modifier.STATIC)) {
				mainMethod = method;
				throw new MainMethodFoundSignal(mainMethod);
			}
		}
		return null;
	}
}