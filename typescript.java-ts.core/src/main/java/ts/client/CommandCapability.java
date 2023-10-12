package ts.client;

import ts.utils.VersionHelper;

public enum CommandCapability implements ISupportable {

	DiagnosticWithCategory("2.3.1");

	private String sinceVersion;

	private CommandCapability(String version) {
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
