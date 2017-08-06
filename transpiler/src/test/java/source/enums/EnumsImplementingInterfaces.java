package source.enums;

public class EnumsImplementingInterfaces {

	public static void main(String[] args) {
		assert 2 == new EnumsImplementingInterfaces().m(E.B);
	}

	int m(I i) {
		return i.m();
	}

}

interface I {
	int m();
}

enum E implements I {

	A, B, C;

	@Override
	public int m() {
		return ordinal() + 1;
	}

}
