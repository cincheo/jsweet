package ts.cmd;

import ts.client.Location;

public interface ITypeScriptLinterHandler {

	void addError(String file, Location startLoc, Location endLoc,
			Severity severity, String code, String message);
}
