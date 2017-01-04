package def.dom;
public class SVGNumberList extends def.js.Object {
    public double numberOfItems;
    native public SVGNumber appendItem(SVGNumber newItem);
    native public void clear();
    native public SVGNumber getItem(double index);
    native public SVGNumber initialize(SVGNumber newItem);
    native public SVGNumber insertItemBefore(SVGNumber newItem, double index);
    native public SVGNumber removeItem(double index);
    native public SVGNumber replaceItem(SVGNumber newItem, double index);
    public static SVGNumberList prototype;
    public SVGNumberList(){}
}

