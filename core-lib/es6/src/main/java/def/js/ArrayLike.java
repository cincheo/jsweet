package def.js;
@jsweet.lang.Interface
@jsweet.lang.SyntacticIterable
public abstract class ArrayLike<T> extends Iterable<T> {
    public double length;
    native public T $get(double n);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<T> iterator();
}

