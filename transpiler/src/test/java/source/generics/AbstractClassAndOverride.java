package source.generics;

public abstract class AbstractClassAndOverride<E extends AbstractClassAndOverride<E>> implements MyInterface2<E> {

	@Override
	public void myFunction(E t) {
		
	}
	
}

interface MyInterface2<T> {
	
	void myFunction(T t);
	
}
