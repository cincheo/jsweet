package def.dom;
@jsweet.lang.Interface
public abstract class StorageEventInit extends EventInit {
    @jsweet.lang.Optional
    public String key;
    @jsweet.lang.Optional
    public String oldValue;
    @jsweet.lang.Optional
    public String newValue;
    public String url;
    @jsweet.lang.Optional
    public Storage storageArea;
}

