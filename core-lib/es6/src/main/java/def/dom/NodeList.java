package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class NodeList extends Iterable<Node> implements java.lang.Iterable<Node> {
    public double length;
    native public Node item(double index);
    native public Node $get(double index);
    public static NodeList prototype;
    public NodeList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<Node> iterator();
}

