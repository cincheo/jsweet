package def.dom;
@jsweet.lang.Interface
public abstract class MSFileSaver extends def.js.Object {
    native public Boolean msSaveBlob(Object blob, String defaultName);
    native public Boolean msSaveOrOpenBlob(Object blob, String defaultName);
    native public Boolean msSaveBlob(Object blob);
    native public Boolean msSaveOrOpenBlob(Object blob);
}

