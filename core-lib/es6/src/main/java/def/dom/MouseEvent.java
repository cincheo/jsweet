package def.dom;

public class MouseEvent extends UIEvent {
    public java.lang.Boolean altKey;
    public int button;
    public int buttons;
    public double clientX;
    public double clientY;
    public java.lang.Boolean ctrlKey;
    public Element fromElement;
    public double layerX;
    public double layerY;
    public java.lang.Boolean metaKey;
    public double movementX;
    public double movementY;
    public double offsetX;
    public double offsetY;
    public double pageX;
    public double pageY;
    public EventTarget relatedTarget;
    public double screenX;
    public double screenY;
    public java.lang.Boolean shiftKey;
    public Element toElement;
    public int which;
    public double x;
    public double y;
    native public java.lang.Boolean getModifierState(java.lang.String keyArg);
    native public void initMouseEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, java.lang.Boolean ctrlKeyArg, java.lang.Boolean altKeyArg, java.lang.Boolean shiftKeyArg, java.lang.Boolean metaKeyArg, double buttonArg, EventTarget relatedTargetArg);
    public static MouseEvent prototype;
    public MouseEvent(java.lang.String typeArg, MouseEventInit eventInitDict){}
    public MouseEvent(java.lang.String typeArg){}
    protected MouseEvent(){}
}

