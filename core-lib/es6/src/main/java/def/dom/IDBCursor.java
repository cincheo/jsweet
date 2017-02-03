package def.dom;

import def.js.Object;

public class IDBCursor extends def.js.Object {
    public java.lang.String direction;
    public java.lang.Object key;
    public java.lang.Object primaryKey;
    public java.lang.Object source;
    native public void advance(double count);
    @jsweet.lang.Name("continue")
    native public void Continue(java.lang.Object key);
    native public IDBRequest delete();
    native public IDBRequest update(java.lang.Object value);
    public java.lang.String NEXT;
    public java.lang.String NEXT_NO_DUPLICATE;
    public java.lang.String PREV;
    public java.lang.String PREV_NO_DUPLICATE;
    public static IDBCursor prototype;
    public IDBCursor(){}
    @jsweet.lang.Name("continue")
    native public void Continue();
}

