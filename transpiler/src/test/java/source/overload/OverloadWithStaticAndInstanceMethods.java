package source.overload;

import static jsweet.util.Lang.$export;

public class OverloadWithStaticAndInstanceMethods {

	public static void main(String[] args) {
		m(new OverloadWithStaticAndInstanceMethods());
		new OverloadWithStaticAndInstanceMethods().m();
		int[] a = new int[2];
		multiplyByInt(a, 2, 2);
	}

	public static void m(OverloadWithStaticAndInstanceMethods o) {
		$export("static", true);
	}

	public void m() {
		$export("instance", true);
	}

	static int multiplyByInt(int a[], final int aSize, final int factor) {
		return multiplyByInt(a, a, aSize, factor);
	}

	private static int multiplyByInt(int res[], int a[], final int aSize, final int factor) {
		long carry = 0;
		for (int i = 0; i < aSize; i++) {
			// carry = unsignedMultAddAdd(a[i], factor, (int) carry, 0);
			res[i] = (int) carry;
			carry >>>= 32;
		}
		return (int) carry;
	}

	static int multiplyByInt2(int a[], final int aSize, final int factor) {
		return multiplyByInt2(null, a, aSize, factor);
	}

	private static int multiplyByInt2(int res[], int a[], final int aSize, final int factor) {
		long carry = 0;
		for (int i = 0; i < aSize; i++) {
			// carry = unsignedMultAddAdd(a[i], factor, (int) carry, 0);
			res[i] = (int) carry;
			carry >>>= 32;
		}
		return (int) carry;
	}

	static int multiplyByInt3(int a[], final int aSize, final int factor) {
		return multiplyByInt3(a, aSize, factor, null);
	}

	private static int multiplyByInt3(int a[], final int aSize, final int factor, int res[]) {
		long carry = 0;
		for (int i = 0; i < aSize; i++) {
			// carry = unsignedMultAddAdd(a[i], factor, (int) carry, 0);
			res[i] = (int) carry;
			carry >>>= 32;
		}
		return (int) carry;
	}
	
}
