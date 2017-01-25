package source.syntax;

interface MyInterface {
}

class MyClass implements MyInterface {

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
	}

}
