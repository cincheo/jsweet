package def.dom;

import def.js.Object;

public class DataTransferItem extends def.js.Object {
    public java.lang.String kind;
    public java.lang.String type;
    native public File getAsFile();
    native public void getAsString(FunctionStringCallback _callback);
    public static DataTransferItem prototype;
    public DataTransferItem(){}
}

