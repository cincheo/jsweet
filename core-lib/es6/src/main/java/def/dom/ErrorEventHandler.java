package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class ErrorEventHandler extends def.js.Object {
    native public void $apply(Event event, java.lang.String source, double fileno, double columnNumber);
    native public void $apply(java.lang.String event, java.lang.String source, double fileno, double columnNumber);
    native public void $apply(Event event, java.lang.String source, double fileno);
    native public void $apply(Event event, java.lang.String source);
    native public void $apply(Event event);
    native public void $apply(java.lang.String event, java.lang.String source, double fileno);
    native public void $apply(java.lang.String event, java.lang.String source);
    native public void $apply(java.lang.String event);
}

