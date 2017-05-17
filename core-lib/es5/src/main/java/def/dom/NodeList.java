package def.dom;
public class NodeList extends def.js.Object {
    public int length;
    native public Node item(int index);
    native public Node $get(int index);
    public static NodeList prototype;
    public NodeList(){}
}

