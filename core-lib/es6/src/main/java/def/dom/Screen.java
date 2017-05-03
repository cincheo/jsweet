package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.MSOrientationChange;

public class Screen extends EventTarget {
    public double availHeight;
    public double availWidth;
    public double bufferDepth;
    public double colorDepth;
    public double deviceXDPI;
    public double deviceYDPI;
    public java.lang.Boolean fontSmoothingEnabled;
    public double height;
    public double logicalXDPI;
    public double logicalYDPI;
    public java.lang.String msOrientation;
    public java.util.function.Function<Event,java.lang.Object> onmsorientationchange;
    public double pixelDepth;
    public double systemXDPI;
    public double systemYDPI;
    public double width;
    native public java.lang.Boolean msLockOrientation(java.lang.String orientations);
    native public java.lang.Boolean msLockOrientation(java.lang.String[] orientations);
    native public void msUnlockOrientation();
    native public void addEventListener(jsweet.util.StringTypes.MSOrientationChange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static Screen prototype;
    public Screen(){}
    native public void addEventListener(jsweet.util.StringTypes.MSOrientationChange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

