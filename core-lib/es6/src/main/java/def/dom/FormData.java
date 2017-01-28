package def.dom;

import def.js.Object;

public class FormData extends def.js.Object {
    native public void append(java.lang.Object name, java.lang.Object value, java.lang.String blobName);
    public static FormData prototype;
    public FormData(){}
    native public void append(java.lang.Object name, java.lang.Object value);
}

