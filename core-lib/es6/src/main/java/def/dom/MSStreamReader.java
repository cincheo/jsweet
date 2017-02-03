package def.dom;

import def.js.StringTypes;
import def.js.StringTypes.abort;
import def.js.StringTypes.error;
import def.js.StringTypes.load;
import def.js.StringTypes.loadend;
import def.js.StringTypes.loadstart;
import def.js.StringTypes.progress;

@jsweet.lang.Extends({MSBaseReader.class})
public class MSStreamReader extends EventTarget {
    public DOMError error;
    native public void readAsArrayBuffer(MSStream stream, double size);
    native public void readAsBinaryString(MSStream stream, double size);
    native public void readAsBlob(MSStream stream, double size);
    native public void readAsDataURL(MSStream stream, double size);
    native public void readAsText(MSStream stream, java.lang.String encoding, double size);
    public static MSStreamReader prototype;
    public MSStreamReader(){}
    public java.util.function.Function<Event,java.lang.Object> onabort;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.util.function.Function<Event,java.lang.Object> onload;
    public java.util.function.Function<ProgressEvent,java.lang.Object> onloadend;
    public java.util.function.Function<Event,java.lang.Object> onloadstart;
    public java.util.function.Function<ProgressEvent,java.lang.Object> onprogress;
    public double readyState;
    public java.lang.Object result;
    native public void abort();
    public double DONE;
    public double EMPTY;
    public double LOADING;
    native public void addEventListener(def.js.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.loadend type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public void readAsArrayBuffer(MSStream stream);
    native public void readAsBinaryString(MSStream stream);
    native public void readAsBlob(MSStream stream);
    native public void readAsDataURL(MSStream stream);
    native public void readAsText(MSStream stream, java.lang.String encoding);
    native public void readAsText(MSStream stream);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(def.js.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.loadend type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

