package def.dom;
public class MouseWheelEvent extends MouseEvent {
    public double wheelDelta;
    public double wheelDeltaX;
    public double wheelDeltaY;
    native public void initMouseWheelEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, double buttonArg, EventTarget relatedTargetArg, String modifiersListArg, double wheelDeltaArg);
    public static MouseWheelEvent prototype;
    public MouseWheelEvent(){}
}

