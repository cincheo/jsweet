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
import ts.client.projectinfo.ProjectInfo;

/**
 * A request to get the project information of the current file.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class ProjectInfoRequest extends Request<ProjectInfoRequestArgs> {

	public ProjectInfoRequest(String file, boolean needFileNameList) {
		super(CommandNames.ProjectInfo.getName(), new ProjectInfoRequestArgs(file, needFileNameList));
	}

	@Override
	public Response<ProjectInfo> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, ProjectInfoResponse.class);
	}

}
