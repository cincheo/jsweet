package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class DOMStringList extends Iterable<java.lang.String> {
    public double length;
    native public java.lang.Boolean contains(java.lang.String str);
    native public java.lang.String item(double index);
    native public java.lang.String $get(double index);
    public static DOMStringList prototype;
    public DOMStringList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.String> iterator();
}

