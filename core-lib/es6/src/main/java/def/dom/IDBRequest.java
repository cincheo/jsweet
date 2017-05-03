package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.error;
import jsweet.util.StringTypes.success;

public class IDBRequest extends EventTarget {
    public DOMError error;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.util.function.Function<Event,java.lang.Object> onsuccess;
    public java.lang.String readyState;
    public java.lang.Object result;
    public java.lang.Object source;
    public IDBTransaction transaction;
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.success type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static IDBRequest prototype;
    public IDBRequest(){}
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.success type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

