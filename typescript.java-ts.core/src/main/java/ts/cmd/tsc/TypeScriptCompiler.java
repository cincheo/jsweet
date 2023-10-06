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
package ts.cmd.tsc;

import java.io.File;

import ts.cmd.AbstractCmd;

public class TypeScriptCompiler extends AbstractCmd<CompilerOptions> implements ITypeScriptCompiler {

	private static final String TSC_FILE_TYPE = "tsc";

	public TypeScriptCompiler(File tscFile, File nodejsFile) {
		super(tscFile, nodejsFile, TSC_FILE_TYPE);
	}

	@Override
	public void dispose() {

	}

}
