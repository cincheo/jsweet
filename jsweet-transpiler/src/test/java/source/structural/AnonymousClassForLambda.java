package source.structural;

import java.util.function.Function;

public class AnonymousClassForLambda {

	public static void main(String[] args) {
		onclick = new Function<String, Object>() {
			@Override
			public Object apply(String s) {
				return s;
			}
		};

	}

	static Function<String, Object> onclick;

}
