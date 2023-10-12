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
public class CompletionDetailsRequestArgs extends FileLocationRequestArgs {

	/**
	 * Names of one or more entries for which to obtain details.
	 */
	private final String[] entryNames;

	private final String prefix;

	public CompletionDetailsRequestArgs(String file, int position, String prefix, String[] entryNames) {
		super(file, position);
		this.prefix = prefix;
		this.entryNames = entryNames;
	}

	public CompletionDetailsRequestArgs(String file, int line, int offset, String prefix, String[] entryNames) {
		super(file, line, offset);
		this.prefix = prefix;
		this.entryNames = entryNames;
	}

	public String getPrefix() {
		return prefix;
	}

	public String[] getEntryNames() {
		return entryNames;
	}
}
