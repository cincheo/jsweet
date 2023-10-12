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
package ts.client.rename;

import java.util.List;

public class RenameResponseBody {

	/**
	 * Information about the item to be renamed.
	 */
	private RenameInfo info;

	/**
	 * An array of span groups (one per file) that refer to the item to be
	 * renamed.
	 */
	private List<SpanGroup> locs;

	public RenameInfo getInfo() {
		return info;
	}

	public List<SpanGroup> getLocs() {
		return locs;
	}
}
