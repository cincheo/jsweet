package def.dom;
public class VideoTrackList extends EventTarget implements Iterable<VideoTrack> {
    public double length;
    public java.util.function.Function<TrackEvent,Object> onaddtrack;
    public java.util.function.Function<Event,Object> onchange;
    public java.util.function.Function<TrackEvent,Object> onremovetrack;
    public double selectedIndex;
    native public VideoTrack getTrackById(String id);
    native public VideoTrack item(double index);
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.change type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.removetrack type, java.util.function.Function<TrackEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    native public VideoTrack $get(double index);
    public static VideoTrackList prototype;
    public VideoTrackList(){}
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.change type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.removetrack type, java.util.function.Function<TrackEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<VideoTrack> iterator();
}

