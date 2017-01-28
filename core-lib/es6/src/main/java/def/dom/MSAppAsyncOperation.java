package def.dom;

import def.js.StringTypes;
import def.js.StringTypes.complete;
import def.js.StringTypes.error;

public class MSAppAsyncOperation extends EventTarget {
    public DOMError error;
    public java.util.function.Function<Event,java.lang.Object> oncomplete;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public double readyState;
    public java.lang.Object result;
    native public void start();
    public double COMPLETED;
    public double ERROR;
    public double STARTED;
    native public void addEventListener(def.js.StringTypes.complete type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static MSAppAsyncOperation prototype;
    public MSAppAsyncOperation(){}
    native public void addEventListener(def.js.StringTypes.complete type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

