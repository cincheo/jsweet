package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class URL extends def.js.Object {
    native public static java.lang.String createObjectURL(java.lang.Object object, ObjectURLOptions options);
    native public static void revokeObjectURL(java.lang.String url);
    native public static java.lang.String createObjectURL(java.lang.Object object);
}

