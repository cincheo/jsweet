package source.structural;

public class LocalClasses {

	String s = "b";

	public static void main(String[] args) {

		class MyLocalClass1 {

			String m() {
				return "a";
			}
		}

		new MyLocalClass1().m();

	}

	public void m() {

		class MyLocalClass2 {
			private String s2;

			private MyLocalClass2(String s2) {
				this.s2 = s2;
			}

			String m() {
				return s2;
			}

			String m2() {
				return s;
			}
		}

		new MyLocalClass2("a").m();
		new MyLocalClass2("a").m2();

	}

}
