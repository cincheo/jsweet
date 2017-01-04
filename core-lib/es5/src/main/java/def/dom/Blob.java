package def.dom;
public class Blob extends def.js.Object {
    public double size;
    public String type;
    native public void msClose();
    native public Object msDetachStream();
    native public Blob slice(double start, double end, String contentType);
    public static Blob prototype;
    public Blob(Object[] blobParts, BlobPropertyBag options){}
    native public Blob slice(double start, double end);
    native public Blob slice(double start);
    native public Blob slice();
    public Blob(Object[] blobParts){}
    public Blob(){}
}

