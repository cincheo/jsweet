package source.structural;

public class InnerClassFieldClash {

	public static void main() {
		new InnerClassFieldClash().setup();
	}

	int a = 0;

	void setup() {
		new B().f();
	}

	class B {

		int a = 1;

		void f() {
			assert a == 1;
		}

	}
}
