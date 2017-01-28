package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class DOMTokenList extends Iterable<java.lang.String> {
    public double length;
    native public void add(java.lang.String... token);
    native public java.lang.Boolean contains(java.lang.String token);
    native public java.lang.String item(double index);
    native public void remove(java.lang.String... token);
    native public java.lang.String toString();
    native public java.lang.Boolean toggle(java.lang.String token, java.lang.Boolean force);
    native public java.lang.String $get(double index);
    public static DOMTokenList prototype;
    public DOMTokenList(){}
    native public java.lang.Boolean toggle(java.lang.String token);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.String> iterator();
}

