package def.dom;
public class DataTransfer extends def.js.Object {
    public String dropEffect;
    public String effectAllowed;
    public FileList files;
    public DataTransferItemList items;
    public DOMStringList types;
    native public Boolean clearData(String format);
    native public String getData(String format);
    native public Boolean setData(String format, String data);
    public static DataTransfer prototype;
    public DataTransfer(){}
    native public Boolean clearData();
}

