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

/**
 * Npm constants.
 *
 */
public final class NpmConstants {

	private NpmConstants() {
	}

	public static final String PACKAGE_JSON = "package.json"; //$NON-NLS-1$
	public static final String NODE_MODULES = "node_modules"; //$NON-NLS-1$
	public static final String NPM = "npm"; //$NON-NLS-1$
	public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$

	// Default package.json values
	public static final String DEFAULT_VERSION = "0.0.0"; //$NON-NLS-1$
	public static final String DEFAULT_DESCRIPTION = "Generated with typescript.java"; //$NON-NLS-1$
	public static final String DEFAULT_LICENSE = "MIT"; //$NON-NLS-1$

}
