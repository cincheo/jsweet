package def.dom;
public class CustomEvent extends Event {
    public Object detail;
    native public void initCustomEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Object detailArg);
    public static CustomEvent prototype;
    public CustomEvent(String typeArg, CustomEventInit eventInitDict){}
    public CustomEvent(String typeArg){}
    protected CustomEvent(){}
}

