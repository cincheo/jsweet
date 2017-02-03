package def.dom;

public class WheelEvent extends MouseEvent {
    public double deltaMode;
    public double deltaX;
    public double deltaY;
    public double deltaZ;
    native public void getCurrentPoint(Element element);
    native public void initWheelEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, double buttonArg, EventTarget relatedTargetArg, java.lang.String modifiersListArg, double deltaXArg, double deltaYArg, double deltaZArg, double deltaMode);
    public double DOM_DELTA_LINE;
    public double DOM_DELTA_PAGE;
    public double DOM_DELTA_PIXEL;
    public static WheelEvent prototype;
    public WheelEvent(java.lang.String typeArg, WheelEventInit eventInitDict){}
    public WheelEvent(java.lang.String typeArg){}
    protected WheelEvent(){}
}

