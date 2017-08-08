/*
 * Some examples borrowed/translated from
 * https://gist.github.com/remojansen/16c661a7afd68e22ac6e.
 * 
 * @author Renaud Pawlak
 * @author https://gist.github.com/remojansen
 */
package source.decorator;

import static def.js.Globals.arguments;
import static def.dom.Globals.console;
import static jsweet.util.Lang.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import def.js.Array;
import def.js.Function;
import def.js.JSON;
import def.js.PropertyDescriptor;
import jsweet.lang.Decorator;

/*
 * Decorators must first be declared as Java annotations (annotated with @Decorator).
 * Then, each decorator must provide an implementation as a global function (of the same name).
 */

@Decorator
@Target(ElementType.TYPE)
@interface MyAnnotation {
	String f1();

	String f2();

	Class<?>[] classes();
}

@Decorator
@Target(ElementType.METHOD)
@interface logMethod {
}

@Decorator
@Target(ElementType.FIELD)
@interface logProperty {
}

class Globals {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		Person p = new Person("remo", "jansen");
		p.saySomething("I love playing", "halo");
		p.name = "Remo";
		String n = p.name;
		console.info(n);
		$export("trace", trace.join(","));
	}

	/*
	 * Follows the decorators implementations as global functions.
	 */
	public static Object MyAnnotation(Object object) {
		// nothing
		return null;
	}

	public static Object logMethod(Object target, String key, def.js.Object descriptor) {

		if (descriptor == null) {
			descriptor = def.js.Object.getOwnPropertyDescriptor(target, key);
		}
		Function originalMethod = (Function) descriptor.$get("value");

		// editing the descriptor/value parameter
		descriptor.$set("value", $noarrow(function(() -> {
			Array<Object> args = $array();
			for (int _i = 0; _i < arguments.length; _i++) {
				array(args)[_i - 0] = arguments[_i];
			}
			String a = args.map(_a -> JSON.stringify(_a)).join().toString();
			// note usage of originalMethod here
			Object result = originalMethod.apply($this, args);
			String r = JSON.stringify(result);
			console.log("Call: " + key + "(" + a + ") => " + r);
			trace.push("Call: " + key + "(" + a + ") => " + r);
			return result;
		})));

		// return edited descriptor as opposed to overwriting the descriptor
		return descriptor;
	}

	public static Object logProperty(Object target, String key) {

		// property value
		Object[] _val = { $this.$get(key) };

		// property getter
		Function getter = function(() -> {
			console.log($template("Get: ${key} => ${_val}"));
			trace.push($template("Get: ${key} => ${_val}"));
			return _val[0];
		});

		// property setter
		Function setter = function((newVal) -> {
			console.log($template("Set: ${key} => ${newVal}"));
			_val[0] = newVal;
		});

		// Delete property.
		if ($this.$delete(key)) {

			// Create new property with getter and setter
			def.js.Object.defineProperty(target, key, new PropertyDescriptor() {
				{
					$set("get", getter);
					$set("set", setter);
					enumerable = true;
					configurable = true;
				}
			});
		}
		
		return null;
	}
}

@MyAnnotation(f1 = "X", f2 = "Y", classes = { SimpleDecorator.class })
public class SimpleDecorator {

}

class Person {

	@logProperty
	public String name;
	public String surname;

	Person(String name, String surname) {
		this.name = name;
		this.surname = surname;
	}

	@logMethod
	public String saySomething(String something, String somethingElse) {
		return this.name + " " + this.surname + " says: " + something + " " + somethingElse;
	}
}
