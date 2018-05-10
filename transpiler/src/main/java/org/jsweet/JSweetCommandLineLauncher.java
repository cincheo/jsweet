/* 
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet;

import static org.jsweet.transpiler.TranspilationHandler.OUTPUT_LOGGER;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsweet.transpiler.EcmaScriptComplianceLevel;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetOptions;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.ModuleResolution;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.ProcessUtil;
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
 * <pre>
 Command line options:
  [-h|--help]

  [-w|--watch]
        Start a process that watches the input directories for changes and
        re-run transpilation on-the-fly.

  [-v|--verbose]
        Turn on general information logging (INFO LEVEL)
        
  [-V|--veryVerbose]
        Turn on all levels of logging

  [--encoding <encoding>]
        Force the Java compiler to use a specific encoding (UTF-8, UTF-16, ...).
        (default: UTF-8)

  [--jdkHome <jdkHome>]
        Set the JDK home directory to be used to find the Java compiler. If not
        set, the transpiler will try to use the JAVA_HOME environment variable.
        Note that the expected JDK version is greater or equals to version 8.

  (-i|--input) input1:input2:...:inputN 
        An input directory (or column-separated input directories) containing
        Java files to be transpiled. Java files will be recursively looked up in
        sub-directories. Inclusion and exclusion patterns can be defined with
        the 'includes' and 'excludes' options.

  [--includes includes1:includes2:...:includesN ]
        A column-separated list of expressions matching files to be included
        (relatively to the input directory).

  [--excludes excludes1:excludes2:...:excludesN ]
        A column-separated list of expressions matching files to be excluded
        (relatively to the input directory).

  [(-d|--defInput) defInput1:defInput2:...:defInputN ]
        An input directory (or column-separated input directories) containing
        TypeScript definition files (*.d.ts) to be used for transpilation.
        Definition files will be recursively looked up in sub-diredctories.

  [--noRootDirectories]
        Skip the root directories (i.e. packages annotated with
        @jsweet.lang.Root) so that the generated file hierarchy starts at the
        root directories rather than including the entire directory structure.

  [--tsout <tsout>]
        Specify where to place generated TypeScript files. (default: .ts)

  [(-o|--jsout) <jsout>]
        Specify where to place generated JavaScript files (ignored if jsFile is
        specified). (default: js)

  [--disableSinglePrecisionFloats]
        By default, for a target version >=ES5, JSweet will force Java floats to
        be mapped to JavaScript numbers that will be constrained with ES5
        Math.fround function. If this option is true, then the calls to
        Math.fround are erased and the generated program will use the JavaScript
        default precision (double precision).

  [--tsOnly]
        Do not compile the TypeScript output (let an external TypeScript
        compiler do so).
  
  [--ignoreDefinitions]
        Ignore definitions from def.* packages, so that they are not generated
        in d.ts definition files. If this option is not set, the transpiler
        generates d.ts definition files in the directory given by the tsout
        option.

  [--declaration]
        Generate the d.ts files along with the js files, so that other programs
        can use them to compile.

  [--dtsout <dtsout>]
        Specify where to place generated d.ts files when the declaration option
        is set (by default, d.ts files are generated in the JavaScript output
        directory - next to the corresponding js files).

  [--candiesJsOut <candiesJsOut>]
        Specify where to place extracted JavaScript files from candies.
        (default: js/candies)

  [--sourceRoot <sourceRoot>]
        Specify the location where debugger should locate Java files instead of
        source locations. Use this flag if the sources will be located at
        run-time in a different location than that at design-time. The location
        specified will be embedded in the sourceMap to direct the debugger where
        the source files will be located.

  [--classpath <classpath>]
        The JSweet transpilation classpath (candy jars). This classpath should
        at least contain the core candy.

  [(-m|--module) <module>]
        The module kind (none, commonjs, amd, system, umd, es2015). (default:
        none)

  [--moduleResolution <moduleResolution>]
        The module resolution strategy (classic, node). (default: classic)

  [-b|--bundle]
        Bundle up all the generated code in a single file, which can be used in
        the browser. The bundle files are called 'bundle.ts', 'bundle.d.ts', or
        'bundle.js' depending on the kind of generated code. NOTE: bundles are
        not compatible with any module kind other than 'none'.

  [(-f|--factoryClassName) <factoryClassName>]
        Use the given factory to tune the default transpiler behavior.

  [--sourceMap]
        Generate source map files for the Java files, so that it is possible to
        debug Java files directly with a debugger that supports source maps
        (most JavaScript debuggers).

  [--enableAssertions]
        Java 'assert' statements are transpiled as runtime JavaScript checks.

  [--header <header>]
        A file that contains a header to be written at the beginning of each
        generated file. If left unspecified, JSweet will generate a default
        header.

  [--workingDir <workingDir>]
        The directory JSweet uses to store temporary files such as extracted
        candies. JSweet uses '.jsweet' if left unspecified.

  [--targetVersion <targetVersion>]
        The EcmaScript target (JavaScript) version. Possible values: [ES3, ES5,
        ES6] (default: ES3)

  [--extraSystemPath <extraSystemPath>]
        Allow an extra path to be added to the system path.
 * </pre>
 * 
 * @author Renaud Pawlak
 */
public class JSweetCommandLineLauncher {

	private static final Logger logger = Logger.getLogger(JSweetCommandLineLauncher.class);

	private static int errorCount = 0;

	private static Pattern toPattern(String expression) {
		if (!expression.contains("*") && !expression.contains(".")) {
			expression += "*";
		}
		return Pattern.compile(expression.replace(".", "\\.").replace("*", ".*"));
	}

	private JSweetCommandLineLauncher() {
	}

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

			LogManager.getLogger("org.jsweet").setLevel(Level.WARN);
			
			if (jsapArgs.getBoolean("verbose")) {
				LogManager.getLogger("org.jsweet").setLevel(Level.INFO);
			}
			
			if (jsapArgs.getBoolean("veryVerbose")) {
				LogManager.getLogger("org.jsweet").setLevel(Level.ALL);
			}

			JSweetConfig.initClassPath(jsapArgs.getString("jdkHome"));

			JSweetTranspilationTask transpilationTask = new JSweetTranspilationTask(jsapArgs);
			transpilationTask.run();
			if (jsapArgs.getBoolean("watch")) {
				new JSweetFileWatcher(transpilationTask).execute();
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

		// Watch
		switchArg = new Switch("watch");
		switchArg.setShortFlag('w');
		switchArg.setLongFlag("watch");
		switchArg.setDefault("false");
		switchArg.setHelp(
				"Start a process that watches the input directories for changes and re-run transpilation on-the-fly.");
		jsap.registerParameter(switchArg);

		// Verbose
		switchArg = new Switch("verbose");
		switchArg.setLongFlag("verbose");
		switchArg.setShortFlag('v');
		switchArg.setHelp("Turn on general information logging (INFO LEVEL)");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		switchArg = new Switch("veryVerbose");
		switchArg.setLongFlag("veryVerbose");
		switchArg.setShortFlag('V');
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

		// Input directories
		optionArg = new FlaggedOption("input");
		optionArg.setShortFlag('i');
		optionArg.setLongFlag("input");
		optionArg.setList(true);
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setListSeparator(':');
		optionArg.setRequired(true);
		optionArg.setHelp(
				"An input directory (or column-separated input directories) containing Java files to be transpiled. Java files will be recursively looked up in sub-directories. Inclusion and exclusion patterns can be defined with the 'includes' and 'excludes' options.");
		jsap.registerParameter(optionArg);

		// Included files
		optionArg = new FlaggedOption("includes");
		optionArg.setLongFlag("includes");
		optionArg.setList(true);
		optionArg.setListSeparator(':');
		optionArg.setHelp(
				"A column-separated list of expressions matching files to be included (relatively to the input directory).");
		jsap.registerParameter(optionArg);

		// Excluded files
		optionArg = new FlaggedOption("excludes");
		optionArg.setLongFlag("excludes");
		optionArg.setList(true);
		optionArg.setListSeparator(':');
		optionArg.setHelp(
				"A column-separated list of expressions matching files to be excluded (relatively to the input directory).");
		jsap.registerParameter(optionArg);

		// Definition directories
		optionArg = new FlaggedOption("defInput");
		optionArg.setShortFlag('d');
		optionArg.setLongFlag("defInput");
		optionArg.setList(true);
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setListSeparator(':');
		optionArg.setRequired(false);
		optionArg.setHelp(
				"An input directory (or column-separated input directories) containing TypeScript definition files (*.d.ts) to be used for transpilation. Definition files will be recursively looked up in sub-diredctories.");
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

		// Disable single precision floats
		switchArg = new Switch("disableSinglePrecisionFloats");
		switchArg.setLongFlag("disableSinglePrecisionFloats");
		switchArg.setHelp(
				"By default, for a target version >=ES5, JSweet will force Java floats to be mapped to JavaScript numbers that will be constrained with ES5 Math.fround function. If this option is true, then the calls to Math.fround are erased and the generated program will use the JavaScript default precision (double precision).");
		jsap.registerParameter(switchArg);

		// Do not generate JavaScript
		switchArg = new Switch("tsOnly");
		switchArg.setLongFlag("tsOnly");
		switchArg.setHelp("Do not compile the TypeScript output (let an external TypeScript compiler do so).");
		jsap.registerParameter(switchArg);

		// Do not generate d.ts files that correspond to def.* packages
		switchArg = new Switch("ignoreDefinitions");
		switchArg.setLongFlag("ignoreDefinitions");
		switchArg.setHelp(
				"Ignore definitions from def.* packages, so that they are not generated in d.ts definition files. If this option is not set, the transpiler generates d.ts definition files in the directory given by the tsout option.");
		jsap.registerParameter(switchArg);

		// Generates declarations
		switchArg = new Switch("declaration");
		switchArg.setLongFlag("declaration");
		switchArg.setHelp(
				"Generate the d.ts files along with the js files, so that other programs can use them to compile.");
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

		// Source root directory for source maps
		optionArg = new FlaggedOption("sourceRoot");
		optionArg.setLongFlag("sourceRoot");
		optionArg.setHelp(
				"Specify the location where debugger should locate Java files instead of source locations. Use this flag if the sources will be located at run-time in a different location than that at design-time. The location specified will be embedded in the sourceMap to direct the debugger where the source files will be located.");
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Classpath
		optionArg = new FlaggedOption("classpath");
		optionArg.setLongFlag("classpath");
		optionArg.setHelp(
				"The JSweet transpilation classpath (candy jars). This classpath should at least contain the core candy.");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Module
		optionArg = new FlaggedOption("module");
		optionArg.setLongFlag("module");
		optionArg.setShortFlag('m');
		optionArg.setDefault("none");
		optionArg.setHelp("The module kind (none, commonjs, amd, system, umd, es2015).");
		optionArg.setStringParser(EnumeratedStringParser.getParser("none;commonjs;amd;system;umd;es2015"));
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Module resolution
		optionArg = new FlaggedOption(JSweetOptions.moduleResolution);
		optionArg.setLongFlag(JSweetOptions.moduleResolution);
		optionArg.setDefault(ModuleResolution.classic.name());
		optionArg.setHelp("The module resolution strategy (classic, node).");
		optionArg.setStringParser(EnumeratedStringParser.getParser("classic;node"));
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Bundle
		switchArg = new Switch("bundle");
		switchArg.setLongFlag("bundle");
		switchArg.setShortFlag('b');
		switchArg.setHelp(
				"Bundle up all the generated code in a single file, which can be used in the browser. The bundle files are called 'bundle.ts', 'bundle.d.ts', or 'bundle.js' depending on the kind of generated code. NOTE: bundles are not compatible with any module kind other than 'none'.");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		// Factory class name
		optionArg = new FlaggedOption("factoryClassName");
		optionArg.setLongFlag("factoryClassName");
		optionArg.setShortFlag('f');
		optionArg.setHelp("Use the given factory to tune the default transpiler behavior.");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// // Adapters
		// optionArg = new FlaggedOption("adapters");
		// optionArg.setLongFlag("adapters");
		// optionArg.setList(true);
		// optionArg.setStringParser(JSAP.STRING_PARSER);
		// optionArg.setListSeparator(':');
		// optionArg.setRequired(false);
		// optionArg.setHelp(
		// "A column-separated list of adapter class names (fully qualified) to
		// be used to extend the transpiler behavior. As the name suggests, an
		// adapter is an object that adapts the TypeScript default printer in
		// order to tune the generated code. All the adapter classes must be
		// accessible within the 'jsweet_extension' directory to be created at
		// the root of the transpiled project. The adapter classes can be
		// provided as Java class files (*.class) or Java source files (*.java).
		// In the latter case, they will be on-the-fly compiled by JSweet.");
		// jsap.registerParameter(optionArg);

		// Debug
		switchArg = new Switch("sourceMap");
		switchArg.setLongFlag("sourceMap");
		switchArg.setHelp(
				"Generate source map files for the Java files, so that it is possible to debug Java files directly with a debugger that supports source maps (most JavaScript debuggers).");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		// Enable assertions
		switchArg = new Switch("enableAssertions");
		switchArg.setLongFlag("enableAssertions");
		switchArg.setHelp("Java 'assert' statements are transpiled as runtime JavaScript checks.");
		switchArg.setDefault("false");
		jsap.registerParameter(switchArg);

		// Header file
		optionArg = new FlaggedOption("header");
		optionArg.setLongFlag("header");
		optionArg.setHelp(
				"A file that contains a header to be written at the beginning of each generated file. If left unspecified, JSweet will generate a default header.");
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Working directory
		optionArg = new FlaggedOption("workingDir");
		optionArg.setLongFlag("workingDir");
		optionArg.setHelp(
				"The directory JSweet uses to store temporary files such as extracted candies. JSweet uses '.jsweet' if left unspecified.");
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		optionArg = new FlaggedOption("targetVersion");
		optionArg.setLongFlag("targetVersion");
		optionArg.setHelp("The EcmaScript target (JavaScript) version. Possible values: "
				+ Arrays.asList(EcmaScriptComplianceLevel.values()));
		optionArg.setDefault("ES3");
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		// Extra system path
		optionArg = new FlaggedOption(JSweetOptions.extraSystemPath);
		optionArg.setLongFlag(JSweetOptions.extraSystemPath);
		optionArg.setHelp("Allow an extra path to be added to the system path.");
		optionArg.setStringParser(FileStringParser.getParser());
		optionArg.setRequired(false);
		jsap.registerParameter(optionArg);

		return jsap;
	}

	private static JSAPResult parseArgs(JSAP jsapSpec, String[] commandLineArgs) {
		OUTPUT_LOGGER.info("JSweet transpiler version " + JSweetConfig.getVersionNumber() + " (build date: "
				+ JSweetConfig.getBuildDate() + ")");

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

	private static class JSweetTranspilationTask implements TranspilationTask {

		private JSAPResult jsapArgs;
		private List<File> inputDirList;
		private LinkedList<File> javaInputFiles;

		public JSweetTranspilationTask(JSAPResult jsapArgs) {
			this.jsapArgs = jsapArgs;
			inputDirList = Arrays.asList(jsapArgs.getFileArray("input"));
			logger.info("input dirs: " + inputDirList);

		}

		@Override
		public List<File> getInputDirList() {
			return inputDirList;
		}

		@Override
		public void run() throws Exception {
			String classPath = jsapArgs.getString("classpath");
			logger.info("classpath: " + classPath);

			ErrorCountTranspilationHandler transpilationHandler = new ErrorCountTranspilationHandler(
					new ConsoleTranspilationHandler());

			try {

				String[] included = jsapArgs.getStringArray("includes");
				String[] excluded = jsapArgs.getStringArray("excludes");

				List<Pattern> includedPatterns = included == null ? null
						: Arrays.asList(included).stream().map(s -> toPattern(s)).collect(Collectors.toList());
				List<Pattern> excludedPatterns = excluded == null ? null
						: Arrays.asList(excluded).stream().map(s -> toPattern(s)).collect(Collectors.toList());

				logger.info("included: " + includedPatterns);
				logger.info("excluded: " + excludedPatterns);

				javaInputFiles = new LinkedList<File>();

				for (File inputDir : inputDirList) {
					Util.addFiles(f -> {
						String path = inputDir.toURI().relativize(f.toURI()).getPath();
						if (path.endsWith(".java")) {
							if (includedPatterns == null || includedPatterns.isEmpty() || includedPatterns != null
									&& includedPatterns.stream().anyMatch(p -> p.matcher(path).matches())) {
								if (excludedPatterns != null && !excludedPatterns.isEmpty()
										&& excludedPatterns.stream().anyMatch(p -> p.matcher(path).matches())) {
									return false;
								}
								return true;
							}
						}
						return false;
					}, inputDir, javaInputFiles);
				}

				File tsOutputDir = null;
				if (jsapArgs.userSpecified("tsout") && jsapArgs.getFile("tsout") != null) {
					tsOutputDir = jsapArgs.getFile("tsout");
					tsOutputDir.mkdirs();
				}
				logger.info("ts output dir: " + tsOutputDir);

				File jsOutputDir = null;
				if (jsapArgs.userSpecified("jsout") && jsapArgs.getFile("jsout") != null) {
					jsOutputDir = jsapArgs.getFile("jsout");
					jsOutputDir.mkdirs();
				}
				logger.info("js output dir: " + jsOutputDir);

				File dtsOutputDir = null;
				if (jsapArgs.userSpecified("dtsout") && jsapArgs.getFile("dtsout") != null) {
					dtsOutputDir = jsapArgs.getFile("dtsout");
				}

				File candiesJsOutputDir = null;
				if (jsapArgs.userSpecified("candiesJsOut") && jsapArgs.getFile("candiesJsOut") != null) {
					candiesJsOutputDir = jsapArgs.getFile("candiesJsOut");
				}

				File sourceRootDir = null;
				if (jsapArgs.userSpecified("sourceRoot") && jsapArgs.getFile("sourceRoot") != null) {
					sourceRootDir = jsapArgs.getFile("sourceRoot");
				}

				JSweetFactory factory = null;
				String factoryClassName = jsapArgs.getString("factoryClassName");

				if (factoryClassName != null) {
					try {
						factory = (JSweetFactory) Thread.currentThread().getContextClassLoader()
								.loadClass(factoryClassName).newInstance();
					} catch (Exception e) {
						try {
							// try forName just in case
							factory = (JSweetFactory) Class.forName(factoryClassName).newInstance();
						} catch (Exception e2) {
							throw new RuntimeException(
									"cannot find or instantiate factory class: " + factoryClassName
											+ " (make sure the class is in the plugin's classpath and that it defines an empty public constructor)",
									e2);
						}
					}
				}

				if (factory == null) {
					factory = new JSweetFactory();
				}

				JSweetTranspiler transpiler = new JSweetTranspiler(factory, jsapArgs.getFile("workingDir"), tsOutputDir,
						jsOutputDir, candiesJsOutputDir, classPath);

				if (jsapArgs.userSpecified("bundle")) {
					transpiler.setBundle(jsapArgs.getBoolean("bundle"));
				}
				if (jsapArgs.userSpecified("noRootDirectories")) {
					transpiler.setNoRootDirectories(jsapArgs.getBoolean("noRootDirectories"));
				}
				if (jsapArgs.userSpecified("sourceMap")) {
					transpiler.setGenerateSourceMaps(jsapArgs.getBoolean("sourceMap"));
				}
				if (sourceRootDir != null) {
					transpiler.setSourceRoot(sourceRootDir);
				}
				if (jsapArgs.userSpecified("module")) {
					transpiler.setModuleKind(ModuleKind.valueOf(jsapArgs.getString("module")));
				}
				if (jsapArgs.userSpecified(JSweetOptions.moduleResolution)) {
					transpiler.setModuleResolution(
							ModuleResolution.valueOf(jsapArgs.getString(JSweetOptions.moduleResolution)));
				}
				if (jsapArgs.userSpecified("encoding")) {
					transpiler.setEncoding(jsapArgs.getString("encoding"));
				}
				if (jsapArgs.userSpecified("enableAssertions")) {
					transpiler.setIgnoreAssertions(!jsapArgs.getBoolean("enableAssertions"));
				}
				if (jsapArgs.userSpecified("declaration")) {
					transpiler.setGenerateDeclarations(jsapArgs.getBoolean("declaration"));
				}
				if (jsapArgs.userSpecified("tsOnly")) {
					transpiler.setGenerateJsFiles(!jsapArgs.getBoolean("tsOnly"));
				}
				if (jsapArgs.userSpecified("ignoreDefinitions")) {
					transpiler.setGenerateDefinitions(!jsapArgs.getBoolean("ignoreDefinitions"));
				}
				if (jsapArgs.userSpecified("dtsOutputDir")) {
					transpiler.setDeclarationsOutputDir(dtsOutputDir);
				}
				if (jsapArgs.userSpecified("header")) {
					transpiler.setHeaderFile(jsapArgs.getFile("header"));
				}
				if (jsapArgs.userSpecified("targetVersion")) {
					transpiler.setEcmaTargetVersion(
							JSweetTranspiler.getEcmaTargetVersion(jsapArgs.getString("targetVersion")));
				}
				if (jsapArgs.userSpecified("disableSinglePrecisionFloats")) {
					transpiler.setDisableSinglePrecisionFloats(jsapArgs.getBoolean("disableSinglePrecisionFloats"));
				}
				if (jsapArgs.userSpecified(JSweetOptions.extraSystemPath)) {
					ProcessUtil.addExtraPath(jsapArgs.getString(JSweetOptions.extraSystemPath));
				}

				if (tsOutputDir != null) {
					transpiler.setTsOutputDir(tsOutputDir);
				}
				if (jsOutputDir != null) {
					transpiler.setJsOutputDir(jsOutputDir);
				}

				// transpiler.setAdapters(Arrays.asList(jsapArgs.getStringArray("adapters")));

				List<File> files = Arrays.asList(jsapArgs.getFileArray("defInput"));
				logger.info("definition input dirs: " + files);

				for (File f : files) {
					transpiler.addTsDefDir(f);
				}

				transpiler.transpile(transpilationHandler, SourceFile.toSourceFiles(javaInputFiles));
			} catch (NoClassDefFoundError error) {
				transpilationHandler.report(JSweetProblem.JAVA_COMPILER_NOT_FOUND, null,
						JSweetProblem.JAVA_COMPILER_NOT_FOUND.getMessage());
			}

			errorCount = transpilationHandler.getErrorCount();
			if (errorCount > 0) {
				OUTPUT_LOGGER.info("transpilation failed with " + errorCount + " error(s) and "
						+ transpilationHandler.getWarningCount() + " warning(s)");
			} else {
				if (transpilationHandler.getWarningCount() > 0) {
					OUTPUT_LOGGER.info(
							"transpilation completed with " + transpilationHandler.getWarningCount() + " warning(s)");
				} else {
					OUTPUT_LOGGER.info("transpilation successfully completed with no errors and no warnings");
				}
			}

		}

	}

}
