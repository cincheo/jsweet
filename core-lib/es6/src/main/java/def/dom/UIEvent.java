package def.dom;

public class UIEvent extends Event {
    public double detail;
    public Window view;
    native public void initUIEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, double detailArg);
    public static UIEvent prototype;
    public UIEvent(java.lang.String type, UIEventInit eventInitDict){}
    public UIEvent(java.lang.String type){}
    protected UIEvent(){}
}

