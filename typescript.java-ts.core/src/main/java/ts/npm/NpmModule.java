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

import java.io.IOException;
import java.util.List;

import ts.OS;

/**
 * Npm module.
 * 
 * @author azerr
 *
 */
public class NpmModule {

	private final String name;
	private final OS os;
	private List<String> versions;

	NpmModule(String name, OS os) {
		this.name = name;
		this.os = os;
	}

	public List<String> getAvailableVersions() throws IOException {
		if (!isLoaded()) {
			versions = NpmHelper.getVersions(name, os);
		}
		return versions;
	}

	public boolean isLoaded() {
		return versions != null;
	}

	public String getName() {
		return name;
	}

}
