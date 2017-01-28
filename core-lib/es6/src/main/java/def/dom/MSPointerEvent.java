package def.dom;

public class MSPointerEvent extends MouseEvent {
    public java.lang.Object currentPoint;
    public double height;
    public double hwTimestamp;
    public java.lang.Object intermediatePoints;
    public java.lang.Boolean isPrimary;
    public double pointerId;
    public java.lang.Object pointerType;
    public double pressure;
    public double rotation;
    public double tiltX;
    public double tiltY;
    public double width;
    native public void getCurrentPoint(Element element);
    native public void getIntermediatePoints(Element element);
    native public void initPointerEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, java.lang.Boolean ctrlKeyArg, java.lang.Boolean altKeyArg, java.lang.Boolean shiftKeyArg, java.lang.Boolean metaKeyArg, double buttonArg, EventTarget relatedTargetArg, double offsetXArg, double offsetYArg, double widthArg, double heightArg, double pressure, double rotation, double tiltX, double tiltY, double pointerIdArg, java.lang.Object pointerType, double hwTimestampArg, java.lang.Boolean isPrimary);
    public static MSPointerEvent prototype;
    public MSPointerEvent(java.lang.String typeArg, PointerEventInit eventInitDict){}
    public MSPointerEvent(java.lang.String typeArg){}
    protected MSPointerEvent(){}
}

