package source.extension;

import static source.extension.A2.m;

public class A1 {

	public static void main(String[] args) {
		assert m() == 123;
		assert A2.m() == 123;
	}

}
