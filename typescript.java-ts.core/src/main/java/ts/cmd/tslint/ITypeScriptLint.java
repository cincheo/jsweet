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
package ts.cmd.tslint;

import java.io.File;
import java.util.List;

import ts.TypeScriptException;
import ts.nodejs.INodejsProcess;
import ts.nodejs.INodejsProcessListener;

public interface ITypeScriptLint {

	/**
	 * Execute 'tslint' command from the given directory.
	 * 
	 * @param baseDir
	 *            the directory where 'tsc' must be executed.
	 * @return
	 * @throws TypeScriptException
	 */
	INodejsProcess execute(File baseDir, TSLintOptions options, List<String> filenames, INodejsProcessListener listener)
			throws TypeScriptException;

	List<String> createCommands(TSLintOptions options, List<String> filenames);
}
