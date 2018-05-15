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
package org.jsweet.transpiler.candy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.TranspilationHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The candy processor extracts and processes what is required from the candy
 * jars. It can include:
 * 
 * <ul>
 * <li>embedded TypeScript definition files (*.d.ts)</li>
 * <li>embedded JavaScript</li>
 * </ul>
 * 
 * @author Louis Grignon
 */
public class CandyProcessor {

	private static final Logger logger = Logger.getLogger(CandyProcessor.class);
	private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private String classPath;

	/**
	 * The name of the directory that will contain the candies.
	 */
	public static final String CANDIES_DIR_NAME = "candies";
	/**
	 * This directory will contain the sources.
	 */
	public static final String CANDIES_SOURCES_DIR_NAME = CANDIES_DIR_NAME + File.separator + "src";
	/**
	 * The name of the file that stores processed candies info.
	 */
	public static final String CANDIES_STORE_FILE_NAME = CANDIES_DIR_NAME + File.separator
			+ CandyStore.class.getSimpleName() + ".json";
	/**
	 * The name of the directory that contains the TypeScript source files.
	 */
	public static final String CANDIES_TSDEFS_DIR_NAME = CANDIES_DIR_NAME + File.separator
			+ JSweetConfig.TS_LIBS_DIR_NAME;
	/**
	 * Default directory for extracted candies' javascript.
	 */
	private static final String CANDIES_DEFAULT_JS_DIR_NAME = CANDIES_DIR_NAME + File.separator + "js";

	private File candiesSourceDir;
	private File candyStoreFile;
	private File candiesTsdefsDir;
	private File candiesJavascriptOutDir;
	private File workingDir;
	private List<File> extractedJsFiles = new LinkedList<>();

	/**
	 * Create a candies processor.
	 * 
	 * @param workingDir
	 *            the directory where the processor will save all cache and
	 *            temporary data for processing
	 * @param classPath
	 *            the classpath where the processor will seek for JSweet candies
	 * @param extractedCandiesJavascriptDir
	 *            see JSweetTranspiler.extractedCandyJavascriptDir
	 */
	public CandyProcessor(File workingDir, String classPath, File extractedCandiesJavascriptDir) {
		this.workingDir = workingDir;
		this.classPath = (classPath == null ? System.getProperty("java.class.path") : classPath);
		String[] cp = this.classPath.split(File.pathSeparator);
		int[] indices = new int[0];
		for (int i = 0; i < cp.length; i++) {
			if (cp[i].replace('\\', '/').matches(".*org/jsweet/lib/.*-testbundle/.*/.*-testbundle-.*\\.jar")) {
				logger.warn("candies processor ignores classpath entry: " + cp[i]);
				indices = ArrayUtils.add(indices, i);
			}
		}
		cp = ArrayUtils.removeAll(cp, indices);
		this.classPath = StringUtils.join(cp, File.pathSeparator);
		logger.info("candies processor classpath: " + this.classPath);
		candiesSourceDir = new File(workingDir, CANDIES_SOURCES_DIR_NAME);
		candyStoreFile = new File(workingDir, CANDIES_STORE_FILE_NAME);
		candiesTsdefsDir = new File(workingDir, CANDIES_TSDEFS_DIR_NAME);

		setCandiesJavascriptOutDir(extractedCandiesJavascriptDir);
	}

	private List<CandyDescriptor> getCandies() {
		return getCandiesStore().getCandies();
	}

	private void setCandiesJavascriptOutDir(File extractedCandiesJavascriptDir) {
		this.candiesJavascriptOutDir = extractedCandiesJavascriptDir;
		if (this.candiesJavascriptOutDir == null) {
			logger.info("extracted candies directory is set to default");
			this.candiesJavascriptOutDir = new File(workingDir, CANDIES_DEFAULT_JS_DIR_NAME);
		}
		logger.info("extracted candies directory: " + extractedCandiesJavascriptDir);
		this.candiesJavascriptOutDir.mkdirs();
	}

	/**
	 * Returns the directory that contains the orginal TypeScript source code of the
	 * processed (merged) candies.
	 */
	public File getCandiesTsdefsDir() {
		return candiesTsdefsDir;
	}

	/**
	 * Does the processing for the candies jars found in the classpath.
	 */
	public void processCandies(TranspilationHandler transpilationHandler) throws IOException {
		CandyStore candiesStore = getCandiesStore();

		LinkedHashMap<File, CandyDescriptor> newCandiesDescriptors = getCandiesDescriptorsFromClassPath(
				transpilationHandler);
		CandyStore newStore = new CandyStore(new ArrayList<>(newCandiesDescriptors.values()));
		if (newStore.equals(candiesStore)) {
			logger.info("candies are up to date");
			return;
		}

		this.candyStore = newStore;
		logger.info("candies changed, processing candies: " + this.candyStore);

		try {
			extractCandies(newCandiesDescriptors);

			writeCandyStore();

		} catch (Throwable t) {
			logger.error("cannot generate candies bundle", t);
			// exit with fatal if no jar ?
		}
	}

	/**
	 * Returns true if the candy store contains the J4TS candy.
	 */
	public boolean isUsingJavaRuntime() {
		if (candyStore == null) {
			return false;
		} else {
			for (CandyDescriptor c : candyStore.getCandies()) {
				if (c.name != null && c.name.equals("j4ts")) {
					logger.info("found j4ts Java runtime in classpath");
					return true;
				}
			}
			return false;
		}
	}

	private LinkedHashMap<File, CandyDescriptor> getCandiesDescriptorsFromClassPath(
			TranspilationHandler transpilationHandler) throws IOException {
		LinkedHashMap<File, CandyDescriptor> jarFilesCollector = new LinkedHashMap<>();
		for (String classPathEntry : classPath.split("[" + System.getProperty("path.separator") + "]")) {
			if (classPathEntry.endsWith(".jar")) {
				File jarFile = new File(classPathEntry);
				if (!jarFile.exists()) {
					// critical warning, candy not found
					logger.warn("candy jar file not found: " + jarFile, new Exception());
					continue;
				}

				try (JarFile jarFileHandle = new JarFile(jarFile)) {
					JarEntry candySpecificEntry = jarFileHandle
							.getJarEntry("META-INF/maven/" + JSweetConfig.MAVEN_CANDIES_GROUP);
					JarEntry candySpecificEntry2 = jarFileHandle.getJarEntry("META-INF/candy-metadata.json");
					boolean isCandy = candySpecificEntry != null || candySpecificEntry2 != null;
					if (isCandy) {
						CandyDescriptor descriptor = CandyDescriptor.fromCandyJar(jarFileHandle,
								candiesJavascriptOutDir.getAbsolutePath());

						checkCandyVersion(descriptor, transpilationHandler);
						jarFilesCollector.put(jarFile, descriptor);
					}
				}

			}
		}
		logger.info(jarFilesCollector.keySet().size() + " candies found in classpath");

		return jarFilesCollector;
	}

	private String normalizeVersion(String version) {
		if (version == null) {
			return null;
		}
		String[] v = JSweetConfig.getVersionNumber().split("\\.");
		if (v.length == 2) {
			return version;
		} else if (v.length == 1) {
			return v[0] + ".0";
		} else {
			return v[0] + "." + v[1];
		}
	}

	private void checkCandyVersion(CandyDescriptor candy, TranspilationHandler transpilationHandler) {

		String actualTranspilerVersion = normalizeVersion(JSweetConfig.getVersionNumber().split("-")[0]);
		String candyTranspilerVersion = normalizeVersion(
				candy.transpilerVersion == null ? null : candy.transpilerVersion.split("-")[0]);

		if (candyTranspilerVersion == null || !candyTranspilerVersion.equals(actualTranspilerVersion)) {
			transpilationHandler.report(JSweetProblem.CANDY_VERSION_DISCREPANCY, null,
					JSweetProblem.CANDY_VERSION_DISCREPANCY.getMessage(candy.name, candy.version,
							actualTranspilerVersion, candyTranspilerVersion));
		}
	}

	private void extractCandies(Map<File, CandyDescriptor> candies) throws IOException {
		File extractedSourcesDir = candiesSourceDir;
		File extractedTsDefsDir = candiesTsdefsDir;
		FileUtils.deleteQuietly(extractedSourcesDir);
		FileUtils.deleteQuietly(extractedTsDefsDir);
		extractedSourcesDir.mkdirs();
		extractedTsDefsDir.mkdirs();
		for (Map.Entry<File, CandyDescriptor> candy : candies.entrySet()) {

			CandyDescriptor candyDescriptor = candy.getValue();
			File jarFile = candy.getKey();

			String candyName = candyDescriptor.name;
			boolean isCore = "jsweet-core".equals(candyName);
			try (JarFile jarFileHandle = new JarFile(jarFile)) {
				String candyJarName = FilenameUtils.getBaseName(jarFile.getName());
				File candyExtractedSourcesDir = new File(extractedSourcesDir, candyJarName);
				File candyExtractedJsDir = new File(candiesJavascriptOutDir, candyJarName);

				extractCandy( //
						candyDescriptor, //
						jarFileHandle, //
						candyExtractedSourcesDir, //
						extractedTsDefsDir, //
						candyExtractedJsDir, //
						isCore ? tsDefName -> false : null);
			}
		}
	}

	private void extractCandy( //
			CandyDescriptor descriptor, //
			JarFile jarFile, //
			File javaOutputDirectory, //
			File tsDefOutputDirectory, //
			File jsOutputDirectory, //
			Predicate<String> isTsDefToBeExtracted) {
		logger.info("extract candy: " + jarFile.getName() + " javaOutputDirectory=" + javaOutputDirectory
				+ " tsDefOutputDirectory=" + tsDefOutputDirectory + " jsOutputDir=" + jsOutputDirectory);

		jarFile.stream()
				.filter(entry -> entry.getName().endsWith(".d.ts")
						&& (entry.getName().startsWith("src/") || entry.getName().startsWith("META-INF/resources/"))) //
				.forEach(entry -> {

					File out;
					if (entry.getName().endsWith(".java")) {
						// RP: this looks like dead code...
						out = new File(javaOutputDirectory + "/" + entry.getName().substring(4));
					} else if (entry.getName().endsWith(".d.ts")) {
						if (isTsDefToBeExtracted != null && !isTsDefToBeExtracted.test(entry.getName())) {
							return;
						}
						out = new File(tsDefOutputDirectory + "/" + entry.getName());
					} else {
						out = null;
					}
					extractEntry(jarFile, entry, out);
				});

		for (String jsFilePath : descriptor.jsFilesPaths) {
			JarEntry entry = jarFile.getJarEntry(jsFilePath);
			String relativeJsPath = jsFilePath.substring(descriptor.jsDirPath.length());

			File out = new File(jsOutputDirectory, relativeJsPath);
			extractEntry(jarFile, entry, out);
			extractedJsFiles.add(out);
		}
	}

	private void extractEntry(JarFile jarFile, JarEntry entry, File out) {
		if (out == null) {
			return;
		}
		out.getParentFile().mkdirs();
		try {
			FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private CandyStore candyStore;

	/**
	 * Cleans the candies store so that it will be read from file next time.
	 */
	public void touch() {
		candyStore = null;
	}

	private CandyStore getCandiesStore() {
		if (candyStore == null) {
			if (candyStoreFile.exists()) {
				try {
					candyStore = gson.fromJson(FileUtils.readFileToString(candyStoreFile), CandyStore.class);
				} catch (Exception e) {
					logger.error("cannot read candies index", e);
				}
			}

			if (candyStore == null) {
				candyStore = new CandyStore();
			}
		}

		return candyStore;
	}

	private void writeCandyStore() {
		if (candyStore != null) {
			try {
				FileUtils.write(candyStoreFile, gson.toJson(candyStore));
			} catch (Exception e) {
				logger.error("cannot read candies index", e);
			}
		}
	}

	/**
	 * Checks if the candy store contains a deprecated candy.
	 */
	public boolean hasDeprecatedCandy() {
		for (CandyDescriptor candy : getCandies()) {
			if (candy.transpilerVersion.startsWith("1")) {
				return true;
			}
		}
		return false;
	}

	public List<File> getExtractedJsFiles() {
		return extractedJsFiles;
	}
}
