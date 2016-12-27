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

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsweet.JSweetDefTranslatorConfig;

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
		logger.info("JSweet init candy project tool - version: " + JSweetDefTranslatorConfig.getVersionNumber());

		JSAP jsapSpec = defineArgs();
		JSAPResult jsapArgs = parseArgs(jsapSpec, args);
		if (!jsapArgs.success() || jsapArgs.getBoolean("help")) {
			printUsage(jsapSpec);
			System.exit(0);
		}
		if (jsapArgs.getBoolean("verbose")) {
			LogManager.getLogger("org.jsweet").setLevel(Level.ALL);
		}

		String artifactId = jsapArgs.getString("artifactId");
		String version = jsapArgs.getString("version");

		List<String> dependencyLocators = asList(defaultString(jsapArgs.getString("deps")).split(","));

		File outDir = jsapArgs.getFile("out");
		if (outDir == null || StringUtils.isBlank(outDir.getPath())) {
			outDir = new File("./target/candy-projects");
		}
		outDir.mkdirs();

		String projectName = "candy-" + artifactId;
		File projectDir = new File(outDir, projectName);

		logger.info("init candy project: \n" //
				+ "* projectName: " + projectName + "\n" //
				+ "* artifactId: " + artifactId + "\n" //
				+ "* version: " + version + "\n" //
				+ " to: " + projectDir.getAbsolutePath());

		FileUtils.copyDirectory(new File("templates/candy-project"), projectDir);

		logger.info("generating .project");
		File projectFile = new File(projectDir, ".project");
		String projectFileContent = FileUtils.readFileToString(projectFile) //
				.replace("${{PROJECT_NAME}}", projectName);
		FileUtils.write(projectFile, projectFileContent);

		logger.info("generating README");
		File readmeFile = new File(projectDir, "README.md");
		String readmeFileContent = FileUtils.readFileToString(projectFile) //
				.replace("${{CANDY_NAME}}", artifactId);
		FileUtils.write(readmeFile, readmeFileContent);

		logger.info("generating pom.xml");
		File pomFile = new File(projectDir, "pom.xml");
		String pomFileContent = FileUtils.readFileToString(pomFile) //
				.replace("${{ARTIFACT_ID}}", artifactId) //
				.replace("${{VERSION}}", version) //
				.replace("${{DEPENDENCIES}}", generateMavenXmlForDependencies(dependencyLocators));
		FileUtils.write(pomFile, pomFileContent);

		logger.info("***************************************************************************");
		logger.info("candy project " + projectName + " successfully created to " + projectDir);
		logger.info("***************************************************************************");
	}

	private static CharSequence generateMavenXmlForDependencies(List<String> dependencyLocators) {
		StringBuilder dependenciesBuilder = new StringBuilder();
		for (String dependencyLocator : dependencyLocators) {
			if (isBlank(dependencyLocator)) {
				continue;
			}

			String[] dependencyParts = dependencyLocator.split("[:]");
			if (dependencyParts.length != 2) {
				logger.warn("dependency format not correct: " + dependencyLocator);
				continue;
			}
			dependenciesBuilder.append("<dependency>\n");
			dependenciesBuilder.append("  <groupId>org.jsweet.candies</groupId>\n");
			dependenciesBuilder.append("  <artifactId>" + dependencyParts[0] + "</artifactId>\n");
			dependenciesBuilder.append("  <version>" + dependencyParts[1] + "</version>\n");
			dependenciesBuilder.append("</dependency>\n");
		}
		return dependenciesBuilder;
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
		optionArg.setShortFlag('a');
		optionArg.setStringParser(JSAP.STRING_PARSER);
		optionArg.setRequired(true);
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
		optionArg.setHelp("Specify the folder where to place generated candy project directory");
		optionArg.setStringParser(FileStringParser.getParser());
		jsap.registerParameter(optionArg);

		// Declared dependencies
		optionArg = new FlaggedOption("deps");
		optionArg.setLongFlag("deps");
		optionArg.setShortFlag('d');
		optionArg.setHelp(
				"Candy's dependencies (other candies on which this one relies) - ex: --deps=jquery:1.10.0-SNAPSHOT,jqueryui:1.9.0");
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
		System.out.println("init project options:");
		System.out.println(jsapSpec.getHelp());
	}
}
