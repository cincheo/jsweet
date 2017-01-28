package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class StyleSheetList extends Iterable<StyleSheet> {
    public double length;
    native public StyleSheet item(double index);
    native public StyleSheet $get(double index);
    public static StyleSheetList prototype;
    public StyleSheetList(){}
    native public StyleSheet item();
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<StyleSheet> iterator();
}

