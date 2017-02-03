package def.dom;

public class FocusEvent extends UIEvent {
    public EventTarget relatedTarget;
    native public void initFocusEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, EventTarget relatedTargetArg);
    public static FocusEvent prototype;
    public FocusEvent(java.lang.String typeArg, FocusEventInit eventInitDict){}
    public FocusEvent(java.lang.String typeArg){}
    protected FocusEvent(){}
}

