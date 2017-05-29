package def.dom;
public class MouseEvent extends UIEvent {
    public Boolean altKey;
    public int button;
    public int buttons;
    public double clientX;
    public double clientY;
    public Boolean ctrlKey;
    public Element fromElement;
    public double layerX;
    public double layerY;
    public Boolean metaKey;
    public double movementX;
    public double movementY;
    public double offsetX;
    public double offsetY;
    public double pageX;
    public double pageY;
    public EventTarget relatedTarget;
    public double screenX;
    public double screenY;
    public Boolean shiftKey;
    public Element toElement;
    public int which;
    public double x;
    public double y;
    native public Boolean getModifierState(String keyArg);
    native public void initMouseEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, Boolean ctrlKeyArg, Boolean altKeyArg, Boolean shiftKeyArg, Boolean metaKeyArg, double buttonArg, EventTarget relatedTargetArg);
    public static MouseEvent prototype;
    public MouseEvent(String typeArg, MouseEventInit eventInitDict){}
    public MouseEvent(String typeArg){}
    protected MouseEvent(){}
}

