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
 * Arguments for ProjectInfoRequest request.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class ProjectInfoRequestArgs extends FileRequestArgs {

	/**
	 * Indicate if the file name list of the project is needed
	 */
	private boolean needFileNameList;

	public ProjectInfoRequestArgs(String file, boolean needFileNameList) {
		super(file, null);
		this.needFileNameList = needFileNameList;
	}

	public boolean isNeedFileNameList() {
		return needFileNameList;
	}
}
