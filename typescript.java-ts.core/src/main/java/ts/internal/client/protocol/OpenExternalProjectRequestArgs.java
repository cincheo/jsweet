package ts.internal.client.protocol;

import java.util.List;

import ts.client.ExternalFile;
import ts.cmd.tsc.CompilerOptions;

/**
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 */
public class OpenExternalProjectRequestArgs {

	/**
	 * Project name
	 */
	String projectFileName;
	/**
	 * List of root files in project
	 */
	List<ExternalFile> rootFiles;
	/**
	 * Compiler options for the project
	 */
	CompilerOptions options;
	// /**
	// * Explicitly specified type acquisition for the project
	// */
	// typeAcquisition?: TypeAcquisition;
	public OpenExternalProjectRequestArgs(String projectFileName, List<ExternalFile> rootFiles,
			CompilerOptions options) {
		this.projectFileName = projectFileName;
		this.rootFiles = rootFiles;
		this.options = options;
	}
}
