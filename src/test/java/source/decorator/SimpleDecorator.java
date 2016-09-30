package source.decorator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import jsweet.lang.Decorator;

class Globals {
	public static Object MyAnnotation(Object object) {
		// nothing
		return null;
	}
}

@MyAnnotation(f1 = "X", f2 = "Y", classes = { SimpleDecorator.class })
public class SimpleDecorator {

}

@Decorator
@Target(ElementType.TYPE)
@interface MyAnnotation {
	String f1();

	String f2();

	Class<?>[] classes();
}
