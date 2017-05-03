package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.complete;
import jsweet.util.StringTypes.error;

public class MSWebViewAsyncOperation extends EventTarget {
    public DOMError error;
    public java.util.function.Function<Event,java.lang.Object> oncomplete;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public double readyState;
    public java.lang.Object result;
    public MSHTMLWebViewElement target;
    public double type;
    native public void start();
    public double COMPLETED;
    public double ERROR;
    public double STARTED;
    public double TYPE_CAPTURE_PREVIEW_TO_RANDOM_ACCESS_STREAM;
    public double TYPE_CREATE_DATA_PACKAGE_FROM_SELECTION;
    public double TYPE_INVOKE_SCRIPT;
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static MSWebViewAsyncOperation prototype;
    public MSWebViewAsyncOperation(){}
    native public void addEventListener(jsweet.util.StringTypes.complete type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

