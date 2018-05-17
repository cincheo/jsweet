package org.jsweet.transpiler;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.Position;
import org.jsweet.transpiler.util.ProcessUtil;

import ts.TypeScriptException;
import ts.TypeScriptNoContentAvailableException;
import ts.client.ITypeScriptServiceClient;
import ts.client.ScriptKindName;
import ts.client.TypeScriptServiceClient;
import ts.client.TypeScriptServiceClient.TypeScriptServiceLogConfiguration;
import ts.client.TypeScriptServiceClient.TypeScriptServiceLogLevel;
import ts.client.diagnostics.DiagnosticEvent;
import ts.client.diagnostics.IDiagnostic;
import ts.client.projectinfo.ProjectInfo;
import ts.cmd.tsc.CompilerOptions;
import ts.internal.client.protocol.OpenExternalProjectRequestArgs.ExternalFile;

public class TypeScript2JavaScriptWithTsserverTranspiler extends TypeScript2JavaScriptTranspiler {

	public static TypeScript2JavaScriptWithTsserverTranspiler INSTANCE = new TypeScript2JavaScriptWithTsserverTranspiler();

	private TypeScript2JavaScriptWithTsserverTranspiler() {
	}

	@Override
	protected synchronized void doTranspile( //
			ErrorCountTranspilationHandler transpilationHandler, //
			Collection<File> tsFiles, //
			Collection<SourceFile> tsSourceFiles, //
			JSweetOptions options, //
			boolean ignoreErrors, //
			OnTsTranspilationCompletedCallback onTsTranspilationCompleted) throws Exception {

		if (options.isTscWatchMode()) {
			throw new RuntimeException(
					"tsserver implementation doesn't support watch mode - but it is so fast you shouldn't need it :)");
		}

		logger.debug("ts2js with tsserver: " + tsFiles);

		CompilerOptions compilerOptions = new CompilerOptions();
		compilerOptions.setTarget(options.getEcmaTargetVersion().name());
		if (options.isUsingModules()) {
			compilerOptions.setModule(options.getModuleKind().name());
		}
		compilerOptions.setModuleResolution(options.getModuleResolution().name());

		if (options.getEcmaTargetVersion().ordinal() >= EcmaScriptComplianceLevel.ES5.ordinal()) {
			compilerOptions.setExperimentalDecorators(true);
			compilerOptions.setEmitDecoratorMetadata(true);
		}

		compilerOptions.setSourceMap(options.isGenerateSourceMaps());
		compilerOptions.setDeclaration(options.isGenerateDeclarations());
		compilerOptions.setRootDir(options.getTsOutputDir().getAbsolutePath());
		compilerOptions.setSkipDefaultLibCheck(options.isSkipTypeScriptChecks());

		if (options.getJsOutputDir() != null) {
			compilerOptions.setOutDir(options.getJsOutputDir().getAbsolutePath());
		}

		Collection<String> sourceFilePaths = tsFiles.stream().map(ts.utils.FileUtils::getPath).collect(toList());
		if (sourceFilePaths.isEmpty()) {
			throw new RuntimeException("no files to transpile");
		}

		logger.info("launching tsserver compilation : \ncompilerOptions=" + compilerOptions + " \nsourcesFilePaths="
				+ sourceFilePaths);
		ITypeScriptServiceClient client = getTypeScriptServiceClient();
		logger.debug("tsserver client built");

		String projectFileName = ts.utils.FileUtils.getPath(options.getTsOutputDir());
		String referenceFileName = sourceFilePaths.iterator().next();

		logger.info("open external project: " + projectFileName);
		client.openExternalProject(projectFileName,
				sourceFilePaths.stream().map(path -> new ExternalFile(path, ScriptKindName.TS, false, null))
						.collect(toList()), //
				compilerOptions);

		logger.debug("tsserver project opened: " + projectFileName);

		for (String fileName : sourceFilePaths) {
			client.updateFile(fileName, null);
		}

		for (String fileName : sourceFilePaths) {
			try {
				Boolean result = client.compileOnSaveEmitFile(fileName, true).get(5000, TimeUnit.MILLISECONDS);
				logger.trace("ts compilation [" + fileName + "] result=" + result);
			} catch (ExecutionException e) {
				Throwable actualException = e.getCause();
				if (actualException instanceof TypeScriptNoContentAvailableException) {
					logger.warn("ts compilation: no content for " + fileName);
				}
			}
		}

		logger.info("tsserver project compiled: " + projectFileName);

		ProjectInfo projectInfo = client.projectInfo(referenceFileName, projectFileName, true).get(5000,
				TimeUnit.MILLISECONDS);
		Collection<DiagnosticEvent> compilationErrors = client.geterrForProject(referenceFileName, 0, projectInfo)
				.get();

		if (!ignoreErrors) {
			for (DiagnosticEvent errorEvent : compilationErrors) {
				File fileInError = new File(errorEvent.getBody().getFile());
				for (IDiagnostic error : errorEvent.getBody().getDiagnostics()) {

					SourcePosition originalPosition = new SourcePosition(fileInError, null,
							new Position(error.getStartLocation().getLine(), error.getStartLocation().getOffset()));
					SourcePosition position = SourceFile.findOriginPosition(originalPosition, tsSourceFiles);
					if (position == null) {
						transpilationHandler.report(JSweetProblem.INTERNAL_TSC_ERROR, originalPosition,
								error.getFullText());
					} else {
						transpilationHandler.report(JSweetProblem.MAPPED_TSC_ERROR, position, error.getFullText());
					}
				}
			}
		} else {
			printTsserverDiagnostics(compilationErrors);
		}

		client.closeExternalProject(projectFileName);

		onTsTranspilationCompleted.call(false, transpilationHandler, tsSourceFiles);

	}

	private void printTsserverDiagnostics(Collection<DiagnosticEvent> events) {
		String diagReport = "";
		for (DiagnosticEvent event : events) {
			if (event.getBody().getDiagnostics().size() > 0) {
				diagReport += event.getBody().getFile() + ":: " + event.getEvent() + "\n";
				for (IDiagnostic diag : event.getBody().getDiagnostics()) {
					diagReport += "  > " + diag.getStartLocation().getLine() + ":" + diag.getStartLocation().getOffset()
							+ diag.getFullText() + "\n";
				}
			}
		}

		logger.info("tsserver diagnostics: " + diagReport);
	}

	private ITypeScriptServiceClient typeScriptServiceClient;

	private ITypeScriptServiceClient getTypeScriptServiceClient() {

		try {
			if (typeScriptServiceClient == null) {
				String tsserverPath = ProcessUtil.getGlobalNpmPackageNodeMainFilePath("typescript", "tsserver");

				TypeScriptServiceClient client = new TypeScriptServiceClient( //
						new File("."), //
						new File(tsserverPath), //
						null, false, false, null, null, //
						new TypeScriptServiceLogConfiguration("/tmp/tss.log", TypeScriptServiceLogLevel.verbose));
				// client.addInterceptor(LoggingInterceptor.getInstance());
				// client.addProcessListener(TraceNodejsProcess.INSTANCE);
				typeScriptServiceClient = client;

				logger.info("creating TypeScriptServiceClient");
			}

			return typeScriptServiceClient;
		} catch (TypeScriptException e) {
			throw new RuntimeException(e);
		}
	}
}
