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
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonObject;

import ts.client.CommandNames;
import ts.client.ITypeScriptServiceClient;
import ts.client.completions.CompletionEntry;
import ts.client.completions.ICompletionEntryFactory;
import ts.client.completions.ICompletionEntryMatcherProvider;

/**
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class CompletionsRequest extends FileLocationRequest<CompletionsRequestArgs> {

	private final transient ICompletionEntryMatcherProvider matcherProvider;
	private final transient ITypeScriptServiceClient client;
	private final transient ICompletionEntryFactory factory;

	public CompletionsRequest(String fileName, int line, int offset, ICompletionEntryMatcherProvider matcherProvider,
			ITypeScriptServiceClient client, ICompletionEntryFactory factory) {
		super(CommandNames.Completions.getName(), new CompletionsRequestArgs(fileName, line, offset, null));
		this.matcherProvider = matcherProvider;
		this.client = client;
		this.factory = factory;
	}

	@Override
	public Response<List<CompletionEntry>> parseResponse(JsonObject json) {
		String fileName = super.getArguments().getFile();
		int line = super.getArguments().getLine();
		int offset = super.getArguments().getOffset();
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(CompletionEntry.class, new InstanceCreator<CompletionEntry>() {
					@Override
					public CompletionEntry createInstance(Type type) {
						return factory.create(matcherProvider.getMatcher(), fileName, line, offset, client);
					}
				}).create();
		return gson.fromJson(json, CompletionsResponse.class);
	}

}
