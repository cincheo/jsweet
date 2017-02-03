package def.dom;

import def.js.Object;

public class IDBKeyRange extends def.js.Object {
    public java.lang.Object lower;
    public java.lang.Boolean lowerOpen;
    public java.lang.Object upper;
    public java.lang.Boolean upperOpen;
    public static IDBKeyRange prototype;
    public IDBKeyRange(){}
    native public static IDBKeyRange bound(java.lang.Object lower, java.lang.Object upper, java.lang.Boolean lowerOpen, java.lang.Boolean upperOpen);
    native public static IDBKeyRange lowerBound(java.lang.Object bound, java.lang.Boolean open);
    native public static IDBKeyRange only(java.lang.Object value);
    native public static IDBKeyRange upperBound(java.lang.Object bound, java.lang.Boolean open);
    native public static IDBKeyRange bound(java.lang.Object lower, java.lang.Object upper, java.lang.Boolean lowerOpen);
    native public static IDBKeyRange bound(java.lang.Object lower, java.lang.Object upper);
    native public static IDBKeyRange lowerBound(java.lang.Object bound);
    native public static IDBKeyRange upperBound(java.lang.Object bound);
}

