package pouet;

import java.util.List;

class Toto {
}

public class Test {

	public Test() {
	}

	private int x;
	protected Toto toto;

	public static void main(String[] args) {
		Test t = new Test();
		t.blah();
	}

	void blah() {
		this.x = 5;
		this.toto = new Toto();

		int l;
		l = 10;
		l += 1;
	}

	public int getX() {
		return x;
	}

}
