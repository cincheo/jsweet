package def.dom;

public class CompositionEvent extends UIEvent {
    public java.lang.String data;
    public java.lang.String locale;
    native public void initCompositionEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, Window viewArg, java.lang.String dataArg, java.lang.String locale);
    public static CompositionEvent prototype;
    public CompositionEvent(java.lang.String typeArg, CompositionEventInit eventInitDict){}
    public CompositionEvent(java.lang.String typeArg){}
    protected CompositionEvent(){}
}

