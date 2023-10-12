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
import ts.client.diagnostics.IDiagnostic;

/**
 * Synchronous request for syntactic diagnostics of one file.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class SyntacticDiagnosticsSyncRequest extends FileRequest<SyntacticDiagnosticsSyncRequestArgs> {

	public SyntacticDiagnosticsSyncRequest(String file, Boolean includeLinePosition) {
		super(CommandNames.SyntacticDiagnosticsSync.getName(),
				new SyntacticDiagnosticsSyncRequestArgs(file, includeLinePosition));
	}

	@Override
	public Response<List<IDiagnostic>> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, SyntacticDiagnosticsSyncResponse.class);
	}

}
