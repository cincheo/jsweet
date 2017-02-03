package def.js;
public class Set<T> extends def.js.Object {
    native public Set<T> add(T value);
    native public void clear();
    native public java.lang.Boolean delete(T value);
    native public IterableIterator<jsweet.util.tuple.Tuple2<T,T>> entries();
    native public void forEach(jsweet.util.function.TriConsumer<T,T,Set<T>> callbackfn, java.lang.Object thisArg);
    native public java.lang.Boolean has(T value);
    native public IterableIterator<T> keys();
    public double size;
    native public IterableIterator<T> values();
    native public java.lang.String $get(Symbol toStringTag);
    public Set(){}
    public Set(Iterable<T> iterable){}
    public static Set<?> prototype;
    native public void forEach(jsweet.util.function.TriConsumer<T,T,Set<T>> callbackfn);
}

