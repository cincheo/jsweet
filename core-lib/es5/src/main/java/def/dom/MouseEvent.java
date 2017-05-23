package def.dom;
public class MouseEvent extends UIEvent {
    public Boolean altKey;
    public double button;
    public double buttons;
    public int clientX;
    public int clientY;
    public Boolean ctrlKey;
    public Element fromElement;
    public int layerX;
    public int layerY;
    public Boolean metaKey;
    public int movementX;
    public int movementY;
    public int offsetX;
    public int offsetY;
    public int pageX;
    public int pageY;
    public EventTarget relatedTarget;
    public int screenX;
    public int screenY;
    public Boolean shiftKey;
    public Element toElement;
    public double which;
    public int x;
    public int y;
    native public Boolean getModifierState(String keyArg);
    native public void initMouseEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, double detailArg, double screenXArg, double screenYArg, double clientXArg, double clientYArg, Boolean ctrlKeyArg, Boolean altKeyArg, Boolean shiftKeyArg, Boolean metaKeyArg, double buttonArg, EventTarget relatedTargetArg);
    public static MouseEvent prototype;
    public MouseEvent(String typeArg, MouseEventInit eventInitDict){}
    public MouseEvent(String typeArg){}
    protected MouseEvent(){}
}

