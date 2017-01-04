package def.dom;
public class CompositionEvent extends UIEvent {
    public String data;
    public String locale;
    native public void initCompositionEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Window viewArg, String dataArg, String locale);
    public static CompositionEvent prototype;
    public CompositionEvent(String typeArg, CompositionEventInit eventInitDict){}
    public CompositionEvent(String typeArg){}
    protected CompositionEvent(){}
}

