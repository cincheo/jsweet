package def.dom;

import def.js.Object;

@jsweet.lang.Interface
public abstract class TextStreamBase extends def.js.Object {
    /**
     * The column number of the current character position in an input stream.
     */
    public double Column;
    /**
     * The current line number in an input stream.
     */
    public double Line;
    /**
     * Closes a text stream.
     * It is not necessary to close standard streams; they close automatically when the process ends. If 
     * you close a standard stream, be aware that any other pointers to that standard stream become invalid.
     */
    native public void Close();
}

