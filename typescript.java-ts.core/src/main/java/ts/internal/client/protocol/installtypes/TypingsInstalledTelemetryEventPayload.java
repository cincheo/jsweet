package ts.internal.client.protocol.installtypes;

public class TypingsInstalledTelemetryEventPayload {

	/**
	 * Comma separated list of installed typing packages
	 */
	private String installedPackages;
	/**
	 * true if install request succeeded, otherwise - false
	 */
	private boolean installSuccess;

	/**
	 * version of typings installer
	 */
	private String typingsInstallerVersion;

	public String getInstalledPackages() {
		return installedPackages;
	}

	public boolean isInstallSuccess() {
		return installSuccess;
	}

	public String getTypingsInstallerVersion() {
		return typingsInstallerVersion;
	}
}
