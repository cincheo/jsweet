package def.dom;
public class MSBlobBuilder extends def.js.Object {
    native public void append(Object data, String endings);
    native public Blob getBlob(String contentType);
    public static MSBlobBuilder prototype;
    public MSBlobBuilder(){}
    native public void append(Object data);
    native public Blob getBlob();
}

