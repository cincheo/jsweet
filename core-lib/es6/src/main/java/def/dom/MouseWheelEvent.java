package def.dom;

public class MouseWheelEvent extends MouseEvent {
    public double wheelDelta;
    public double wheelDeltaX;
    public double wheelDeltaY;
    native public void initMouseWheelEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, double buttonArg, EventTarget relatedTargetArg, java.lang.String modifiersListArg, double wheelDeltaArg);
    public static MouseWheelEvent prototype;
    public MouseWheelEvent(){}
}

