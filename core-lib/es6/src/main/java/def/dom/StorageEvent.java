package def.dom;

public class StorageEvent extends Event {
    public java.lang.String key;
    public java.lang.Object newValue;
    public java.lang.Object oldValue;
    public Storage storageArea;
    public java.lang.String url;
    native public void initStorageEvent(java.lang.String typeArg, java.lang.Boolean canBubbleArg, java.lang.Boolean cancelableArg, java.lang.String keyArg, java.lang.Object oldValueArg, java.lang.Object newValueArg, java.lang.String urlArg, Storage storageAreaArg);
    public static StorageEvent prototype;
    public StorageEvent(){}
}

