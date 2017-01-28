package def.dom;

import def.js.Iterable;

@jsweet.lang.SyntacticIterable
public class FileList extends Iterable<File> {
    public double length;
    native public File item(double index);
    native public File $get(double index);
    public static FileList prototype;
    public FileList(){}
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<File> iterator();
}

