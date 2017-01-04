package def.dom;
public class Selection extends def.js.Object {
    public Node anchorNode;
    public double anchorOffset;
    public Node focusNode;
    public double focusOffset;
    public Boolean isCollapsed;
    public double rangeCount;
    public String type;
    native public void addRange(Range range);
    native public void collapse(Node parentNode, double offset);
    native public void collapseToEnd();
    native public void collapseToStart();
    native public Boolean containsNode(Node node, Boolean partlyContained);
    native public void deleteFromDocument();
    native public void empty();
    native public void extend(Node newNode, double offset);
    native public Range getRangeAt(double index);
    native public void removeAllRanges();
    native public void removeRange(Range range);
    native public void selectAllChildren(Node parentNode);
    native public void setBaseAndExtent(Node baseNode, double baseOffset, Node extentNode, double extentOffset);
    native public String toString();
    public static Selection prototype;
    public Selection(){}
}

