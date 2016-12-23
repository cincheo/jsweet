package def.dom;
public class FocusEvent extends UIEvent {
    public EventTarget relatedTarget;
    native public void initFocusEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, double detailArg, EventTarget relatedTargetArg);
    public static FocusEvent prototype;
    public FocusEvent(String typeArg, FocusEventInit eventInitDict){}
    public FocusEvent(String typeArg){}
    protected FocusEvent(){}
}

