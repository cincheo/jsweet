package def.dom;
public class TextTrackCue extends EventTarget {
    public double endTime;
    public String id;
    public java.util.function.Function<Event,Object> onenter;
    public java.util.function.Function<Event,Object> onexit;
    public Boolean pauseOnExit;
    public double startTime;
    public String text;
    public TextTrack track;
    native public DocumentFragment getCueAsHTML();
    native public void addEventListener(jsweet.util.StringTypes.enter type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.exit type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static TextTrackCue prototype;
    public TextTrackCue(double startTime, double endTime, String text){}
    native public void addEventListener(jsweet.util.StringTypes.enter type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.exit type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
    protected TextTrackCue(){}
}

