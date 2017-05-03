package def.dom;

import def.js.Object;
import jsweet.util.StringTypes;
import jsweet.util.StringTypes.pointercancel;
import jsweet.util.StringTypes.pointerdown;
import jsweet.util.StringTypes.pointerenter;
import jsweet.util.StringTypes.pointerleave;
import jsweet.util.StringTypes.pointermove;
import jsweet.util.StringTypes.pointerout;
import jsweet.util.StringTypes.pointerover;
import jsweet.util.StringTypes.pointerup;
import jsweet.util.StringTypes.wheel;

@jsweet.lang.Interface
public abstract class GlobalEventHandlers extends def.js.Object {
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointercancel;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerdown;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerenter;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerleave;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointermove;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerout;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerover;
    public java.util.function.Function<PointerEvent,java.lang.Object> onpointerup;
    public java.util.function.Function<WheelEvent,java.lang.Object> onwheel;
    native public void addEventListener(jsweet.util.StringTypes.pointercancel type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerdown type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerenter type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerleave type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointermove type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerout type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerover type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointerup type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.wheel type, java.util.function.Function<WheelEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public void addEventListener(jsweet.util.StringTypes.pointercancel type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerdown type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerenter type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerleave type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointermove type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerout type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerover type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.pointerup type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(jsweet.util.StringTypes.wheel type, java.util.function.Function<WheelEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

