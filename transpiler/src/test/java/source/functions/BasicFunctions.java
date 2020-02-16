package source.functions;

import java.util.function.Function;

public class BasicFunctions {

	public static void main(String[] args) {
		Function<Integer, Integer> f = x -> x;
		assert f.apply(2) == 2;
		assert Function.<Integer>identity().apply(2) == 2;
		
	}
	
}
