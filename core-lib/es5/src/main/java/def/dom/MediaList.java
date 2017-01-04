package def.dom;
public class MediaList extends def.js.Object implements Iterable<java.lang.String> {
    public double length;
    public String mediaText;
    native public void appendMedium(String newMedium);
    native public void deleteMedium(String oldMedium);
    native public String item(double index);
    native public String toString();
    native public java.lang.String $get(double index);
    public static MediaList prototype;
    public MediaList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.String> iterator();
}

