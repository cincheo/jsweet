package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.addtrack;
import jsweet.util.StringTypes.change;
import jsweet.util.StringTypes.removetrack;

@jsweet.lang.SyntacticIterable
public class AudioTrackList {
    public double length;
    public java.util.function.Function<TrackEvent,java.lang.Object> onaddtrack;
    public java.util.function.Function<Event,java.lang.Object> onchange;
    public java.util.function.Function<TrackEvent,java.lang.Object> onremovetrack;
    native public AudioTrack getTrackById(java.lang.String id);
    native public AudioTrack item(double index);
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.change type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.removetrack type, java.util.function.Function<TrackEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public AudioTrack $get(double index);
    public static AudioTrackList prototype;
    public AudioTrackList(){}
    native public void addEventListener(jsweet.util.StringTypes.addtrack type, java.util.function.Function<TrackEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.change type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.removetrack type, java.util.function.Function<TrackEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<AudioTrack> iterator();
}

