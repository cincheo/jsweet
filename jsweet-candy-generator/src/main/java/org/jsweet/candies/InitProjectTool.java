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

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jsweet.CandyTool.getResourceFile;

import java.io.Console;
import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.util.ProcessUtil;
import org.jsweet.util.Server;

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

		FileUtils.copyDirectory(getResourceFile("templates/candy-project"), projectDir);

		logger.info("generating README");
		File readmeFile = new File(projectDir, "README.md");
		String readmeFileContent = FileUtils.readFileToString(readmeFile) //
				.replace("${{CANDY_NAME}}", artifactId).replace("${{CANDY_VERSION}}", version);
		FileUtils.write(readmeFile, readmeFileContent);

		logger.info("generating pom.xml");
		File pomFile = new File(projectDir, "pom.xml");
		String pomFileContent = FileUtils.readFileToString(pomFile) //
				.replace("${{ARTIFACT_ID}}", artifactId) //
				.replace("${{VERSION}}", version) //
				.replace("${{DEPENDENCIES}}", generateMavenXmlForDependencies(dependencyLocators));
		FileUtils.write(pomFile, pomFileContent);

		if (BooleanUtils.toBoolean(jsapArgs.getString("createGitHubRepository"))) {
			String gitHubUser = jsapArgs.getString("gitHubUser");
			String gitHubPass = jsapArgs.getString("gitHubPass");

			Console console = System.console();
			if (isBlank(gitHubUser)) {
				gitHubUser = console.readLine("GitHub username: ");
			}

			if (isBlank(gitHubPass)) {
				gitHubPass = new String(console.readPassword("GitHub password for " + gitHubUser + ": "));
			}

			createGitHubRepo(projectName, "Java API bridge for " + artifactId + " (JSweet candy)", gitHubUser,
					gitHubPass);

			ProcessUtil.init();
			ProcessUtil.runCmd(projectDir, (out) -> {
				logger.info("git: " + out);
			}, "git init && git remote add origin https://github.com/jsweet-candies/" + projectName);
		}

		logger.info("***************************************************************************");
		logger.info("candy project " + projectName + " successfully created to " + projectDir);
		logger.info("***************************************************************************");
	}

	private static void createGitHubRepo(String repoName, String repoDescription, String gitHubUser,
			String gitHubPassword) {
		Server gitHubServer = new Server("https", "api.github.com", 443);
		gitHubServer.addPermanentHeader("Accept", "application/vnd.github.v3+json");
		gitHubServer.setCredentials(gitHubUser, gitHubPassword);
		gitHubServer.post("/orgs/jsweet-candies/repos", //
				"name", repoName, //
				"description", repoDescription);
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
			dependenciesBuilder.append("\t\t<dependency>\n");
			dependenciesBuilder.append("\t\t\t<groupId>org.jsweet.candies.ext</groupId>\n");
			dependenciesBuilder.append("\t\t\t<artifactId>" + dependencyParts[0] + "</artifactId>\n");
			dependenciesBuilder.append("\t\t\t<version>" + dependencyParts[1] + "</version>\n");
			dependenciesBuilder.append("\t\t</dependency>\n");
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

		// Create GitHub repository
		optionArg = new FlaggedOption("createGitHubRepository");
		optionArg.setShortFlag('r');
		optionArg.setLongFlag("createGitHubRepository");
		optionArg.setHelp(
				"If true, automatically creates a GitHub repository for the candy - default: true \n provide gitHubUser and gitHubPass if you don't want to be prompted");
		optionArg.setDefault("false");
		jsap.registerParameter(optionArg);

		// GitHub username
		optionArg = new FlaggedOption("gitHubUser");
		optionArg.setLongFlag("gitHubUser");
		optionArg.setShortFlag('u');
		optionArg.setHelp("GitHub username");
		jsap.registerParameter(optionArg);

		// GitHub password
		optionArg = new FlaggedOption("gitHubPass");
		optionArg.setLongFlag("gitHubPass");
		optionArg.setShortFlag('p');
		optionArg.setHelp("GitHub password");
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
