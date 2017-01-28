package def.dom;
@jsweet.lang.Interface
public abstract class TextStreamReader extends TextStreamBase {
    /**
     * Returns a specified number of characters from an input stream, starting at the current pointer position.
     * Does not return until the ENTER key is pressed.
     * Can only be used on a stream in reading mode; causes an error in writing or appending mode.
     */
    native public java.lang.String Read(double characters);
    /**
     * Returns all characters from an input stream.
     * Can only be used on a stream in reading mode; causes an error in writing or appending mode.
     */
    native public java.lang.String ReadAll();
    /**
     * Returns an entire line from an input stream.
     * Although this method extracts the newline character, it does not add it to the returned string.
     * Can only be used on a stream in reading mode; causes an error in writing or appending mode.
     */
    native public java.lang.String ReadLine();
    /**
     * Skips a specified number of characters when reading from an input text stream.
     * Can only be used on a stream in reading mode; causes an error in writing or appending mode.
     * @param characters Positive number of characters to skip forward. (Backward skipping is not supported.)
     */
    native public void Skip(double characters);
    /**
     * Skips the next line when reading from an input text stream.
     * Can only be used on a stream in reading mode, not writing or appending mode.
     */
    native public void SkipLine();
    /**
     * Indicates whether the stream pointer position is at the end of a line.
     */
    public java.lang.Boolean AtEndOfLine;
    /**
     * Indicates whether the stream pointer position is at the end of a stream.
     */
    public java.lang.Boolean AtEndOfStream;
}

