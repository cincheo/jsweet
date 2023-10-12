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
 * Arguments for geterr messages.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class GeterrRequestArgs {

	/**
	 * List of file names for which to compute compiler errors. The files will
	 * be checked in list order.
	 */
	private String[] files;

	/**
	 * Delay in milliseconds to wait before starting to compute errors for the
	 * files in the file list
	 */
	private int delay;

	public GeterrRequestArgs(String[] files, int delay) {
		this.files = files;
		this.delay = delay;
	}

	public String[] getFiles() {
		return files;
	}

	public int getDelay() {
		return delay;
	}
}
