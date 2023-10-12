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

import java.util.List;

/**
 * 
 * The body of a "references" response message.
 *
 */
public class ReferencesResponseBody {

	/**
	 * The file locations referencing the symbol.
	 */
	private List<ReferencesResponseItem> refs;

	/**
	 * The name of the symbol.
	 */
	private String symbolName;

	/**
	 * The start character offset of the symbol (on the line provided by the
	 * references request).
	 */
	private int symbolStartOffset;

	/**
	 * The full display name of the symbol.
	 */
	private String symbolDisplayString;

	public List<ReferencesResponseItem> getRefs() {
		return refs;
	}

	public String getSymbolName() {
		return symbolName;
	}

	public int getSymbolStartOffset() {
		return symbolStartOffset;
	}

	public String getSymbolDisplayString() {
		return symbolDisplayString;
	}
}
