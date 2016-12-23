package def.dom;
public class AriaRequestEvent extends Event {
    public String attributeName;
    public String attributeValue;
    public static AriaRequestEvent prototype;
    public AriaRequestEvent(String type, AriaRequestEventInit eventInitDict){}
    public AriaRequestEvent(String type){}
    protected AriaRequestEvent(){}
}

