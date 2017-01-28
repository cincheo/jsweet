package def.dom;

import def.js.Object;

public class Geolocation extends def.js.Object {
    native public void clearWatch(double watchId);
    native public void getCurrentPosition(PositionCallback successCallback, PositionErrorCallback errorCallback, PositionOptions options);
    native public double watchPosition(PositionCallback successCallback, PositionErrorCallback errorCallback, PositionOptions options);
    public static Geolocation prototype;
    public Geolocation(){}
    native public void getCurrentPosition(PositionCallback successCallback, PositionErrorCallback errorCallback);
    native public void getCurrentPosition(PositionCallback successCallback);
    native public double watchPosition(PositionCallback successCallback, PositionErrorCallback errorCallback);
    native public double watchPosition(PositionCallback successCallback);
}

