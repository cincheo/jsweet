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
package ts.client.rename;

/**
 * Information about the item to be renamed.
 *
 */
public class RenameInfo {

	/**
	 * True if item can be renamed.
	 */
	private boolean canRename;

	/**
	 * Error message if item can not be renamed.
	 */
	private String localizedErrorMessage;

	/**
	 * Display name of the item to be renamed.
	 */
	private String displayName;

	/**
	 * Full display name of item to be renamed.
	 */
	private String fullDisplayName;

	/**
	 * The items's kind (such as 'className' or 'parameterName' or plain
	 * 'text').
	 */
	private String kind;

	/**
	 * Optional modifiers for the kind (such as 'public').
	 */
	private String kindModifiers;

	public boolean isCanRename() {
		return canRename;
	}

	public String getLocalizedErrorMessage() {
		return localizedErrorMessage;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getFullDisplayName() {
		return fullDisplayName;
	}

	public String getKind() {
		return kind;
	}

	public String getKindModifiers() {
		return kindModifiers;
	}

}
