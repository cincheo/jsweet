package def.dom;
public class IDBIndex extends def.js.Object {
    public jsweet.util.union.Union<String,String[]> keyPath;
    public String name;
    public IDBObjectStore objectStore;
    public Boolean unique;
    public Boolean multiEntry;
    native public IDBRequest count(Object key);
    native public IDBRequest get(Object key);
    native public IDBRequest getKey(Object key);
    native public IDBRequest openCursor(IDBKeyRange range, String direction);
    native public IDBRequest openKeyCursor(IDBKeyRange range, String direction);
    public static IDBIndex prototype;
    public IDBIndex(){}
    native public IDBRequest count();
    native public IDBRequest openCursor(IDBKeyRange range);
    native public IDBRequest openCursor();
    native public IDBRequest openKeyCursor(IDBKeyRange range);
    native public IDBRequest openKeyCursor();
}

