package ts.internal.client.protocol;

import com.google.gson.JsonObject;

import ts.client.CommandNames;

/**
 * Request to open or update external project.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 * 
 */
public class CloseExternalProjectRequest extends Request<CloseExternalProjectRequestArgs> {

	public CloseExternalProjectRequest(String projectFileName) {
		super(CommandNames.CloseExternalProject.getName(),
				new CloseExternalProjectRequestArgs(projectFileName));
	}

	@Override
	public Response<?> parseResponse(JsonObject json) {
		// This request doesn't return response.
		return null;
	}
}
