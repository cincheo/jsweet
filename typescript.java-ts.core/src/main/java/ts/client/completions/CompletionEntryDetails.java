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

import java.util.List;

import ts.client.codefixes.CodeAction;

/**
 * Additional completion entry details, available on demand
 */
public class CompletionEntryDetails {

	/**
	 * The symbol's name.
	 */
	String name;
	/**
	 * The symbol's kind (such as 'className' or 'parameterName').
	 */
	String kind;
	/**
	 * Optional modifiers for the kind (such as 'public').
	 */
	String kindModifiers;
	/**
	 * Display parts of the symbol (similar to quick info).
	 */
	List<SymbolDisplayPart> displayParts;

	/**
	 * Documentation strings for the symbol.
	 */
	List<SymbolDisplayPart> documentation;

	/**
	 * JSDoc tags for the symbol.
	 */
	List<JSDocTagInfo> tags;

	/**
	 * The associated code actions for this entry
	 */
	List<CodeAction> codeActions;

	public String getName() {
		return name;
	}

	public String getKind() {
		return kind;
	}

	public String getKindModifiers() {
		return kindModifiers;
	}

	public List<SymbolDisplayPart> getDisplayParts() {
		return displayParts;
	}

	public List<SymbolDisplayPart> getDocumentation() {
		return documentation;
	}

	public List<JSDocTagInfo> getTags() {
		return tags;
	}

	public List<CodeAction> getCodeActions() {
		return codeActions;
	}
}
