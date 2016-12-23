package java.io;

/**
 * JSweet implementation.
 */
public abstract class Reader implements Closeable {

    protected Object lock;

    protected Reader() {
        this.lock = this;
    }

    protected Reader(Object lock) {
        if (lock == null) {
            throw new NullPointerException();
        }
        this.lock = lock;
    }

	public int read() throws IOException {
		char cb[] = new char[1];
		if (read(cb, 0, 1) == -1)
			return -1;
		else
			return cb[0];
	}

	public int read(char cbuf[]) throws IOException {
		return read(cbuf, 0, cbuf.length);
	}

	abstract public int read(char cbuf[], int off, int len) throws IOException;

	/** Maximum skip-buffer size */
	private static final int maxSkipBufferSize = 8192;

	/** Skip buffer, null until allocated */
	private char skipBuffer[] = null;

	public long skip(long n) throws IOException {
		if (n < 0L)
			throw new IllegalArgumentException("skip value is negative");
		int nn = (int) Math.min(n, maxSkipBufferSize);
		if ((skipBuffer == null) || (skipBuffer.length < nn))
			skipBuffer = new char[nn];
		long r = n;
		while (r > 0) {
			int nc = read(skipBuffer, 0, (int) Math.min(r, nn));
			if (nc == -1)
				break;
			r -= nc;
		}
		return n - r;
	}

	public boolean ready() throws IOException {
		return false;
	}

	public boolean markSupported() {
		return false;
	}

	public void mark(int readAheadLimit) throws IOException {
		throw new IOException("mark() not supported");
	}

	public void reset() throws IOException {
		throw new IOException("reset() not supported");
	}

	abstract public void close() throws IOException;

}
