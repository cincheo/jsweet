package def.js;
@jsweet.lang.Interface
@jsweet.lang.SyntacticIterable
public abstract class IArguments extends Iterable<java.lang.Object> implements java.lang.Iterable<java.lang.Object> {
    native public java.lang.Object $get(double index);
    public double length;
    public Function callee;
    native public java.lang.Object callee(java.lang.Object... args);
    /** From Iterable, to allow foreach loop (do not use directly). */
    @jsweet.lang.Erased
    native public java.util.Iterator<java.lang.Object> iterator();
}

