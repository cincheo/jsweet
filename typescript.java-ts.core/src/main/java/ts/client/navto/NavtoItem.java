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
package ts.client.navto;

import ts.client.FileSpan;
import ts.client.IKindProvider;

/**
 * An item found in a navto response.
 */
public class NavtoItem extends FileSpan implements IKindProvider {

	/**
	 * The symbol's name.
	 */
	private String name;

	/**
	 * The symbol's kind (such as 'className' or 'parameterName').
	 */
	private String kind;

	/**
	 * exact, substring, or prefix.
	 */
	private String matchKind;

	/**
	 * If this was a case sensitive or insensitive match.
	 */
	private Boolean isCaseSensitive;

	/**
	 * Optional modifiers for the kind (such as 'public').
	 */
	private String kindModifiers;

	/**
	 * Name of symbol's container symbol (if any); for example, the class name if
	 * symbol is a class member.
	 */
	private String containerName;

	/**
	 * Kind of symbol's container symbol (if any).
	 */
	private String containerKind;

	public String getName() {
		return name;
	}

	@Override
	public String getKind() {
		return kind;
	}

	public String getMatchKind() {
		return matchKind;
	}

	public Boolean getIsCaseSensitive() {
		return isCaseSensitive;
	}

	@Override
	public String getKindModifiers() {
		return kindModifiers;
	}

	public String getContainerName() {
		return containerName;
	}

	public String getContainerKind() {
		return containerKind;
	}

}
