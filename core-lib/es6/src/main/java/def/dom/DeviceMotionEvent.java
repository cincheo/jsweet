package def.dom;

public class DeviceMotionEvent extends Event {
    public DeviceAcceleration acceleration;
    public DeviceAcceleration accelerationIncludingGravity;
    public double interval;
    public DeviceRotationRate rotationRate;
    native public void initDeviceMotionEvent(java.lang.String type, java.lang.Boolean bubbles, java.lang.Boolean cancelable, DeviceAccelerationDict acceleration, DeviceAccelerationDict accelerationIncludingGravity, DeviceRotationRateDict rotationRate, double interval);
    public static DeviceMotionEvent prototype;
    public DeviceMotionEvent(){}
}

