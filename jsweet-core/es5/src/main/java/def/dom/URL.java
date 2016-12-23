package def.dom;
@jsweet.lang.Interface
public abstract class URL extends def.js.Object {
    native public static String createObjectURL(Object object, ObjectURLOptions options);
    native public static void revokeObjectURL(String url);
    native public static String createObjectURL(Object object);
}

