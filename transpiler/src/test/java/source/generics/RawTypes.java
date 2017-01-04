package source.generics;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;

class Cls<T> {
	
}

@SuppressWarnings("rawtypes")
public class RawTypes<T> extends Cls implements Itf {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		RawTypes<String> r1 = new RawTypes();
		@SuppressWarnings("unused")
		RawTypes r2 = new RawTypes();
        Function f  = o->o;
        System.out.println("Hi, random=" + f.apply(4));     
        Deque<String> deque1 = new LinkedList();
        Deque deque2 = new LinkedList();
	}
	
}

interface Itf<T> {
	
}

