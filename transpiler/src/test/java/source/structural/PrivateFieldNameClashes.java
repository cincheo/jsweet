package source.structural;

public class PrivateFieldNameClashes {
}

abstract class Parent {
	private final byte[] buffer = new byte[8];
}

class Child extends Parent {
	Buffer buffer;
}

interface Buffer {
}
