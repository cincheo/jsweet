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

import ts.client.format.FormatCodeSettings;

/**
 * Arguments for format messages.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class FormatRequestArgs extends FileLocationRequestArgs {

	/**
	 * Last line of range for which to format text in file.
	 */
	private final Integer endLine;

	/**
	 * Character offset on last line of range for which to format text in file.
	 */
	private final Integer endOffset;

	/**
	 * End position of the range for which to format text in file.
	 */
	private final Integer endPosition;

	/**
	 * Format options to be used.
	 */
	private FormatCodeSettings options;

	public FormatRequestArgs(String file, int position, int endPosition) {
		super(file, position);
		this.endLine = null;
		this.endOffset = null;
		this.endPosition = endPosition;
		this.options = null;
	}

	public FormatRequestArgs(String file, int line, int offset, int endLine, int endOffset) {
		super(file, line, offset);
		this.endLine = endLine;
		this.endOffset = endOffset;
		this.endPosition = null;
		this.options = null;
	}

	public Integer getEndLine() {
		return endLine;
	}

	public Integer getEndOffset() {
		return endOffset;
	}

	public Integer getEndPosition() {
		return endPosition;
	}

	public FormatCodeSettings getOptions() {
		return options;
	}

	public void setOptions(FormatCodeSettings options) {
		this.options = options;
	}
}
