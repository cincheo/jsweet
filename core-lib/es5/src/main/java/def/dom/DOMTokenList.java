package def.dom;
public class DOMTokenList extends def.js.Object implements Iterable<java.lang.String> {
    public double length;
    native public void add(String... token);
    native public Boolean contains(String token);
    native public String item(double index);
    native public void remove(String... token);
    native public String toString();
    native public Boolean toggle(String token, Boolean force);
    native public java.lang.String $get(double index);
    public static DOMTokenList prototype;
    public DOMTokenList(){}
    native public Boolean toggle(String token);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.String> iterator();
}

