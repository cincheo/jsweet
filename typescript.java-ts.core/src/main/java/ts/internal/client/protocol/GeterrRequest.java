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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import ts.client.CommandNames;
import ts.client.diagnostics.DiagnosticEvent;

/**
 * Geterr request; value of command field is "geterr". Wait for delay
 * milliseconds and then, if during the wait no change or reload messages have
 * arrived for the first file in the files list, get the syntactic errors for
 * the file, field requests, and then get the semantic errors for the file.
 * Repeat with a smaller delay for each subsequent file on the files list. Best
 * practice for an editor is to send a file list containing each file that is
 * currently visible, in most-recently-used order.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class GeterrRequest extends Request<GeterrRequestArgs> implements IRequestEventable<DiagnosticEvent> {

	private final transient List<DiagnosticEvent> events;

	public GeterrRequest(String[] files, int delay) {
		super(CommandNames.Geterr.getName(), new GeterrRequestArgs(files, delay));
		this.events = new ArrayList<>();
	}

	@Override
	public Response<?> parseResponse(JsonObject json) {
		return null;
	}

	@Override
	public List<String> getKeys() {
		String[] files = super.getArguments().getFiles();
		List<String> keys = new ArrayList<>(files.length * 2);
		for (String file : files) {
			keys.add("syntaxDiag_" + file);
			keys.add("semanticDiag_" + file);
		}
		return keys;
	}

	@Override
	public boolean accept(DiagnosticEvent event) {
		events.add(event);
		return events.size() > 1;
	}

	@Override
	public List<DiagnosticEvent> getEvents() {
		return events;
	}

}
