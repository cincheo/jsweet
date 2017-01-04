package def.dom;
public class UIEvent extends Event {
    public double detail;
    public Window view;
    native public void initUIEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, double detailArg);
    public static UIEvent prototype;
    public UIEvent(String type, UIEventInit eventInitDict){}
    public UIEvent(String type){}
    protected UIEvent(){}
}

