package def.decorator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import jsweet.lang.Decorator;

@Decorator
@Target(ElementType.TYPE)
public @interface MyDecorator {
}
