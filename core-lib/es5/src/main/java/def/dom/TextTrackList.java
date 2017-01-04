package def.dom;
public class TextTrackList extends EventTarget implements Iterable<TextTrack> {
    public double length;
    public java.util.function.Function<TrackEvent,Object> onaddtrack;
    native public TextTrack item(double index);
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    native public TextTrack $get(double index);
    public static TextTrackList prototype;
    public TextTrackList(){}
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<TextTrack> iterator();
}

