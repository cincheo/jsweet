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
import java.util.concurrent.TimeUnit;

import ts.TypeScriptException;
import ts.ScriptElementKind;
import ts.client.IKindProvider;
import ts.client.ITypeScriptServiceClient;
import ts.client.TextSpan;
import ts.internal.matcher.LCSS;
import ts.utils.StringUtils;

/**
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class CompletionEntry implements IKindProvider {

	// Negative value ensures subsequence matches have a lower relevance than
	// standard JDT or template proposals
	private static final int SUBWORDS_RANGE_START = -9000;
	private static final int minPrefixLengthForTypes = 1;

	/**
	 * The symbol's name.
	 */
	private String name;
	/**
	 * The symbol's kind (such as 'className' or 'parameterName').
	 */
	private String kind;
	/**
	 * Optional modifiers for the kind (such as 'public').
	 */
	private String kindModifiers;
	/**
	 * A string that is used for comparing completion items so that they can be
	 * ordered. This is often the same as the name but may be different in
	 * certain circumstances.
	 */
	private String sortText;
	/**
	 * An optional span that indicates the text to be replaced by this
	 * completion item. If present, this span should be used instead of the
	 * default one.
	 */
	private TextSpan replacementSpan;

	/**
	 * Indicating if commiting this completion entry will require additional
	 * code action to be made to avoid errors. The code action is normally
	 * adding an additional import statement.
	 */
	private Boolean hasAction;

	private Boolean isFunction;

	private int relevance;

	private final String fileName;
	private final int line;
	private final int offset;

	private final transient ICompletionEntryMatcher matcher;

	private final transient ITypeScriptServiceClient client;

	private List<CompletionEntryDetails> entryDetails;

	public CompletionEntry(ICompletionEntryMatcher matcher, String fileName, int line, int offset,
			ITypeScriptServiceClient client) {
		this.matcher = matcher;
		this.fileName = fileName;
		this.line = line;
		this.offset = offset;
		this.client = client;
	}

	/**
	 * Returns the file name where completion was done.
	 * 
	 * @return the file name where completion was done.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns the line number where completion was done.
	 * 
	 * @return the line number where completion was done.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Returns the offset where completion was done.
	 * 
	 * @return the offset where completion was done.
	 */
	public int getOffset() {
		return offset;
	}

	public String getName() {
		return name;
	}

	public String getKind() {
		return kind;
	}

	public String getKindModifiers() {
		return kindModifiers;
	}

	public String getSortText() {
		return sortText;
	}

	public TextSpan getReplacementSpan() {
		return replacementSpan;
	}

	public boolean isFunction() {
		if (isFunction == null) {
			ScriptElementKind tsKind = ScriptElementKind.getKind(getKind());
			isFunction = (tsKind != null && (ScriptElementKind.CONSTRUCTOR == tsKind || ScriptElementKind.FUNCTION == tsKind
					|| ScriptElementKind.METHOD == tsKind));
		}
		return isFunction;
	}

	public int getRelevance() {
		return relevance;
	}

	public boolean updatePrefix(String prefix) {
		Integer relevanceBoost = null;
		int[] bestSequence = null;
		if (StringUtils.isEmpty(prefix)) {
			relevanceBoost = 0;
		} else {
			bestSequence = matcher.bestSubsequence(name, prefix);
			if ((bestSequence != null && bestSequence.length > 0)) {
				relevanceBoost = 0;
				if (name.equals(prefix)) {
					if (minPrefixLengthForTypes < prefix.length()) {
						relevanceBoost = 16 * (RelevanceConstants.R_EXACT_NAME + RelevanceConstants.R_CASE);
					}
				} else if (name.equalsIgnoreCase(prefix)) {
					if (minPrefixLengthForTypes < prefix.length()) {
						relevanceBoost = 16 * RelevanceConstants.R_EXACT_NAME;
					}
				} else if (startsWithIgnoreCase(prefix, name)) {
					// Don't adjust score
				} else {
					int score = LCSS.scoreSubsequence(bestSequence);
					relevanceBoost = SUBWORDS_RANGE_START + score;
				}
			}
		}
		if (relevanceBoost != null) {
			relevance = relevanceBoost;
			return true;
		}
		return false;
	}

	private boolean startsWithIgnoreCase(String prefix, String name) {
		return prefix.toUpperCase().startsWith(name.toUpperCase());
	}

	public ICompletionEntryMatcher getMatcher() {
		return matcher;
	}

	public List<CompletionEntryDetails> getEntryDetails() throws TypeScriptException {
		if (entryDetails != null) {
			return entryDetails;
		}
		try {
			this.entryDetails = client.completionEntryDetails(fileName, line, offset, new String[] { name }, this)
					.get(5000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.entryDetails;
	}

	public boolean hasActions() {
		return hasAction != null && hasAction;
	}

}
