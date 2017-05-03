package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.error;
import jsweet.util.StringTypes.message;

@jsweet.lang.Extends({AbstractWorker.class})
public class Worker extends EventTarget {
    public java.util.function.Function<MessageEvent,java.lang.Object> onmessage;
    native public void postMessage(java.lang.Object message, java.lang.Object ports);
    native public void terminate();
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    public static Worker prototype;
    public Worker(java.lang.String stringUrl){}
    public java.util.function.Function<Event,java.lang.Object> onerror;
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public void postMessage(java.lang.Object message);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
    protected Worker(){}
}

