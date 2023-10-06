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
package ts.resources;

import java.io.File;

import ts.TypeScriptException;
import ts.client.completions.ICompletionEntryMatcher;
import ts.client.format.FormatCodeSettings;
import ts.cmd.tslint.TslintSettingsStrategy;

/**
 * TypeScript project settings API.
 *
 */
public interface ITypeScriptProjectSettings {

	/**
	 * Returns the strategy to synchronize editor content with tsserver.
	 * 
	 * @return the strategy to synchronize editor content with tsserver.
	 */
	SynchStrategy getSynchStrategy();

	/**
	 * Returns the node.js install path.
	 * 
	 * @return the node.js install path.
	 */
	File getNodejsInstallPath() throws TypeScriptException;

	/**
	 * Returns the nodejs version.
	 * 
	 * @return the nodejs version.
	 */
	String getNodeVersion();

	/**
	 * Returns the typescript/bin/tsc file to execute.
	 * 
	 * @return the typescript/bin/tsc file to execute.
	 */
	File getTscFile() throws TypeScriptException;

	/**
	 * Returns the version of the TypeScript runtime and null otherwise.
	 * 
	 * @return the version of TypeScript runtime and null otherwise.
	 */
	String getTypeScriptVersion();

	/**
	 * Returns the typescript/bin/tsserver file to execute.
	 * 
	 * @return the typescript/bin/tsserver file to execute.
	 * @throws TypeScriptException
	 */
	File getTypesScriptDir() throws TypeScriptException;

	File getTsserverPluginsFile() throws TypeScriptException;

	/**
	 * Returns the tslint/bin/tslint file to execute.
	 * 
	 * @return the tslint/bin/tslint file to execute.
	 */
	File getTslintFile() throws TypeScriptException;

	/**
	 * Returns the completion entry matcher to use to filter TypeScript
	 * completion entries.
	 */
	ICompletionEntryMatcher getCompletionEntryMatcher();

	boolean isUseCodeSnippetsOnMethodSuggest();

	// ------------- tslint settings

	TslintSettingsStrategy getTslintStrategy();

	File getCustomTslintJsonFile();

	/**
	 * Dispose the settings.
	 */
	void dispose();

	FormatCodeSettings getFormatOptions();

	/**
	 * Returns true if telemetry must be enable and false otherwise.
	 * 
	 * @return true if telemetry must be enable and false otherwise.
	 */
	boolean isEnableTelemetry();

	boolean isDisableAutomaticTypingAcquisition();

}
