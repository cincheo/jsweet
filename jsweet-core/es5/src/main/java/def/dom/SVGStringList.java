package def.dom;
public class SVGStringList extends def.js.Object {
    public double numberOfItems;
    native public String appendItem(String newItem);
    native public void clear();
    native public String getItem(double index);
    native public String initialize(String newItem);
    native public String insertItemBefore(String newItem, double index);
    native public String removeItem(double index);
    native public String replaceItem(String newItem, double index);
    public static SVGStringList prototype;
    public SVGStringList(){}
}

