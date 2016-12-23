package java.io;

/**
 * JSweet implementation.
 */
public class BufferedReader extends Reader {

	private Reader in;

	private char cb[];
	private int nChars, nextChar;

	private static final int INVALIDATED = -2;
	private static final int UNMARKED = -1;
	private int markedChar;
	private int readAheadLimit;

	private boolean skipLF;

	private boolean markedSkipLF;

	private static int defaultCharBufferSize = 8192;
	private static int defaultExpectedLineLength = 80;

	public BufferedReader(Reader in, int sz) {
		super(in);
		markedChar = UNMARKED;
		readAheadLimit = 0;
		skipLF = false;
		markedSkipLF = false;
		if (sz <= 0)
			throw new IllegalArgumentException("Buffer size <= 0");
		this.in = in;
		cb = new char[sz];
		nextChar = nChars = 0;
	}

	public BufferedReader(Reader in) {
		this(in, defaultCharBufferSize);
	}

	private void ensureOpen() throws IOException {
		if (in == null)
			throw new IOException("Stream closed");
	}

	private void fill() throws IOException {
		int dst;
		if (markedChar <= UNMARKED) {
			dst = 0;
		} else {
			int delta = nextChar - markedChar;
			if (delta >= readAheadLimit) {
				markedChar = INVALIDATED;
				readAheadLimit = 0;
				dst = 0;
			} else {
				if (readAheadLimit <= cb.length) {
					System.arraycopy(cb, markedChar, cb, 0, delta);
					markedChar = 0;
					dst = delta;
				} else {
					char ncb[] = new char[readAheadLimit];
					System.arraycopy(cb, markedChar, ncb, 0, delta);
					cb = ncb;
					markedChar = 0;
					dst = delta;
				}
				nextChar = nChars = delta;
			}
		}

		int n;
		do {
			n = in.read(cb, dst, cb.length - dst);
		} while (n == 0);
		if (n > 0) {
			nChars = dst + n;
			nextChar = dst;
		}
	}

	public int read() throws IOException {
		synchronized (lock) {
			ensureOpen();
			for (;;) {
				if (nextChar >= nChars) {
					fill();
					if (nextChar >= nChars)
						return -1;
				}
				if (skipLF) {
					skipLF = false;
					if (cb[nextChar] == '\n') {
						nextChar++;
						continue;
					}
				}
				return cb[nextChar++];
			}
		}
	}

	private int read1(char[] cbuf, int off, int len) throws IOException {
		if (nextChar >= nChars) {
			if (len >= cb.length && markedChar <= UNMARKED && !skipLF) {
				return in.read(cbuf, off, len);
			}
			fill();
		}
		if (nextChar >= nChars)
			return -1;
		if (skipLF) {
			skipLF = false;
			if (cb[nextChar] == '\n') {
				nextChar++;
				if (nextChar >= nChars)
					fill();
				if (nextChar >= nChars)
					return -1;
			}
		}
		int n = Math.min(len, nChars - nextChar);
		System.arraycopy(cb, nextChar, cbuf, off, n);
		nextChar += n;
		return n;
	}

	public int read(char cbuf[], int off, int len) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}

			int n = read1(cbuf, off, len);
			if (n <= 0)
				return n;
			while ((n < len) && in.ready()) {
				int n1 = read1(cbuf, off + n, len - n);
				if (n1 <= 0)
					break;
				n += n1;
			}
			return n;
		}
	}

	String readLine(boolean ignoreLF) throws IOException {
		StringBuffer s = null;
		int startChar;

		synchronized (lock) {
			ensureOpen();
			boolean omitLF = ignoreLF || skipLF;

			for (;;) {

				if (nextChar >= nChars)
					fill();
				if (nextChar >= nChars) {
					if (s != null && s.length() > 0)
						return s.toString();
					else
						return null;
				}
				boolean eol = false;
				char c = 0;
				int i;

				if (omitLF && (cb[nextChar] == '\n'))
					nextChar++;
				skipLF = false;
				omitLF = false;

				charLoop: for (i = nextChar; i < nChars; i++) {
					c = cb[i];
					if ((c == '\n') || (c == '\r')) {
						eol = true;
						break charLoop;
					}
				}

				startChar = nextChar;
				nextChar = i;

				if (eol) {
					String str;
					if (s == null) {
						str = new String(cb, startChar, i - startChar);
					} else {
						s.append(cb, startChar, i - startChar);
						str = s.toString();
					}
					nextChar++;
					if (c == '\r') {
						skipLF = true;
					}
					return str;
				}

				if (s == null)
					s = new StringBuffer(defaultExpectedLineLength);
				s.append(cb, startChar, i - startChar);
			}
		}
	}

	public String readLine() throws IOException {
		return readLine(false);
	}

	public long skip(long n) throws IOException {
		if (n < 0L) {
			throw new IllegalArgumentException("skip value is negative");
		}
		synchronized (lock) {
			ensureOpen();
			long r = n;
			while (r > 0) {
				if (nextChar >= nChars)
					fill();
				if (nextChar >= nChars) /* EOF */
					break;
				if (skipLF) {
					skipLF = false;
					if (cb[nextChar] == '\n') {
						nextChar++;
					}
				}
				long d = nChars - nextChar;
				if (r <= d) {
					nextChar += r;
					r = 0;
					break;
				} else {
					r -= d;
					nextChar = nChars;
				}
			}
			return n - r;
		}
	}

	public boolean ready() throws IOException {
		ensureOpen();

		if (skipLF) {
			if (nextChar >= nChars && in.ready()) {
				fill();
			}
			if (nextChar < nChars) {
				if (cb[nextChar] == '\n')
					nextChar++;
				skipLF = false;
			}
		}
		return (nextChar < nChars) || in.ready();
	}

	public boolean markSupported() {
		return true;
	}

	public void mark(int readAheadLimit) throws IOException {
		if (readAheadLimit < 0) {
			throw new IllegalArgumentException("Read-ahead limit < 0");
		}
		ensureOpen();
		this.readAheadLimit = readAheadLimit;
		markedChar = nextChar;
		markedSkipLF = skipLF;
	}

	public void reset() throws IOException {
		ensureOpen();
		if (markedChar < 0)
			throw new IOException((markedChar == INVALIDATED) ? "Mark invalid" : "Stream not marked");
		nextChar = markedChar;
		skipLF = markedSkipLF;
	}

	public void close() throws IOException {
		if (in == null)
			return;
		try {
			in.close();
		} finally {
			in = null;
			cb = null;
		}
	}

}
