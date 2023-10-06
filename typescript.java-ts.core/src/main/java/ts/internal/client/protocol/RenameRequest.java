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
import ts.client.rename.RenameResponseBody;

/**
 * Rename request; value of command field is "rename". Return response giving
 * the file locations that reference the symbol found in file at location line,
 * col. Also return full display name of the symbol so that client can print it
 * unambiguously.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class RenameRequest extends FileLocationRequest<RenameRequestArgs> {

	public RenameRequest(String file, int line, int offset, Boolean findInComments, Boolean findInStrings) {
		super(CommandNames.Rename.getName(), new RenameRequestArgs(file, line, offset, findInComments, findInStrings));
	}

	@Override
	public Response<RenameResponseBody> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, RenameResponse.class);
	}

}
