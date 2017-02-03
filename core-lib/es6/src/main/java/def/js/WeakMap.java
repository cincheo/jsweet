package def.js;
public class WeakMap<K,V> extends def.js.Object {
    native public void clear();
    native public java.lang.Boolean delete(K key);
    native public V get(K key);
    native public java.lang.Boolean has(K key);
    native public WeakMap<K,V> set(K key, V value);
    native public java.lang.String $get(Symbol toStringTag);
    public WeakMap(){}
    public WeakMap(Iterable<jsweet.util.tuple.Tuple2<K,V>> iterable){}
    public static WeakMap<?,?> prototype;
    native public WeakMap<K,V> set(K key);
}

