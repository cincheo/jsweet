package def.dom;

public class MediaSource extends EventTarget {
    public SourceBufferList activeSourceBuffers;
    public double duration;
    public double readyState;
    public SourceBufferList sourceBuffers;
    native public SourceBuffer addSourceBuffer(java.lang.String type);
    native public void endOfStream(double error);
    native public void removeSourceBuffer(SourceBuffer sourceBuffer);
    public static MediaSource prototype;
    public MediaSource(){}
    native public static java.lang.Boolean isTypeSupported(java.lang.String type);
    native public void endOfStream();
}

