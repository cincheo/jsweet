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
package ts.npm;

import java.util.HashMap;
import java.util.Map;

import ts.OS;

/**
 * Npm modules manager.
 *
 */
public class NpmModulesManager {

	private final OS os;
	private final Map<String, NpmModule> modules;

	public NpmModulesManager(OS os) {
		this.os = os;
		this.modules = new HashMap<>();
	}

	public NpmModule getNPMModule(String moduleName) {
		NpmModule module = modules.get(moduleName);
		if (module == null) {
			module = new NpmModule(moduleName, os);
			modules.put(moduleName, module);
		}
		return module;
	}

	public void resetCache(String moduleName) {
		modules.remove(moduleName);
	}

}
