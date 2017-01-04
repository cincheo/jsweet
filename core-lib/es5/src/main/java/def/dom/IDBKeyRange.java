package def.dom;
public class IDBKeyRange extends def.js.Object {
    public Object lower;
    public Boolean lowerOpen;
    public Object upper;
    public Boolean upperOpen;
    public static IDBKeyRange prototype;
    public IDBKeyRange(){}
    native public static IDBKeyRange bound(Object lower, Object upper, Boolean lowerOpen, Boolean upperOpen);
    native public static IDBKeyRange lowerBound(Object bound, Boolean open);
    native public static IDBKeyRange only(Object value);
    native public static IDBKeyRange upperBound(Object bound, Boolean open);
    native public static IDBKeyRange bound(Object lower, Object upper, Boolean lowerOpen);
    native public static IDBKeyRange bound(Object lower, Object upper);
    native public static IDBKeyRange lowerBound(Object bound);
    native public static IDBKeyRange upperBound(Object bound);
}

