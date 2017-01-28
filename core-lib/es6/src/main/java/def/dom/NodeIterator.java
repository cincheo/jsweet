package def.dom;

import def.js.Object;

public class NodeIterator extends def.js.Object {
    public java.lang.Boolean expandEntityReferences;
    public NodeFilter filter;
    public Node root;
    public double whatToShow;
    native public void detach();
    native public Node nextNode();
    native public Node previousNode();
    public static NodeIterator prototype;
    public NodeIterator(){}
}

