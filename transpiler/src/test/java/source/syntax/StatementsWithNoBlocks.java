package source.syntax;

import static def.dom.Globals.console;
import static jsweet.util.Lang.$export;

import def.js.Array;

public class StatementsWithNoBlocks {

	static Array<String> trace = new Array<>();
	
	static void m1() {
		int i = 1;
		if (i == 1)
			console.log("is true");
		else
			console.log("is false");
	}

	static void m2() {
		int i = 1;
		if (i == 1) {
			console.log("is true");
		} else {
			console.log("is false");
		}
	}

	static void m3() {
		int i = 1;
		if (i == 1) {
			console.log("is true");
		} else
			console.log("is false");
	}

	static void m4() {
		int i = 1;
		if (i == 1)
			console.log("is true");
		else {
			console.log("is false");
		}
	}

	static void m5() {
		int i = 1;
		if (i == 1)
			for (String s : new String[] {})
				console.log(s);
		else {
			console.log("is false");
		}
	}

	static void m6() {
		for (String s : new String[] {"aa", "bb"})
			trace.push(s);
		
		int i = 0;
		for (; i < 3; i ++)
			trace.push(""  + i);
		
	}
	
	public static void main(String[] args) {
		m1();
		m2();
		m3();
		m4();
		m5();
		m6();
		$export("trace", trace.join(","));
	}

}
