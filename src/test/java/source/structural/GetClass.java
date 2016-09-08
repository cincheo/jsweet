package source.structural;

import static jsweet.util.Globals.$export;

public class GetClass {

	public static void main(String[] args) {
		AClass1 o = new AClass1();
		$export("name1", AClass1.class.getName());
		$export("name2", o.getClass().getName());
		$export("simplename1", AClass1.class.getSimpleName());
		$export("simplename2", o.getClass().getSimpleName());
		Functions.m1(o);
		new Functions().m2();
		new Functions().m3();
	}

}

class AClass1 {
}

class Functions {
	public static void m1(Object o) {
		$export("name3", o.getClass().getName());
		$export("simplename3", o.getClass().getSimpleName());
	}

	public void m2() {
		$export("name4", getClass().getName());
		$export("simplename4", getClass().getSimpleName());
	}

	public void m3() {
		$export("name5", this.getClass().getName());
		$export("simplename5", this.getClass().getSimpleName());
	}
}
