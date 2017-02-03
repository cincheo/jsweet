package def.dom;

import def.js.StringTypes;
import def.js.StringTypes.ended;

public class OscillatorNode extends AudioNode {
    public AudioParam detune;
    public AudioParam frequency;
    public java.util.function.Function<Event,java.lang.Object> onended;
    public java.lang.String type;
    native public void setPeriodicWave(PeriodicWave periodicWave);
    native public void start(double when);
    native public void stop(double when);
    native public void addEventListener(def.js.StringTypes.ended type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static OscillatorNode prototype;
    public OscillatorNode(){}
    native public void start();
    native public void stop();
    native public void addEventListener(def.js.StringTypes.ended type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

