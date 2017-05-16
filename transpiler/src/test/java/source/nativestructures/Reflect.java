package source.nativestructures;

import static jsweet.util.Lang.any;

import def.js.Array;
import jsweet.util.Lang;

public class Reflect {

    static Array<String> trace = new Array<>();
	
    public static void main(String[] args) {
	String className = "source.nativestructures.MyAccessedClass";
	Class<?> c = null;
	Object o = null;
	try {
	    c = Class.forName(className);
	    o = c.newInstance();
	} catch (Exception e) {
	}
	MyAccessedClass[] array = (MyAccessedClass[]) java.lang.reflect.Array.newInstance(c, 3);
	assert array.length == 3;
	assert c.isInstance(o);
	assert MyAccessedClass.class.isInstance(o);
	assert MyAccessedInterface.class.isInstance(o);
	assert char.class.isInstance('c');
	assert "c".getClass() == String.class;
	// this works with JSweet (because chars are mapped to strings)
	assert "c".getClass() == any(char.class);
	assert "c".getClass() == any(Character.class);
	Object oo = 'c';
	assert oo.getClass().isInstance(oo);
	assert !StringBuffer.class.isInstance(o);
	//assert MyAccessedInterface.class.isAssignableFrom(MyAccessedClass.class);
	//assert c.isAssignableFrom(MyAccessedClass.class);
	Lang.$export("trace", trace.join(","));
    }

}

class MyAccessedClass implements MyAccessedInterface {

    public MyAccessedClass() {
	Reflect.trace.push("constructor");
    }

}

interface MyAccessedInterface {
    
}