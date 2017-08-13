package source.extension;

public class UseOfGlobalVariable {

	public static void main(String[] args) {

		System.out.println("I = " + Globals.I);

		System.out.println("i = " + Globals.i);

	}

}

class Globals {

	// ok
	static final int I = 1;

	// ko
	static int i = 2;

}
