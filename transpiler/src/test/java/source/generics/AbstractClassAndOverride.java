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
	 * This is a function.
	 * @param s a string param
	 * @param i
	 */
	public void testDoc2(String s, int i) {
	}

	/**
	 * This is a function.
	 * @param s a string param
	 * @param i
	 * @return a fake value
	 */
	public boolean testDoc3(String s, int i) {
		return true;
	}

}

interface MyInterface2<T> {
	
	void myFunction(T t);
	
}
