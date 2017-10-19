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
package org.jsweet.transpiler.extension;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.SourceFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class deals with the extension directory, which can contain classes to
 * tune the transpiler's default behavior.
 * 
 * @author Renaud Pawlak
 */
public class ExtensionManager {

	private final static Logger logger = Logger.getLogger(ExtensionManager.class);

	private File extensionDir;

	/**
	 * Creates a new extension manager for the given directory.
	 */
	public ExtensionManager(String extensionDirPath) {
		extensionDir = new File(extensionDirPath);
	}

	/**
	 * Checks if some Java source files are to be compiled in the extension
	 * directory, and compile them if necessary.
	 */
	public void checkAndCompileExtension(File workingDir, String compileClassPath) {
		if (!extensionDir.exists()) {
			return;
		}

		logger.info("checking extension (working dir = " + workingDir + ")");
		initExtensionClassPath();

		SourceFile[] sourceFiles = SourceFile.getSourceFiles(extensionDir);

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		List<String> optionList = new ArrayList<String>();
		optionList.addAll(Arrays.asList("-classpath", compileClassPath));

		File extensionDescriptorFile = new File(workingDir, "extension.json");
		Map<String, String> extensionDescriptor = new HashMap<>();
		try {
			extensionDescriptor = new Gson().fromJson(FileUtils.readFileToString(extensionDescriptorFile),
					new TypeToken<Map<String, String>>() {
					}.getType());
		} catch (Exception e) {
			// swallow
		}

		List<File> filesToCompile = Arrays.asList(SourceFile.toFiles(sourceFiles));
		boolean change = filesToCompile.size() != extensionDescriptor.size();
		if (!change) {
			for (File f : filesToCompile) {
				if (!(extensionDescriptor.containsKey(f.getPath())
						&& extensionDescriptor.get(f.getPath()).equals("" + f.lastModified()))) {
					change = true;
					break;
				}
			}
		}
		if (change) {
			logger.info("compiling " + filesToCompile.size() + " extension file(s)");
			extensionDescriptor.clear();
			for (File f : filesToCompile) {
				extensionDescriptor.put(f.getPath(), "" + f.lastModified());
			}
			try {
				logger.debug("writing extension descriptor: " + extensionDescriptor);
				FileUtils.deleteQuietly(extensionDescriptorFile);
				FileUtils.writeStringToFile(extensionDescriptorFile, new Gson().toJson(extensionDescriptor));
				if (!extensionDescriptorFile.exists()) {
					throw new Exception();
				} else {
					logger.debug("created " + extensionDescriptorFile.getAbsolutePath());
				}
			} catch (Exception e) {
				throw new RuntimeException("unable to create extension description file", e);
			}
			Iterable<? extends JavaFileObject> compilationUnits1 = fileManager
					.getJavaFileObjectsFromFiles(filesToCompile);
			if (!compiler.getTask(null, fileManager, null, optionList, null, compilationUnits1).call()) {
				throw new RuntimeException("JSweet extension compilation failed");
			}
		}

	}

	/**
	 * Initializes the classpath by adding the extension directory to the right
	 * class loader.
	 */
	private void initExtensionClassPath() {
		if (!extensionDir.exists()) {
			return;
		}
		try {
			if (!(PrinterAdapter.class.getClassLoader() instanceof URLClassLoader)) {
				throw new RuntimeException(
						"local extensions are not supported in this environment, please use a packaged extension");
			}
			URLClassLoader urlClassLoader = (URLClassLoader) PrinterAdapter.class.getClassLoader();
			Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			addURLMethod.setAccessible(true);
			boolean foundExtension = false;
			for (URL url : urlClassLoader.getURLs()) {
				if (url.getPath().endsWith("/" + JSweetConfig.EXTENSION_DIR)) {
					foundExtension = true;
					break;
				}
			}
			if (!foundExtension) {
				addURLMethod.invoke(urlClassLoader, extensionDir.toURI().toURL());
				logger.debug("updated classpath with: " + extensionDir.toURI().toURL());
			}
		} catch (Exception e) {
			throw new RuntimeException("fail to initalize extension classpath", e);
		}
	}

}
