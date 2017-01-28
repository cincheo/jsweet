package def.dom;

import def.js.Object;

public class PositionError extends def.js.Object {
    public double code;
    public java.lang.String message;
    native public java.lang.String toString();
    public double PERMISSION_DENIED;
    public double POSITION_UNAVAILABLE;
    public double TIMEOUT;
    public static PositionError prototype;
    public PositionError(){}
}

