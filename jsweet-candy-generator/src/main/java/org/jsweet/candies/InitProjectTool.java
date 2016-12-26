/* 
 * Copyright (C) 2015 Louis Grignon <louis.grignon@gmail.com>
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
package org.jsweet.candies;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultString;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.TypescriptDef2Java;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * executable tool to generate / package candies
 * 
 * @author Louis Grignon
 */
public class InitProjectTool {

	private static final Logger logger = Logger.getLogger(InitProjectTool.class);

	public static void main(String[] args) throws Throwable {

		logger.info("JSweet candy scaffold tool - version: " + JSweetDefTranslatorConfig.getVersionNumber());

		JSAP jsapSpec = defineArgs();
		JSAPResult jsapArgs = parseArgs(jsapSpec, args);

		if (!jsapArgs.success() || jsapArgs.getBoolean("help")) {
			printUsage(jsapSpec);
			System.exit(0);
		}

		if (jsapArgs.getBoolean("verbose")) {
			LogManager.getLogger("org.jsweet").setLevel(Level.ALL);
		}

		String candyName = jsapArgs.getString("name");
		String candyVersion = jsapArgs.getString("version");

		File outDir = jsapArgs.getFile("out");
		if (outDir == null || StringUtils.isBlank(outDir.getPath())) {
			outDir = new File("./candy-" + candyName + "-sources");
		}
		outDir.mkdirs();

		List<File> tsFiles = Stream.of(jsapArgs.getString("tsFiles").split(",")) //
				.map(File::new) //
				.filter(file -> file.exists()) //
				.collect(toList());
		List<File> tsDependencies = Stream.of(defaultString(jsapArgs.getString("tsDeps")).split(",")) //
				.map(File::new) //
				.filter(file -> file.exists()) //
				.collect(toList());

		logger.info("scaffolding candy: \n" //
				+ "* candyName: " + candyName + "\n" //
				+ "* candyVersion: " + candyVersion + "\n" //
				+ "* tsFiles: " + tsFiles + "\n" //
				+ "* tsDependencies: " + tsDependencies + "\n" //
				+ " to out: " + outDir.getAbsolutePath());

		TypescriptDef2Java.translate( //
				tsFiles, //
				tsDependencies, //
				outDir, //
				null, //
				false);

		logger.info("**************************************************************");
		logger.info("candy " + candyName + " successfully generated to " + outDir);
		logger.info("**************************************************************");
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

		// Candy name
		optionArg = new FlaggedOption("artifactId");
		optionArg.setLongFlag("artifactId");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(true);
		optionArg.setDefault("UTF-8");
		optionArg.setHelp("artifactId of the candy");
		jsap.registerParameter(optionArg);

		// Candy version
		optionArg = new FlaggedOption("version");
		optionArg.setLongFlag("version");
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setDefault("1");
		optionArg.setHelp("Version of the library defined by the candy");
		jsap.registerParameter(optionArg);

		// Output directory
		optionArg = new FlaggedOption("out");
		optionArg.setLongFlag("out");
		optionArg.setShortFlag('o');
		optionArg.setHelp("Specify where to place generated candy sources");
		optionArg.setStringParser(FileStringParser.getParser());
		jsap.registerParameter(optionArg);

		// ts files
		optionArg = new FlaggedOption("tsFiles");
		optionArg.setLongFlag("tsFiles");
		optionArg.setRequired(true);
		optionArg.setHelp("list of ts definition file paths describing the library");
		jsap.registerParameter(optionArg);

		// ts dependencies
		optionArg = new FlaggedOption("tsDeps");
		optionArg.setLongFlag("tsDeps");
		optionArg.setHelp(
				"list of ts definition file paths describing the library dependencies (ex lib.core.d.ts, ...)");
		jsap.registerParameter(optionArg);

		return jsap;
	}

	private static JSAPResult parseArgs(JSAP jsapSpec, String[] commandLineArgs) {
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

		return arguments;
	}

	private static void printUsage(JSAP jsapSpec) {
		System.out.println("Command line options:");
		System.out.println(jsapSpec.getHelp());
	}
}
