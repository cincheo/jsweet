package def.dom;

import def.js.Object;

public class EventTarget extends def.js.Object {
    native public void addEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    native public java.lang.Boolean dispatchEvent(Event evt);
    native public void removeEventListener(java.lang.String type, EventListener listener, java.lang.Boolean useCapture);
    public static EventTarget prototype;
    public EventTarget(){}
    native public void addEventListener(java.lang.String type, EventListener listener);
    native public void removeEventListener(java.lang.String type, EventListener listener);
    native public void addEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void removeEventListener(java.lang.String type, EventListenerObject listener, java.lang.Boolean useCapture);
    native public void addEventListener(java.lang.String type, EventListenerObject listener);
    native public void removeEventListener(java.lang.String type, EventListenerObject listener);
}

