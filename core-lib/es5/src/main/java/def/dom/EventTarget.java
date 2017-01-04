package def.dom;
public class EventTarget extends def.js.Object {
    native public void addEventListener(String type, EventListener listener, Boolean useCapture);
    native public Boolean dispatchEvent(Event evt);
    native public void removeEventListener(String type, EventListener listener, Boolean useCapture);
    public static EventTarget prototype;
    public EventTarget(){}
    native public void addEventListener(String type, EventListener listener);
    native public void removeEventListener(String type, EventListener listener);
    native public void addEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void removeEventListener(String type, EventListenerObject listener, Boolean useCapture);
    native public void addEventListener(String type, EventListenerObject listener);
    native public void removeEventListener(String type, EventListenerObject listener);
}

