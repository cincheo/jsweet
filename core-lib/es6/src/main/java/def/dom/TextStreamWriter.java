package def.dom;
@jsweet.lang.Interface
public abstract class TextStreamWriter extends TextStreamBase {
    /**
     * Sends a string to an output stream.
     */
    native public void Write(java.lang.String s);
    /**
     * Sends a specified number of blank lines (newline characters) to an output stream.
     */
    native public void WriteBlankLines(double intLines);
    /**
     * Sends a string followed by a newline character to an output stream.
     */
    native public void WriteLine(java.lang.String s);
}

