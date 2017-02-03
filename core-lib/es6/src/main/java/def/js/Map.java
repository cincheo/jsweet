package def.js;
public class Map<K,V> extends def.js.Object {
    native public void clear();
    native public java.lang.Boolean delete(K key);
    native public IterableIterator<jsweet.util.tuple.Tuple2<K,V>> entries();
    native public void forEach(jsweet.util.function.TriConsumer<V,K,Map<K,V>> callbackfn, java.lang.Object thisArg);
    native public V get(K key);
    native public java.lang.Boolean has(K key);
    native public IterableIterator<K> keys();
    native public Map<K,V> set(K key, V value);
    public double size;
    native public IterableIterator<V> values();
    native public java.lang.String $get(Symbol toStringTag);
    public Map(){}
    public Map(Iterable<jsweet.util.tuple.Tuple2<K,V>> iterable){}
    public static Map<?,?> prototype;
    native public void forEach(jsweet.util.function.TriConsumer<V,K,Map<K,V>> callbackfn);
    native public Map<K,V> set(K key);
}

