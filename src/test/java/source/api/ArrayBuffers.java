package source.api;

import static jsweet.util.Globals.$export;
import static jsweet.util.Globals.any;

import jsweet.lang.Array;
import jsweet.lang.ArrayBuffer;
import jsweet.lang.Int32Array;

public class ArrayBuffers {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		ArrayBuffer arb = new ArrayBuffer(2 * 2 * 4);
		Int32Array array = new Int32Array(arb);
		int[] array2 = any(array);
		array2[1] = 1;
		//array.$set(0, 1);
		int whatever = (int) (double) new Int32Array(arb).$get(0);
		trace.push(String.valueOf(whatever));
		trace.push(String.valueOf(array2[0]));
		trace.push(String.valueOf(array2[1]));
		$export("trace", trace.join(","));
	}

}
