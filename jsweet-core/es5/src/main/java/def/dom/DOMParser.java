package def.dom;
public class DOMParser extends def.js.Object {
    native public Document parseFromString(String source, String mimeType);
    public static DOMParser prototype;
    public DOMParser(){}
}

