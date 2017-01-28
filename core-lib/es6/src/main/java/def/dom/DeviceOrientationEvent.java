package def.dom;

public class DeviceOrientationEvent extends Event {
    public java.lang.Boolean absolute;
    public double alpha;
    public double beta;
    public double gamma;
    native public void initDeviceOrientationEvent(java.lang.String type, java.lang.Boolean bubbles, java.lang.Boolean cancelable, double alpha, double beta, double gamma, java.lang.Boolean absolute);
    public static DeviceOrientationEvent prototype;
    public DeviceOrientationEvent(){}
}

