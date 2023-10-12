package ts.client.installtypes;

public class EndInstallTypesEventBody extends InstallTypesEventBody {

	/**
	 * true if installation succeeded, otherwise false
	 */
	private boolean success;

	public boolean isSuccess() {
		return success;
	}
}
