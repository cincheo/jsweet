package source.structural;

public class InterfaceInheritance {

}

interface SensorActuator {
	public String getName();

	public String getType();

	public String getSignalId();

	public void dispose();
}

interface Sensor extends SensorActuator {
	public void emitSignal(String s);
}

interface Actuator extends SensorActuator {
	public void updateSignalId();

	public void actuate();
}
