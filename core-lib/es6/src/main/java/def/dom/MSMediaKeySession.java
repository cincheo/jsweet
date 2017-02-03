package def.dom;

import def.js.Uint8Array;

public class MSMediaKeySession extends EventTarget {
    public MSMediaKeyError error;
    public java.lang.String keySystem;
    public java.lang.String sessionId;
    native public void close();
    native public void update(Uint8Array key);
    public static MSMediaKeySession prototype;
    public MSMediaKeySession(){}
}

