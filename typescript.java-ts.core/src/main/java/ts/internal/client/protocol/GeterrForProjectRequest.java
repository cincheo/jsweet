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
import ts.client.projectinfo.ProjectInfo;

/**
 * GeterrForProjectRequest request; value of command field is
 * "geterrForProject". It works similarly with 'Geterr', only it request for
 * every file in this project.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class GeterrForProjectRequest extends Request<GeterrForProjectRequestArgs>
		implements IRequestEventable<DiagnosticEvent> {

	private final transient ProjectInfo projectInfo;
	private final transient List<DiagnosticEvent> events;

	public GeterrForProjectRequest(String file, int delay, ProjectInfo projectInfo) {
		super(CommandNames.GeterrForProject.getName(), new GeterrForProjectRequestArgs(file, delay));
		this.projectInfo = projectInfo;
		this.events = new ArrayList<>();
	}

	@Override
	public Response<?> parseResponse(JsonObject json) {
		return null;
	}

	@Override
	public List<String> getKeys() {
		List<String> files = projectInfo.getFileNames();
		List<String> keys = new ArrayList<>((files.size() - 1) * 2);
		for (String file : files) {
			if (!file.endsWith("lib.d.ts")) {
				keys.add("syntaxDiag_" + file);
				keys.add("semanticDiag_" + file);
			}
		}
		return keys;
	}

	@Override
	public boolean accept(DiagnosticEvent event) {
		events.add(event);
		return events.size() >= getKeys().size();
	}

	@Override
	public List<DiagnosticEvent> getEvents() {
		return events;
	}
}
