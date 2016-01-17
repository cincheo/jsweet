package source.syntax;

import static jsweet.dom.Globals.console;

public class StatementsWithNoBlocks {

	void m1() {
		int i = 1;
		if (i == 1)
			console.log("is true");
		else
			console.log("is false");
	}

	void m2() {
		int i = 1;
		if (i == 1) {
			console.log("is true");
		} else {
			console.log("is false");
		}
	}

	void m3() {
		int i = 1;
		if (i == 1) {
			console.log("is true");
		} else
			console.log("is false");
	}

	void m4() {
		int i = 1;
		if (i == 1)
			console.log("is true");
		else {
			console.log("is false");
		}
	}

	void m5() {
		int i = 1;
		if (i == 1)
			for (String s : new String[] {})
				console.log(s);
		else {
			console.log("is false");
		}
	}

	void m6() {
		for (String s : new String[] {})
			console.log(s);
		int i = 1;
		console.log(i);
	}

}
