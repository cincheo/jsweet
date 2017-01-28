package def.dom;

public class PopStateEvent extends Event {
    public java.lang.Object state;
    native public void initPopStateEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.Object stateArg);
    public static PopStateEvent prototype;
    public PopStateEvent(){}
}

