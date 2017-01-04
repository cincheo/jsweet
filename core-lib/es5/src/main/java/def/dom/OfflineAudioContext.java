package def.dom;
public class OfflineAudioContext extends AudioContext {
    public java.util.function.Function<Event,Object> oncomplete;
    native public void startRendering();
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static OfflineAudioContext prototype;
    public OfflineAudioContext(double numberOfChannels, double length, double sampleRate){}
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
    protected OfflineAudioContext(){}
}

