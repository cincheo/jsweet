package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class StyleSheetPageList extends Iterable<CSSPageRule> {
    public double length;
    native public CSSPageRule item(double index);
    native public CSSPageRule $get(double index);
    public static StyleSheetPageList prototype;
    public StyleSheetPageList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<CSSPageRule> iterator();
}

