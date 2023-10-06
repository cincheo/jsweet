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
import ts.client.FileSpan;

/**
 * Go to definition request; value of command field is "definition". Return
 * response giving the file locations that define the symbol found in file at
 * location line, col.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class DefinitionRequest extends FileLocationRequest<FileLocationRequestArgs> {

	public DefinitionRequest(String file, int line, int offset) {
		super(CommandNames.Definition.getName(), new FileLocationRequestArgs(file, line, offset));
	}

	@Override
	public Response<List<FileSpan>> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, DefinitionResponse.class);
	}

}
