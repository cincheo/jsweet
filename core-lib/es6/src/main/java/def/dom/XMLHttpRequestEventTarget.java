package def.dom;

import def.js.Object;
import jsweet.util.StringTypes;
import jsweet.util.StringTypes.abort;
import jsweet.util.StringTypes.error;
import jsweet.util.StringTypes.load;
import jsweet.util.StringTypes.loadend;
import jsweet.util.StringTypes.loadstart;
import jsweet.util.StringTypes.progress;
import jsweet.util.StringTypes.timeout;

@jsweet.lang.Interface
public abstract class XMLHttpRequestEventTarget extends def.js.Object {
    public java.util.function.Function<Event,java.lang.Object> onabort;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.util.function.Function<Event,java.lang.Object> onload;
    public java.util.function.Function<ProgressEvent,java.lang.Object> onloadend;
    public java.util.function.Function<Event,java.lang.Object> onloadstart;
    public java.util.function.Function<ProgressEvent,java.lang.Object> onprogress;
    public java.util.function.Function<ProgressEvent,java.lang.Object> ontimeout;
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.loadend type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.timeout type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.loadend type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.timeout type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

