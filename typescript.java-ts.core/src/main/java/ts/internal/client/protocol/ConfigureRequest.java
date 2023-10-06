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
import ts.client.configure.ConfigureRequestArguments;

/**
 * Configure request; value of command field is "configure". Specifies host
 * information, such as host type, tab size, and indent size.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class ConfigureRequest extends Request<ConfigureRequestArguments> {

	public ConfigureRequest(ConfigureRequestArguments arguments) {
		super(CommandNames.Configure.getName(), arguments);
	}

	@Override
	public Response<?> parseResponse(JsonObject json) {
		return null;
	}

}
