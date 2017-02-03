package def.dom;

import def.js.Object;
import def.js.Uint8Array;

public class MSMediaKeys extends def.js.Object {
    public java.lang.String keySystem;
    native public MSMediaKeySession createSession(java.lang.String type, Uint8Array initData, Uint8Array cdmData);
    public static MSMediaKeys prototype;
    public MSMediaKeys(java.lang.String keySystem){}
    native public static java.lang.Boolean isTypeSupported(java.lang.String keySystem, java.lang.String type);
    native public MSMediaKeySession createSession(java.lang.String type, Uint8Array initData);
    native public static java.lang.Boolean isTypeSupported(java.lang.String keySystem);
    protected MSMediaKeys(){}
}

