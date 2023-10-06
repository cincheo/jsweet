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
 * Arguments for GeterrForProject request.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class GeterrForProjectRequestArgs {

	/**
	 * the file requesting project error list
	 */
	private String file;

	/**
	 * Delay in milliseconds to wait before starting to compute errors for the
	 * files in the file list
	 */
	private int delay;

	public GeterrForProjectRequestArgs(String file, int delay) {
		this.file = file;
		this.delay = delay;
	}

	public String getFile() {
		return file;
	}

	public int getDelay() {
		return delay;
	}
}
