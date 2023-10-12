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
 * Arguments for CompileOnSaveEmitFileRequest
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class CompileOnSaveEmitFileRequestArgs extends FileRequestArgs {

	/**
	 * if true - then file should be recompiled even if it does not have any
	 * changes.
	 */
	private final Boolean forced;

	public CompileOnSaveEmitFileRequestArgs(String file, Boolean forced) {
		super(file, null);
		this.forced = forced;
	}

	public Boolean getForced() {
		return forced;
	}

}
