package def.dom;

import def.js.Object;

public class IDBIndex extends def.js.Object {
    public java.lang.String keyPath;
    public java.lang.String name;
    public IDBObjectStore objectStore;
    public java.lang.Boolean unique;
    native public IDBRequest count(java.lang.Object key);
    native public IDBRequest get(java.lang.Object key);
    native public IDBRequest getKey(java.lang.Object key);
    native public IDBRequest openCursor(IDBKeyRange range, java.lang.String direction);
    native public IDBRequest openKeyCursor(IDBKeyRange range, java.lang.String direction);
    public static IDBIndex prototype;
    public IDBIndex(){}
    native public IDBRequest count();
    native public IDBRequest openCursor(IDBKeyRange range);
    native public IDBRequest openCursor();
    native public IDBRequest openKeyCursor(IDBKeyRange range);
    native public IDBRequest openKeyCursor();
}

