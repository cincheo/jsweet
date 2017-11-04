package source.structural;

import java.util.Map;

class T1 {

	public void m() {
		
	}
	
}

class T2 extends T1 {

}

public class InheritanceWithGenerics {

	protected <T> T resolveObject(T elementDefaultObject, String elementName, Map<String, String> attributes) {
		return elementDefaultObject;
	}

}

class SubClass {

	protected <T> T resolveObject(T elementDefaultObject, String elementName, Map<String, String> attributes) {
		if (elementDefaultObject instanceof T1) {
			T1 piece = new T2();
			return (T) piece;
		}
		return elementDefaultObject;
	}
}
