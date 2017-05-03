package source.doc;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.array;
import static jsweet.util.Lang.string;

import java.util.function.BiFunction;

enum MyEnum {
	A, B, C
}

class C1 {
	int n;

	{
		n = 4;
	}
}

class C2 {
	static int n;

	static {
		n = 4;
	}
}

public class LanguageSpecifications {
	public static void basicConcepts_coreTypes_primitiveTypes() {
		int i = 2;
		assert i == 2;
		double d = i + 4;
		assert d == 6;
		String s = "string" + '0' + i;
		assert s == "string02";
		boolean b = false;
		assert!b;
	}

	public static void basicConcepts_coreTypes_allowedJavaObjects() {
		Integer i = 2;
		assert i == 2;

		Double d = i + 4d;
		assert d.toString() == "6";
		assert !((Object)d == "6");

		BiFunction<String, Integer, String> f = (str, integer) -> {
			return str.substring(integer);
		};
		assert"bc" == f.apply("abc", 1);
	}

	public static void basicConcepts_coreTypes_javaArrays() {
		int[] arrayOfInts = { 1, 2, 3, 4 };
		assert arrayOfInts.length == 4;
		assert arrayOfInts[0] == 1;

		int i = 0;
		for (int intItem : arrayOfInts) { 
			assert arrayOfInts[i++] == intItem;
		}
	}

	public static void basicConcepts_coreTypes_coreJavascript() {
		String str = "This is a test string";
		assert str.toLowerCase() == "this is a test string";
		assert string(str).substr(1) == string("his is a test string");

		String[] strings = { "a", "b", "c" };
		array(strings).push("d");
		assert strings[3] == "d";
	}

	public static void basicConcepts_enums() {

		MyEnum e = MyEnum.A;
		assert MyEnum.A == e;
		assert e.name() == "A"; 
		assert e.ordinal() == 0;
		assert MyEnum.valueOf("A") == e;
		assert array(MyEnum.values()).indexOf(MyEnum.valueOf("C")) == 2;
	}

	public static void semantics_initializers() {
		assert new C1().n == 4;

		assert C2.n == 4;
	}

	private int n;
	public static LanguageSpecifications instance;

	public static void main(String[] args) {
		instance = new LanguageSpecifications();
		instance.n = 4;

		$export("main_n", instance.n);

		basicConcepts_coreTypes_primitiveTypes();
		basicConcepts_coreTypes_allowedJavaObjects();
		basicConcepts_coreTypes_javaArrays();
		basicConcepts_coreTypes_coreJavascript();
		basicConcepts_enums();
		
		semantics_initializers();

		$export("finished", true);

	}
}
