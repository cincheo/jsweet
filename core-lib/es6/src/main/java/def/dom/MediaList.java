package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class MediaList extends Iterable<java.lang.String> {
    public double length;
    public java.lang.String mediaText;
    native public void appendMedium(java.lang.String newMedium);
    native public void deleteMedium(java.lang.String oldMedium);
    native public java.lang.String item(double index);
    native public java.lang.String toString();
    native public java.lang.String $get(double index);
    public static MediaList prototype;
    public MediaList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.String> iterator();
}

