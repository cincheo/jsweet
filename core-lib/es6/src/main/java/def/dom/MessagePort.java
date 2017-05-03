package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes.message;

public class MessagePort extends EventTarget {
    public java.util.function.Function<MessageEvent,java.lang.Object> onmessage;
    native public void close();
    native public void postMessage(java.lang.Object message, java.lang.Object ports);
    native public void start();
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static MessagePort prototype;
    public MessagePort(){}
    native public void postMessage(java.lang.Object message);
    native public void postMessage();
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

