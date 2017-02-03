package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class WindowTimersExtension extends def.js.Object {
    native public void clearImmediate(double handle);
    native public void msClearImmediate(double handle);
    native public double msSetImmediate(java.lang.Object expression, java.lang.Object... args);
    native public double setImmediate(java.lang.Object expression, java.lang.Object... args);
}

