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

import com.google.gson.JsonObject;

import ts.client.CommandNames;
import ts.client.refactors.RefactorEditInfo;

/**
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class GetEditsForRefactorRequest extends Request {

	public GetEditsForRefactorRequest(String file, int line, int offset, String refactor, String action) {
		super(CommandNames.GetEditsForRefactor.getName(),
				new GetEditsForRefactorRequestArgs(file, line, offset, refactor, action));
	}

	public GetEditsForRefactorRequest(String file, int startLine, int startOffset, int endtLine, int endOffset,
			String refactor, String action) {
		super(CommandNames.GetEditsForRefactor.getName(), new GetEditsForRefactorRangeRequestArgs(file, startLine,
				startOffset, endtLine, endOffset, refactor, action));
	}

	@Override
	public Response<RefactorEditInfo> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, GetEditsForRefactorResponse.class);
	}

}
