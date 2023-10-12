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
package ts.cmd.tslint;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import ts.client.Location;
import ts.cmd.ITypeScriptLinterHandler;
import ts.cmd.Severity;
import ts.utils.StringUtils;

/**
 * TypeScript Compiler (tsc) helper.
 *
 */
public class TslintHelper {

	// ex: "(no-var-keyword) sample.ts[1, 1]: forbidden 'var' keyword, use 'let'
	// or 'const' instead", errors);
	// sample.ts(1,14): error TS1003: Identifier expected.

	private static final Pattern TSLINT_PATTERN = Pattern
			.compile("^\\(\\s*(.*)\\)\\s([^\\s].*)\\[(\\d+,\\s*\\d+)\\]:\\s*(.*)$");

	/**
	 * Process "tslint" message and call the well
	 * {@link ITypeScriptLinterHandler} method.
	 * 
	 * @param text
	 * @param handler
	 */
	public static void processVerboseMessage(String text, ITypeScriptLinterHandler handler) {
		if (StringUtils.isEmpty(text)) {
			return;
		}

		Scanner scanner = null;
		try {
			scanner = new Scanner(text);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				line = line.trim(); // remove leading whitespace
				Matcher m = TSLINT_PATTERN.matcher(line);
				if (m.matches()) {
					// Error in an ts file.
					String code = m.group(1);
					String file = m.group(2);
					String[] location = m.group(3).split(",");
					Location startLoc = createLocation(location, true);
					Location endLoc = null; // createLocation(location, false);
					String message = m.group(4);
					handler.addError(file, startLoc, endLoc, Severity.error, code, message);
				}
			}
		} finally {
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
			return Integer.parseInt(location[index].trim());
		}
		return 0;
	}

	public static void processJsonMessage(String text, ITypeScriptLinterHandler handler) {
		if (StringUtils.isEmpty(text)) {
			return;
		}

		Scanner scanner = null;
		try {
			scanner = new Scanner(text);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				line = line.trim(); // remove leading whitespace
				JsonArray array = Json.parse(line).asArray();
				for (JsonValue value : array) {
					// [{"endPosition":{"character":3,"line":0,"position":3},"failure":"forbidden
					// 'var' keyword, use 'let' or 'const'
					// instead","name":"sample.ts","ruleName":"no-var-keyword","startPosition":{"character":0,"line":0,"position":0}},{"endPosition":{"character":13,"line":0,"position":13},"failure":"missing
					// semicolon","name":"sample.ts","ruleName":"semicolon","startPosition":{"character":13,"line":0,"position":13}},{"endPosition":{"character":12,"line":0,"position":12},"failure":"missing
					// whitespace","name":"sample.ts","ruleName":"whitespace","startPosition":{"character":11,"line":0,"position":11}}]
					JsonObject item = value.asObject();
					String name = item.getString("name", null);
					String ruleName = item.getString("ruleName", null);
					String failure = item.getString("failure", null);
					Location startLoc = createLocation(item.get("startPosition"));
					Location endLoc = createLocation(item.get("endPosition"));
					handler.addError(name, startLoc, endLoc, Severity.error, ruleName, failure);
				}
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	private static Location createLocation(JsonValue value) {
		if (value == null || !value.isObject()) {
			return null;
		}
		JsonObject loc = value.asObject();
		return new Location(loc.getInt("line", -1), loc.getInt("character", -1), loc.getInt("position", -1));
	}

}