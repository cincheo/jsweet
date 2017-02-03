package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class MSFileSaver extends def.js.Object {
    native public java.lang.Boolean msSaveBlob(java.lang.Object blob, java.lang.String defaultName);
    native public java.lang.Boolean msSaveOrOpenBlob(java.lang.Object blob, java.lang.String defaultName);
    native public java.lang.Boolean msSaveBlob(java.lang.Object blob);
    native public java.lang.Boolean msSaveOrOpenBlob(java.lang.Object blob);
}

