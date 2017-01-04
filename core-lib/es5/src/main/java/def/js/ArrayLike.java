package def.js;
@jsweet.lang.Interface
public abstract class ArrayLike<T> extends def.js.Object implements Iterable<T> {
    public final double length=0;
    native public T $get(double n);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<T> iterator();
}

