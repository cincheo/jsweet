package def.dom;
public class DataTransferItem extends def.js.Object {
    public String kind;
    public String type;
    native public File getAsFile();
    native public void getAsString(FunctionStringCallback _callback);
    public static DataTransferItem prototype;
    public DataTransferItem(){}
}

