package def.dom;
public class PopStateEvent extends Event {
    public Object state;
    native public void initPopStateEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Object stateArg);
    public static PopStateEvent prototype;
    public PopStateEvent(){}
}

