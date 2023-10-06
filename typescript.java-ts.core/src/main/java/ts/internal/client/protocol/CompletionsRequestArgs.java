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
public class CompletionsRequestArgs extends FileLocationRequestArgs {

	private final String prefix;

	public CompletionsRequestArgs(String file, int position, String prefix) {
		super(file, position);
		this.prefix = prefix;
	}

	public CompletionsRequestArgs(String file, int line, int offset, String prefix) {
		super(file, line, offset);
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}
}
