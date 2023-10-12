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
 * Argument for RenameRequest request.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class RenameRequestArgs extends FileLocationRequestArgs {

	/**
	 * Should text at specified location be found/changed in comments?
	 */
	private Boolean findInComments;
	/**
	 * Should text at specified location be found/changed in strings?
	 */
	private Boolean findInStrings;

	public RenameRequestArgs(String file, int line, int offset, Boolean findInComments, Boolean findInStrings) {
		super(file, line, offset);
		this.findInComments = findInComments;
		this.findInStrings = findInStrings;
	}

	public Boolean getFindInComments() {
		return findInComments;
	}

	public Boolean getFindInStrings() {
		return findInStrings;
	}
}
