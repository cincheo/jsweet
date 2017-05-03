package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.blocked;
import jsweet.util.StringTypes.error;
import jsweet.util.StringTypes.upgradeneeded;

public class IDBOpenDBRequest extends IDBRequest {
    public java.util.function.Function<Event,java.lang.Object> onblocked;
    public java.util.function.Function<IDBVersionChangeEvent,java.lang.Object> onupgradeneeded;
    native public void addEventListener(jsweet.util.StringTypes.blocked type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerErrorEventFunction(error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerFunction(error type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.upgradeneeded type, java.util.function.Function<IDBVersionChangeEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static IDBOpenDBRequest prototype;
    public IDBOpenDBRequest(){}
    native public void addEventListener(jsweet.util.StringTypes.blocked type, java.util.function.Function<Event,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerErrorEventFunction(error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerFunction(error type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.upgradeneeded type, java.util.function.Function<IDBVersionChangeEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

