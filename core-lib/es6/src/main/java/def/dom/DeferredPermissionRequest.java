package def.dom;

import def.js.Object;

public class DeferredPermissionRequest extends def.js.Object {
    public double id;
    public java.lang.String type;
    public java.lang.String uri;
    native public void allow();
    native public void deny();
    public static DeferredPermissionRequest prototype;
    public DeferredPermissionRequest(){}
}

