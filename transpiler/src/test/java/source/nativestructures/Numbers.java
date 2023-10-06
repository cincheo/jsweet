package source.nativestructures;

import static jsweet.util.Lang.$export;
import static def.js.Globals.isNaN;

import def.js.Array;
import jsweet.lang.TsNoCheck;

//without this annotation, TypeScript would complain about the asserts in line 44/45
// ("[ts] This condition will always return 'true'")
@TsNoCheck
public class Numbers {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		trace.push(">");
		boolean knowPages = true;
		int pageMax = 1;
		trace.push(Integer.toString(knowPages ? pageMax : 0));

		Integer i1 = new Integer(Character.valueOf(' '));
		Integer i2 = new Integer('x');
		Integer i3 = new Integer("12");
		assert i1.intValue() == 32;
		assert i2.intValue() == 120;
		assert i3.intValue() == 12;

		Long l1 = new Long(Character.valueOf(' '));
		Long l2 = new Long('x');
		Long l3 = new Long("12");
		assert l1.intValue() == 32;
		assert l2.intValue() == 120;
		assert l3.intValue() == 12;

		Double d1 = new Double("2.30");
		assert d1.doubleValue() == 2.30;
		Double d2 = new Double(2.31);
		assert d2.doubleValue() == 2.31;

		Double.valueOf("1.2");
		assert isNaN(Double.NaN);
		assert isNaN(Float.NaN);
		assert 1 != Double.NaN;
		assert 2 != Float.NaN;

		$export("trace", trace.join(","));
	}

}
