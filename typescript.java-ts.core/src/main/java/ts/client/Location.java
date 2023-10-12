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

import ts.TypeScriptException;

/**
 * Bean for location line/offset used by tsserver.
 *
 */
public class Location {

	private static final int NO_POSITION = -1;

	private final IPositionProvider positionProvider;
	private int line;
	private int offset;
	private int position;

	public Location(IPositionProvider positionProvider) {
		this.positionProvider = positionProvider;
		this.position = NO_POSITION;
	}

	public Location() {
		this(null);
	}

	public Location(int line, int offset, int position) {
		this();
		this.line = line;
		this.offset = offset;
		this.position = position;
	}

	public Location(int line, int offset) {
		this(line, offset, NO_POSITION);
	}

	/**
	 * Returns the line location.
	 * 
	 * @return the line location.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Returns the offset location.
	 * 
	 * @return the offset location.
	 */
	public int getOffset() {
		return offset;
	}

	public int getPosition() {
		if (position == NO_POSITION && positionProvider != null) {
			try {
				position = positionProvider.getPosition(line, offset);
			} catch (TypeScriptException e) {
				e.printStackTrace();
			}
		}
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
