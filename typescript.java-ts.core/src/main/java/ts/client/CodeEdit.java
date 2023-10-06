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
package ts.client;

/**
 * Object found in response messages defining an editing instruction for a span
 * of text in source code. The effect of this instruction is to replace the text
 * starting at start and ending one character before end with newText. For an
 * insertion, the text span is empty. For a deletion, newText is empty.
 */
public class CodeEdit {

	/**
	 * First character of the text span to edit.
	 */
	private Location start;

	/**
	 * One character past last character of the text span to edit.
	 */
	private Location end;

	/**
	 * Replace the span defined above with this string (may be the empty
	 * string).
	 */
	private String newText;

	/**
	 * Returns first character of the text span to edit.
	 * 
	 * @return first character of the text span to edit.
	 */
	public Location getStart() {
		return start;
	}

	/**
	 * Returns one character past last character of the text span to edit.
	 * 
	 * @return one character past last character of the text span to edit.
	 */
	public Location getEnd() {
		return end;
	}

	/**
	 * Replace the span defined above with this string (may be the empty string)
	 * 
	 * @return replace the span defined above with this string (may be the empty
	 *         string)
	 */
	public String getNewText() {
		return newText;
	}
}
