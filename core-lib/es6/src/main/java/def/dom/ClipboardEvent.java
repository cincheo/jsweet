package def.dom;

public class ClipboardEvent extends Event {
    public DataTransfer clipboardData;
    public static ClipboardEvent prototype;
    public ClipboardEvent(java.lang.String type, ClipboardEventInit eventInitDict){}
    public ClipboardEvent(java.lang.String type){}
    protected ClipboardEvent(){}
}

