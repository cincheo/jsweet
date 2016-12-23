package def.dom;
public class MimeTypeArray extends def.js.Object implements Iterable<Plugin> {
    public double length;
    native public Plugin item(double index);
    native public Plugin namedItem(String type);
    native public Plugin $get(double index);
    public static MimeTypeArray prototype;
    public MimeTypeArray(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Plugin> iterator();
}

