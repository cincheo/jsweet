package org.jsweet.transpiler.util;

import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;

public class MainMethodFinder extends TreeScanner {
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
}