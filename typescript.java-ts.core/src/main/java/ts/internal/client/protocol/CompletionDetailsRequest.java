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
package ts.internal.client.protocol;

import java.util.List;

import com.google.gson.JsonObject;

import ts.client.CommandNames;
import ts.client.completions.CompletionEntryDetails;

/**
 * Completion entry details request; value of command field is
 * "completionEntryDetails". Given a file location (file, line, col) and an
 * array of completion entry names return more detailed information for each
 * completion entry.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class CompletionDetailsRequest extends FileLocationRequest<CompletionDetailsRequestArgs> {

	public CompletionDetailsRequest(String file, int line, int offset, String prefix, String[] entryNames) {
		super(CommandNames.CompletionEntryDetails.getName(),
				new CompletionDetailsRequestArgs(file, line, offset, prefix, entryNames));
	}

	@Override
	public Response<List<CompletionEntryDetails>> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, CompletionDetailsResponse.class);
	}

}
