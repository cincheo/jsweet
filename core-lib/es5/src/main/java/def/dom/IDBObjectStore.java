package def.dom;
public class IDBObjectStore extends def.js.Object {
    public DOMStringList indexNames;
    public jsweet.util.union.Union<String,String[]> keyPath;
    public String name;
    public IDBTransaction transaction;
    public Boolean autoIncrement;
    native public IDBRequest add(Object value, Object key);
    native public IDBRequest clear();
    native public IDBRequest count(Object key);
    native public IDBIndex createIndex(String name, String keyPath, IDBIndexParameters optionalParameters);
    native public IDBRequest delete(Object key);
    native public void deleteIndex(String indexName);
    native public IDBRequest get(Object key);
    native public IDBIndex index(String name);
    native public IDBRequest openCursor(Object range, String direction);
    native public IDBRequest put(Object value, Object key);
    public static IDBObjectStore prototype;
    public IDBObjectStore(){}
    native public IDBRequest add(Object value);
    native public IDBRequest count();
    native public IDBIndex createIndex(String name, String keyPath);
    native public IDBRequest openCursor(Object range);
    native public IDBRequest openCursor();
    native public IDBRequest put(Object value);
    native public IDBIndex createIndex(String name, String[] keyPath, IDBIndexParameters optionalParameters);
    native public IDBIndex createIndex(String name, String[] keyPath);
}

