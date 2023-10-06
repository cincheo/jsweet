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
import ts.client.compileonsave.CompileOnSaveAffectedFileListSingleProject;

/**
 * Request to obtain the list of files that should be regenerated if target file
 * is recompiled. NOTE: this us query-only operation and does not generate any
 * output on disk.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class CompileOnSaveAffectedFileListRequest extends FileRequest<FileRequestArgs> {

	public CompileOnSaveAffectedFileListRequest(String fileName) {
		super(CommandNames.CompileOnSaveAffectedFileList.getName(), new FileRequestArgs(fileName, null));
	}

	@Override
	public Response<List<CompileOnSaveAffectedFileListSingleProject>> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, CompileOnSaveAffectedFileListResponse.class);
	}

}
