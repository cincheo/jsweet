package def.dom;

import def.js.StringTypes;
import def.js.StringTypes.abort;
import def.js.StringTypes.error;
import def.js.StringTypes.load;
import def.js.StringTypes.loadend;
import def.js.StringTypes.loadstart;
import def.js.StringTypes.progress;
import def.js.StringTypes.readystatechange;
import def.js.StringTypes.timeout;

@jsweet.lang.Extends({XMLHttpRequestEventTarget.class})
public class XMLHttpRequest extends EventTarget {
    public java.lang.String msCaching;
    public java.util.function.Function<ProgressEvent,java.lang.Object> onreadystatechange;
    public double readyState;
    public java.lang.Object response;
    public java.lang.Object responseBody;
    public java.lang.String responseText;
    public java.lang.String responseType;
    public java.lang.Object responseXML;
    public double status;
    public java.lang.String statusText;
    public double timeout;
    public XMLHttpRequestUpload upload;
    public java.lang.Boolean withCredentials;
    native public void abort();
    native public java.lang.String getAllResponseHeaders();
    native public java.lang.String getResponseHeader(java.lang.String header);
    native public java.lang.Boolean msCachingEnabled();
    native public void open(java.lang.String method, java.lang.String url, java.lang.Boolean async, java.lang.String user, java.lang.String password);
    native public void overrideMimeType(java.lang.String mime);
    native public void send(Document data);
    native public void send(java.lang.String data);
    native public void send(java.lang.Object data);
    native public void setRequestHeader(java.lang.String header, java.lang.String value);
    public double DONE;
    public double HEADERS_RECEIVED;
    public double LOADING;
    public double OPENED;
    public double UNSENT;
    native public void addEventListener(def.js.StringTypes.readystatechange type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    public static XMLHttpRequest prototype;
    public XMLHttpRequest(){}
    native public static XMLHttpRequest create();
    public java.util.function.Function<Event,java.lang.Object> onabort;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.util.function.Function<Event,java.lang.Object> onload;
    public java.util.function.Function<ProgressEvent,java.lang.Object> onloadend;
    public java.util.function.Function<Event,java.lang.Object> onloadstart;
    public java.util.function.Function<ProgressEvent,java.lang.Object> onprogress;
    public java.util.function.Function<ProgressEvent,java.lang.Object> ontimeout;
    native public void addEventListener(def.js.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.loadend type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.timeout type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public void open(java.lang.String method, java.lang.String url, java.lang.Boolean async, java.lang.String user);
    native public void open(java.lang.String method, java.lang.String url, java.lang.Boolean async);
    native public void open(java.lang.String method, java.lang.String url);
    native public void send();
    native public void addEventListener(def.js.StringTypes.abort type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.load type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.loadend type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.loadstart type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.readystatechange type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.timeout type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

