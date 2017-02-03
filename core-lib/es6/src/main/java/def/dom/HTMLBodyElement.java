package def.dom;

import def.js.StringTypes;
import def.js.StringTypes.MSContentZoom;
import def.js.StringTypes.afterprint;
import def.js.StringTypes.beforeprint;
import def.js.StringTypes.beforeunload;
import def.js.StringTypes.hashchange;
import def.js.StringTypes.message;
import def.js.StringTypes.offline;
import def.js.StringTypes.online;
import def.js.StringTypes.orientationchange;
import def.js.StringTypes.pagehide;
import def.js.StringTypes.pageshow;
import def.js.StringTypes.popstate;
import def.js.StringTypes.resize;
import def.js.StringTypes.storage;
import def.js.StringTypes.unload;

public class HTMLBodyElement extends HTMLElement {
    public java.lang.Object aLink;
    public java.lang.String background;
    public java.lang.Object bgColor;
    public java.lang.String bgProperties;
    public java.lang.Object link;
    public java.lang.Boolean noWrap;
    public java.util.function.Function<Event,java.lang.Object> onafterprint;
    public java.util.function.Function<Event,java.lang.Object> onbeforeprint;
    public java.util.function.Function<BeforeUnloadEvent,java.lang.Object> onbeforeunload;
    public java.util.function.Function<FocusEvent,java.lang.Object> onblur;
    public java.util.function.Function<Event,java.lang.Object> onerror;
    public java.util.function.Function<FocusEvent,java.lang.Object> onfocus;
    public java.util.function.Function<HashChangeEvent,java.lang.Object> onhashchange;
    public java.util.function.Function<Event,java.lang.Object> onload;
    public java.util.function.Function<MessageEvent,java.lang.Object> onmessage;
    public java.util.function.Function<Event,java.lang.Object> onoffline;
    public java.util.function.Function<Event,java.lang.Object> ononline;
    public java.util.function.Function<Event,java.lang.Object> onorientationchange;
    public java.util.function.Function<PageTransitionEvent,java.lang.Object> onpagehide;
    public java.util.function.Function<PageTransitionEvent,java.lang.Object> onpageshow;
    public java.util.function.Function<PopStateEvent,java.lang.Object> onpopstate;
    public java.util.function.Function<UIEvent,java.lang.Object> onresize;
    public java.util.function.Function<StorageEvent,java.lang.Object> onstorage;
    public java.util.function.Function<Event,java.lang.Object> onunload;
    public java.lang.Object text;
    public java.lang.Object vLink;
    native public TextRange createTextRange();
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMSGestureEventAny(MSContentZoom type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMSManipulationEventAny(MSContentZoom type, java.util.function.Function<MSManipulationEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMSPointerEventAny(MSContentZoom type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.afterprint type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerAriaRequestEventAny(MSContentZoom type, java.util.function.Function<AriaRequestEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.beforeprint type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.beforeunload type, java.util.function.Function<BeforeUnloadEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerCommandEventAny(MSContentZoom type, java.util.function.Function<CommandEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerErrorEventAny(MSContentZoom type, java.util.function.Function<ErrorEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerFocusEventAny(MSContentZoom type, java.util.function.Function<FocusEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.hashchange type, java.util.function.Function<HashChangeEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerKeyboardEventAny(MSContentZoom type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.message type, java.util.function.Function<MessageEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMouseEventAny(MSContentZoom type, java.util.function.Function<MouseEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMouseWheelEventAny(MSContentZoom type, java.util.function.Function<MouseWheelEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.offline type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.online type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.orientationchange type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pagehide type, java.util.function.Function<PageTransitionEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.pageshow type, java.util.function.Function<PageTransitionEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerDragEventAny(MSContentZoom type, java.util.function.Function<DragEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerPointerEventAny(MSContentZoom type, java.util.function.Function<PointerEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.popstate type, java.util.function.Function<PopStateEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerProgressEventAny(MSContentZoom type, java.util.function.Function<ProgressEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.resize type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerUIEventAny(MSContentZoom type, java.util.function.Function<UIEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.storage type, java.util.function.Function<StorageEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerTouchEventAny(MSContentZoom type, java.util.function.Function<TouchEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(def.js.StringTypes.unload type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerEventAny(MSContentZoom type, java.util.function.Function<Event,java.lang.Object> listener, java.lang.Boolean useCapture);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerWheelEventAny(MSContentZoom type, java.util.function.Function<WheelEvent,java.lang.Object> listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static HTMLBodyElement prototype;
    public HTMLBodyElement(){}
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMSGestureEventAny(MSContentZoom type, java.util.function.Function<MSGestureEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMSManipulationEventAny(MSContentZoom type, java.util.function.Function<MSManipulationEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMSPointerEventAny(MSContentZoom type, java.util.function.Function<MSPointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.afterprint type, java.util.function.Function<Event,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerAriaRequestEventAny(MSContentZoom type, java.util.function.Function<AriaRequestEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.beforeprint type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.beforeunload type, java.util.function.Function<BeforeUnloadEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerCommandEventAny(MSContentZoom type, java.util.function.Function<CommandEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerErrorEventAny(MSContentZoom type, java.util.function.Function<ErrorEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerFocusEventAny(MSContentZoom type, java.util.function.Function<FocusEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.hashchange type, java.util.function.Function<HashChangeEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerKeyboardEventAny(MSContentZoom type, java.util.function.Function<KeyboardEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.message type, java.util.function.Function<MessageEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMouseEventAny(MSContentZoom type, java.util.function.Function<MouseEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerMouseWheelEventAny(MSContentZoom type, java.util.function.Function<MouseWheelEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.offline type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.online type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.orientationchange type, java.util.function.Function<Event,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pagehide type, java.util.function.Function<PageTransitionEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.pageshow type, java.util.function.Function<PageTransitionEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerDragEventAny(MSContentZoom type, java.util.function.Function<DragEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerPointerEventAny(MSContentZoom type, java.util.function.Function<PointerEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.popstate type, java.util.function.Function<PopStateEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerProgressEventAny(MSContentZoom type, java.util.function.Function<ProgressEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.resize type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerUIEventAny(MSContentZoom type, java.util.function.Function<UIEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.storage type, java.util.function.Function<StorageEvent,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerTouchEventAny(MSContentZoom type, java.util.function.Function<TouchEvent,java.lang.Object> listener);
    native public void addEventListener(def.js.StringTypes.unload type, java.util.function.Function<Event,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerEventAny(MSContentZoom type, java.util.function.Function<Event,java.lang.Object> listener);
    @jsweet.lang.Name("addEventListener")
    native public void addEventListenerListenerWheelEventAny(MSContentZoom type, java.util.function.Function<WheelEvent,java.lang.Object> listener);
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
}

