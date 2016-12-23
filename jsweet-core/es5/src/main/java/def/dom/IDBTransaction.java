package def.dom;
public class IDBTransaction extends EventTarget {
    public IDBDatabase db;
    public DOMError error;
    public String mode;
    public java.util.function.Function<Event,Object> onabort;
    public java.util.function.Function<Event,Object> oncomplete;
    public java.util.function.Function<Event,Object> onerror;
    native public void abort();
    native public IDBObjectStore objectStore(String name);
    public String READ_ONLY;
    public String READ_WRITE;
    public String VERSION_CHANGE;
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static IDBTransaction prototype;
    public IDBTransaction(){}
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

