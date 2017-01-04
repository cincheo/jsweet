package def.dom;
public class AudioBufferSourceNode extends AudioNode {
    public AudioBuffer buffer;
    public Boolean loop;
    public double loopEnd;
    public double loopStart;
    public java.util.function.Function<Event,Object> onended;
    public AudioParam playbackRate;
    native public void start(double when, double offset, double duration);
    native public void stop(double when);
    native public void addEventListener(jsweet.util.StringTypes.ended type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static AudioBufferSourceNode prototype;
    public AudioBufferSourceNode(){}
    native public void start(double when, double offset);
    native public void start(double when);
    native public void start();
    native public void stop();
    native public void addEventListener(jsweet.util.StringTypes.ended type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

