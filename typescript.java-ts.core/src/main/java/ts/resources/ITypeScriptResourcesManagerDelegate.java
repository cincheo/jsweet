/**
 *  Copyright (c) 2015-2016 Angelo ZERR
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

/**
 * TypeScript resources manager.
 *
 */
public interface ITypeScriptResourcesManagerDelegate {

	/**
	 * Return a TypeScript project associated with the specified resource. New
	 * project should be created only if it is a first such call on the
	 * resource.
	 * 
	 * @param project
	 * @param force
	 *            true if tsconfig.json project must be created if it doesn't
	 *            exists, and false otherwise.
	 * @return
	 * @throws IOException
	 */
	ITypeScriptProject getTypeScriptProject(Object project, boolean force) throws IOException;

	/**
	 * Returns true if the given file object is a JavaScript file and false
	 * otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a JavaScript file and false
	 *         otherwise.
	 */
	boolean isJsFile(Object fileObject);

	/**
	 * Returns true if the given file object is a JXS file and false otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a JSX file and false otherwise.
	 */
	boolean isJsxFile(Object fileObject);

	/**
	 * Returns true if the given file object is a TypeScript/JXS file and false
	 * otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a TypeScript/JXS file and false
	 *         otherwise.
	 */
	boolean isTsxFile(Object fileObject);

	/**
	 * Returns true if the given file object is a TypeScript file and false
	 * otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a TypeScript file and false
	 *         otherwise.
	 */
	boolean isTsFile(Object fileObject);

	/**
	 * Returns true if the given file object is a TypeScript or TypeScript/JSX
	 * file and false otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a TypeScript or TypeScript/JSX
	 *         file and false otherwise.
	 */
	boolean isTsOrTsxFile(Object fileObject);

	/**
	 * Returns true if the given file object is a TypeScript definition file
	 * *.d.ts and false otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a TypeScript definition file
	 *         *.d.ts and false otherwise.
	 */
	boolean isDefinitionTsFile(Object fileObject);

	/**
	 * Returns true if the given file object is a TypeScript or TypeScript/JSX
	 * or JSX file and false otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a TypeScript or TypeScript/JSX
	 *         or JSX file and false otherwise.
	 */
	boolean isTsOrTsxOrJsxFile(Object fileObject);

	/**
	 * Returns true if the given file object is TypeScript/JSX or JSX file and
	 * false otherwise.
	 * 
	 * @param fileObject
	 * @return true if the given file object is a TypeScript or TypeScript/JSX
	 *         or JSX file and false otherwise.
	 */
	boolean isTsxOrJsxFile(Object fileObject);

	/**
	 * Returns the corresponding TypeScript file from the js or js.map file and
	 * null otherwise.
	 * 
	 * @param file
	 *            *.js or *.js.map file
	 * @return the corresponding TypeScript file from the js or js.map file and
	 *         null otherwise.
	 */
	String getTypeScriptFilename(Object file);

}
