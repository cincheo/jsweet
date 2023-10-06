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
package ts.client.projectinfo;

import java.util.List;

/**
 * Response message body for "projectInfo" request
 *
 */
public class ProjectInfo {

	/**
	 * For configured project, this is the normalized path of the
	 * 'tsconfig.json' file For inferred project, this is undefined
	 */
	private String configFileName;
	/**
	 * The list of normalized file name in the project, including 'lib.d.ts'
	 */
	private List<String> fileNames;
	/**
	 * Indicates if the project has a active language service instance
	 */
	private Boolean languageServiceDisabled;

	public String getConfigFileName() {
		return configFileName;
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public Boolean getLanguageServiceDisabled() {
		return languageServiceDisabled;
	}

}
