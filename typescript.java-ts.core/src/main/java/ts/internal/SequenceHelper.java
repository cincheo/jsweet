package ts.internal;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceHelper {

	private final static AtomicInteger requestSeq = new AtomicInteger();
	private final static AtomicInteger tmpSeq = new AtomicInteger();
	
	public static int getRequestSeq() {
		return requestSeq.getAndIncrement();
	}

	public static int getTempSeq() {
		return tmpSeq.getAndIncrement();
	}
}
