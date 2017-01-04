package def.dom;
@jsweet.lang.Extends({AbstractWorker.class})
public class Worker extends EventTarget {
    public java.util.function.Function<MessageEvent,Object> onmessage;
    native public void postMessage(Object message, Object ports);
    native public void terminate();
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,Object> listener, Boolean useCapture);
    public static Worker prototype;
    public Worker(String stringUrl){}
    public java.util.function.Function<Event,Object> onerror;
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    native public void postMessage(Object message);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
    protected Worker(){}
}

