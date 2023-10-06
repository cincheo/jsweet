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

import java.io.IOException;

public class TypeScriptResourcesManager {

	private static final ConfigurableTypeScriptResourcesManager INSTANCE = ConfigurableTypeScriptResourcesManager
			.getInstance();

	/**
	 * Returns a TypeScript project object associated with the specified
	 * resource. May return null if resource doesn't point at a valid TypeScript
	 * project.
	 * 
	 * @param project
	 * @return a TypeScript project object associated with the specified
	 *         resource. May return null if resource doesn't point at a valid
	 *         TypeScript project.
	 * @throws IOException
	 */
	public static ITypeScriptProject getTypeScriptProject(Object project) {
		try {
			return INSTANCE.getTypeScriptProject(project, false);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns a TypeScript project object associated with the specified
	 * resource. May return null if resource doesn't point at a valid TypeScript
	 * project.
	 * 
	 * @param project
	 * @param force
	 *            true if .TypeScript-project must be created if it doesn't
	 *            exists, and false otherwise.
	 * 
	 * @return a TypeScript project object associated with the specified
	 *         resource. May return null if resource doesn't point at a valid
	 *         TypeScript project.
	 * @throws IOException
	 */
	public static ITypeScriptProject getTypeScriptProject(Object project, boolean force) throws IOException {
		return INSTANCE.getTypeScriptProject(project, force);
	}

	/**
	 * Returns true if the given file object is a TypeScript file and false
	 * otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a TypeScript file and false
	 *         otherwise.
	 */
	public static boolean isTSFile(Object fileObject) {
		return INSTANCE.isTSFile(fileObject);
	}
	
	public static boolean isJSFile(Object fileObject) {
		return INSTANCE.isJsFile(fileObject);
	}
}
