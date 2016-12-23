package def.dom;
public class MSPointerEvent extends MouseEvent {
    public Object currentPoint;
    public double height;
    public double hwTimestamp;
    public Object intermediatePoints;
    public Boolean isPrimary;
    public double pointerId;
    public Object pointerType;
    public double pressure;
    public double rotation;
    public double tiltX;
    public double tiltY;
    public double width;
    native public void getCurrentPoint(Element element);
    native public void getIntermediatePoints(Element element);
    native public void initPointerEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, Boolean ctrlKeyArg, Boolean altKeyArg, Boolean shiftKeyArg, Boolean metaKeyArg, double buttonArg, EventTarget relatedTargetArg, double offsetXArg, double offsetYArg, double widthArg, double heightArg, double pressure, double rotation, double tiltX, double tiltY, double pointerIdArg, Object pointerType, double hwTimestampArg, Boolean isPrimary);
    public static MSPointerEvent prototype;
    public MSPointerEvent(String typeArg, PointerEventInit eventInitDict){}
    public MSPointerEvent(String typeArg){}
    protected MSPointerEvent(){}
}

