package source.enums;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.any;

import jsweet.lang.StringEnum;

public class RemovedStringEnums {

	public static void main(String[] args) {
		m(MyStringEnum.A);
		
		$export("value", MyStringEnum.A);
	}

	public static void m(MyStringEnum e) {
		assert any(e) == "A";
		switch (e) {
		case A:
			break;
		case B:
		case C:
		default:
			assert false;
		}
	}

}

@StringEnum
enum MyStringEnum {

	A, B, C

}
