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
import ts.internal.FileTempHelper;

/**
 * Reload request message; value of command field is "reload". Reload contents
 * of file with name given by the 'file' argument from temporary file with name
 * given by the 'tmpfile' argument. The two names can be identical.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class ReloadRequest extends FileRequest<ReloadRequestArgs> {

	public ReloadRequest(String fileName, String tmpfile, int seq) {
		super(CommandNames.Reload.getName(), new ReloadRequestArgs(fileName, null, tmpfile), seq);
	}

	@Override
	public Response<?> parseResponse(JsonObject json) {
		int requestSeq = json.get("request_seq").getAsInt();
		FileTempHelper.freeTempFile(requestSeq);
		return GsonHelper.DEFAULT_GSON.fromJson(json, Response.class);
	}

}
