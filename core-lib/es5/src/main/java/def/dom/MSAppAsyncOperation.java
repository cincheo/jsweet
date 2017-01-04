package def.dom;
public class MSAppAsyncOperation extends EventTarget {
    public DOMError error;
    public java.util.function.Function<Event,Object> oncomplete;
    public java.util.function.Function<Event,Object> onerror;
    public double readyState;
    public Object result;
    native public void start();
    public double COMPLETED;
    public double ERROR;
    public double STARTED;
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static MSAppAsyncOperation prototype;
    public MSAppAsyncOperation(){}
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

