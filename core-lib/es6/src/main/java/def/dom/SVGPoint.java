package def.dom;

import def.js.Object;

public class SVGPoint extends def.js.Object {
    public double x;
    public double y;
    native public SVGPoint matrixTransform(SVGMatrix matrix);
    public static SVGPoint prototype;
    public SVGPoint(){}
}

