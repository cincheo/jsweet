package def.dom;
@jsweet.lang.Extends({ChildNode.class})
public class DocumentType extends Node {
    public NamedNodeMap entities;
    public String internalSubset;
    public String name;
    public NamedNodeMap notations;
    public String publicId;
    public String systemId;
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static DocumentType prototype;
    public DocumentType(){}
    native public void remove();
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

