package ts.client.installtypes;

import com.google.gson.JsonObject;

public interface IInstallTypesListener {

	void onBegin(BeginInstallTypesEventBody body);

	void logTelemetry(String telemetryEventName, JsonObject payload);

	void onEnd(EndInstallTypesEventBody body);

}
