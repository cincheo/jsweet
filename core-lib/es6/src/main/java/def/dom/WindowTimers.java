package def.dom;
@jsweet.lang.Interface
@jsweet.lang.Extends({WindowTimersExtension.class})
public abstract class WindowTimers extends java.lang.Object {
    native public void clearInterval(double handle);
    native public void clearTimeout(double handle);
    native public double setInterval(java.lang.Object handler, java.lang.Object timeout, java.lang.Object... args);
    native public double setTimeout(java.lang.Object handler, java.lang.Object timeout, java.lang.Object... args);
    native public void clearImmediate(double handle);
    native public void msClearImmediate(double handle);
    native public double msSetImmediate(java.lang.Object expression, java.lang.Object... args);
    native public double setImmediate(java.lang.Object expression, java.lang.Object... args);
    native public double setInterval(java.lang.Object handler);
    native public double setTimeout(java.lang.Object handler);
}

