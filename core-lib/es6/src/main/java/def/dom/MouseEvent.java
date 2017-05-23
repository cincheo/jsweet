package def.dom;

public class MouseEvent extends UIEvent {
    public java.lang.Boolean altKey;
    public int button;
    public double buttons;
    public int clientX;
    public int clientY;
    public java.lang.Boolean ctrlKey;
    public Element fromElement;
    public int layerX;
    public int layerY;
    public java.lang.Boolean metaKey;
    public int movementX;
    public int movementY;
    public int offsetX;
    public int offsetY;
    public int pageX;
    public int pageY;
    public EventTarget relatedTarget;
    public int screenX;
    public int screenY;
    public java.lang.Boolean shiftKey;
    public Element toElement;
    public double which;
    public int x;
    public int y;
    native public java.lang.Boolean getModifierState(java.lang.String keyArg);
    native public void initMouseEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, java.lang.Boolean ctrlKeyArg, java.lang.Boolean altKeyArg, java.lang.Boolean shiftKeyArg, java.lang.Boolean metaKeyArg, double buttonArg, EventTarget relatedTargetArg);
    public static MouseEvent prototype;
    public MouseEvent(java.lang.String typeArg, MouseEventInit eventInitDict){}
    public MouseEvent(java.lang.String typeArg){}
    protected MouseEvent(){}
}

