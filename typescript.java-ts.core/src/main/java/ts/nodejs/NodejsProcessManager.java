/**
 *  Copyright (c) 2015-2016 Angelo ZERR and Genuitec LLC.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Piotr Tomiak <piotr@genuitec.com> - support for tern.js debugging
 */
package ts.nodejs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ts.TypeScriptException;

/**
 * {@link NodejsProcess} manager.
 * 
 */
public class NodejsProcessManager {

	private final static NodejsProcessManager INSTANCE = new NodejsProcessManager();

	/**
	 * Returns the manager singleton.
	 * 
	 * @return
	 */
	public static NodejsProcessManager getInstance() {
		return INSTANCE;
	}

	/**
	 * List of node.js tern processes created.
	 */
	private final List<INodejsProcess> processes;

	/**
	 * Listener added for each process created.
	 */
	private final INodejsProcessListener listener = new NodejsProcessAdapter() {

		@Override
		public void onStart(INodejsProcess server) {
			synchronized (NodejsProcessManager.this.processes) {
				// here the process is started, add it to the list of processes.
				NodejsProcessManager.this.processes.add(server);
			}
		}

		@Override
		public void onStop(INodejsProcess server) {
			synchronized (NodejsProcessManager.this.processes) {
				// here the process is stopped, remove it to the list of
				// processes.
				NodejsProcessManager.this.processes.remove(server);
			}
		}

	};

	public NodejsProcessManager() {
		this.processes = new ArrayList<INodejsProcess>();
	}

	/**
	 * Create the process with the given tern project base dir where
	 * tsconfig.json is hosted and the given base dir of node.js exe.
	 * 
	 * @param projectDir
	 *            project base dir where tsconfig.json is hosted.
	 * @param tsFile
	 *            the tsserver, tsc file.
	 * @param nodejsFile
	 *            the nodejs exe file
	 * @return an instance of the node tern process.
	 * @throws TypeScriptException
	 */
	public INodejsProcess create(File projectDir, File tsFile, File nodejsFile,
			INodejsLaunchConfiguration configuration, String fileType) throws TypeScriptException {
		INodejsProcess process = new NodejsProcess(projectDir, tsFile, nodejsFile, configuration, fileType);
		process.addProcessListener(listener);
		return process;
	}

	/**
	 * Kill all node.js processes created by the manager.
	 */
	public void dispose() {
		synchronized (processes) {
			for (INodejsProcess server : processes) {
				try {
					server.kill();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			processes.clear();
		}
	}

}
