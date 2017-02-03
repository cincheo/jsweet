package def.dom;

import def.js.Object;

public class Range extends def.js.Object {
    public java.lang.Boolean collapsed;
    public Node commonAncestorContainer;
    public Node endContainer;
    public double endOffset;
    public Node startContainer;
    public double startOffset;
    native public DocumentFragment cloneContents();
    native public Range cloneRange();
    native public void collapse(java.lang.Boolean toStart);
    native public double compareBoundaryPoints(double how, Range sourceRange);
    native public DocumentFragment createContextualFragment(java.lang.String fragment);
    native public void deleteContents();
    native public void detach();
    native public java.lang.Boolean expand(java.lang.String Unit);
    native public DocumentFragment extractContents();
    native public ClientRect getBoundingClientRect();
    native public ClientRectList getClientRects();
    native public void insertNode(Node newNode);
    native public void selectNode(Node refNode);
    native public void selectNodeContents(Node refNode);
    native public void setEnd(Node refNode, double offset);
    native public void setEndAfter(Node refNode);
    native public void setEndBefore(Node refNode);
    native public void setStart(Node refNode, double offset);
    native public void setStartAfter(Node refNode);
    native public void setStartBefore(Node refNode);
    native public void surroundContents(Node newParent);
    native public java.lang.String toString();
    public double END_TO_END;
    public double END_TO_START;
    public double START_TO_END;
    public double START_TO_START;
    public static Range prototype;
    public Range(){}
}

