package source.decorator.other;

public class Globals {

	public static Object MyOtherDecorator(Object... args) {
		System.out.println("this is a decorator for " + args[0]);
		return null;
	}

}
