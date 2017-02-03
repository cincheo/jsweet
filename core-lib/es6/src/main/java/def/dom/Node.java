package def.dom;

public class Node extends EventTarget {
    public NamedNodeMap attributes;
    public java.lang.String baseURI;
    public NodeList childNodes;
    public Node firstChild;
    public Node lastChild;
    public java.lang.String localName;
    public java.lang.String namespaceURI;
    public Node nextSibling;
    public java.lang.String nodeName;
    public double nodeType;
    public java.lang.String nodeValue;
    public Document ownerDocument;
    public HTMLElement parentElement;
    public Node parentNode;
    public java.lang.String prefix;
    public Node previousSibling;
    public java.lang.String textContent;
    native public Node appendChild(Node newChild);
    native public Node cloneNode(java.lang.Boolean deep);
    native public double compareDocumentPosition(Node other);
    native public java.lang.Boolean hasAttributes();
    native public java.lang.Boolean hasChildNodes();
    native public Node insertBefore(Node newChild, Node refChild);
    native public java.lang.Boolean isDefaultNamespace(java.lang.String namespaceURI);
    native public java.lang.Boolean isEqualNode(Node arg);
    native public java.lang.Boolean isSameNode(Node other);
    native public java.lang.String lookupNamespaceURI(java.lang.String prefix);
    native public java.lang.String lookupPrefix(java.lang.String namespaceURI);
    native public void normalize();
    native public Node removeChild(Node oldChild);
    native public Node replaceChild(Node newChild, Node oldChild);
    public double ATTRIBUTE_NODE;
    public double CDATA_SECTION_NODE;
    public double COMMENT_NODE;
    public double DOCUMENT_FRAGMENT_NODE;
    public double DOCUMENT_NODE;
    public double DOCUMENT_POSITION_CONTAINED_BY;
    public double DOCUMENT_POSITION_CONTAINS;
    public double DOCUMENT_POSITION_DISCONNECTED;
    public double DOCUMENT_POSITION_FOLLOWING;
    public double DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC;
    public double DOCUMENT_POSITION_PRECEDING;
    public double DOCUMENT_TYPE_NODE;
    public double ELEMENT_NODE;
    public double ENTITY_NODE;
    public double ENTITY_REFERENCE_NODE;
    public double NOTATION_NODE;
    public double PROCESSING_INSTRUCTION_NODE;
    public double TEXT_NODE;
    public static Node prototype;
    public Node(){}
    native public Node cloneNode();
    native public Node insertBefore(Node newChild);
}

