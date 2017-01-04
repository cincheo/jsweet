package def.dom;
public class FormData extends def.js.Object {
    native public void append(Object name, Object value, String blobName);
    public static FormData prototype;
    public FormData(HTMLFormElement form){}
    native public void append(Object name, Object value);
    public FormData(){}
}

