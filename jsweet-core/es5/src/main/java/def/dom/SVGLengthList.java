package def.dom;
public class SVGLengthList extends def.js.Object {
    public double numberOfItems;
    native public SVGLength appendItem(SVGLength newItem);
    native public void clear();
    native public SVGLength getItem(double index);
    native public SVGLength initialize(SVGLength newItem);
    native public SVGLength insertItemBefore(SVGLength newItem, double index);
    native public SVGLength removeItem(double index);
    native public SVGLength replaceItem(SVGLength newItem, double index);
    public static SVGLengthList prototype;
    public SVGLengthList(){}
}

