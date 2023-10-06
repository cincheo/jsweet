/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.internal.client.protocol;

import ts.client.ScriptKindName;

/**
 * Information found in an "open" request.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class OpenRequestArgs extends FileRequestArgs {

	/**
	 * Used when a version of the file content is known to be more up to date
	 * than the one on disk. Then the known content will be used upon opening
	 * instead of the disk copy
	 */
	private final String fileContent;
	/**
	 * Used to specify the script kind of the file explicitly. It could be one
	 * of the following: "TS", "JS", "TSX", "JSX"
	 */
	private final String scriptKindName;

	public OpenRequestArgs(String file, String projectName, String fileContent, ScriptKindName scriptKindName) {
		super(file, projectName);
		this.fileContent = fileContent;
		this.scriptKindName = scriptKindName != null ? scriptKindName.name() : null;
	}

	public String getFileContent() {
		return fileContent;
	}

	public String getScriptKindName() {
		return scriptKindName;
	}

}
