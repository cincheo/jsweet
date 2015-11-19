/* Copyright 2015 CINCHEO SAS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet;

import java.io.File;
import java.util.LinkedList;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.Severity;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.util.Util;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.martiansoftware.jsap.stringparsers.FileStringParser;
import com.sun.tools.javac.main.JavaCompiler;

/**
 * The command line launcher for the JSweet transpiler.
 * 
 * @author Renaud Pawlak
 */
public class JSweetCommandLineLauncher {

	private static int errorCount = 0;

	/**
	 * JSweet transpiler command line entry point. To use the JSweet transpiler
	 * from Java, see {@link JSweetTranspiler}.
	 */
	public static void main(String[] args) {
		try {
			JSAP jsapSpec;
			JSAPResult jsapArgs = parseArgs(jsapSpec = defineArgs(), args);

			if (!jsapArgs.success()) {
				printUsage(jsapSpec);
			}

			String classPath = jsapArgs.getString("classpath");
			System.out.println("classpath: " + classPath);

			File tsOutputDir = jsapArgs.getFile("tsout");
			tsOutputDir.mkdirs();
			System.out.println("ts output dir: " + tsOutputDir);

			File jsOutputDir = null;
			if (jsapArgs.getFile("jsout") != null) {
				jsOutputDir = jsapArgs.getFile("jsout");
				jsOutputDir.mkdirs();
			}
			System.out.println("js output dir: " + jsOutputDir);

			File inputDir = new File(jsapArgs.getString("input"));
			System.out.println("input dir: " + inputDir);

			LinkedList<File> files = new LinkedList<File>();
			Util.addFiles(".java", inputDir, files);

			JSweetTranspiler transpiler = new JSweetTranspiler(tsOutputDir, jsOutputDir, classPath);
			transpiler.setBundle(jsapArgs.getBoolean("bundle"));

			File bundlesDirectory = null;
			if (jsapArgs.getFile("bundlesDirectory") != null) {
				bundlesDirectory = jsapArgs.getFile("bundlesDirectory");
				bundlesDirectory.getParentFile().mkdirs();
			}
			System.out.println("bundles directory: " + bundlesDirectory);
			transpiler.setBundlesDirectory(bundlesDirectory);

			transpiler.setPreserveSourceLineNumbers(jsapArgs.getBoolean("debug"));
			if (args.length > 4) {
				File f = new File(args[4]);
				if (f.exists()) {
					transpiler.setTsDefDirs(f);
					System.out.println("tsdef dir: " + args[4]);
				} else {
					System.out.println("WARNING: tsdef dir does not exist - " + args[4]);
				}
			}

			transpiler.setModuleKind(ModuleKind.valueOf(jsapArgs.getString("module")));

			TranspilationHandler transpilationHandler = new TranspilationHandler() {
				@Override
				public void report(JSweetProblem problem, SourcePosition sourcePosition, String message) {
					String file = "<unknown>";
					String startLine = "<unknown>";
					if (sourcePosition != null) {
						if (sourcePosition.getFile() != null) {
							file = sourcePosition.getFile().getName();
						}
						startLine = "" + sourcePosition.getStartLine();
					}
					System.out.println(problem.getSeverity() + ": " + message + " at " + file + ":" + startLine);
					if (problem.getSeverity() == Severity.ERROR) {
						errorCount++;
					}
				}

				@Override
				public void onCompleted(JSweetTranspiler transpiler, boolean fullPass, SourceFile[] files) {
				}
			};

			transpiler.setEncoding(jsapArgs.getString("encoding"));

			transpiler.transpile(transpilationHandler, SourceFile.toSourceFiles(files));

			System.out.println("transpilation done, errors: " + errorCount);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		System.exit(errorCount > 0 ? 1 : 0);
	}

	private static JSAP defineArgs() throws JSAPException {
		// Verbose output
		JSAP jsap = new JSAP();
		Switch switchArg;
		FlaggedOption optionArg;

		// help
		switchArg = new Switch("help");
		switchArg.setShortFlag('h');
		switchArg.setLongFlag("help");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		// Java compiler's encoding
		optionArg = new FlaggedOption("encoding");
		optionArg.setLongFlag("encoding");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(false);
		optionArg.setDefault("UTF-8");
		optionArg.setHelp("Force the Java compiler to use a specific encoding (UTF-8, UTF-16, ...).");
		jsap.registerParameter(optionArg);

		// Input directory
		optionArg = new FlaggedOption("input");
		optionArg.setShortFlag('i');
		optionArg.setLongFlag("input");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(true);
		optionArg.setHelp("An input dir containing Java files to be transpiled.");
		jsap.registerParameter(optionArg);

		// TypeScript output directory
		optionArg = new FlaggedOption("tsout");
		optionArg.setLongFlag("tsout");
		optionArg.setDefault(".ts");
		optionArg.setHelp("Specify where to place generated TypeScript files.");
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// JavaScript output directory
		optionArg = new FlaggedOption("jsout");
		optionArg.setShortFlag('o');
		optionArg.setLongFlag("jsout");
		optionArg.setDefault("js");
		optionArg.setHelp("Specify where to place generated JavaScript files (ignored if jsFile is specified).");
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Classpath
		optionArg = new FlaggedOption("classpath");
		optionArg.setLongFlag("classpath");
		optionArg.setHelp("The JSweet transpilation classpath (candy jars). This classpath should at least contain the core candy.");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Module
		optionArg = new FlaggedOption("module");
		optionArg.setLongFlag("module");
		optionArg.setShortFlag('m');
		optionArg.setDefault("none");
		optionArg.setHelp("The module kind (none, commonjs, amd, system or umd).");
		optionArg.setStringParser(EnumeratedStringParser.getParser("none;commonjs;amd;system;umd"));
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Bundle
		switchArg = new Switch("bundle");
		switchArg.setLongFlag("bundle");
		switchArg.setShortFlag('b');
		switchArg.setHelp(
				"Bundle up the generated files and used modules to bundle files, which can be used in the browser. Bundles contain all the dependencies and are thus standalone. There is one bundle generated per entry (a Java 'main' method) in the program. By default, bundles are generated in the entry directory, but the output directory can be set by using the --bundlesDirectory option. NOTE: bundles will be generated only when choosing the commonjs module kind.");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		// Bundles directory
		optionArg = new FlaggedOption("bundlesDirectory");
		optionArg.setLongFlag("bundlesDirectory");
		optionArg.setHelp("Generate all the bundles (see option --bundle) within the given directory.");
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Debug
		switchArg = new Switch("debug");
		switchArg.setLongFlag("debug");
		switchArg.setShortFlag('d');
		switchArg.setHelp(
				"Set the transpiler to debug mode. In debug mode, source map files are generated so that it is possible to debug them in the browser. This feature is not available yet when using the --module option.");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		return jsap;
	}

	private static JSAPResult parseArgs(JSAP jsapSpec, String[] commandLineArgs) {
		System.out.println("JSweet transpiler version " + JSweetConfig.getVersionNumber() + " (build date: " + JSweetConfig.getBuildDate() + ")");
		System.out.println("Java compiler version: " + JavaCompiler.version());

		if (jsapSpec == null) {
			throw new IllegalStateException("no args, please call setArgs before");
		}
		JSAPResult arguments = jsapSpec.parse(commandLineArgs);
		if (!arguments.success()) {
			// print out specific error messages describing the problems
			for (java.util.Iterator<?> errs = arguments.getErrorMessageIterator(); errs.hasNext();) {
				System.out.println("Error: " + errs.next());
			}
		}
		if (!arguments.success() || arguments.getBoolean("help")) {
		}

		return arguments;
	}

	private static void printUsage(JSAP jsapSpec) {
		System.out.println("Command line options:");
		System.out.println(jsapSpec.getHelp());
		System.exit(-1);
	}

}
