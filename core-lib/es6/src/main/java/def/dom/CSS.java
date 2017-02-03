package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class CSS extends def.js.Object {
    native public static java.lang.Boolean supports(java.lang.String property, java.lang.String value);
    native public static java.lang.Boolean supports(java.lang.String property);
}

