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
package ts.client;

/**
 * Object found in response messages defining a span of text in a specific
 * source file.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class FileSpan extends TextSpan {

	/**
	 * File containing text span.
	 */
	private String file;

	public String getFile() {
		return file;
	}
}
