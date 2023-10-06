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
import ts.client.signaturehelp.SignatureHelpItems;

/**
 * Signature help request; value of command field is "signatureHelp". Given a
 * file location (file, line, col), return the signature help.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class SignatureHelpRequest extends FileLocationRequest<SignatureHelpRequestArgs> {

	public SignatureHelpRequest(String file, int line, int offset) {
		super(CommandNames.SignatureHelp.getName(), new SignatureHelpRequestArgs(file, line, offset));
	}

	@Override
	public Response<SignatureHelpItems> parseResponse(JsonObject json) {
		return GsonHelper.DEFAULT_GSON.fromJson(json, SignatureHelpResponse.class);
	}

}
