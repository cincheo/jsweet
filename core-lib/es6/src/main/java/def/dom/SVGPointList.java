package def.dom;

import def.js.Object;

public class SVGPointList extends def.js.Object {
    public double numberOfItems;
    native public SVGPoint appendItem(SVGPoint newItem);
    native public void clear();
    native public SVGPoint getItem(double index);
    native public SVGPoint initialize(SVGPoint newItem);
    native public SVGPoint insertItemBefore(SVGPoint newItem, double index);
    native public SVGPoint removeItem(double index);
    native public SVGPoint replaceItem(SVGPoint newItem, double index);
    public static SVGPointList prototype;
    public SVGPointList(){}
}

