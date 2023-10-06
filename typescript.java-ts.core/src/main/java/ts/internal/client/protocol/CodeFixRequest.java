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
import ts.client.codefixes.CodeAction;

/**
 * Configure request; value of command field is "configure". Specifies host
 * information, such as host type, tab size, and indent size.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class CodeFixRequest extends Request<CodeFixRequestArgs> {

	public CodeFixRequest(String file, int startLine, int startOffset, int endLine, int endOffset,
			List<Integer> errorCodes) {
		super(CommandNames.GetCodeFixes.getName(),
				new CodeFixRequestArgs(file, startLine, startOffset, endLine, endOffset, errorCodes));
	}

	@Override
	public Response<List<CodeAction>> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, GetCodeFixesResponse.class);
	}

}
