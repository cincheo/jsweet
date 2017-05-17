package def.dom;

public class NodeList {
    public double length;
    native public Node item(double index);
    native public Node $get(double index);
    public static NodeList prototype;
    public NodeList(){}
}

