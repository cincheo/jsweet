package def.dom;
public class CSSRuleList extends def.js.Object implements Iterable<CSSRule> {
    public double length;
    native public CSSRule item(double index);
    native public CSSRule $get(double index);
    public static CSSRuleList prototype;
    public CSSRuleList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<CSSRule> iterator();
}

