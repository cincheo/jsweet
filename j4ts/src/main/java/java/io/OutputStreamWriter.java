package java.io;

import static jsweet.util.Globals.any;

import java.nio.charset.Charset;

/**
 * JSweet implementation (partial).
 * 
 * TODO: actual support of charsets.
 */
public class OutputStreamWriter extends Writer {

    private final OutputStream out;

    public OutputStreamWriter(OutputStream out, String charsetName)
        throws UnsupportedEncodingException
    {
        super(out);
        if (charsetName == null)
            throw new NullPointerException("charsetName");
        this.out = out;
    }

    public OutputStreamWriter(OutputStream out) {
        super(out);
        this.out = out;
    }

    public OutputStreamWriter(OutputStream out, Charset cs) {
        super(out);
        if (cs == null)
            throw new NullPointerException("charset");
        this.out = out;
    }

    void flushBuffer() throws IOException {
        out.flush();
    }

    public void write(int c) throws IOException {
        out.write(c);
    }

    public void write(char cbuf[], int off, int len) throws IOException {
    	byte[] buf = any(cbuf);
        out.write(buf, off, len);
    }

    public void write(String str, int off, int len) throws IOException {
        out.write(str.getBytes(), off, len);
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void close() throws IOException {
        out.close();
    }
}
