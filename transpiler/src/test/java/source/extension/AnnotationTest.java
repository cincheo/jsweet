package source.extension;

import java.util.List;

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
	
}
