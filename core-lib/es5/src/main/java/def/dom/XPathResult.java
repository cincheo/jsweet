package def.dom;
public class XPathResult extends def.js.Object {
    public Boolean booleanValue;
    public Boolean invalidIteratorState;
    public double numberValue;
    public double resultType;
    public Node singleNodeValue;
    public double snapshotLength;
    public String stringValue;
    native public Node iterateNext();
    native public Node snapshotItem(double index);
    public double ANY_TYPE;
    public double ANY_UNORDERED_NODE_TYPE;
    public double BOOLEAN_TYPE;
    public double FIRST_ORDERED_NODE_TYPE;
    public double NUMBER_TYPE;
    public double ORDERED_NODE_ITERATOR_TYPE;
    public double ORDERED_NODE_SNAPSHOT_TYPE;
    public double STRING_TYPE;
    public double UNORDERED_NODE_ITERATOR_TYPE;
    public double UNORDERED_NODE_SNAPSHOT_TYPE;
    public static XPathResult prototype;
    public XPathResult(){}
}

