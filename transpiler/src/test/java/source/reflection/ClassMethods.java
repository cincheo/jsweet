package source.reflection;

import java.lang.reflect.Field;

import static jsweet.util.Lang.$export;

public class ClassMethods {

	public static void main(String[] args) throws Exception {
		var clazz = TestClassForReflection.class;

		// Class.getFields
		Field[] fields = clazz.getFields();
		assert fields != null;
		assert fields.length >= 4;

		assert fields[2].getName() == "s";
		assert fields[2].getType() == String.class;
		assert fields[1].getName() == "b";
		assert fields[1].getType() == int.class;
		
		// Class.getDeclaredFields
		Field[] declaredFields = clazz.getDeclaredFields();
        assert declaredFields != null;
        assert declaredFields.length == 3;
        assert declaredFields[2].getName() == "c";
        assert declaredFields[2].getType() == int.class;
        
        // Field.get
        TestClassForReflection instance = new TestClassForReflection();
        instance.s = "myVal";
        String sValFromReflection = (String) fields[2].get(instance);
        assert instance.s == sValFromReflection;
	}

}

abstract class TestBaseClassForReflection {
    public String s;
    public float f;
}

class TestClassForReflection extends TestBaseClassForReflection {

    public int a;
	public int b;
	private int c;

	public static void m1(Object o) {
		$export("name3", o.getClass().getName());
		$export("simplename3", o.getClass().getSimpleName());
	}

	public void m2() {
		$export("name4", getClass().getName());
		$export("simplename4", getClass().getSimpleName());
	}

	public void m3() {
		$export("name5", this.getClass().getName());
		$export("simplename5", this.getClass().getSimpleName());
	}
}
