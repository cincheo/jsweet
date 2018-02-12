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
import ts.nodejs.TraceNodejsProcess;

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
			throw new RuntimeException("tsserver implementation doesn't support watch mode - but it is so fast you shouldn't need it :)");
		}
		
		logger.debug("ts2js with tsserver: " + tsFiles);

		CompilerOptions compilerOptions = new CompilerOptions();
		compilerOptions.setTarget(options.getEcmaTargetVersion().name());
		compilerOptions.setModule(options.getModuleKind().name());
		compilerOptions.setModuleResolution(options.getModuleResolution().name());

		if (options.getEcmaTargetVersion().ordinal() >= EcmaScriptComplianceLevel.ES5.ordinal()) {
			compilerOptions.setExperimentalDecorators(true);
			compilerOptions.setEmitDecoratorMetadata(true);
		}

		if (options.isGenerateSourceMaps()) {
			compilerOptions.setSourceMap(true);
		}
		if (options.isGenerateDeclarations()) {
			compilerOptions.setDeclaration(true);
		}
		compilerOptions.setRootDir(options.getTsOutputDir().getAbsolutePath());

		if (options.getJsOutputDir() != null) {
			compilerOptions.setOutDir(options.getJsOutputDir().getAbsolutePath());
		}

		if (options.isSkipTypeScriptChecks()) {
			compilerOptions.setSkipDefaultLibCheck(true);
		}

		Collection<String> sourceFilePaths = tsFiles.stream().map(ts.utils.FileUtils::getPath).collect(toList());
		if (sourceFilePaths.isEmpty()) {
			throw new RuntimeException("no files to transpile");
		}

		logger.info("launching tsserver compilation : \ncompilerOptions=" + compilerOptions + " \nsourcesFilePaths="
				+ sourceFilePaths);
		ITypeScriptServiceClient client = getTypeScriptServiceClient();

		logger.info("tsserver client built");

		String projectFileName = ts.utils.FileUtils.getPath(options.getTsOutputDir());
		String referenceFileName = sourceFilePaths.iterator().next();

		logger.info("open external project: " + projectFileName);
		client.openExternalProject(projectFileName,
				sourceFilePaths.stream().map(path -> new ExternalFile(path, ScriptKindName.TS, false, null))
						.collect(toList()), //
				compilerOptions);

		logger.info("tsserver project opened ");

		for (String fileName : sourceFilePaths) {
			client.updateFile(fileName, null);
		}

		for (String fileName : sourceFilePaths) {
			try {
			Boolean result = client.compileOnSaveEmitFile(fileName, true).get(5000, TimeUnit.MILLISECONDS);
			logger.info("COMPILE >>> " + fileName + " >>>> " + result);
			} catch (ExecutionException e) {
				 Throwable actualException = e.getCause();
				 if (actualException instanceof TypeScriptNoContentAvailableException) {
					 logger.warn("NO CONTENT FOR " + fileName);
				 }
			}
		}

		logger.info("tsserver project compiled ");

		ProjectInfo projectInfo = client.projectInfo(referenceFileName, projectFileName, true).get(5000,
				TimeUnit.MILLISECONDS);
		Collection<DiagnosticEvent> compilationErrors = client.geterrForProject(referenceFileName, 0, projectInfo)
				.get();
		printTsserverDiagnostics(compilationErrors);

		if (!ignoreErrors) {
			for (DiagnosticEvent errorEvent : compilationErrors) {
				File fileInError = new File(errorEvent.getBody().getFile());
				for (IDiagnostic error : errorEvent.getBody().getDiagnostics()) {
					SourcePosition position = new SourcePosition(fileInError, null,
							new Position(error.getStartLocation().getLine(), error.getStartLocation().getOffset()));
					transpilationHandler.report(JSweetProblem.MAPPED_TSC_ERROR, position, error.getFullText());
				}
			}
		}

		client.closeExternalProject(projectFileName);

	}

	private void printTsserverDiagnostics(Collection<DiagnosticEvent> events) {
		System.out.println("========== DISPLAY DIAGNOSTICS ============");
		for (DiagnosticEvent event : events) {
			System.out.println(event.getBody().getFile() + ":: " + event.getEvent());
			for (IDiagnostic diag : event.getBody().getDiagnostics()) {
				System.out.println("  > " + diag.getStartLocation().getLine() + ":"
						+ diag.getStartLocation().getOffset() + diag.getFullText());
			}
		}
		System.out.println("========== END ===========");
	}

	private ITypeScriptServiceClient typeScriptServiceClient;

	private ITypeScriptServiceClient getTypeScriptServiceClient() {

		try {
			if (typeScriptServiceClient == null) {
				String typescriptModulePath = ProcessUtil.getGlobalNpmPackagePath("typescript");

				TypeScriptServiceClient client = new TypeScriptServiceClient( //
						new File("."), //
						new File(typescriptModulePath), //
						null, false, false, null, null, //
						new TypeScriptServiceLogConfiguration("/tmp/tss.log", TypeScriptServiceLogLevel.verbose));
				// client.addInterceptor(LoggingInterceptor.getInstance());
				client.addProcessListener(TraceNodejsProcess.INSTANCE);
				typeScriptServiceClient = client;

				logger.info("creating TypeScriptServiceClient");
			}

			return typeScriptServiceClient;
		} catch (TypeScriptException e) {
			throw new RuntimeException(e);
		}
	}
}
