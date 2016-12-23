package def.dom;
public class MessageEvent extends Event {
    public Object data;
    public String origin;
    public Object ports;
    public Window source;
    native public void initMessageEvent(String typeArg, Boolean canBubbleArg, Boolean cancelableArg, Object dataArg, String originArg, String lastEventIdArg, Window sourceArg);
    public static MessageEvent prototype;
    public MessageEvent(String type, MessageEventInit eventInitDict){}
    public MessageEvent(String type){}
    protected MessageEvent(){}
}

