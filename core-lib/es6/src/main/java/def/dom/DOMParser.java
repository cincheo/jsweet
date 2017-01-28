package def.dom;

import def.js.Object;

public class DOMParser extends def.js.Object {
    native public Document parseFromString(java.lang.String source, java.lang.String mimeType);
    public static DOMParser prototype;
    public DOMParser(){}
}

