package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class Plugin extends Iterable<MimeType> {
    public java.lang.String description;
    public java.lang.String filename;
    public double length;
    public java.lang.String name;
    public java.lang.String version;
    native public MimeType item(double index);
    native public MimeType namedItem(java.lang.String type);
    native public MimeType $get(double index);
    public static Plugin prototype;
    public Plugin(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<MimeType> iterator();
}

