package source.generics;

@SuppressWarnings("rawtypes")
public class RawTypes<T> extends Cls implements Itf {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		RawTypes r = new RawTypes();
	}
	
}

interface Itf<T> {
	
}

class Cls<T> {
	
}
