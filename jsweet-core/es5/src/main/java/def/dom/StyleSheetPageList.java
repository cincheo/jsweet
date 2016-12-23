package def.dom;
public class StyleSheetPageList extends def.js.Object implements Iterable<CSSPageRule> {
    public double length;
    native public CSSPageRule item(double index);
    native public CSSPageRule $get(double index);
    public static StyleSheetPageList prototype;
    public StyleSheetPageList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<CSSPageRule> iterator();
}

