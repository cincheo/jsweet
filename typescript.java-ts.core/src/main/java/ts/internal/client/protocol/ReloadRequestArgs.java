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

/**
 * Arguments for reload request.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class ReloadRequestArgs extends FileRequestArgs {

	/**
	 * Name of temporary file from which to reload file contents. May be same as
	 * file.
	 */
	private final String tmpfile;

	public ReloadRequestArgs(String file, String projectName, String tmpfile) {
		super(file, projectName);
		this.tmpfile = tmpfile;
	}

	public String getTmpfile() {
		return tmpfile;
	}
}
