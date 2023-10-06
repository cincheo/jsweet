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
package ts.nodejs;

import java.io.File;
import java.util.List;

/**
 * Listener for node.js process.
 * 
 * @author azerr
 * 
 */
public interface INodejsProcessListener {

	/**
	 * Callback called when the given node.js process is created.
	 * 
	 * @param process
	 * @param commands
	 * @param projectDir
	 */
	void onCreate(INodejsProcess process, List<String> commands, File projectDir);

	/**
	 * Callback called when the given node.js process start.
	 * 
	 * @param process
	 */
	void onStart(INodejsProcess process);

	/**
	 * Callback called when the given node.js process send data.
	 * 
	 * @param process
	 * @param jsonObject
	 *            the data.
	 */
	void onMessage(INodejsProcess process, String response);

	/**
	 * Callback called when the given node.js process stop.
	 * 
	 * @param process
	 */
	void onStop(INodejsProcess process);

	/**
	 * Callback called when the given node.js throws error.
	 * 
	 * @param process
	 * @param line
	 *            the error.
	 */
	void onError(INodejsProcess process, String line);

}
