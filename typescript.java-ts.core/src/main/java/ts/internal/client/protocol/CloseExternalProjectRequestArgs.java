package ts.internal.client.protocol;

/**
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class CloseExternalProjectRequestArgs {

	/**
	 * Project name
	 */
	String projectFileName;

	public CloseExternalProjectRequestArgs(String projectFileName) {
		this.projectFileName = projectFileName;
	}
}
