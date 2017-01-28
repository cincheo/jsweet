package def.dom;

import def.js.Object;

public class Event extends def.js.Object {
    public java.lang.Boolean bubbles;
    public java.lang.Boolean cancelBubble;
    public java.lang.Boolean cancelable;
    public EventTarget currentTarget;
    public java.lang.Boolean defaultPrevented;
    public double eventPhase;
    public java.lang.Boolean isTrusted;
    public java.lang.Boolean returnValue;
    public Element srcElement;
    public EventTarget target;
    public double timeStamp;
    public java.lang.String type;
    native public void initEvent(java.lang.String eventTypeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg);
    native public void preventDefault();
    native public void stopImmediatePropagation();
    native public void stopPropagation();
    public double AT_TARGET;
    public double BUBBLING_PHASE;
    public double CAPTURING_PHASE;
    public static Event prototype;
    public Event(java.lang.String type, EventInit eventInitDict){}
    public Event(java.lang.String type){}
    protected Event(){}
}

