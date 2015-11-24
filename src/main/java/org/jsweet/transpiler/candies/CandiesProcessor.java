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
import java.util.HashMap;
import java.util.LinkedList;
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

	private File candiesSourceDir;
	private File candiesProcessedDir;
	private File candiesStoreFile;
	private File candiesTsdefsDir;
	private List<String> candyClassPathEntries;
	private boolean forceCandiesCompatibility;

	/**
	 * Create a candies processor.
	 * 
	 * @param workingDir
	 *            the directory where the processor will save all cache and
	 *            temporary data for processing
	 * @param classPath
	 *            the classpath where the processor will seek for JSweet candies
	 */
	public CandiesProcessor(File workingDir, String classPath) {
		this.classPath = (classPath == null ? System.getProperty("java.class.path") : classPath);
		String[] cp = this.classPath.split(File.pathSeparator);
		int[] indices = new int[0];
		for (int i = 0; i < cp.length; i++) {
			if (cp[i].replace('\\', '/').endsWith("org/jsweet/lib/core-bin/latest/core-bin-latest.jar")) {
				logger.warn("candies processor ignores classpath entry: " + cp[i]);
				indices = ArrayUtils.add(indices, i);
			}
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
	public void processCandies() throws IOException {
		CandiesStore candiesStore = getCandiesStore();

		List<CandyDescriptor> newCandiesDescriptors = getCandiesDescriptorsFromClassPath(new HashMap<CandyDescriptor, File>());
		CandiesStore newStore = new CandiesStore(newCandiesDescriptors);
		if (newStore.equals(candiesStore)) {
			logger.info("candies are up to date");
			return;
		}

		this.candiesStore = newStore;
		logger.info("candies changed, processing candies: " + this.candiesStore);

		try {
			extractCandies();

			mergeCandies();

			writeCandiesStore();

		} catch (Throwable t) {
			logger.error("cannot generate candies bundle", t);
			// exit with fatal if no jar ?
		}
	}

	/**
	 * Ignore candies version check and tries to transpile anyway
	 * 
	 * @param force
	 *            whether check should be disabled or not<
	 */
	public void setForceCandiesCompatibility(boolean force) {
		this.forceCandiesCompatibility = force;
	}

	private void mergeCandies() {
		logger.info("merging candies");
		CandiesMerger merger = new CandiesMerger(candiesProcessedDir, candyClassPathEntries);
		Map<Class<?>, List<Class<?>>> mergedMixins = merger.merge();
		extractSourcesForClasses(mergedMixins.keySet());
	}

	private List<CandyDescriptor> getCandiesDescriptorsFromClassPath(Map<CandyDescriptor, File> jarFilesCollector) throws IOException {
		List<CandyDescriptor> candiesDescriptors = new LinkedList<>();
		for (String classPathEntry : classPath.split("[" + System.getProperty("path.separator") + "]")) {
			if (classPathEntry.endsWith(".jar")) {
				File jarFile = new File(classPathEntry);
				try (JarFile jarFileHandle = new JarFile(jarFile)) {
					JarEntry candySpecificEntry = jarFileHandle.getJarEntry("META-INF/maven/" + JSweetConfig.MAVEN_CANDIES_GROUP);
					boolean isCandy = candySpecificEntry != null;
					if (isCandy) {
						CandyDescriptor descriptor = CandyDescriptor.fromCandyJar(jarFileHandle);

						checkCandyVersion(descriptor);
						candiesDescriptors.add(descriptor);
						jarFilesCollector.put(descriptor, jarFile);
					}
				}

			}
		}

		return candiesDescriptors;
	}

	private void checkCandyVersion(CandyDescriptor candy) {
		// TODO : check major version change
		// we assume candies will always remain compatible with jsweet minor
		// versions changes
	}

	private void extractCandies() throws IOException {
		File extractedSourcesDir = candiesSourceDir;
		File extractedTsDefsDir = candiesTsdefsDir;
		File extractedClassesDir = candiesProcessedDir;
		FileUtils.deleteQuietly(extractedSourcesDir);
		FileUtils.deleteQuietly(extractedTsDefsDir);
		FileUtils.deleteQuietly(extractedClassesDir);
		extractedSourcesDir.mkdirs();
		extractedTsDefsDir.mkdirs();
		extractedClassesDir.mkdirs();
		candyClassPathEntries = new ArrayList<>();

		for (String classPathEntry : classPath.split("[" + System.getProperty("path.separator") + "]")) {
			if (classPathEntry.endsWith(".jar")) {
				File jarFileDescriptor = new File(classPathEntry);
				logger.info("scanning sources in jar: " + classPathEntry);
				try (JarFile jarFile = new JarFile(jarFileDescriptor)) {
					JarEntry candySpecificEntry = jarFile.getJarEntry("META-INF/maven/" + JSweetConfig.MAVEN_CANDIES_GROUP);
					boolean isCandy = candySpecificEntry != null;
					if (isCandy) {
						candyClassPathEntries.add(classPathEntry);
						String groupName = FilenameUtils.getBaseName(jarFile.getName());
						String versionName = jarFileDescriptor.getParentFile().getName();

						// remove version
						groupName = groupName.substring(0, groupName.lastIndexOf("-" + versionName));
						int i = groupName.indexOf("--");
						if (i > 0) {
							groupName = groupName.substring(0, i);
						}

						boolean isCore = "core".equals(groupName);
						extractCandy(jarFile, new File(extractedSourcesDir, FilenameUtils.getBaseName(jarFile.getName())), extractedTsDefsDir,
								isCore ? tsDefName -> false : null, extractedClassesDir);
					}
				}
			}
		}
	}

	private void extractCandy(JarFile jarFile, File javaOutputDirectory, File tsDefOutputDirectory, Predicate<String> isTsDefToBeExtracted,
			File classesOutputDirectory) {
		logger.info("extract candy: " + jarFile.getName() + " javaOutputDirectory=" + javaOutputDirectory + " tsDefOutputDirectory=" + tsDefOutputDirectory
				+ " classesOutputDirectory=" + classesOutputDirectory);

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
					out.getParentFile().mkdirs();
					try {
						FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), out);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
	}

	private void extractSourcesForClasses(Collection<Class<?>> classes) {
		logger.info("extract sources for: " + classes);

		List<String> files = classes.stream().map(c -> "src/" + c.getName().replace('.', '/') + ".java").collect(Collectors.toList());
		for (String candyClassPathEntry : candyClassPathEntries) {
			try (JarFile jarFile = new JarFile(candyClassPathEntry)) {
				jarFile.stream().filter(entry -> files.contains(entry.getName())) //
						.forEach(entry -> {
							File out = new File(candiesSourceDir + "/" + entry.getName().substring(4));
							out.getParentFile().mkdirs();
							try {
								logger.debug("extracting source: " + out);
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
