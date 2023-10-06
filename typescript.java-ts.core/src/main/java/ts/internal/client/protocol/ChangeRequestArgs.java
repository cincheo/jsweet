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
 * Arguments for change request message.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class ChangeRequestArgs extends FormatRequestArgs {

	/**
	 * Optional string to insert at location (file, line, offset).
	 */
	private final String insertString;

	public ChangeRequestArgs(String file, int position, int endPosition, String insertString) {
		super(file, position, endPosition);
		this.insertString = insertString;
	}

	public ChangeRequestArgs(String file, int line, int offset, int endLine, int endOffset, String insertString) {
		super(file, line, offset, endLine, endOffset);
		this.insertString = insertString;
	}

	public String getInsertString() {
		return insertString;
	}
}
