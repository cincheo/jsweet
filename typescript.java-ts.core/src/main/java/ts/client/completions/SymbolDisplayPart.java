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
package ts.client.completions;

/**
 * Part of a symbol description.
 * 
 */
public class SymbolDisplayPart {

	private static final String PARAMETER_NAME_KIND = "parameterName";

	/**
	 * Text of an item describing the symbol.
	 */
	private String text;

	/**
	 * The symbol's kind (such as 'className' or 'parameterName' or plain
	 * 'text').
	 */
	private String kind;

	public String getText() {
		return text;
	}

	public String getKind() {
		return kind;
	}

	public boolean isParameterName() {
		return PARAMETER_NAME_KIND.equals(kind);
	}

}
