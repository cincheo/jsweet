package def.dom;

import def.js.Object;

public class IDBObjectStore extends def.js.Object {
    public DOMStringList indexNames;
    public java.lang.String keyPath;
    public java.lang.String name;
    public IDBTransaction transaction;
    native public IDBRequest add(java.lang.Object value, java.lang.Object key);
    native public IDBRequest clear();
    native public IDBRequest count(java.lang.Object key);
    native public IDBIndex createIndex(java.lang.String name, java.lang.String keyPath, java.lang.Object optionalParameters);
    native public IDBRequest delete(java.lang.Object key);
    native public void deleteIndex(java.lang.String indexName);
    native public IDBRequest get(java.lang.Object key);
    native public IDBIndex index(java.lang.String name);
    native public IDBRequest openCursor(java.lang.Object range, java.lang.String direction);
    native public IDBRequest put(java.lang.Object value, java.lang.Object key);
    public static IDBObjectStore prototype;
    public IDBObjectStore(){}
    native public IDBRequest add(java.lang.Object value);
    native public IDBRequest count();
    native public IDBIndex createIndex(java.lang.String name, java.lang.String keyPath);
    native public IDBRequest openCursor(java.lang.Object range);
    native public IDBRequest openCursor();
    native public IDBRequest put(java.lang.Object value);
}

