package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class DataTransferItemList extends Iterable<File> {
    public double length;
    native public DataTransferItem add(File data);
    native public void clear();
    native public File item(double index);
    native public void remove(double index);
    native public File $get(double index);
    public static DataTransferItemList prototype;
    public DataTransferItemList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<File> iterator();
}

