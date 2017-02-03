package def.dom;

public class MessageEvent extends Event {
    public java.lang.Object data;
    public java.lang.String origin;
    public java.lang.Object ports;
    public Window source;
    native public void initMessageEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.Object dataArg, java.lang.String originArg, java.lang.String lastEventIdArg, Window sourceArg);
    public static MessageEvent prototype;
    public MessageEvent(){}
}

