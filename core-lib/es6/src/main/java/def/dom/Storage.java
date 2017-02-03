package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class Storage extends Iterable<java.lang.String> {
    public double length;
    native public void clear();
    native public java.lang.Object getItem(java.lang.String key);
    native public java.lang.String key(double index);
    native public void removeItem(java.lang.String key);
    native public void setItem(java.lang.String key, java.lang.String data);
    native public java.lang.Object $get(java.lang.String key);
    native public java.lang.String $get(double index);
    public static Storage prototype;
    public Storage(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.String> iterator();
}

