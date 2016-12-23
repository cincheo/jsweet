package def.dom;
import def.js.Uint8Array;
public class MSMediaKeys extends def.js.Object {
    public String keySystem;
    native public MSMediaKeySession createSession(String type, Uint8Array initData, Uint8Array cdmData);
    public static MSMediaKeys prototype;
    public MSMediaKeys(String keySystem){}
    native public static Boolean isTypeSupported(String keySystem, String type);
    native public MSMediaKeySession createSession(String type, Uint8Array initData);
    native public static Boolean isTypeSupported(String keySystem);
    protected MSMediaKeys(){}
}

