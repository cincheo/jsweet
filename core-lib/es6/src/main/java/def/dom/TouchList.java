package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class TouchList extends Iterable<Touch> {
    public double length;
    native public Touch item(double index);
    native public Touch $get(double index);
    public static TouchList prototype;
    public TouchList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Touch> iterator();
}

