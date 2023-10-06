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

/**
 * Supportable API.
 *
 */
public interface ISupportable {

	/**
	 * Returns true if the given tsserver command can be supported by the
	 * TypeScript version configured for the project and false otherwise.
	 * 
	 * @param command
	 * @return true if the given tsserver command can be supported by the
	 *         TypeScript version configured for the project and false
	 *         otherwise.
	 */
	boolean canSupport(String version);

}
