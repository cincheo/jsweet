package source.enums;

import static jsweet.util.Lang.any;

import jsweet.lang.StringType;

public class StringEnums {

	public static void main(String[] args) {
		m(MyStringEnum.A);
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

@StringType
enum MyStringEnum {

	A, B, C

}
