package def.dom;
public class WheelEvent extends MouseEvent {
    public double deltaMode;
    public double deltaX;
    public double deltaY;
    public double deltaZ;
    native public void getCurrentPoint(Element element);
    native public void initWheelEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, double buttonArg, EventTarget relatedTargetArg, String modifiersListArg, double deltaXArg, double deltaYArg, double deltaZArg, double deltaMode);
    public double DOM_DELTA_LINE;
    public double DOM_DELTA_PAGE;
    public double DOM_DELTA_PIXEL;
    public static WheelEvent prototype;
    public WheelEvent(String typeArg, WheelEventInit eventInitDict){}
    public WheelEvent(String typeArg){}
    protected WheelEvent(){}
}

