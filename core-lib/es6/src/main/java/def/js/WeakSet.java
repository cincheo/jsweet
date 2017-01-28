package def.js;
public class WeakSet<T> extends def.js.Object {
    native public WeakSet<T> add(T value);
    native public void clear();
    native public java.lang.Boolean delete(T value);
    native public java.lang.Boolean has(T value);
    native public java.lang.String $get(Symbol toStringTag);
    public WeakSet(){}
    public WeakSet(Iterable<T> iterable){}
    public static WeakSet<?> prototype;
}

