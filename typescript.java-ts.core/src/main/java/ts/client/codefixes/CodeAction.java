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
package ts.client.codefixes;

import java.util.List;

public class CodeAction {

	/** Description of the code action to display in the UI of the editor */
	private String description;
	/** Text changes to apply to each file as part of the code action */
	private List<FileCodeEdits> changes;

	public String getDescription() {
		return description;
	}

	public List<FileCodeEdits> getChanges() {
		return changes;
	}
}
