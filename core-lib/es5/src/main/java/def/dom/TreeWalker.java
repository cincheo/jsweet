package def.dom;
public class TreeWalker extends def.js.Object {
    public Node currentNode;
    public Boolean expandEntityReferences;
    public NodeFilter filter;
    public Node root;
    public double whatToShow;
    native public Node firstChild();
    native public Node lastChild();
    native public Node nextNode();
    native public Node nextSibling();
    native public Node parentNode();
    native public Node previousNode();
    native public Node previousSibling();
    public static TreeWalker prototype;
    public TreeWalker(){}
}

