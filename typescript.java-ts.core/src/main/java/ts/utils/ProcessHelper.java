/**
 *  Copyright (c) 2016-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ts.OS;

/**
 * Helper for Process.
 *
 */
public class ProcessHelper {

	private static final String PATH_ENV = "PATH";

	/**
	 * Returns the given filename location.
	 * 
	 * @param fileName
	 * @param os
	 * @param extension
	 * @return the given filename location
	 */
	public static File findLocation(String fileName, OS os, String extension) {
		String fileNameWithExt = extension == null ? fileName : fileName + extension;
		String path = System.getenv(PATH_ENV);
		String[] paths = path.split("" + File.pathSeparatorChar, 0);
		List<String> directories = new ArrayList<String>();
		for (String p : paths) {
			directories.add(p);
		}

		// ensure /usr/local/bin is included for OS X
		if (os == OS.MacOS) {
			directories.add("/usr/local/bin");
		}

		// search for filename in the PATH directories
		for (String directory : directories) {
			File file = new File(directory, fileNameWithExt);

			if (file.exists()) {
				return file;
			}
		}
		return which(fileName, os);
	}

	/**
	 * Returns the given filename location by using command "which $filename".
	 * 
	 * @param fileName
	 *            file name to search.
	 * @param os
	 *            the OS.
	 * @return the given filename location by using command "which $filename".
	 */
	public static File which(String fileName, OS os) {
		String[] command = whichCommand(fileName, os);
		BufferedReader reader = null;
		try {
			Process p = Runtime.getRuntime().exec(command);
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String foundFile = reader.readLine();
			if (StringUtils.isEmpty(foundFile)) {
				return null;
			}
			File f = new File(foundFile);
			return f.exists() ? f : null;
		} catch (IOException e) {
			return null;
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	private static String[] whichCommand(String fileName, OS os) {
		return getCommand("where " + fileName, os);
	}

	public static String[] getCommand(String command, OS os) {
		File defaultShell = defaultShell(os);
		String image = defaultShell.isAbsolute() ? defaultShell.getAbsolutePath() : defaultShell.getPath();
		if (os == OS.Windows) {
			return new String[] { image, "/c", command };
		}
		return new String[] { image, "-c", command };
	}

	/**
	 * Returns the default shell to launch. Looks at the environment variable
	 * "SHELL" first before assuming some default default values.
	 *
	 * @return The default shell to launch.
	 */
	private static final File defaultShell(OS os) {
		String shell = null;
		if (os == OS.Windows) {
			if (System.getenv("ComSpec") != null && !"".equals(System.getenv("ComSpec").trim())) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				shell = System.getenv("ComSpec").trim(); //$NON-NLS-1$
			} else {
				shell = "cmd.exe"; //$NON-NLS-1$
			}
		}
		if (StringUtils.isEmpty(shell)) {
			if (System.getenv("SHELL") != null && !"".equals(System.getenv("SHELL").trim())) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				shell = System.getenv("SHELL").trim(); //$NON-NLS-1$
			} else {
				shell = "/bin/sh"; //$NON-NLS-1$
			}
		}
		return new File(shell);
	}
}
