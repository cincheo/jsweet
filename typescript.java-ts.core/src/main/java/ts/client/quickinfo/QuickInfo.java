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
package ts.client.quickinfo;

import ts.client.Location;

public class QuickInfo {

	/**
	 * The symbol's kind (such as 'className' or 'parameterName' or plain
	 * 'text').
	 */
	private String kind;

	/**
	 * Optional modifiers for the kind (such as 'public').
	 */
	private String kindModifiers;

	/**
	 * Starting file location of symbol.
	 */
	private Location start;

	/**
	 * One past last character of symbol.
	 */
	private Location end;

	/**
	 * Type and kind of symbol.
	 */
	private String displayString;

	/**
	 * Documentation associated with symbol.
	 */
	private String documentation;

	public String getKind() {
		return kind;
	}

	public String getKindModifiers() {
		return kindModifiers;
	}

	public Location getStart() {
		return start;
	}

	public Location getEnd() {
		return end;
	}

	public String getDisplayString() {
		return displayString;
	}

	public String getDocumentation() {
		return documentation;
	}
}
