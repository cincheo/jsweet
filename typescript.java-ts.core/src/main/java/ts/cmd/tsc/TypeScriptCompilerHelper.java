/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.cmd.tsc;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ts.client.Location;
import ts.cmd.Severity;
import ts.utils.FileUtils;
import ts.utils.StringUtils;

/**
 * TypeScript Compiler (tsc) helper.
 *
 */
public class TypeScriptCompilerHelper {

	private static final Pattern TSC_ERROR_PATTERN = Pattern.compile(
			"^([^\\s].*)\\((\\d+|\\d+,\\d+|\\d+,\\d+,\\d+,\\d+)\\):\\s+(error|warning|info)\\s+(TS\\d+)\\s*:\\s*(.*)$");

	private static final String COMPILATION_COMPLETE_WATCHING_FOR_FILE_CHANGES = "Compilation complete. Watching for file changes.";

	private static final String TSFILE = "TSFILE:";

	/**
	 * Process "tsc" message and call the well
	 * {@link ITypeScriptCompilerMessageHandler} method.
	 * 
	 * @param text
	 * @param handler
	 */
	public static void processMessage(String text, ITypeScriptCompilerMessageHandler handler) {
		if (StringUtils.isEmpty(text)) {
			return;
		}

		Scanner scanner = null;
		try {
			scanner = new Scanner(text);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				line = line.trim(); // remove leading whitespace
				if (line.endsWith(FileUtils.TS_EXTENSION) || line.endsWith(FileUtils.TSX_EXTENSION)) {
					// Occurs when tsc is called with --listFiles
					handler.addFile(line, false);
				} else if (line.contains(COMPILATION_COMPLETE_WATCHING_FOR_FILE_CHANGES)) {
					// Occurs when tsc is called with --watch when compilation
					// is finished.
					handler.onCompilationCompleteWatchingForFileChanges();
				} else if (line.startsWith(TSFILE)) {
					handler.addFile(line.substring(TSFILE.length(), line.length()).trim(), true);
				} else {
					Matcher m = TSC_ERROR_PATTERN.matcher(line);
					if (m.matches()) {
						// Error in an ts file.
						String file = m.group(1);
						String[] location = m.group(2).split(",");
						Location startLoc = createLocation(location, true);
						Location endLoc = createLocation(location, false);
						String severity = m.group(3);
						String code = m.group(4);
						String message = m.group(5);
						handler.addError(file, startLoc, endLoc,
								StringUtils.isEmpty(severity) ? Severity.info : Severity.valueOf(severity), code,
								message);
					}
				}
			}
		} finally

		{
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	private static Location createLocation(String[] location, boolean start) {
		if (start) {
			int line = getInt(location, 0);
			int offset = getInt(location, 1);
			return new Location(line, offset);
		}
		return null;
	}

	private static int getInt(String[] location, int index) {
		if (index < location.length) {
			return Integer.parseInt(location[index]);
		}
		return 0;
	}
}
