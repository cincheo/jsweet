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

import ts.client.TextSpan;

/**
 * A group of text spans, all in 'file'.
 *
 */
public class SpanGroup {

	/**
	 * The file to which the spans apply
	 */
	private String file;

	/**
	 * The text spans in this group
	 */
	private List<TextSpan> locs;

	public String getFile() {
		return file;
	}

	public List<TextSpan> getLocs() {
		return locs;
	}

}
