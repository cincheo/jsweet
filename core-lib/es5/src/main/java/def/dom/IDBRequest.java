package def.dom;
import jsweet.util.union.Union3;
public class IDBRequest extends EventTarget {
    public DOMError error;
    public java.util.function.Function<Event,Object> onerror;
    public java.util.function.Function<Event,Object> onsuccess;
    public String readyState;
    public Object result;
    public Union3<IDBObjectStore,IDBIndex,IDBCursor> source;
    public IDBTransaction transaction;
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.success type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static IDBRequest prototype;
    public IDBRequest(){}
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.success type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

