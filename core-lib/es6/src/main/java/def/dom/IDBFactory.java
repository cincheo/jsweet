package def.dom;

import def.js.Object;

public class IDBFactory extends def.js.Object {
    native public double cmp(java.lang.Object first, java.lang.Object second);
    native public IDBOpenDBRequest deleteDatabase(java.lang.String name);
    native public IDBOpenDBRequest open(java.lang.String name, double version);
    public static IDBFactory prototype;
    public IDBFactory(){}
    native public IDBOpenDBRequest open(java.lang.String name);
}

