package def.dom;
public class AudioTrackList extends EventTarget implements Iterable<AudioTrack> {
    public double length;
    public java.util.function.Function<TrackEvent,Object> onaddtrack;
    public java.util.function.Function<Event,Object> onchange;
    public java.util.function.Function<TrackEvent,Object> onremovetrack;
    native public AudioTrack getTrackById(String id);
    native public AudioTrack item(double index);
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.change type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.removetrack type, java.util.function.Function<TrackEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    native public AudioTrack $get(double index);
    public static AudioTrackList prototype;
    public AudioTrackList(){}
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.change type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.removetrack type, java.util.function.Function<TrackEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<AudioTrack> iterator();
}

