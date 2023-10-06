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
 * Request the edits that a particular refactoring action produces. Callers must
 * specify the name of the refactor and the name of the action.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class GetEditsForRefactorRequestArgs extends FileLocationRequestArgs {

	/* The 'name' property from the refactoring that offered this action */
	private String refactor;
	/* The 'name' property from the refactoring action */
	private String action;

	public GetEditsForRefactorRequestArgs(String file, int position, String refactor, String action) {
		super(file, position);
		this.refactor = refactor;
		this.action = action;
	}

	public GetEditsForRefactorRequestArgs(String file, int line, int offset, String refactor, String action) {
		super(file, line, offset);
		this.refactor = refactor;
		this.action = action;
	}

	public String getRefactor() {
		return refactor;
	}

	public String getAction() {
		return action;
	}
}
