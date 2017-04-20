package source.structural;

import static jsweet.util.Lang.$export;

import jsweet.lang.Interface;

public class StaticMembersInInterfaces {

	public static void main(String[] args) {
		IStaticMethods.m();
		IStaticMethods2.m();
		$export("value3", IStaticMethods.s);
		$export("value4", IStaticMethods2.s);
		$export("value5", Impl2.MY_CONSTANT);
		new Impl2().m();
	}

}

interface IStaticMethods {

	static void m() {
		$export("value1", "test1");
	}

	String s = "test3";
	String MY_CONSTANT = "cst";
	
}

@Interface
class IStaticMethods2 {

	static void m() {
		$export("value2", "test2");
	}
	
	static String s = "test4";
	
}

class Impl2 implements IStaticMethods {
	
	public void m() {
		$export("value6", MY_CONSTANT);
	}
	
}
