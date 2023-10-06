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
import ts.client.ScriptKindName;

/**
 * Open request; value of command field is "open". Notify the server that the
 * client has file open. The server will not monitor the filesystem for changes
 * in this file and will assume that the client is updating the server (using
 * the change and/or reload messages) when the file changes. Server does not
 * currently send a response to an open request.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 * 
 */
public class OpenRequest extends Request<OpenRequestArgs> {

	public OpenRequest(String file, String projectName, String fileContent, ScriptKindName scriptKindName) {
		super(CommandNames.Open.getName(), new OpenRequestArgs(file, projectName, fileContent, scriptKindName));
	}

	@Override
	public Response<?> parseResponse(JsonObject json) {
		// This request doesn't return response.
		return null;
	}
}
