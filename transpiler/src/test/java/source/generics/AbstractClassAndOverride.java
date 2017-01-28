package source.generics;

import def.js.Date;

public abstract class AbstractClassAndOverride<E extends AbstractClassAndOverride<E>> implements MyInterface2<E> {

	@Override
	public void myFunction(E t) {

	}

	public Date testDoc(String s, int i) {
		return null;
	}

	/**
	 * This is a function that takes a {@link java.lang.String Java string
	 * class}.
	 * 
	 * @param s
	 *            a string param
	 * @param i
	 */
	public void testDoc2(String s, int i) {
	}

	/**
	 * This is a function.
	 * 
	 * @param s
	 *            a string param
	 * @param i
	 * @return a fake value
	 */
	public boolean testDoc3(String s, int i) {
		return true;
	}

	/**
	 * A private method doc test.
	 */
	private void m() {
		
	}
	
}

interface MyInterface2<T> {

	void myFunction(T t);

}


/**
 * A doc test.
 * @author renaudpawlak
 */
enum MyEnum {
	
}
