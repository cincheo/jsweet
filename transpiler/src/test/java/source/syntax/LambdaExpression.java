package source.syntax;

import jsweet.util.Lang;

public class LambdaExpression {

	public static void main(String[] args) {

		Lang.function(() -> {
			System.out.println("hello");
		}).$apply();

		Lang.$apply(Lang.function(() -> {
			System.out.println("hello");
		}));

	}

}
