package def.js;
@jsweet.lang.Interface
public abstract class JSON extends def.js.Object {
    /**
      * Converts a JavaScript Object Notation (JSON) string into an object.
      * @param text A valid JSON string.
      * @param reviver A function that transforms the results. This function is called for each member of the object. 
      * If a member contains nested objects, the nested objects are transformed before the parent object is. 
      */
    native public static java.lang.Object parse(java.lang.String text, java.util.function.BiFunction<java.lang.Object,java.lang.Object,java.lang.Object> reviver);
    /**
      * Converts a JavaScript value to a JavaScript Object Notation (JSON) string.
      * @param value A JavaScript value, usually an object or array, to be converted.
      */
    native public static java.lang.String stringify(java.lang.Object value);
    /**
      * Converts a JavaScript value to a JavaScript Object Notation (JSON) string.
      * @param value A JavaScript value, usually an object or array, to be converted.
      * @param replacer A function that transforms the results.
      */
    native public static java.lang.String stringify(java.lang.Object value, java.util.function.BiFunction<java.lang.String,java.lang.Object,java.lang.Object> replacer);
    /**
      * Converts a JavaScript value to a JavaScript Object Notation (JSON) string.
      * @param value A JavaScript value, usually an object or array, to be converted.
      * @param replacer Array that transforms the results.
      */
    native public static java.lang.String stringify(java.lang.Object value, java.lang.Object[] replacer);
    /**
      * Converts a JavaScript value to a JavaScript Object Notation (JSON) string.
      * @param value A JavaScript value, usually an object or array, to be converted.
      * @param replacer A function that transforms the results.
      * @param space Adds indentation, white space, and line break characters to the return-value JSON text to make it easier to read.
      */
    native public static java.lang.String stringify(java.lang.Object value, java.util.function.BiFunction<java.lang.String,java.lang.Object,java.lang.Object> replacer, java.lang.Object space);
    /**
      * Converts a JavaScript value to a JavaScript Object Notation (JSON) string.
      * @param value A JavaScript value, usually an object or array, to be converted.
      * @param replacer Array that transforms the results.
      * @param space Adds indentation, white space, and line break characters to the return-value JSON text to make it easier to read.
      */
    native public static java.lang.String stringify(java.lang.Object value, java.lang.Object[] replacer, java.lang.Object space);
    native public static java.lang.String $getStatic(Symbol toStringTag);
    /**
      * Converts a JavaScript Object Notation (JSON) string into an object.
      * @param text A valid JSON string.
      * @param reviver A function that transforms the results. This function is called for each member of the object. 
      * If a member contains nested objects, the nested objects are transformed before the parent object is. 
      */
    native public static java.lang.Object parse(java.lang.String text);
}

