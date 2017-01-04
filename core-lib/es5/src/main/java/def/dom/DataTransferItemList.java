package def.dom;
public class DataTransferItemList extends def.js.Object implements Iterable<DataTransferItem> {
    public double length;
    native public DataTransferItem add(File data);
    native public void clear();
    native public DataTransferItem item(double index);
    native public void remove(double index);
    native public DataTransferItem $get(double index);
    public static DataTransferItemList prototype;
    public DataTransferItemList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<DataTransferItem> iterator();
}

