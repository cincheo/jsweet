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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ts.internal.SequenceHelper;

/**
 * Client-initiated request message
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public abstract class Request<T> extends Message {

	/**
	 * The command to execute
	 */
	private final String command;

	/**
	 * Object containing arguments for the command
	 */
	private final T arguments;

	public Request(String command, T arguments) {
		this(command, arguments, null);
	}

	public Request(String command, T arguments, Integer seq) {
		super(MessageType.request, seq != null ? seq : SequenceHelper.getRequestSeq());
		this.command = command;
		this.arguments = arguments;
	}

	public String getCommand() {
		return command;
	}

	public T getArguments() {
		return arguments;
	}

	public abstract <R> Response<R> parseResponse(JsonObject json);

	protected Gson getGson() {
		return GsonHelper.DEFAULT_GSON;
	}
}
