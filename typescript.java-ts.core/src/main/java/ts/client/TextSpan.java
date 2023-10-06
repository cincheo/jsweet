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
package ts.client;

import ts.TypeScriptException;

/**
 * Object found in response messages defining a span of text in source code.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class TextSpan {

	/**
	 * First character of the definition.
	 */
	private Location start;

	/**
	 * One character past last character of the definition.
	 */
	private Location end;

	public Location getStart() {
		return start;
	}

	public Location getEnd() {
		return end;
	}
	
	public boolean contains(int position) throws TypeScriptException {
		int positionStart = start.getPosition();
		return positionStart <= position && position < (positionStart + getLength());
	}

	public int getLength() throws TypeScriptException {
		int positionStart = start.getPosition();
		int positionEnd = end.getPosition();
		return positionEnd - positionStart;
	}
}
