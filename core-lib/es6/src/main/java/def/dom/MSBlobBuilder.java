package def.dom;

import def.js.Object;

public class MSBlobBuilder extends def.js.Object {
    native public void append(java.lang.Object data, java.lang.String endings);
    native public Blob getBlob(java.lang.String contentType);
    public static MSBlobBuilder prototype;
    public MSBlobBuilder(){}
    native public void append(java.lang.Object data);
    native public Blob getBlob();
}

