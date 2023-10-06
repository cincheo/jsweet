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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ts.TypeScriptException;
import ts.utils.FileUtils;

/**
 * Abstract class for node.js process.
 *
 */
public abstract class AbstractNodejsProcess implements INodejsProcess {

	/**
	 * The node.js base dir. If null, it uses installed node.js
	 */
	protected final File nodejsFile;

	/**
	 * The project dir where tsconfig.json is hosted.
	 */
	protected final File projectDir;

	private final INodejsLaunchConfiguration launchConfiguration;

	/**
	 * Process listeners.
	 */
	protected final List<INodejsProcessListener> listeners;

	public AbstractNodejsProcess(File nodejsFile, File projectDir) throws TypeScriptException {
		this(nodejsFile, projectDir, null);
	}

	/**
	 * Nodejs process constructor.
	 * 
	 * @param nodejsFile
	 *            the node.exe file.
	 * @param projectDir
	 *            the project base dir where tsconfig.json is hosted.
	 * @throws TernException
	 */
	public AbstractNodejsProcess(File nodejsFile, File projectDir, INodejsLaunchConfiguration launchConfiguration)
			throws TypeScriptException {
		this.projectDir = checkProjectDir(projectDir);
		this.nodejsFile = checkNodejsFile(nodejsFile);
		this.listeners = new ArrayList<INodejsProcessListener>();
		this.launchConfiguration = launchConfiguration;
	}

	private File checkProjectDir(File projectDir) throws TypeScriptException {
		if (projectDir == null) {
			throw new TypeScriptException("project directory cannot be null");
		}
		if (!projectDir.exists()) {
			throw new TypeScriptException("Cannot find project directory " + FileUtils.getPath(projectDir));
		}
		return projectDir;
	}

	private File checkNodejsFile(File nodejsFile) throws TypeScriptException {
		if (nodejsFile == null) {
			// node.js file cannot be null. In this case it uses installed
			// node.js
			return null;
		}
		if (!nodejsFile.exists()) {
			throw new TypeScriptException("Cannot find node file " + FileUtils.getPath(nodejsFile));
		}
		return nodejsFile;
	}

	protected List<String> createNodeArgs() {
		if (launchConfiguration == null) {
			return null;
		}
		return launchConfiguration.createNodeArgs();
	}
	
	protected Map<String, String> createNodeEnvironmentVariables() {
		if (launchConfiguration == null) {
			return null;
		}
		return launchConfiguration.createNodeEnvironmentVariables();
	}

	/**
	 * return the project dir where tsconfig.json is hosted.
	 * 
	 * @return
	 */
	public File getProjectDir() {
		return projectDir;
	}

	/**
	 * Add the given process listener.
	 * 
	 * @param listener
	 */
	public void addProcessListener(INodejsProcessListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/**
	 * Remove the given process listener.
	 * 
	 * @param listener
	 */
	public void removeProcessListener(INodejsProcessListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Notify start process.
	 */
	protected void notifyCreateProcess(List<String> commands, File projectDir) {
		synchronized (listeners) {
			for (INodejsProcessListener listener : listeners) {
				listener.onCreate(this, commands, projectDir);
			}
		}
	}

	/**
	 * Notify start process.
	 * 
	 * @param startTime
	 *            time when node.js process is started.
	 */
	protected void notifyStartProcess(long startTime) {
		synchronized (listeners) {
			for (INodejsProcessListener listener : listeners) {
				listener.onStart(this);
			}
		}
	}

	/**
	 * Notify stop process.
	 */
	protected void notifyStopProcess() {
		synchronized (listeners) {
			for (INodejsProcessListener listener : listeners) {
				listener.onStop(this);
			}
		}
	}

	/**
	 * Notify data process.
	 * 
	 * @param jsonObject
	 */
	protected void notifyMessage(String message) {
		synchronized (listeners) {
			for (INodejsProcessListener listener : listeners) {
				listener.onMessage(this, message);
			}
		}
	}

	/**
	 * Notify error process.
	 */
	protected void notifyErrorProcess(String line) {
		synchronized (listeners) {
			for (INodejsProcessListener listener : listeners) {
				listener.onError(AbstractNodejsProcess.this, line);
			}
		}
	}

}
