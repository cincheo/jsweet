package def.dom;

public class CustomEvent extends Event {
    public java.lang.Object detail;
    native public void initCustomEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.Object detailArg);
    public static CustomEvent prototype;
    public CustomEvent(java.lang.String typeArg, CustomEventInit eventInitDict){}
    public CustomEvent(java.lang.String typeArg){}
    protected CustomEvent(){}
}

