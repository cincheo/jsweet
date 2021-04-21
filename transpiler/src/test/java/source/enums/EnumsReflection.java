package source.enums;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class EnumsReflection {
	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		MyEnum3 e = MyEnum3.A;
		MyClass3 c = new MyClass3();
		assert c.getClass().equals(MyClass3.class);
		assert e.getClass().equals(MyEnum3.class);
		//assert e.getDeclaringClass().equals(MyEnum3.class);
		
		//trace.push("" + (MyComplexEnum.RATIO_16_9.compareTo(MyComplexEnum.RATIO_2_1)<0));

		$export("trace", ">" + trace.join(","));
	}

}


enum MyEnum3 {
	A, B, C;

	public void m() {
		
	}
	
}

class MyClass3 {
	
}