package def.dom;
public class Screen extends EventTarget {
    public double availHeight;
    public double availWidth;
    public double bufferDepth;
    public double colorDepth;
    public double deviceXDPI;
    public double deviceYDPI;
    public Boolean fontSmoothingEnabled;
    public double height;
    public double logicalXDPI;
    public double logicalYDPI;
    public String msOrientation;
    public java.util.function.Function<Event,Object> onmsorientationchange;
    public double pixelDepth;
    public double systemXDPI;
    public double systemYDPI;
    public double width;
    native public Boolean msLockOrientation(String orientations);
    native public void msUnlockOrientation();
    native public void addEventListener(jsweet.util.StringTypes.MSOrientationChange type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static Screen prototype;
    public Screen(){}
    native public void addEventListener(jsweet.util.StringTypes.MSOrientationChange type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public Boolean msLockOrientation(String[] orientations);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

