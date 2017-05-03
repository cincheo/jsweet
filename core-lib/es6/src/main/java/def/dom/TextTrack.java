package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.cuechange;
import jsweet.util.StringTypes.error;
import jsweet.util.StringTypes.load;

public class TextTrack extends EventTarget {
    public TextTrackCueList activeCues;
    public TextTrackCueList cues;
    public java.lang.String inBandMetadataTrackDispatchType;
    public java.lang.String kind;
    public java.lang.String label;
    public java.lang.String language;
    public java.lang.Object mode;
    public java.util.function.Function<Event,java.lang.Object> oncuechange;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.util.function.Function<Event,java.lang.Object> onload;
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
    native public void addEventListener(jsweet.util.StringTypes.cuechange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static TextTrack prototype;
    public TextTrack(){}
    native public void addEventListener(jsweet.util.StringTypes.cuechange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

