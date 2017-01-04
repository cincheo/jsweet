package def.dom;
public class TextTrackCueList extends def.js.Object implements Iterable<TextTrackCue> {
    public double length;
    native public TextTrackCue getCueById(String id);
    native public TextTrackCue item(double index);
    native public TextTrackCue $get(double index);
    public static TextTrackCueList prototype;
    public TextTrackCueList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<TextTrackCue> iterator();
}

