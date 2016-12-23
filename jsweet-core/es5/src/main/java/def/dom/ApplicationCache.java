package def.dom;
public class ApplicationCache extends EventTarget {
    public java.util.function.Function<Event,Object> oncached;
    public java.util.function.Function<Event,Object> onchecking;
    public java.util.function.Function<Event,Object> ondownloading;
    public java.util.function.Function<Event,Object> onerror;
    public java.util.function.Function<Event,Object> onnoupdate;
    public java.util.function.Function<Event,Object> onobsolete;
    public java.util.function.Function<ProgressEvent,Object> onprogress;
    public java.util.function.Function<Event,Object> onupdateready;
    public double status;
    native public void abort();
    native public void swapCache();
    native public void update();
    public double CHECKING;
    public double DOWNLOADING;
    public double IDLE;
    public double OBSOLETE;
    public double UNCACHED;
    public double UPDATEREADY;
    native public void addEventListener(jsweet.util.StringTypes.cached type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.checking type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.downloading type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.noupdate type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.obsolete type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.progress type, java.util.function.Function<ProgressEvent,Object> listener, Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.updateready type, java.util.function.Function<Event,Object> listener, Boolean useCapture);
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    public static ApplicationCache prototype;
    public ApplicationCache(){}
    native public void addEventListener(jsweet.util.StringTypes.cached type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.checking type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.downloading type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.error type, java.util.function.Function<ErrorEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.noupdate type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.obsolete type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.progress type, java.util.function.Function<ProgressEvent,Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.updateready type, java.util.function.Function<Event,Object> listener);
    native public void addEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
}

