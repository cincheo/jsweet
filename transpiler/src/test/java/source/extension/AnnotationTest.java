package source.extension;

import java.util.EventObject;
import java.util.List;
import java.util.Map;

import jsweet.lang.Name;

class Superclass {
	@Name("_f1")
	String f1;
	String f2;
	@Name("_f3")
	String f3() {
		return "";
	}
	String f4() {
		return "";
	}
}

public class AnnotationTest extends Superclass {

	public static void main(String[] args) {
		new ExtendJDK(null).getSource();
	}
	
	public void toBeErased() {
		// this will not be transpiled because the method will be erased
		javax.activity.InvalidActivityException exception = null;
	}

	public void m() {
		String s = f1;
		s = f2;
		s = f3();
		s = f4();
		s = f5;
		s = f6;
		s = f7();
		s = f8();
	}
	
	@Name("_f5")
	String f5;
	String f6;
	@Name("_f7")
	String f7() {
		return "";
	}
	String f8() {
		return "";
	}
	
	List<?> testGenerics;
	Map<String, ?> testGenerics2;
	
}


class ReplaceConstructorTest extends Superclass {

	String s;
	int i;
	
	public ReplaceConstructorTest(String s, int i) {
		this.s = s;
		this.i = i;
	}

	public ReplaceConstructorTest(String s) {
		this.s = s;
	}
	
}

class EventObject2 {
	
	Object source;
	
	public EventObject2(Object source) {
		this.source = source;
	}

	public Object getSource() {
		return source;
	}
}

class ExtendJDK extends EventObject {

	private static final long serialVersionUID = 1L;

	public ExtendJDK(Object source) {
		super(source);
	}
	
}

