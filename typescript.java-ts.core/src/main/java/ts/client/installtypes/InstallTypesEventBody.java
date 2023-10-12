package ts.client.installtypes;

import java.util.List;

public class InstallTypesEventBody {

	/**
	 * correlation id to match begin and end events
	 */
	int eventId;
	
	/**
	 * list of packages to install
	 */
	List<String> packages;

	public int getEventId() {
		return eventId;
	}

	public List<String> getPackages() {
		return packages;
	}
}
