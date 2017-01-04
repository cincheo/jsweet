package def.dom;
public class MessagePort extends EventTarget {
    public java.util.function.Function<MessageEvent,Object> onmessage;
    native public void close();
    native public void postMessage(Object message, Object ports);
    native public void start();
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static MessagePort prototype;
    public MessagePort(){}
    native public void postMessage(Object message);
    native public void postMessage();
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

