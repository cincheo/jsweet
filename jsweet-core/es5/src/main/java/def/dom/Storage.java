package def.dom;
public class Storage extends def.js.Object implements Iterable<java.lang.String> {
    public double length;
    native public void clear();
    native public Object getItem(String key);
    native public String key(double index);
    native public void removeItem(String key);
    native public void setItem(String key, String data);
    native public java.lang.Object $get(String key);
    native public java.lang.String $get(double index);
    public static Storage prototype;
    public Storage(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.String> iterator();
}

