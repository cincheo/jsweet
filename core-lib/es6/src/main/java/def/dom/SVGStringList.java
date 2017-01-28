package def.dom;

import def.js.Object;

public class SVGStringList extends def.js.Object {
    public double numberOfItems;
    native public java.lang.String appendItem(java.lang.String newItem);
    native public void clear();
    native public java.lang.String getItem(double index);
    native public java.lang.String initialize(java.lang.String newItem);
    native public java.lang.String insertItemBefore(java.lang.String newItem, double index);
    native public java.lang.String removeItem(double index);
    native public java.lang.String replaceItem(java.lang.String newItem, double index);
    public static SVGStringList prototype;
    public SVGStringList(){}
}

