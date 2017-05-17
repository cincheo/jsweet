package def.dom;
@jsweet.lang.Interface
public abstract class NodeListOf<TNode extends Node> extends NodeList implements Iterable<TNode> {
    public double length;
    native public TNode item(int index);
    native public TNode $get(int index);
}

