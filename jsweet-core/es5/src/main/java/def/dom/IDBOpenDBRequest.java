package def.dom;
public class IDBOpenDBRequest extends IDBRequest {
    public java.util.function.Function<Event,Object> onblocked;
    public java.util.function.Function<IDBVersionChangeEvent,Object> onupgradeneeded;
    native public void addEventListener(jsweet.util.StringTypes.blocked type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.success type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.upgradeneeded type, java.util.function.Function<IDBVersionChangeEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static IDBOpenDBRequest prototype;
    public IDBOpenDBRequest(){}
    native public void addEventListener(jsweet.util.StringTypes.blocked type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.success type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.upgradeneeded type, java.util.function.Function<IDBVersionChangeEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

