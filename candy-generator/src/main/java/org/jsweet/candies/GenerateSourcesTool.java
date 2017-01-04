/* 
 * JSweet candy tools - http://www.jsweet.org
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
package org.jsweet.candies;

import static org.jsweet.CandyTool.getResourceFile;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultString;

import java.io.File;
import java.net.URISyntaxException;
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
public class GenerateSourcesTool {

	public static enum JSweetCoreVersion {
		ES5, ES6, NONE;

		public static JSweetCoreVersion parse(String value) {
			try {
				return valueOf(value.toUpperCase());
			} catch (Exception e) {
				return ES5;
			}
		}
	}

	private static final Logger logger = Logger.getLogger(GenerateSourcesTool.class);

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

		File outDir = jsapArgs.getFile("out");
		if (outDir == null || StringUtils.isBlank(outDir.getPath())) {
			outDir = new File("./candy-" + candyName + "-sources");
		}
		outDir.mkdirs();

		List<File> tsFiles = Stream.of(jsapArgs.getString("tsFiles").split(",")) //
				.filter(StringUtils::isNotBlank) //
				.map(File::new) //
				.collect(toList());
		List<File> tsDependencies = Stream.of(defaultString(jsapArgs.getString("tsDeps")).split(",")) //
				.filter(StringUtils::isNotBlank) //
				.map(File::new) //
				.collect(toList());

		JSweetCoreVersion coreVersion = JSweetCoreVersion.parse(jsapArgs.getString("coreVersion"));

		addCoreDependencies(coreVersion, tsDependencies);

		logger.info("scaffolding candy: \n" //
				+ "* candyName: " + candyName + "\n" //
				+ "* tsFiles: " + tsFiles + "\n" //
				+ "* tsDependencies: " + tsDependencies + "\n" //
				+ "* coreVersion: " + coreVersion + "\n" //
				+ " to out: " + outDir.getAbsolutePath());

		TypescriptDef2Java.translate( //
				tsFiles, //
				tsDependencies, //
				outDir, //
				null, //
				false, //
				false);

		logger.info("**************************************************************");
		logger.info("candy " + candyName + " successfully generated to " + outDir);
		logger.info("**************************************************************");
	}

	private static void addCoreDependencies(JSweetCoreVersion coreVersion, List<File> tsDependencies)
			throws URISyntaxException {
		switch (coreVersion) {
		case ES5:
		default:
			tsDependencies.add(getResourceFile("typings/lib.core/lib.core.d.ts"));
			tsDependencies.add(getResourceFile("typings/lib.core/lib.core.ext.d.ts"));
			tsDependencies.add(getResourceFile("typings/lib.core/lib.dom.d.ts"));
			break;
		case ES6:
			tsDependencies.add(getResourceFile("typings/lib.core/lib.es6.d.ts"));
			tsDependencies.add(getResourceFile("typings/lib.core/lib.core.ext.d.ts"));
			break;
		case NONE:
			break;
		}
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
		optionArg = new FlaggedOption("name");
		optionArg.setLongFlag("name");
		optionArg.setShortFlag('n');
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(true);
		optionArg.setHelp("Name / artifactId of the candy");
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
		optionArg.setShortFlag('f');
		optionArg.setRequired(true);
		optionArg.setHelp("list of ts definition file paths describing the library");
		jsap.registerParameter(optionArg);

		// ts dependencies
		optionArg = new FlaggedOption("tsDeps");
		optionArg.setLongFlag("tsDeps");
		optionArg.setShortFlag('d');
		optionArg.setHelp(
				"list of ts definition file paths describing the library dependencies (ex lib.core.d.ts, ...)");
		jsap.registerParameter(optionArg);

		// ES version
		optionArg = new FlaggedOption("coreVersion");
		optionArg.setLongFlag("coreVersion");
		optionArg.setShortFlag('c');
		optionArg.setDefault("es5");
		optionArg.setHelp(
				"Version of jsweet-core (ES version) to be used - one of es5, es6 - will be included by default for generation");
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
		System.out.println("scaffold options:");
		System.out.println(jsapSpec.getHelp());
	}
}
