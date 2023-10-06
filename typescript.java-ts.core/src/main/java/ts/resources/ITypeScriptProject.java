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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ts.TypeScriptException;
import ts.client.ISupportable;
import ts.client.ITypeScriptClientListener;
import ts.client.ITypeScriptServiceClient;
import ts.client.diagnostics.DiagnosticEvent;
import ts.client.navto.NavtoItem;
import ts.cmd.tsc.CompilerOptionCapability;
import ts.cmd.tsc.ITypeScriptCompiler;
import ts.cmd.tslint.ITypeScriptLint;

/**
 * TypeScript project API.
 *
 */
public interface ITypeScriptProject {

	/**
	 * Returns associated tsclient if any. This call may result in creating one if
	 * it hasn't been created already.
	 * 
	 * @return
	 * @throws TypeScriptException
	 */
	ITypeScriptServiceClient getClient() throws TypeScriptException;

	/**
	 * Returns the tsc compiler.
	 * 
	 * @return
	 */
	ITypeScriptCompiler getCompiler() throws TypeScriptException;

	List<String> getSupportedCodeFixes() throws TypeScriptException;

	boolean canFix(Integer errorCode);

	ITypeScriptFile getOpenedFile(String fileName);

	void dispose() throws TypeScriptException;

	<T> T getData(String key);

	void setData(String key, Object value);

	// -------------- TypeScript server.

	void addServerListener(ITypeScriptClientListener listener);

	void removeServerListener(ITypeScriptClientListener listener);

	void disposeServer();

	void disposeCompiler();

	boolean isServerDisposed();

	/**
	 * Returns the tslint linter.
	 * 
	 * @return
	 * @throws TypeScriptException
	 */
	ITypeScriptLint getTslint() throws TypeScriptException;

	ITypeScriptProjectSettings getProjectSettings();

	void disposeTslint();

	/**
	 * Returns true if the given tsserver command can be supported by the TypeScript
	 * version configured for the project and false otherwise.
	 * 
	 * @param command
	 * @return true if the given tsserver command can be supported by the TypeScript
	 *         version configured for the project and false otherwise.
	 */
	boolean canSupport(ISupportable command);

	/**
	 * Returns true if the given tsc compiler option can be supported by the
	 * TypeScript version configured for the project and false otherwise.
	 * 
	 * @param option
	 * @return true if the given tsc compiler option can be supported by the
	 *         TypeScript version configured for the project and false otherwise.
	 */
	boolean canSupport(CompilerOptionCapability option);

	CompletableFuture<List<DiagnosticEvent>> geterrForProject(String file, int delay) throws TypeScriptException;

	CompletableFuture<List<NavtoItem>> navto(String fileName, String searchValue, Integer maxResultCount,
			Boolean currentFileOnly, String projectFileName) throws TypeScriptException;

}
