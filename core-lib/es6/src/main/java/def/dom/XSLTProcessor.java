package def.dom;

import def.js.Object;

public class XSLTProcessor extends def.js.Object {
    native public void clearParameters();
    native public java.lang.Object getParameter(java.lang.String namespaceURI, java.lang.String localName);
    native public void importStylesheet(Node style);
    native public void removeParameter(java.lang.String namespaceURI, java.lang.String localName);
    native public void reset();
    native public void setParameter(java.lang.String namespaceURI, java.lang.String localName, java.lang.Object value);
    native public Document transformToDocument(Node source);
    native public DocumentFragment transformToFragment(Node source, Document document);
    public static XSLTProcessor prototype;
    public XSLTProcessor(){}
}

