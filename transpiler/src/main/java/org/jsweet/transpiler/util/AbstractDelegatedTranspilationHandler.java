package org.jsweet.transpiler.util;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.SourcePosition;
import org.jsweet.transpiler.TranspilationHandler;

public abstract class AbstractDelegatedTranspilationHandler implements TranspilationHandler {

	private TranspilationHandler delegate;
	
	public AbstractDelegatedTranspilationHandler(TranspilationHandler delegate) {
		this.delegate = delegate;
	}

	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message) {
		delegate.report(problem, sourcePosition, message);
	}

	@Override
	public void onCompleted(JSweetTranspiler transpiler, boolean fullPass, SourceFile[] files) {
		delegate.onCompleted(transpiler, fullPass, files);
	}
}