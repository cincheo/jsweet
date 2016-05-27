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
package org.jsweet.transpiler.candies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

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
 * The candies processor extracts and processes what is required from the candy
 * jars in order to ensure safe transpilation.
 * 
 * <ul>
 * <li>The embedded TypeScript definition files (*.d.ts)</li>
 * <li>Cross-candies mixins, which are merged by {@link CandiesMerger}</li>
 * </ul>
 */
public class CandiesProcessor {

	private static final Logger logger = Logger.getLogger(CandiesProcessor.class);
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
	 * This directory will contain processed (merged) bytecode.
	 */
	public static final String CANDIES_PROCESSED_DIR_NAME = CANDIES_DIR_NAME + File.separator + "processed";
	/**
	 * The name of the file that stores processed candies info.
	 */
	public static final String CANDIES_STORE_FILE_NAME = CANDIES_DIR_NAME + File.separator + CandiesStore.class.getSimpleName() + ".json";
	/**
	 * The name of the directory that contains the TypeScript source files.
	 */
	public static final String CANDIES_TSDEFS_DIR_NAME = CANDIES_DIR_NAME + File.separator + JSweetConfig.TS_LIBS_DIR_NAME;
	/**
	 * Default directory for extracted candies' javascript.
	 */
	private static final String CANDIES_DEFAULT_JS_DIR_NAME = CANDIES_DIR_NAME + File.separator + "js";

	private File candiesSourceDir;
	private File candiesProcessedDir;
	private File candiesStoreFile;
	private File candiesTsdefsDir;
	private File candiesJavascriptOutDir;
	private File workingDir;

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
	public CandiesProcessor(File workingDir, String classPath, File extractedCandiesJavascriptDir) {
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
		candiesProcessedDir = new File(workingDir, CANDIES_PROCESSED_DIR_NAME);
		candiesStoreFile = new File(workingDir, CANDIES_STORE_FILE_NAME);
		candiesTsdefsDir = new File(workingDir, CANDIES_TSDEFS_DIR_NAME);
		logger.debug("processed classes dir: " + getCandiesProcessedDir() + " - " + getCandiesProcessedDir().getAbsolutePath());

		setCandiesJavascriptOutDir(extractedCandiesJavascriptDir);
	}

	private void setCandiesJavascriptOutDir(File extractedCandiesJavascriptDir) {
		this.candiesJavascriptOutDir = extractedCandiesJavascriptDir;
		if (this.candiesJavascriptOutDir == null) {
			this.candiesJavascriptOutDir = new File(workingDir, CANDIES_DEFAULT_JS_DIR_NAME);
		}
		logger.info("extracted candies directory: " + extractedCandiesJavascriptDir);
		this.candiesJavascriptOutDir.mkdirs();
	}

	/**
	 * Returns the directory that contains the processed (merged) candies.
	 */
	public File getCandiesProcessedDir() {
		return candiesProcessedDir;
	}

	/**
	 * Returns the directory that contains the orginal TypeScript source code of
	 * the processed (merged) candies.
	 */
	public File getCandiesTsdefsDir() {
		return candiesTsdefsDir;
	}

	/**
	 * Do the processing for the candies jars found in the classpath.
	 */
	public void processCandies(TranspilationHandler transpilationHandler) throws IOException {
		CandiesStore candiesStore = getCandiesStore();

		LinkedHashMap<File, CandyDescriptor> newCandiesDescriptors = getCandiesDescriptorsFromClassPath(transpilationHandler);
		CandiesStore newStore = new CandiesStore(new ArrayList<>(newCandiesDescriptors.values()));
		if (newStore.equals(candiesStore)) {
			logger.info("candies are up to date");
			return;
		}

		this.candiesStore = newStore;
		logger.info("candies changed, processing candies: " + this.candiesStore);

		try {
			extractCandies(newCandiesDescriptors);

			mergeCandies(newCandiesDescriptors);

			writeCandiesStore();

		} catch (Throwable t) {
			logger.error("cannot generate candies bundle", t);
			// exit with fatal if no jar ?
		}
	}

	private void mergeCandies(Map<File, CandyDescriptor> candies) {
		logger.info("merging candies");
		CandiesMerger merger = new CandiesMerger(candiesProcessedDir, new ArrayList<>(candies.keySet()));
		Map<Class<?>, List<Class<?>>> mergedMixins = merger.merge();
		extractSourcesForClasses(candies, mergedMixins.keySet());
	}

	private LinkedHashMap<File, CandyDescriptor> getCandiesDescriptorsFromClassPath(TranspilationHandler transpilationHandler) throws IOException {
		LinkedHashMap<File, CandyDescriptor> jarFilesCollector = new LinkedHashMap<>();
		for (String classPathEntry : classPath.split("[" + System.getProperty("path.separator") + "]")) {
			if (classPathEntry.endsWith(".jar")) {
				File jarFile = new File(classPathEntry);
				try (JarFile jarFileHandle = new JarFile(jarFile)) {
					JarEntry candySpecificEntry = jarFileHandle.getJarEntry("META-INF/maven/" + JSweetConfig.MAVEN_CANDIES_GROUP);
					JarEntry candySpecificEntry2 = jarFileHandle.getJarEntry("META-INF/candy-metadata.json");
					boolean isCandy = candySpecificEntry != null || candySpecificEntry2 != null;
					if (isCandy) {
						CandyDescriptor descriptor = CandyDescriptor.fromCandyJar(jarFileHandle);

						checkCandyVersion(descriptor, transpilationHandler);
						jarFilesCollector.put(jarFile, descriptor);
					}
				}

			}
		}
		logger.info("candies: " + jarFilesCollector.keySet());

		return jarFilesCollector;
	}

	private void checkCandyVersion(CandyDescriptor candy, TranspilationHandler transpilationHandler) {

		String actualTranspilerVersion = JSweetConfig.getVersionNumber().split("-")[0];
		String candyTranspilerVersion = candy.transpilerVersion == null ? null : candy.transpilerVersion.split("-")[0];

		if (candyTranspilerVersion == null || !candyTranspilerVersion.equals(actualTranspilerVersion)) {
			transpilationHandler.report(JSweetProblem.CANDY_VERSION_DISCREPANCY, null,
					JSweetProblem.CANDY_VERSION_DISCREPANCY.getMessage(candy.name, candy.version, actualTranspilerVersion, candyTranspilerVersion));
		}
	}

	private void extractCandies(Map<File, CandyDescriptor> candies) throws IOException {
		File extractedSourcesDir = candiesSourceDir;
		File extractedTsDefsDir = candiesTsdefsDir;
		File extractedClassesDir = candiesProcessedDir;
		FileUtils.deleteQuietly(extractedSourcesDir);
		FileUtils.deleteQuietly(extractedTsDefsDir);
		FileUtils.deleteQuietly(extractedClassesDir);
		extractedSourcesDir.mkdirs();
		extractedTsDefsDir.mkdirs();
		extractedClassesDir.mkdirs();
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
						isCore ? tsDefName -> false : null, //
						extractedClassesDir);
			}
		}
	}

	private void extractCandy( //
			CandyDescriptor descriptor, //
			JarFile jarFile, //
			File javaOutputDirectory, //
			File tsDefOutputDirectory, //
			File jsOutputDirectory, //
			Predicate<String> isTsDefToBeExtracted, //
			File classesOutputDirectory) {
		logger.info("extract candy: " + jarFile.getName() + " javaOutputDirectory=" + javaOutputDirectory + " tsDefOutputDirectory=" + tsDefOutputDirectory
				+ " classesOutputDirectory=" + classesOutputDirectory + " jsOutputDir=" + jsOutputDirectory);

		jarFile.stream()
				.filter(entry -> entry.getName().endsWith(".d.ts") && entry.getName().startsWith("src/") || entry.getName().endsWith("package-info.class")) //
				.forEach(entry -> {

					File out;
					if (entry.getName().endsWith(".java")) {
						out = new File(javaOutputDirectory + "/" + entry.getName().substring(4));
					} else if (entry.getName().endsWith(".d.ts")) {
						if (isTsDefToBeExtracted != null && !isTsDefToBeExtracted.test(entry.getName())) {
							return;
						}

						out = new File(tsDefOutputDirectory + "/" + entry.getName());
					} else {
						out = new File(classesOutputDirectory + "/" + entry.getName());
					}
					extractEntry(jarFile, entry, out);
				});

		for (String jsFilePath : descriptor.jsFilesPaths) {
			JarEntry entry = jarFile.getJarEntry(jsFilePath);
			String relativeJsPath = jsFilePath.substring(descriptor.jsDirPath.length());

			File out = new File(jsOutputDirectory, relativeJsPath);
			extractEntry(jarFile, entry, out);
		}
	}

	private void extractEntry(JarFile jarFile, JarEntry entry, File out) {
		out.getParentFile().mkdirs();
		try {
			FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void extractSourcesForClasses(Map<File, CandyDescriptor> candies, Collection<Class<?>> classes) {
		logger.info("extract sources for: " + classes);

		List<String> files = classes.stream().map(c -> "src/" + c.getName().replace('.', '/') + ".java").collect(Collectors.toList());
		for (File candyClassPathEntry : candies.keySet()) {
			try (JarFile jarFile = new JarFile(candyClassPathEntry)) {
				jarFile.stream().filter(entry -> files.contains(entry.getName())) //
						.forEach(entry -> {
							File out = new File(candiesSourceDir + "/" + entry.getName().substring(4));
							out.getParentFile().mkdirs();
							try {
								FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), out);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						});
			} catch (Exception e) {
				logger.error("error extracting sources for " + candyClassPathEntry, e);
			}
		}
	}

	private CandiesStore candiesStore;

	/**
	 * Cleans the candies store so that it will be read from file next time.
	 */
	public void touch() {
		candiesStore = null;
	}

	private CandiesStore getCandiesStore() {
		if (candiesStore == null) {
			if (candiesStoreFile.exists()) {
				try {
					candiesStore = gson.fromJson(FileUtils.readFileToString(candiesStoreFile), CandiesStore.class);
				} catch (Exception e) {
					logger.error("cannot read candies index", e);
				}
			}

			if (candiesStore == null) {
				candiesStore = new CandiesStore();
			}
		}

		return candiesStore;
	}

	private void writeCandiesStore() {
		if (candiesStore != null) {
			try {
				FileUtils.write(candiesStoreFile, gson.toJson(candiesStore));
			} catch (Exception e) {
				logger.error("cannot read candies index", e);
			}
		}
	}

}
