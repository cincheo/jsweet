package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.ended;

public class AudioBufferSourceNode extends AudioNode {
    public AudioBuffer buffer;
    public java.lang.Boolean loop;
    public double loopEnd;
    public double loopStart;
    public java.util.function.Function<Event,java.lang.Object> onended;
    public AudioParam playbackRate;
    native public void start(double when, double offset, double duration);
    native public void stop(double when);
    native public void addEventListener(jsweet.util.StringTypes.ended type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static AudioBufferSourceNode prototype;
    public AudioBufferSourceNode(){}
    native public void start(double when, double offset);
    native public void start(double when);
    native public void start();
    native public void stop();
    native public void addEventListener(jsweet.util.StringTypes.ended type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

