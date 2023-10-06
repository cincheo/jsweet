/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.client.compileonsave;

import java.util.List;

/**
 * Contains a list of files that should be regenerated in a project
 *
 */
public class CompileOnSaveAffectedFileListSingleProject {

	/**
	 * Project name
	 */
	private String projectFileName;
	/**
	 * List of files names that should be recompiled
	 */
	private List<String> fileNames;

	public String getProjectFileName() {
		return projectFileName;
	}

	public List<String> getFileNames() {
		return fileNames;
	}
}
