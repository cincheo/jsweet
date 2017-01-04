package def.dom;
public class TextTrack extends EventTarget {
    public TextTrackCueList activeCues;
    public TextTrackCueList cues;
    public String inBandMetadataTrackDispatchType;
    public String kind;
    public String label;
    public String language;
    public Object mode;
    public java.util.function.Function<Event,Object> oncuechange;
    public java.util.function.Function<Event,Object> onerror;
    public java.util.function.Function<Event,Object> onload;
    public double readyState;
    native public void addCue(TextTrackCue cue);
    native public void removeCue(TextTrackCue cue);
    public double DISABLED;
    public double ERROR;
    public double HIDDEN;
    public double LOADED;
    public double LOADING;
    public double NONE;
    public double SHOWING;
    native public void addEventListener(jsweet.util.StringTypes.cuechange type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static TextTrack prototype;
    public TextTrack(){}
    native public void addEventListener(jsweet.util.StringTypes.cuechange type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

