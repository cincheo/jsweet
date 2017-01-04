package def.dom;
import def.js.Uint8Array;
public class MSMediaKeySession extends EventTarget {
    public MSMediaKeyError error;
    public String keySystem;
    public String sessionId;
    native public void close();
    native public void update(Uint8Array key);
    public static MSMediaKeySession prototype;
    public MSMediaKeySession(){}
}

