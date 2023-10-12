package ts.cmd.tsc;

import ts.utils.VersionHelper;

public enum CompilerOptionCapability {

	listEmittedFiles("2.0.0");

	private String sinceVersion;

	private CompilerOptionCapability(String version) {
		this.sinceVersion = version;
	}

	/**
	 * Return true if the tsc compiler option support the given version and
	 * false otherwise.
	 * 
	 * @param version
	 * @return true if the tsc compiler option support the given version and
	 *         false otherwise.
	 */
	public boolean canSupport(String version) {
		return VersionHelper.canSupport(version, sinceVersion);
	}
}
