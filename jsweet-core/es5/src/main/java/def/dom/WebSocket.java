package def.dom;
public class WebSocket extends EventTarget {
    public String binaryType;
    public double bufferedAmount;
    public String extensions;
    public java.util.function.Function<CloseEvent,Object> onclose;
    public java.util.function.Function<Event,Object> onerror;
    public java.util.function.Function<MessageEvent,Object> onmessage;
    public java.util.function.Function<Event,Object> onopen;
    public String protocol;
    public double readyState;
    public String url;
    native public void close(double code, String reason);
    native public void send(Object data);
    public double CLOSED;
    public double CLOSING;
    public double CONNECTING;
    public double OPEN;
    native public void addEventListener(jsweet.util.StringTypes.close type, java.util.function.Function<CloseEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.open type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static WebSocket prototype;
    public WebSocket(String url, String protocols){}
    native public void close(double code);
    native public void close();
    native public void addEventListener(jsweet.util.StringTypes.close type, java.util.function.Function<CloseEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.message type, java.util.function.Function<MessageEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.open type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    public WebSocket(String url){}
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    public WebSocket(String url, String[] protocols){}
    native public void addEventListener(String type, EventListenerObject listener);
    protected WebSocket(){}
}

