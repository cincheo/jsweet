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
import com.google.gson.InstanceCreator;
import com.google.gson.JsonObject;

import ts.client.CommandNames;
import ts.client.IPositionProvider;
import ts.client.Location;
import ts.client.navbar.NavigationBarItem;

/**
 * NavTree request; value of command field is "navtree". Return response giving
 * the navigation tree of the requested file.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class NavTreeRequest extends FileRequest<FileRequestArgs> {

	// Set positionProvider to transient to ignore Gson serialization
	private final transient IPositionProvider positionProvider;

	public NavTreeRequest(String fileName, IPositionProvider positionProvider) {
		super(CommandNames.NavTree.getName(), new FileRequestArgs(fileName, null));
		this.positionProvider = positionProvider;
	}

	@Override
	public Response<NavigationBarItem> parseResponse(JsonObject json) {
		Gson gson = GsonHelper.DEFAULT_GSON;
		if (positionProvider != null) {
			gson = new GsonBuilder().registerTypeAdapter(Location.class, new InstanceCreator<Location>() {
				@Override
				public Location createInstance(Type type) {
					return new Location(positionProvider);
				}
			}).create();
		}
		return gson.fromJson(json, NavTreeResponse.class);
	}

}
