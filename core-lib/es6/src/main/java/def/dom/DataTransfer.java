package def.dom;

import def.js.Object;

public class DataTransfer extends def.js.Object {
    public java.lang.String dropEffect;
    public java.lang.String effectAllowed;
    public FileList files;
    public DataTransferItemList items;
    public DOMStringList types;
    native public java.lang.Boolean clearData(java.lang.String format);
    native public java.lang.String getData(java.lang.String format);
    native public java.lang.Boolean setData(java.lang.String format, java.lang.String data);
    public static DataTransfer prototype;
    public DataTransfer(){}
    native public java.lang.Boolean clearData();
}

