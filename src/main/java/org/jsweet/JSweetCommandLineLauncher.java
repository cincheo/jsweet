/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
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

import static org.jsweet.transpiler.TranspilationHandler.OUTPUT_LOGGER;

import java.io.File;
import java.util.LinkedList;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.Util;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * The command line launcher for the JSweet transpiler.
 * 
 * @author Renaud Pawlak
 */
public class JSweetCommandLineLauncher {

	private static final Logger logger = Logger.getLogger(JSweetCommandLineLauncher.class);

	private static int errorCount = 0;

	/**
	 * JSweet transpiler command line entry point. To use the JSweet transpiler
	 * from Java, see {@link JSweetTranspiler}.
	 */
	public static void main(String[] args) {
		try {
			JSAP jsapSpec = defineArgs();
			JSAPResult jsapArgs = parseArgs(jsapSpec, args);

			if (!jsapArgs.success()) {
				printUsage(jsapSpec);
				System.exit(-1);
			}

			if (jsapArgs.getBoolean("help")) {
				printUsage(jsapSpec);
			}

			if (jsapArgs.getBoolean("verbose")) {
				LogManager.getLogger("org.jsweet").setLevel(Level.ALL);
			}

			JSweetConfig.initClassPath(jsapArgs.getString("jdkHome"));

			String classPath = jsapArgs.getString("classpath");
			logger.info("classpath: " + classPath);

			ErrorCountTranspilationHandler transpilationHandler = new ErrorCountTranspilationHandler(new ConsoleTranspilationHandler());

			try {
				File tsOutputDir = jsapArgs.getFile("tsout");
				tsOutputDir.mkdirs();
				logger.info("ts output dir: " + tsOutputDir);

				File jsOutputDir = null;
				if (jsapArgs.getFile("jsout") != null) {
					jsOutputDir = jsapArgs.getFile("jsout");
					jsOutputDir.mkdirs();
				}
				logger.info("js output dir: " + jsOutputDir);

				File dtsOutputDir = null;
				if (jsapArgs.getFile("dtsout") != null) {
					dtsOutputDir = jsapArgs.getFile("dtsout");
				}

				File candiesJsOutputDir = null;
				if (jsapArgs.getFile("candiesJsOut") != null) {
					candiesJsOutputDir = jsapArgs.getFile("candiesJsOut");
				}

				File inputDir = new File(jsapArgs.getString("input"));
				logger.info("input dir: " + inputDir);

				LinkedList<File> files = new LinkedList<File>();
				Util.addFiles(".java", inputDir, files);

				JSweetTranspiler transpiler = new JSweetTranspiler(tsOutputDir, jsOutputDir, candiesJsOutputDir, classPath);

				transpiler.setBundle(jsapArgs.getBoolean("bundle"));
				transpiler.setNoRootDirectories(jsapArgs.getBoolean("noRootDirectories"));
				File bundlesDirectory = null;
				if (jsapArgs.getFile("bundlesDirectory") != null) {
					bundlesDirectory = jsapArgs.getFile("bundlesDirectory");
					bundlesDirectory.getParentFile().mkdirs();
				}
				logger.info("bundles directory: " + bundlesDirectory);
				transpiler.setBundlesDirectory(bundlesDirectory);
				transpiler.setPreserveSourceLineNumbers(jsapArgs.getBoolean("sourceMap"));
				transpiler.setModuleKind(ModuleKind.valueOf(jsapArgs.getString("module")));
				transpiler.setEncoding(jsapArgs.getString("encoding"));
				transpiler.setIgnoreAssertions(jsapArgs.getBoolean("ignoreAssertions"));
				transpiler.setGenerateDeclarations(jsapArgs.getBoolean("declaration"));
				transpiler.setGenerateJsFiles(!jsapArgs.getBoolean("tsOnly"));
				transpiler.setInterfaceTracking(!jsapArgs.getBoolean("disableJavaAddons"));
				transpiler.setSupportGetClass(!jsapArgs.getBoolean("disableJavaAddons"));
				transpiler.setSupportSaticLazyInitialization(!jsapArgs.getBoolean("disableJavaAddons"));
				transpiler.setGenerateDefinitions(jsapArgs.getBoolean("definitions"));
				transpiler.setDeclarationsOutputDir(dtsOutputDir);

				transpiler.transpile(transpilationHandler, SourceFile.toSourceFiles(files));
			} catch (NoClassDefFoundError error) {
				transpilationHandler.report(JSweetProblem.JAVA_COMPILER_NOT_FOUND, null, JSweetProblem.JAVA_COMPILER_NOT_FOUND.getMessage());
			}

			errorCount = transpilationHandler.getErrorCount();
			if (errorCount > 0) {
				OUTPUT_LOGGER.info("transpilation failed with " + errorCount + " error(s) and " + transpilationHandler.getWarningCount() + " warning(s)");
			} else {
				if (transpilationHandler.getWarningCount() > 0) {
					OUTPUT_LOGGER.info("transpilation completed with " + transpilationHandler.getWarningCount() + " warning(s)");
				} else {
					OUTPUT_LOGGER.info("transpilation successfully completed with no errors and no warnings");
				}
			}
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

		// Help
		switchArg = new Switch("help");
		switchArg.setShortFlag('h');
		switchArg.setLongFlag("help");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		// Verbose
		switchArg = new Switch("verbose");
		switchArg.setLongFlag("verbose");
		switchArg.setShortFlag('v');
		switchArg.setHelp("Turn on all levels of logging.");
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

		// JDK home directory
		optionArg = new FlaggedOption("jdkHome");
		optionArg.setLongFlag("jdkHome");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(false);
		optionArg.setHelp(
				"Set the JDK home directory to be used to find the Java compiler. If not set, the transpiler will try to use the JAVA_HOME environment variable. Note that the expected JDK version is greater or equals to version 8.");
		jsap.registerParameter(optionArg);

		// Input directory
		optionArg = new FlaggedOption("input");
		optionArg.setShortFlag('i');
		optionArg.setLongFlag("input");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(true);
		optionArg.setHelp("An input dir containing Java files to be transpiled.");
		jsap.registerParameter(optionArg);

		// Skip empty root dirs
		switchArg = new Switch("noRootDirectories");
		switchArg.setLongFlag("noRootDirectories");
		switchArg.setHelp(
				"Skip the root directories (i.e. packages annotated with @jsweet.lang.Root) so that the generated file hierarchy starts at the root directories rather than including the entire directory structure.");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

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

		// Do not generate JavaScript
		switchArg = new Switch("tsOnly");
		switchArg.setLongFlag("tsOnly");
		switchArg.setHelp("Tells the transpiler to not compile the TypeScript output (let an external TypeScript compiler do so).");
		jsap.registerParameter(switchArg);

		// Do not generate JavaScript
		switchArg = new Switch("disableJavaAddons");
		switchArg.setLongFlag("disableJavaAddons");
		switchArg.setHelp(
				"Tells the transpiler disable runtime addons (instanceof, overloading, class name access, static initialization [...] will not be fully supported).");
		jsap.registerParameter(switchArg);

		// Do not generate JavaScript
		switchArg = new Switch("definitions");
		switchArg.setLongFlag("definitions");
		switchArg.setHelp(
				"Tells the transpiler to generate definitions from def.* packages in d.ts definition files. Regular program elements will be ignored and no plain .ts files will be generated. The output directory is given by the tsout option. This option can be used to create candies for existing JavaScript libraries and must not be confused with the 'declaration' option, that generates the definitions along with a program written in JSweet.");
		jsap.registerParameter(switchArg);

		// Generates declarations
		switchArg = new Switch("declaration");
		switchArg.setLongFlag("declaration");
		switchArg.setHelp("Tells the transpiler to generate the d.ts files along with the js files, so that other programs can use them to compile.");
		jsap.registerParameter(switchArg);

		// Declarations output directory
		optionArg = new FlaggedOption("dtsout");
		optionArg.setLongFlag("dtsout");
		optionArg.setHelp(
				"Specify where to place generated d.ts files when the declaration option is set (by default, d.ts files are generated in the JavaScript output directory - next to the corresponding js files).");
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Candies javascript output directory
		optionArg = new FlaggedOption("candiesJsOut");
		optionArg.setLongFlag("candiesJsOut");
		optionArg.setDefault("js/candies");
		optionArg.setHelp("Specify where to place extracted JavaScript files from candies.");
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
		switchArg = new Switch("sourceMap");
		switchArg.setLongFlag("sourceMap");
		switchArg.setHelp(
				"Set the transpiler to generate source map files for the Java files, so that it is possible to debug them in the browser. This feature is not available yet when using the --module option. Currently, when this option is on, the generated TypeScript file is not pretty printed in a programmer-friendly way (disable it in order to generate readable TypeScript code).");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		// Ignore assertions
		switchArg = new Switch("ignoreAssertions");
		switchArg.setLongFlag("ignoreAssertions");
		switchArg.setHelp("Set the transpiler to ignore 'assert' statements, i.e. no code is generated for assertions.");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		return jsap;
	}

	private static JSAPResult parseArgs(JSAP jsapSpec, String[] commandLineArgs) {
		OUTPUT_LOGGER.info("JSweet transpiler version " + JSweetConfig.getVersionNumber() + " (build date: " + JSweetConfig.getBuildDate() + ")");

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
	}

}
