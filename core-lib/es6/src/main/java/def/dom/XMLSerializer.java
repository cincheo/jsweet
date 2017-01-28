package def.dom;

import def.js.Object;

public class XMLSerializer extends def.js.Object {
    native public java.lang.String serializeToString(Node target);
    public static XMLSerializer prototype;
    public XMLSerializer(){}
}

