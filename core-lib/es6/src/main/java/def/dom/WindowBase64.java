package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class WindowBase64 extends def.js.Object {
    native public java.lang.String atob(java.lang.String encodedString);
    native public java.lang.String btoa(java.lang.String rawString);
}

