package def.dom;
public class ClipboardEvent extends Event {
    public DataTransfer clipboardData;
    public static ClipboardEvent prototype;
    public ClipboardEvent(String type, ClipboardEventInit eventInitDict){}
    public ClipboardEvent(String type){}
    protected ClipboardEvent(){}
}

