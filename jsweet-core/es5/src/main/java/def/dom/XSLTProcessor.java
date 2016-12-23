package def.dom;
public class XSLTProcessor extends def.js.Object {
    native public void clearParameters();
    native public Object getParameter(String namespaceURI, String localName);
    native public void importStylesheet(Node style);
    native public void removeParameter(String namespaceURI, String localName);
    native public void reset();
    native public void setParameter(String namespaceURI, String localName, Object value);
    native public Document transformToDocument(Node source);
    native public DocumentFragment transformToFragment(Node source, Document document);
    public static XSLTProcessor prototype;
    public XSLTProcessor(){}
}

