package ts.internal.client.protocol.installtypes;

public class TelemetryEventBody {

	private String telemetryEventName;
	
	private Object paylod;
	
	public String getTelemetryEventName() {
		return telemetryEventName;
	}
	
	public Object getPaylod() {
		return paylod;
	}
}
