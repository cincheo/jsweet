package def.dom;

public class PermissionRequest extends DeferredPermissionRequest {
    public java.lang.String state;
    native public void defer();
    public static PermissionRequest prototype;
    public PermissionRequest(){}
}

