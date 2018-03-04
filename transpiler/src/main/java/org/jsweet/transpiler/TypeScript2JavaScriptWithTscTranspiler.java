package org.jsweet.transpiler;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.ProcessUtil;

public class TypeScript2JavaScriptWithTscTranspiler extends TypeScript2JavaScriptTranspiler {

	private Process tsCompilationProcess;

	@Override
	protected synchronized void doTranspile( //
			ErrorCountTranspilationHandler transpilationHandler, //
			Collection<File> tsFiles, //
			Collection<SourceFile> tsSourceFiles, //
			JSweetOptions options, //
			boolean ignoreErrors, //
			OnTsTranspilationCompletedCallback onTsTranspilationCompleted) throws Exception {

		if (tsCompilationProcess != null && options.isTscWatchMode()) {
			return;
		}
		logger.debug("ts2js with TSC: " + tsFiles);
		if (options.isTscWatchMode()) {
			watchedFiles = tsSourceFiles;
		}

		LinkedList<String> args = new LinkedList<>();
		args.addAll(asList("--target", options.getEcmaTargetVersion().name()));

		if (options.isUsingModules()) {
			args.add("--module");
			args.add(options.getModuleKind().toString());
		}

		args.add("--moduleResolution");
		args.add(options.getModuleResolution().toString());

		if (options.getEcmaTargetVersion().ordinal() >= EcmaScriptComplianceLevel.ES5.ordinal()) {
			args.add("--experimentalDecorators");
			args.add("--emitDecoratorMetadata");
		}

		if (options.isTscWatchMode()) {
			args.add("--watch");
		}
		if (options.isGenerateSourceMaps()) {
			args.add("--sourceMap");
		}
		if (options.isGenerateDeclarations()) {
			args.add("--declaration");
		}
		args.addAll(asList("--rootDir", options.getTsOutputDir().getAbsolutePath()));
		// args.addAll(asList("--sourceRoot", tsOutputDir.toString()));

		if (options.getJsOutputDir() != null) {
			args.addAll(asList("--outDir", options.getJsOutputDir().getAbsolutePath()));
		}
		
		File tscRootFile = getOrCreateTscRootFile(options.getTsOutputDir());
		if (tscRootFile.exists()) {
			args.add(relativizeTsFile(options.getTsOutputDir(), tscRootFile).toString());
		}
		
		for (File file : tsFiles) {
			String filePath = relativizeTsFile(options.getTsOutputDir(), file).toString();
			args.add(filePath);
		}

		logger.info("launching tsc...");
		boolean[] fullPass = { true };

		if (options.isSkipTypeScriptChecks()) {
			args.add("--skipDefaultLibCheck");
			args.add("--skipLibCheck");
		}
		
		tsCompilationProcess = ProcessUtil.runCommand("tsc", options.getTsOutputDir(), options.isTscWatchMode(),
				line -> {
					logger.info(line);
					TscOutput output = parseTscOutput(line);
					if (output.position != null) {
						if (ignoreErrors) {
							return;
						}
						SourcePosition position = SourceFile.findOriginPosition(output.position, tsSourceFiles);
						if (position == null) {
							transpilationHandler.report(JSweetProblem.INTERNAL_TSC_ERROR, output.position,
									output.message);
						} else {
							transpilationHandler.report(JSweetProblem.MAPPED_TSC_ERROR, position, output.message);
						}
					} else {
						if (output.message.startsWith("message TS6042:")) {
							onTsTranspilationCompleted.call(fullPass[0], transpilationHandler, tsSourceFiles);
							fullPass[0] = false;
						} else {
							// TODO enhance tsc feedbacks support: some
							// messages are swallowed here: for instance
							// error TS1204: Cannot compile modules into
							// 'commonjs', 'amd', 'system' or 'umd' when
							// targeting 'ES6' or higher.
						}
					}
				}, process -> {
					tsCompilationProcess = null;
					onTsTranspilationCompleted.call(fullPass[0], transpilationHandler, tsSourceFiles);
					fullPass[0] = false;
				}, () -> {
					if (!ignoreErrors && transpilationHandler.getProblemCount() == 0) {
						transpilationHandler.report(JSweetProblem.INTERNAL_TSC_ERROR, null, "Unknown tsc error");
					}
				}, args.toArray(new String[0]));
	}

	private Collection<SourceFile> watchedFiles;
	/**
	 * The name of the file generated in the root package to avoid the TypeScript
	 * compiler to skip empty directories.
	 */
	public final static String TSCROOTFILE = ".tsc-rootfile.ts";

	private Path relativizeTsFile(File tsOutputDir, File file) {
		try {
			return tsOutputDir.getAbsoluteFile().getCanonicalFile().toPath()
					.relativize(file.getAbsoluteFile().getCanonicalFile().toPath());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static class TscOutput {
		public SourcePosition position;
		public String message;

		@Override
		public String toString() {
			return message + " - " + position;
		}
	}

	private static Pattern errorRE = Pattern.compile("(.*)\\((.*)\\): error TS[0-9]+: (.*)");

	private static TscOutput parseTscOutput(String outputString) {
		Matcher m = errorRE.matcher(outputString);
		TscOutput error = new TscOutput();
		if (m.matches()) {
			String[] pos = m.group(2).split(",");
			error.position = new SourcePosition(new File(m.group(1)), null, Integer.parseInt(pos[0]),
					Integer.parseInt(pos[1]));
			StringBuilder sb = new StringBuilder(m.group(3));
			sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
			if (sb.charAt(sb.length() - 1) == '.') {
				sb.deleteCharAt(sb.length() - 1);
			}
			error.message = sb.toString();
		} else {
			error.message = outputString;
		}
		return error;
	}

	public Collection<SourceFile> getWatchedFiles() {
		return watchedFiles;
	}

	public SourceFile getWatchedFile(File javaFile) {
		if (watchedFiles != null) {
			for (SourceFile f : watchedFiles) {
				if (f.getJavaFile().getAbsoluteFile().equals(javaFile.getAbsoluteFile())) {
					return f;
				}
			}
		}
		return null;
	}

	public void stopWatch() {
		if (tsCompilationProcess != null) {
			tsCompilationProcess.destroyForcibly();
			while (tsCompilationProcess != null && tsCompilationProcess.isAlive()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
				logger.error("tsc did not terminate");
			}
			try {
				if (tsCompilationProcess != null) {
					tsCompilationProcess.waitFor();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tsCompilationProcess = null;
			watchedFiles = null;
		}
	}
	
	private File getOrCreateTscRootFile(File tsOutputDir) throws IOException {
		File tscRootFile = new File(tsOutputDir, TSCROOTFILE);

		if (!tscRootFile.exists()) {
			FileUtils.write(tscRootFile, "// Root empty file generated by JSweet to avoid tsc behavior, which\n"
					+ "// does not preserve the entire file hierarchy for empty directories.", false);
		}
		return tscRootFile;
	}
}
