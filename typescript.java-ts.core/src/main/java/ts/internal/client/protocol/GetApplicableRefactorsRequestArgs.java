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
public class GetApplicableRefactorsRequestArgs extends FileLocationRequestArgs {

	public GetApplicableRefactorsRequestArgs(String file, int position) {
		super(file, position);
	}

	public GetApplicableRefactorsRequestArgs(String file, int line, int offset) {
		super(file, line, offset);
	}
}
