package source.syntax;

import jsweet.util.Lang;

@FunctionalInterface
interface MyFunct {
    double combine(double a, double b);
}

public class LambdaExpression {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		Lang.function(() -> {
			System.out.println("hello");
		}).$apply();

		Lang.$apply(Lang.function(() -> {
			System.out.println("hello");
		}));
		
	    MyFunct SUM2 = new MyFunct() {
	        @Override
	        public double combine(double a, double b) {
	            return a + b;
	        }
	    };
	    MyFunct SUM = (a, b) -> a + b;
	    MyFunct MAX = Math::max;
	}

}
