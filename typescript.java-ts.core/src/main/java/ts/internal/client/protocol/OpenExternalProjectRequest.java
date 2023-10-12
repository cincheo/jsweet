package ts.internal.client.protocol;

import java.util.List;

import com.google.gson.JsonObject;

import ts.client.CommandNames;
import ts.client.ExternalFile;
import ts.cmd.tsc.CompilerOptions;

/**
 * Request to open or update external project.
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 * 
 */
public class OpenExternalProjectRequest extends Request<OpenExternalProjectRequestArgs> {

	public OpenExternalProjectRequest(String projectFileName, List<ExternalFile> rootFiles, CompilerOptions options) {
		super(CommandNames.OpenExternalProject.getName(),
				new OpenExternalProjectRequestArgs(projectFileName, rootFiles, options));
	}

	@Override
	public Response<?> parseResponse(JsonObject json) {
		// This request doesn't return response.
		return null;
	}
}
