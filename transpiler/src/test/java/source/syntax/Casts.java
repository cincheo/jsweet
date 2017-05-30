package source.syntax;

interface MyInterface {
	void m1();
}

interface MyOtherInterface extends MyInterface {
	void m2();
}

interface MyYetOtherInterface {
	void m2();
}

class MyClass implements MyInterface {
	@Override
	public void m1() {
	}
}

public class Casts {

	<T extends MyInterface> void m(T object1, MyInterface object2) {
		// Java is very flexible
		Casts c1 = (Casts) object1;
		Casts c2 = (Casts) object2;
	}

	<T> void m2(T object1, MyClass object2) {
		// Java is very flexible
		Casts c1 = (Casts) object1;
		Casts c2 = (Casts) (Object) object2;
	}

	<T extends MyClass> void m2(T object1, MyInterface object2) {
		// Java is very flexible
		MyClass c1 = object1;
		MyClass c2 = (MyClass) object2;
		MyOtherInterface o1 = (MyOtherInterface) object2;
		MyYetOtherInterface o2 = (MyYetOtherInterface) object2;
	}

	void m2(MyOtherInterface i) {
		MyInterface o = (MyOtherInterface) i;
	}

	void m2(MyYetOtherInterface i) {
		MyInterface o = (MyInterface) i;
	}

}
