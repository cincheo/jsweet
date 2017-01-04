package def.dom;
public class DOMImplementation extends def.js.Object {
    native public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype);
    native public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId);
    native public Document createHTMLDocument(String title);
    native public Boolean hasFeature(String feature, String version);
    public static DOMImplementation prototype;
    public DOMImplementation(){}
}

