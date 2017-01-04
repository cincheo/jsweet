package def.dom;
public class IDBCursor extends def.js.Object {
    public String direction;
    public Object key;
    public Object primaryKey;
    public jsweet.util.union.Union<IDBObjectStore,IDBIndex> source;
    native public void advance(double count);
    @jsweet.lang.Name("continue")
    native public void Continue(Object key);
    native public IDBRequest delete();
    native public IDBRequest update(Object value);
    public String NEXT;
    public String NEXT_NO_DUPLICATE;
    public String PREV;
    public String PREV_NO_DUPLICATE;
    public static IDBCursor prototype;
    public IDBCursor(){}
    @jsweet.lang.Name("continue")
    native public void Continue();
}

