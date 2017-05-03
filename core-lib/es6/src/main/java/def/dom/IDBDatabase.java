package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.abort;
import jsweet.util.StringTypes.error;

public class IDBDatabase extends EventTarget {
    public java.lang.String name;
    public DOMStringList objectStoreNames;
    public java.util.function.Function<Event,java.lang.Object> onabort;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.lang.String version;
    native public void close();
    native public IDBObjectStore createObjectStore(java.lang.String name, java.lang.Object optionalParameters);
    native public void deleteObjectStore(java.lang.String name);
    native public IDBTransaction transaction(java.lang.Object storeNames, java.lang.String mode);
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static IDBDatabase prototype;
    public IDBDatabase(){}
    native public IDBObjectStore createObjectStore(java.lang.String name);
    native public IDBTransaction transaction(java.lang.Object storeNames);
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

