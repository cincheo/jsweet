package def.dom;

@jsweet.lang.Extends({ChildNode.class})
public class DocumentType extends Node {
    public NamedNodeMap entities;
    public java.lang.String internalSubset;
    public java.lang.String name;
    public NamedNodeMap notations;
    public java.lang.String publicId;
    public java.lang.String systemId;
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static DocumentType prototype;
    public DocumentType(){}
    native public void remove();
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

