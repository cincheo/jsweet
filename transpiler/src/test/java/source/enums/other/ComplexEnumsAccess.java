package source.enums.other;

import static jsweet.util.Lang.$export;

import def.js.Array;
import source.enums.MyComplexEnum2;

public class ComplexEnumsAccess {
	public static Array<String> trace2 = new Array<String>();

	public static void main(String[] args) {
		// TODO: this does not work with modules
		//MyComplexEnum2.aStaticMethod();
		trace2.push("" + MyComplexEnum2.RATIO_2_1.getValue());
		trace2.push("" + MyComplexEnum2.RATIO_2_1.str);
		trace2.push("" + MyComplexEnum2.RATIO_2_1.otherName);
		trace2.push("" + (MyComplexEnum2.RATIO_16_9 == MyComplexEnum2.RATIO_16_9));
		trace2.push("" + (MyComplexEnum2.RATIO_16_9.name() == MyComplexEnum2.RATIO_16_9.name()));
		trace2.push("" + (MyComplexEnum2.RATIO_16_9.ordinal() == MyComplexEnum2.RATIO_16_9.ordinal()));
		trace2.push("" + (MyComplexEnum2.RATIO_16_9 != (MyComplexEnum2) MyComplexEnum2.RATIO_3_2));

		$export("trace2", ">" + trace2.join(","));
	}
}

