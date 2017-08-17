package source.structural;

import static jsweet.util.Lang.$export;

import jsweet.lang.Replace;

public class ReplaceAnnotation {

	public static void main(String[] args) {
		$export("test1", new ReplaceAnnotation().m1());
		$export("test2", new ReplaceAnnotation().m2());
		$export("test3", new ReplaceAnnotation().m3());
		$export("test4", new ReplaceAnnotation().m4());
		$export("test5", new ReplaceAnnotation().m5());
	}

	int i = 1;
	int j = 2;

	@Replace("this.j = 3;")
	public ReplaceAnnotation() {
	}
	
	@Replace("return this.i + 1;")
	public int m1() {
		return i;
	}

	public int m2() {
		return i;
	}

	public int m3() {
		return i;
	}

	public int m4() {
		return i;
	}
	
	public int m5() {
		return j;
	}
	
	
}
