package source.structural;

import static jsweet.util.Globals.$export;

import jsweet.lang.Interface;

public class StaticMembersInInterfaces {

	public static void main(String[] args) {
		IStaticMethods.m();
		IStaticMethods2.m();
		$export("value3", IStaticMethods.s);
		$export("value4", IStaticMethods2.s);
	}

}

interface IStaticMethods {

	static void m() {
		$export("value1", "test1");
	}

	String s = "test3";
	
}

@Interface
class IStaticMethods2 {

	static void m() {
		$export("value2", "test2");
	}
	
	static String s = "test4";
	
}
