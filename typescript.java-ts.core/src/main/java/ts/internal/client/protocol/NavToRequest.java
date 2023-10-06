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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ts.client.CommandNames;
import ts.client.navto.NavtoItem;

/**
 * Navto request message; value of command field is "navto". Return list of
 * objects giving file locations and symbols that match the search term given in
 * argument 'searchTerm'. The context for the search is given by the named file.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class NavToRequest extends FileRequest<NavtoRequestArgs> {

	public NavToRequest(String fileName, String searchValue, Integer maxResultCount, Boolean currentFileOnly,
			String projectFileName) {
		super(CommandNames.NavTo.getName(),
				new NavtoRequestArgs(fileName, searchValue, maxResultCount, currentFileOnly, projectFileName));
	}

	@Override
	public Response<List<NavtoItem>> parseResponse(JsonObject json) {
		Gson gson = GsonHelper.DEFAULT_GSON;
		return gson.fromJson(json, NavtoResponse.class);
	}

}
