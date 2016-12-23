package def.dom;
public class MediaSource extends EventTarget {
    public SourceBufferList activeSourceBuffers;
    public double duration;
    public String readyState;
    public SourceBufferList sourceBuffers;
    native public SourceBuffer addSourceBuffer(String type);
    native public void endOfStream(double error);
    native public void removeSourceBuffer(SourceBuffer sourceBuffer);
    public static MediaSource prototype;
    public MediaSource(){}
    native public static Boolean isTypeSupported(String type);
    native public void endOfStream();
}

