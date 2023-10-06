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
package ts.nodejs;

import ts.TypeScriptException;

/**
 * Node.js process API.
 *
 */
public interface INodejsProcess {

	/**
	 * Joint to the stdout thread;
	 * 
	 * @throws InterruptedException
	 */
	void join() throws InterruptedException;

	/**
	 * Add the given process listener.
	 * 
	 * @param listener
	 */
	void addProcessListener(INodejsProcessListener listener);

	/**
	 * Remove the given process listener.
	 * 
	 * @param listener
	 */
	void removeProcessListener(INodejsProcessListener listener);

	/**
	 * Start the node.js process.
	 */
	void start();

	/**
	 * Returns true if the node.js process is started and false otherwise.
	 * 
	 * @return true if the node.js process is started and false otherwise.
	 */
	boolean isStarted();

	/**
	 * Kill the node.js process.
	 */
	public void kill();

	/**
	 * Send request.
	 * 
	 * @param request
	 * @throws TypeScriptException
	 */
	void sendRequest(String request) throws TypeScriptException;
}
