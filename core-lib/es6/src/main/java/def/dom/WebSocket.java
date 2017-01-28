package def.dom;

import def.js.StringTypes;
import def.js.StringTypes.close;
import def.js.StringTypes.error;
import def.js.StringTypes.message;
import def.js.StringTypes.open;

public class WebSocket extends EventTarget {
    public java.lang.String binaryType;
    public double bufferedAmount;
    public java.lang.String extensions;
    public java.util.function.Function<CloseEvent,java.lang.Object> onclose;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.util.function.Function<MessageEvent,java.lang.Object> onmessage;
    public java.util.function.Function<Event,java.lang.Object> onopen;
    public java.lang.String protocol;
    public double readyState;
    public java.lang.String url;
    native public void close(double code, java.lang.String reason);
    native public void send(java.lang.Object data);
    public double CLOSED;
    public double CLOSING;
    public double CONNECTING;
    public double OPEN;
    native public void addEventListener(def.js.StringTypes.close type, java.util.function.Function<CloseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.message type, java.util.function.Function<MessageEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.open type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static WebSocket prototype;
    public WebSocket(java.lang.String url, java.lang.String protocols){}
    public WebSocket(java.lang.String url, java.lang.Object protocols){}
    native public void close(double code);
    native public void close();
    native public void addEventListener(def.js.StringTypes.close type, java.util.function.Function<CloseEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.message type, java.util.function.Function<MessageEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.open type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    public WebSocket(java.lang.String url){}
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
    protected WebSocket(){}
}

