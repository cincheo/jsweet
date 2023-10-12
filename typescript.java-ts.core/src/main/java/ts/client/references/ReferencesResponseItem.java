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
package ts.client.references;

import ts.client.FileSpan;

public class ReferencesResponseItem extends FileSpan {
	/**
	 * Text of line containing the reference. Including this with the response
	 * avoids latency of editor loading files to show text of reference line
	 * (the server already has loaded the referencing files).
	 */
	private String lineText;

	/**
	 * True if reference is a write location, false otherwise.
	 */
	private boolean isWriteAccess;

	/**
	 * True if reference is a definition, false otherwise.
	 */
	private boolean isDefinition;

	public String getLineText() {
		return lineText;
	}

	public boolean isWriteAccess() {
		return isWriteAccess;
	}

	public boolean isDefinition() {
		return isDefinition;
	}

}
