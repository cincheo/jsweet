package def.dom;

import def.js.Object;

public class SVGElementInstanceList extends def.js.Object {
    public double length;
    native public SVGElementInstance item(double index);
    public static SVGElementInstanceList prototype;
    public SVGElementInstanceList(){}
}

