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
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class FileLocationRequestArgs extends FileRequestArgs {

	/**
	 * The line number for the request (1-based).
	 */
	private final Integer line;

	/**
	 * The character offset (on the line) for the request (1-based).
	 */
	private final Integer offset;

	/**
	 * Position (can be specified instead of line/offset pair)
	 */
	private final Integer position;

	public FileLocationRequestArgs(String file, int line, int offset) {
		this(file, line, offset, null);
	}

	public FileLocationRequestArgs(String file, int line, int offset, String projectName) {
		this(file, line, offset, null, projectName);
	}

	public FileLocationRequestArgs(String file, int position) {
		this(file, position, null);
	}

	public FileLocationRequestArgs(String file, int position, String projectName) {
		this(file, null, null, position, projectName);
	}

	private FileLocationRequestArgs(String file, Integer line, Integer offset, Integer position, String projectName) {
		super(file, projectName);
		this.line = line;
		this.offset = offset;
		this.position = position;
	}

	public Integer getLine() {
		return line;
	}

	public Integer getOffset() {
		return offset;
	}

	public Integer getPosition() {
		return position;
	}

}
