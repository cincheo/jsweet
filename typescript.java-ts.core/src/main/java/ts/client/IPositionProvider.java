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
 * Position provider.
 *
 */
public interface IPositionProvider {

	/**
	 * Returns the location (line/offset) from the given position.
	 * 
	 * @param position
	 * @return the location (line/offset) from the given position.
	 * @throws TypeScriptException
	 */
	Location getLocation(int position) throws TypeScriptException;

	/**
	 * Returns the position from the given line, offset.
	 * 
	 * @param line
	 * @param offset
	 * @return the position from the given line, offset.
	 * @throws TypeScriptException
	 */
	int getPosition(int line, int offset) throws TypeScriptException;

	/**
	 * Returns the position from the given location (line/offset).
	 * 
	 * @param loc
	 * @return the position from the given location (line/offset)
	 * @throws TypeScriptException
	 */
	int getPosition(Location loc) throws TypeScriptException;

}
