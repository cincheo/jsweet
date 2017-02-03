package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class TextTrackCueList extends Iterable<TextTrackCue> {
    public double length;
    native public TextTrackCue getCueById(java.lang.String id);
    native public TextTrackCue item(double index);
    native public TextTrackCue $get(double index);
    public static TextTrackCueList prototype;
    public TextTrackCueList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<TextTrackCue> iterator();
}

