package def.dom;
public class DOMStringList extends def.js.Object implements Iterable<java.lang.String> {
    public double length;
    native public Boolean contains(String str);
    native public String item(double index);
    native public java.lang.String $get(double index);
    public static DOMStringList prototype;
    public DOMStringList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.String> iterator();
}

