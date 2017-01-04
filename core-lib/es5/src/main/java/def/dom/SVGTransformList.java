package def.dom;
public class SVGTransformList extends def.js.Object {
    public double numberOfItems;
    native public SVGTransform appendItem(SVGTransform newItem);
    native public void clear();
    native public SVGTransform consolidate();
    native public SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix);
    native public SVGTransform getItem(double index);
    native public SVGTransform initialize(SVGTransform newItem);
    native public SVGTransform insertItemBefore(SVGTransform newItem, double index);
    native public SVGTransform removeItem(double index);
    native public SVGTransform replaceItem(SVGTransform newItem, double index);
    public static SVGTransformList prototype;
    public SVGTransformList(){}
}

