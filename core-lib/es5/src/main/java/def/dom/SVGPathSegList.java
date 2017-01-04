package def.dom;
public class SVGPathSegList extends def.js.Object {
    public double numberOfItems;
    native public SVGPathSeg appendItem(SVGPathSeg newItem);
    native public void clear();
    native public SVGPathSeg getItem(double index);
    native public SVGPathSeg initialize(SVGPathSeg newItem);
    native public SVGPathSeg insertItemBefore(SVGPathSeg newItem, double index);
    native public SVGPathSeg removeItem(double index);
    native public SVGPathSeg replaceItem(SVGPathSeg newItem, double index);
    public static SVGPathSegList prototype;
    public SVGPathSegList(){}
}

