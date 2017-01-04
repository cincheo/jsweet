package def.dom;
public class Plugin extends def.js.Object implements Iterable<MimeType> {
    public String description;
    public String filename;
    public double length;
    public String name;
    public String version;
    native public MimeType item(double index);
    native public MimeType namedItem(String type);
    native public MimeType $get(double index);
    public static Plugin prototype;
    public Plugin(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<MimeType> iterator();
}

