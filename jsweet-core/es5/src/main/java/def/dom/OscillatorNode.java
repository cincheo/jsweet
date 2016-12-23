package def.dom;
public class OscillatorNode extends AudioNode {
    public AudioParam detune;
    public AudioParam frequency;
    public java.util.function.Function<Event,Object> onended;
    public String type;
    native public void setPeriodicWave(PeriodicWave periodicWave);
    native public void start(double when);
    native public void stop(double when);
    native public void addEventListener(jsweet.util.StringTypes.ended type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static OscillatorNode prototype;
    public OscillatorNode(){}
    native public void start();
    native public void stop();
    native public void addEventListener(jsweet.util.StringTypes.ended type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

