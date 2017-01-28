package def.dom;

import def.js.Object;

public class MSStream extends def.js.Object {
    public java.lang.String type;
    native public void msClose();
    native public java.lang.Object msDetachStream();
    public static MSStream prototype;
    public MSStream(){}
}

