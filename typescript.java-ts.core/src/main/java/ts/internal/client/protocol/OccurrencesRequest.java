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
import ts.client.occurrences.OccurrencesResponseItem;

/**
 * Get occurrences request; value of command field is "occurrences". Return
 * response giving spans that are relevant in the file at a given line and
 * column.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class OccurrencesRequest extends FileLocationRequest<FileLocationRequestArgs> {

	public OccurrencesRequest(String file, int line, int offset) {
		super(CommandNames.Occurrences.getName(), new FileLocationRequestArgs(file, line, offset));
	}

	@Override
	public Response<List<OccurrencesResponseItem>> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, OccurrencesResponse.class);
	}

}
