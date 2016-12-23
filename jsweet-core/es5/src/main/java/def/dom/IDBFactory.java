package def.dom;
public class IDBFactory extends def.js.Object {
    native public double cmp(Object first, Object second);
    native public IDBOpenDBRequest deleteDatabase(String name);
    native public IDBOpenDBRequest open(String name, double version);
    public static IDBFactory prototype;
    public IDBFactory(){}
    native public IDBOpenDBRequest open(String name);
}

