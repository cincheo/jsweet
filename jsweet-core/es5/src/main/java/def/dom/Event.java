package def.dom;
public class Event extends def.js.Object {
    public Boolean bubbles;
    public Boolean cancelBubble;
    public Boolean cancelable;
    public EventTarget currentTarget;
    public Boolean defaultPrevented;
    public double eventPhase;
    public Boolean isTrusted;
    public Boolean returnValue;
    public Element srcElement;
    public EventTarget target;
    public double timeStamp;
    public String type;
    native public void initEvent(String eventTypeArg, Boolean canBubbleArg, Boolean cancelableArg);
    native public void preventDefault();
    native public void stopImmediatePropagation();
    native public void stopPropagation();
    public double AT_TARGET;
    public double BUBBLING_PHASE;
    public double CAPTURING_PHASE;
    public static Event prototype;
    public Event(String type, EventInit eventInitDict){}
    public Event(String type){}
    protected Event(){}
}

