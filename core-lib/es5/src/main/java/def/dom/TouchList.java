package def.dom;
public class TouchList extends def.js.Object implements Iterable<Touch> {
    public int length;
    native public Touch item(int index);
    native public Touch $get(int index);
    public static TouchList prototype;
    public TouchList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Touch> iterator();
}

