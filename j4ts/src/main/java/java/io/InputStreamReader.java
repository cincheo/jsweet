package java.io;

import static jsweet.util.Globals.any;

import java.nio.charset.Charset;

/**
 * JSweet implementation.
 */

public class InputStreamReader extends Reader {

	InputStream in;

	public InputStreamReader(InputStream in) {
		super(in);
		this.in = in;
	}

	public InputStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
		super(in);
		this.in = in;
	}

	public InputStreamReader(InputStream in, Charset cs) {
		super(in);
		this.in = in;
		if (cs == null)
			throw new NullPointerException("charset");
	}

	public int read(char cbuf[], int offset, int length) throws IOException {
		byte[] buf = any(cbuf);
		return in.read(buf, offset, length);
	}

	public boolean ready() throws IOException {
		return in.available()>0;
	}

	public void close() throws IOException {
		in.close();
	}
}
