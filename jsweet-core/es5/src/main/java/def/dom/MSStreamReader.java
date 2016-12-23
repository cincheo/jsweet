package def.dom;
@jsweet.lang.Extends({MSBaseReader.class})
public class MSStreamReader extends EventTarget {
    public DOMError error;
    native public void readAsArrayBuffer(MSStream stream, double size);
    native public void readAsBinaryString(MSStream stream, double size);
    native public void readAsBlob(MSStream stream, double size);
    native public void readAsDataURL(MSStream stream, double size);
    native public void readAsText(MSStream stream, String encoding, double size);
    public static MSStreamReader prototype;
    public MSStreamReader(){}
    public java.util.function.Function<Event,Object> onabort;
    public java.util.function.Function<Event,Object> onerror;
    public java.util.function.Function<Event,Object> onload;
    public java.util.function.Function<ProgressEvent,Object> onloadend;
    public java.util.function.Function<Event,Object> onloadstart;
    public java.util.function.Function<ProgressEvent,Object> onprogress;
    public double readyState;
    public Object result;
    native public void abort();
    public double DONE;
    public double EMPTY;
    public double LOADING;
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.loadend type, java.util.function.Function<ProgressEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.loadstart type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.progress type, java.util.function.Function<ProgressEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    native public void readAsArrayBuffer(MSStream stream);
    native public void readAsBinaryString(MSStream stream);
    native public void readAsBlob(MSStream stream);
    native public void readAsDataURL(MSStream stream);
    native public void readAsText(MSStream stream, String encoding);
    native public void readAsText(MSStream stream);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.loadend type, java.util.function.Function<ProgressEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.loadstart type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.progress type, java.util.function.Function<ProgressEvent,Object> listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

