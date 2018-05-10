package org.jsweet.transpiler;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.Util;

public abstract class TypeScript2JavaScriptTranspiler {

	public static interface OnTsTranspilationCompletedCallback {
		void call(boolean fullPass, ErrorCountTranspilationHandler handler, Collection<SourceFile> files);
	}
	
	protected final Logger logger = Logger.getLogger(getClass());

	public void ts2js( //
			ErrorCountTranspilationHandler transpilationHandler, //
			Collection<SourceFile> tsSourceFiles, //
			Collection<File> tsDefDirs, //
			JSweetOptions options, //
			boolean ignoreErrors, //
			OnTsTranspilationCompletedCallback onTsTranspilationCompleted) {
		try {
			
			LinkedHashSet<File> tsFiles = new LinkedHashSet<>();
			for (SourceFile sourceFile : tsSourceFiles) {
					tsFiles.add(sourceFile.getTsFile());
			}
			for (File dir : tsDefDirs) {
				Util.addFiles(".d.ts", dir, tsFiles);
			}

			doTranspile(transpilationHandler, tsFiles, tsSourceFiles, options, ignoreErrors,
					onTsTranspilationCompleted);

		} catch (Exception e) {
			logger.error("ts2js transpilation failed", e);

			if (!ignoreErrors && transpilationHandler.getProblemCount() == 0) {
				transpilationHandler.report(JSweetProblem.INTERNAL_TSC_ERROR, null, "Unknown tsc error");
			}
		}
	}

	protected abstract void doTranspile( //
			ErrorCountTranspilationHandler transpilationHandler, //
			Collection<File> tsFiles, //
			Collection<SourceFile> tsSourceFiles, //
			JSweetOptions options, //
			boolean ignoreErrors, //
			OnTsTranspilationCompletedCallback onTsTranspilationCompleted) throws Exception;

}
