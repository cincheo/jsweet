package source.nativestructures;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class NativeSystem {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {

		long l1 = System.currentTimeMillis();

		trace.push("" + (l1 > 0));
		
		long l2 = System.currentTimeMillis();

		trace.push("" + (l2 >= l1));

		$export("trace", trace.join(","));

		long nano1 = System.nanoTime();
		System.out.println("nano1="+nano1);

		long nano2 = System.nanoTime();
		System.out.println("nano2="+nano2);
		
		$export("nanoTime", nano2 >= nano1);
	}

}
