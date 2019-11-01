package pouet;

import java.util.ArrayList;
import java.util.List;

class Toto {
}

class Pichade {
	
}

public class Test extends Pichade {

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

		System.out.println(this.x);
		
		int l;
		l = 10;
		l += 1;
		
		List<String> list = new ArrayList<>();
		for (String s : list) {
			System.out.println("sss = " + s);
		}
	}

	public int getX() {
		return x;
	}

}
