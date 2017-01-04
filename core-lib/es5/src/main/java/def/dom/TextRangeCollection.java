package def.dom;
public class TextRangeCollection extends def.js.Object implements Iterable<TextRange> {
    public double length;
    native public TextRange item(double index);
    native public TextRange $get(double index);
    public static TextRangeCollection prototype;
    public TextRangeCollection(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<TextRange> iterator();
}

