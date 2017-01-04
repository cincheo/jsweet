package def.dom;
public class MSWebViewAsyncOperation extends EventTarget {
    public DOMError error;
    public java.util.function.Function<Event,Object> oncomplete;
    public java.util.function.Function<Event,Object> onerror;
    public double readyState;
    public Object result;
    public MSHTMLWebViewElement target;
    public double type;
    native public void start();
    public double COMPLETED;
    public double ERROR;
    public double STARTED;
    public double TYPE_CAPTURE_PREVIEW_TO_RANDOM_ACCESS_STREAM;
    public double TYPE_CREATE_DATA_PACKAGE_FROM_SELECTION;
    public double TYPE_INVOKE_SCRIPT;
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static MSWebViewAsyncOperation prototype;
    public MSWebViewAsyncOperation(){}
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

