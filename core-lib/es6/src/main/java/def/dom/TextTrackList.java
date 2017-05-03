package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.addtrack;

@jsweet.lang.SyntacticIterable
public class TextTrackList {
    public double length;
    public java.util.function.Function<TrackEvent,java.lang.Object> onaddtrack;
    native public TextTrack item(double index);
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public TextTrack $get(double index);
    public static TextTrackList prototype;
    public TextTrackList(){}
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<TextTrack> iterator();
}

