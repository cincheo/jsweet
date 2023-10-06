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
public class NavtoRequestArgs extends FileRequestArgs {

	/**
	 * Search term to navigate to from current location; term can be '.*' or an
	 * identifier prefix.
	 */
	private final String searchValue;

	/**
	 * Optional limit on the number of items to return.
	 */
	private Integer maxResultCount;
	/**
	 * Optional flag to indicate we want results for just the current file or the
	 * entire project.
	 */
	private final Boolean currentFileOnly;

	public NavtoRequestArgs(String file, String searchValue, Integer maxResultCount, Boolean currentFileOnly,
			String projectFileName) {
		super(file, projectFileName);
		this.searchValue = searchValue;
		this.maxResultCount = maxResultCount;
		this.currentFileOnly = currentFileOnly;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public Integer getMaxResultCount() {
		return maxResultCount;
	}

	public Boolean getCurrentFileOnly() {
		return currentFileOnly;
	}

}
