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

import ts.client.ITypeScriptServiceClient;

/**
 * TypeScript {@link CompletionEntry} factory.
 *
 */
public interface ICompletionEntryFactory {

	/**
	 * Default factory.
	 */
	public static final ICompletionEntryFactory DEFAULT = new ICompletionEntryFactory() {

		@Override
		public CompletionEntry create(ICompletionEntryMatcher matcher, String fileName, int line, int offset,
				ITypeScriptServiceClient client) {
			return new CompletionEntry(matcher, fileName, line, offset, client);
		}
	};

	/**
	 * Create {@link CompletionEntry} instance.
	 * 
	 * @param matcher
	 * @param fileName
	 * @param line
	 * @param offset
	 * @param client
	 * @return
	 */
	public CompletionEntry create(ICompletionEntryMatcher matcher, String fileName, int line, int offset,
			ITypeScriptServiceClient client);

}
