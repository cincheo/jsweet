/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.nodejs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ts.OS;
import ts.utils.FileUtils;
import ts.utils.IOUtils;
import ts.utils.ProcessHelper;
import ts.utils.StringUtils;

/**
 * Node path helper.
 *
 */
public class NodejsProcessHelper {

	private static final String NODE_FILENAME = "node";

	private static final String[] WINDOWS_NODE_PATHS = new String[] {
			"C:/Program Files/nodejs/node.exe".replace('/', File.separatorChar),
			"C:/Program Files (x86)/nodejs/node.exe".replace('/', File.separatorChar), NODE_FILENAME };

	private static final String[] MACOS_NODE_PATHS = new String[] { "/usr/local/bin/node", "/opt/local/bin/node",
			NODE_FILENAME };

	private static final String[] LINUX_NODE_PATHS = new String[] { "/usr/local/bin/node", NODE_FILENAME };

	private NodejsProcessHelper() {
	}

	public static String getNodejsPath(OS os) {
		String path = getDefaultNodejsPath(os);
		if (path != null) {
			return path;
		}
		File nodeFile = findNode(os);
		if (nodeFile != null) {
			return nodeFile.getAbsolutePath();
		}
		return NODE_FILENAME;
	}

	public static String getDefaultNodejsPath(OS os) {
		String[] paths = getDefaultNodejsPaths(os);
		String path = null;
		for (int i = 0; i < paths.length; i++) {
			path = paths[i];
			if (new File(path).exists()) {
				return path;
			}
		}
		return null;
	}

	public static String[] getDefaultNodejsPaths(OS os) {
		switch (os) {
		case Windows:
			return WINDOWS_NODE_PATHS;
		case MacOS:
			return MACOS_NODE_PATHS;
		default:
			return LINUX_NODE_PATHS;
		}
	}

	public static String[] getNodejsPaths(OS os) {
		List<String> paths = new ArrayList<>(Arrays.asList(getDefaultNodejsPaths(os)));
		File nodeFile = findNode(os);
		if (nodeFile != null) {
			paths.add(0, nodeFile.getAbsolutePath());
		}
		return paths.toArray(StringUtils.EMPTY_STRING);
	}

	public static File findNode(OS os) {
		String extension = os == OS.Windows ? ".exe" : null;
		return ProcessHelper.findLocation(NODE_FILENAME, os, extension);
	}

	/**
	 * Returns the nodejs version and null otherwise.
	 * 
	 * @param nodejsFile
	 * @return the nodejs version and null otherwise.
	 */
	public static String getNodeVersion(File nodejsFile) {
		if (nodejsFile != null) {
			BufferedReader reader = null;
			try {
				String command = FileUtils.getPath(nodejsFile) + " --version";
				Process p = Runtime.getRuntime().exec(command);
				reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				return reader.readLine();
			} catch (IOException e) {
				return null;
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
		return null;
	}
}
