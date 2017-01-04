package def.dom;
public class NodeIterator extends def.js.Object {
    public Boolean expandEntityReferences;
    public NodeFilter filter;
    public Node root;
    public double whatToShow;
    native public void detach();
    native public Node nextNode();
    native public Node previousNode();
    public static NodeIterator prototype;
    public NodeIterator(){}
}

