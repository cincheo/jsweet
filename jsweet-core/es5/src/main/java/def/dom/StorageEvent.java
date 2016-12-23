package def.dom;
public class StorageEvent extends Event {
    public String url;
    @jsweet.lang.Optional
    public String key;
    @jsweet.lang.Optional
    public String oldValue;
    @jsweet.lang.Optional
    public String newValue;
    @jsweet.lang.Optional
    public Storage storageArea;
    public static StorageEvent prototype;
    public StorageEvent(String type, StorageEventInit eventInitDict){}
    public StorageEvent(String type){}
    protected StorageEvent(){}
}

