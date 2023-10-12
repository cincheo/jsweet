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
import ts.client.refactors.ApplicableRefactorInfo;

/**
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class GetApplicableRefactorsRequest extends Request {

	public GetApplicableRefactorsRequest(String file, int line, int offset) {
		super(CommandNames.GetApplicableRefactors.getName(), new GetApplicableRefactorsRequestArgs(file, line, offset));
	}

	public GetApplicableRefactorsRequest(String file, int startLine, int startOffset, int endLine, int endOffset) {
		super(CommandNames.GetApplicableRefactors.getName(),
				new GetApplicableRefactorsRangeRequestArgs(file, startLine, startOffset, endLine, endOffset));
	}

	@Override
	public Response<List<ApplicableRefactorInfo>> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, GetApplicableRefactorsResponse.class);
	}

}
