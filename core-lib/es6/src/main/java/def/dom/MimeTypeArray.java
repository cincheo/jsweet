package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class MimeTypeArray extends Iterable<Plugin> {
    public double length;
    native public Plugin item(double index);
    native public Plugin namedItem(java.lang.String type);
    native public Plugin $get(double index);
    public static MimeTypeArray prototype;
    public MimeTypeArray(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Plugin> iterator();
}

