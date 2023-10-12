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
 * Request to recompile the file. All generated outputs (.js, .d.ts or .js.map
 * files) is written on disk.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class CompileOnSaveEmitFileRequest extends Request<CompileOnSaveEmitFileRequestArgs> {

	public CompileOnSaveEmitFileRequest(String file, Boolean forced) {
		super(CommandNames.CompileOnSaveEmitFile.getName(), new CompileOnSaveEmitFileRequestArgs(file, forced));
	}

	@Override
	public Response<Boolean> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, CompileOnSaveEmitFileResponse.class);
	}

}
