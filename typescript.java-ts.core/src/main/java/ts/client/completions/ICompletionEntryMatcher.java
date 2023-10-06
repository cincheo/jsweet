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
package ts.client.completions;

import ts.internal.matcher.LCSS;

/**
 * Matcher for completion entry.
 * 
 */
public interface ICompletionEntryMatcher {

	public static ICompletionEntryMatcher LCS = new ICompletionEntryMatcher() {

		@Override
		public int[] bestSubsequence(String completion, String token) {
			return LCSS.bestSubsequence(completion, token);
		}

	};
	
	public static ICompletionEntryMatcher START_WITH_MATCHER = new ICompletionEntryMatcher() {

		@Override
		public int[] bestSubsequence(String completion, String token) {
			if (!completion.startsWith(token)) {
				return null;
			}
			return new int[] { 0, token.length() - 1 };
		}
	};

	int[] bestSubsequence(String completion, String token);

}
