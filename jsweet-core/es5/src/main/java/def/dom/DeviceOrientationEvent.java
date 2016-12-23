package def.dom;
public class DeviceOrientationEvent extends Event {
    public Boolean absolute;
    public double alpha;
    public double beta;
    public double gamma;
    native public void initDeviceOrientationEvent(String type, Boolean bubbles, Boolean cancelable, double alpha, double beta, double gamma, Boolean absolute);
    public static DeviceOrientationEvent prototype;
    public DeviceOrientationEvent(){}
}

