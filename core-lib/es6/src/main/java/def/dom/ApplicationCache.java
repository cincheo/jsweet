package def.dom;

import def.js.StringTypes;
import def.js.StringTypes.cached;
import def.js.StringTypes.checking;
import def.js.StringTypes.downloading;
import def.js.StringTypes.error;
import def.js.StringTypes.noupdate;
import def.js.StringTypes.obsolete;
import def.js.StringTypes.progress;
import def.js.StringTypes.updateready;

public class ApplicationCache extends EventTarget {
    public java.util.function.Function<Event,java.lang.Object> oncached;
    public java.util.function.Function<Event,java.lang.Object> onchecking;
    public java.util.function.Function<Event,java.lang.Object> ondownloading;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.util.function.Function<Event,java.lang.Object> onnoupdate;
    public java.util.function.Function<Event,java.lang.Object> onobsolete;
    public java.util.function.Function<ProgressEvent,java.lang.Object> onprogress;
    public java.util.function.Function<Event,java.lang.Object> onupdateready;
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
    native public void addEventListener(def.js.StringTypes.cached type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.checking type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.downloading type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.noupdate type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.obsolete type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.updateready type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static ApplicationCache prototype;
    public ApplicationCache(){}
    native public void addEventListener(def.js.StringTypes.cached type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.checking type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.downloading type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.error type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.noupdate type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.obsolete type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.progress type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.updateready type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

