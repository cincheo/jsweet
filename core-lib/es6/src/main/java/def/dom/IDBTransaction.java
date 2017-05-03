package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.abort;
import jsweet.util.StringTypes.complete;
import jsweet.util.StringTypes.error;

public class IDBTransaction extends EventTarget {
    public IDBDatabase db;
    public DOMError error;
    public java.lang.String mode;
    public java.util.function.Function<Event,java.lang.Object> onabort;
    public java.util.function.Function<Event,java.lang.Object> oncomplete;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    native public void abort();
    native public IDBObjectStore objectStore(java.lang.String name);
    public java.lang.String READ_ONLY;
    public java.lang.String READ_WRITE;
    public java.lang.String VERSION_CHANGE;
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static IDBTransaction prototype;
    public IDBTransaction(){}
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

