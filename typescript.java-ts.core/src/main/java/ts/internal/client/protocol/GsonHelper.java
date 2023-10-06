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

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import ts.client.diagnostics.Diagnostic;
import ts.client.diagnostics.DiagnosticWithLinePosition;
import ts.client.diagnostics.IDiagnostic;

public class GsonHelper {

	private static final JsonParser JSON_PARSER = new JsonParser();

	public static final Gson DEFAULT_GSON = new GsonBuilder()
			.registerTypeAdapter(IDiagnostic.class, new DiagnosticAdapter()).create();

	public static JsonElement parse(String json) throws JsonSyntaxException {
		return JSON_PARSER.parse(json);
	}

	private static class DiagnosticAdapter implements JsonDeserializer<IDiagnostic> {

		@Override
		public IDiagnostic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if (json.isJsonObject()) {
				JsonObject obj = json.getAsJsonObject();
				if (obj.get("start").isJsonObject()) {
					// {"start":{"line":1,"offset":13},"end":{"line":1,"offset":13},"text":"Identifier
					// expected.","code":1003}
					return DEFAULT_GSON.fromJson(json, Diagnostic.class);
				} else {
					return DEFAULT_GSON.fromJson(json, DiagnosticWithLinePosition.class);
				}
			}
			return null;
		}
	}
}
