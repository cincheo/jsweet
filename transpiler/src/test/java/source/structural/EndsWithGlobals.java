package source.structural;

public class EndsWithGlobals {
    int i;
    int f() { return 5; }
}

class Main {
	public static void main(String[] args) {
		EndsWithGlobals bg = new EndsWithGlobals();
		int ii = bg.i;
		int ff = bg.f();
	}
}
