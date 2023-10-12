/**
 *  Copyright (c) 2015-2016 Angelo ZERR
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

	/**
	 * Extension file
	 */
	public static final String JS_EXTENSION = "js";
	public static final String TS_EXTENSION = "ts";
	public static final String JSX_EXTENSION = "jsx";
	public static final String TSX_EXTENSION = "tsx";
	public static final String MAP_EXTENSION = "map";
	public static final String DEFINITION_TS_EXTENSION = ".d.ts";
	
	/**
	 * Configuration file
	 */
	public static final String TSCONFIG_START = "tsconfig";
	public static final String TSCONFIG_END = ".json";

	public static final String TSCONFIG_JSON = "tsconfig.json";
	public static final String JSCONFIG_JSON = "jsconfig.json";
	public static final String TSLINT_JSON = "tslint.json";
	
	/**
	 * Folders
	 */
	public static final String NODE_MODULES = "node_modules";
	public static final String BOWER_COMPONENTS = "bower_components";

	public static String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1)
			return null;
		if (index == fileName.length() - 1)
			return ""; //$NON-NLS-1$
		return fileName.substring(index + 1);
	}

	public static String getTypeScriptFilename(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1 || index == fileName.length() - 1) {
			return null;
		}
		String ext = fileName.substring(index + 1);
		if (FileUtils.JS_EXTENSION.equals(ext)) {
			StringBuilder tsFileName = new StringBuilder(fileName.substring(0, index));
			tsFileName.append('.');
			tsFileName.append(FileUtils.TS_EXTENSION);
			return tsFileName.toString();
		} else if (FileUtils.MAP_EXTENSION.equals(ext)) {
			return getTypeScriptFilename(fileName.substring(0, index));
		}
		return null;
	}

	public static boolean isJsOrJsMapFile(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1 || index == fileName.length() - 1) {
			return false;
		}
		String ext = fileName.substring(index + 1);
		if (FileUtils.JS_EXTENSION.equals(ext)) {
			return true;
		} else if (FileUtils.MAP_EXTENSION.equals(ext)) {
			return isJsOrJsMapFile(fileName.substring(0, index));
		}
		return false;
	}

	/**
	 * Returns the normalized path of the given file.
	 * 
	 * @param file
	 * @return the normalized path of the given file.
	 */
	public static String getPath(File file) {
		return getPath(file, true);
	}

	/**
	 * Returns the path of the given file.
	 * 
	 * @param file
	 * @param normalize
	 * @return
	 */
	public static String getPath(File file, boolean normalize) {
		String path = null;
		try {
			path = file.getCanonicalPath();
		} catch (IOException e) {
			path = file.getPath();
		}
		return normalize ? normalizeSlashes(path) : null;
	}

	/**
	 * Replace '\' with '/' from the given path because tsserver normalize it
	 * like this.
	 * 
	 * @param path
	 * @return
	 */
	public static String normalizeSlashes(String path) {
		return path.replaceAll("\\\\", "/");
	}

	public static String getContents(final File file) throws IOException {
		InputStream in = null;
		try {
			in = openInputStream(file);
			return IOUtils.toString(in, null);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static FileInputStream openInputStream(final File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		return new FileInputStream(file);
	}

	public static boolean isTsConfigFile(String filename) {
		if (FileUtils.TSCONFIG_JSON.equals(filename)) {
			return true;
		}
		return filename.startsWith(TSCONFIG_START) && filename.endsWith(TSCONFIG_END);
	}

}
