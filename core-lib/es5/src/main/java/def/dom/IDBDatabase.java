package def.dom;
public class IDBDatabase extends EventTarget {
    public String name;
    public DOMStringList objectStoreNames;
    public java.util.function.Function<Event,Object> onabort;
    public java.util.function.Function<Event,Object> onerror;
    public double version;
    native public void close();
    native public IDBObjectStore createObjectStore(String name, IDBObjectStoreParameters optionalParameters);
    native public void deleteObjectStore(String name);
    native public IDBTransaction transaction(String storeNames, String mode);
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static IDBDatabase prototype;
    public IDBDatabase(){}
    native public IDBObjectStore createObjectStore(String name);
    native public IDBTransaction transaction(String storeNames);
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public IDBTransaction transaction(String[] storeNames, String mode);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public IDBTransaction transaction(String[] storeNames);
    native public void addEventListener(String type, EventListenerObject listener);
}

