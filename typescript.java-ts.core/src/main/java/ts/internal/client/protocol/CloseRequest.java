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

/**
 * Close request; value of command field is "close". Notify the server that the
 * client has closed a previously open file. If file is still referenced by open
 * files, the server will resume monitoring the filesystem for changes to file.
 * Server does not currently send a response to a close request.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class CloseRequest extends FileRequest<FileRequestArgs> {

	public CloseRequest(String fileName) {
		super(CommandNames.Close.getName(), new FileRequestArgs(fileName, null));
	}

	@Override
	public Response<?> parseResponse(JsonObject json) {
		// This request doesn't return response.
		return null;
	}

}
