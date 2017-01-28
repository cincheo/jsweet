package def.dom;

import def.js.Object;

public class DOMImplementation extends def.js.Object {
    native public Document createDocument(java.lang.String namespaceURI, java.lang.String qualifiedName, DocumentType doctype);
    native public DocumentType createDocumentType(java.lang.String qualifiedName, java.lang.String publicId, java.lang.String systemId);
    native public Document createHTMLDocument(java.lang.String title);
    native public java.lang.Boolean hasFeature(java.lang.String feature, java.lang.String version);
    public static DOMImplementation prototype;
    public DOMImplementation(){}
}

