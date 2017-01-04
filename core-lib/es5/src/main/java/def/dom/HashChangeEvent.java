package def.dom;
public class HashChangeEvent extends Event {
    public String newURL;
    public String oldURL;
    public static HashChangeEvent prototype;
    public HashChangeEvent(String type, HashChangeEventInit eventInitDict){}
    public HashChangeEvent(String type){}
    protected HashChangeEvent(){}
}

