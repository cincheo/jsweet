package source.api;

import static jsweet.util.Lang.$export;

public class Characters {

	public static void main(String[] args) {
		new Characters().test();
	}

	final char memberChar = 'c';
	final int memberCharInt = (int) 'h';
	final static char memberCharStatic = 'z';
	final static int memberCharIntStatic = (int) 'w';

	void test() {
		Character c = 'C';
		assert Character.isAlphabetic(c.charValue());
		assert !Character.isDigit(c);
		assert Character.isLetter(c);
		assert Character.isLetterOrDigit(c);
		assert Character.isLetterOrDigit(c.charValue());
		assert Character.isUpperCase(c);
		assert !Character.isLowerCase(c);
		assert Character.isLowerCase(Character.toLowerCase(c));
		assert 'c' == Character.toLowerCase(c);
		assert c == Character.toUpperCase(Character.toLowerCase(c));
		char c2 = 'C';
		assert c2 == Character.toUpperCase(Character.toLowerCase(c2));

		char charD = 'D'; /* 68 */
		switch (charD) {
		case 68:
			$export("switch_int", true);
			break;
		case memberCharStatic:
			System.out.println("static");
			break;
		case memberCharIntStatic:
			System.out.println("static int");
			break;
		case memberCharInt:
			System.out.println("char ");
			break;
		case memberChar:
			System.out.println("int");
			break;
		}

		switch (charD) {
		case 'D':
			$export("switch_char", true);
			break;
		}

		switch (charD) {
		case (char) 68:
			$export("switch_char_cast_int", true);
			break;
		}

		switch (charD) {
		case (char) 'D':
			$export("switch_char_cast_char", true);
			break;
		}

		switch (charD) {
		case (int) 68:
			$export("switch_int_cast_int", true);
			break;
		}

		switch (charD) {
		case (int) 'D':
			$export("switch_int_cast_char", true);
			break;
		}
	}

}
