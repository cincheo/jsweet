package def.dom;

import def.js.Object;

public class Blob extends def.js.Object {
    public double size;
    public java.lang.String type;
    native public void msClose();
    native public java.lang.Object msDetachStream();
    native public Blob slice(double start, double end, java.lang.String contentType);
    public static Blob prototype;
    public Blob(java.lang.Object[] blobParts, BlobPropertyBag options){}
    native public Blob slice(double start, double end);
    native public Blob slice(double start);
    native public Blob slice();
    public Blob(java.lang.Object[] blobParts){}
    public Blob(){}
}

