package def.dom;
@jsweet.lang.Interface
@jsweet.lang.Extends({WindowTimersExtension.class})
public abstract class WindowTimers extends Object {
    native public void clearInterval(double handle);
    native public void clearTimeout(double handle);
    native public double setInterval(Object handler, Object timeout, Object... args);
    native public double setTimeout(Object handler, Object timeout, Object... args);
    native public void clearImmediate(double handle);
    native public void msClearImmediate(double handle);
    native public double msSetImmediate(Object expression, Object... args);
    native public double setImmediate(Object expression, Object... args);
    native public double setInterval(Object handler);
    native public double setTimeout(Object handler);
}

