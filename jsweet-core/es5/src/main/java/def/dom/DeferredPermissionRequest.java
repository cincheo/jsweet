package def.dom;
public class DeferredPermissionRequest extends def.js.Object {
    public double id;
    public String type;
    public String uri;
    native public void allow();
    native public void deny();
    public static DeferredPermissionRequest prototype;
    public DeferredPermissionRequest(){}
}

