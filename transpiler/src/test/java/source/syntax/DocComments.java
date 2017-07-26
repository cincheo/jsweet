package source.syntax;

import java.util.List;

/**
 * This is a test of comment.
 * 
 * @author Renaud Pawlak
 */
public class DocComments {

	/**
	 * A method, which has some doc comment.
	 * 
	 * Another line of comment.
	 * 
	 * @param s1 string 1
	 * @param s2 string 2
	 * @param i integer
	 */
	public String m(String s1, String s2, int i) {
		return "";
	}


	/**
	 * Use lists.
	 */
	public List<String> m2(List<Integer> aList) { return null; }
	
	/**
	 * This is a constant field.
	 */
	public static final String FIELD = "test";
	
}

/**
 * A class comment to be erased.
 * @author Notto Beerased
 */
class CommentedC {
	/**
	 * A constructor for C.
	 */
	public CommentedC(String s) {
	}
}
/**
 * A class comment to be used.
 */
class CommentedC2 {
	public CommentedC2(String sToBeDocumented) {
	}

	/**
	 * Sub overload.
	 * @param i
	 */
	public void overload(int i) {
		
	}

	/**
	 * Main overload.
	 * @param s
	 * @param j
	 */
	public void overload(String s, int j) {
		
	}
	
}

/**
 * An enum test.
 */
enum E { 
	XX_A, /** Test enum */ XX_B, XX_C
}

abstract class Base {
	abstract int m(String base1, int base2);
}

class TestOverride extends Base {

	@Override
	int m(String base1, int base2) {
		return 0;
	}

	/**
	 * Use interface.
	 * @param i is an interface
	 */
	void useInterface(I i) {
		
	}
}

interface I {
	
}


/**
 * Class comment.
 */
class ConstructorOverride<T> {

	/**
	 * C1 comment.
	 */
	public ConstructorOverride(Object o) {
	}
	
	/**
	 * C2 comment.
	 */
	public ConstructorOverride(Object o, T t) {
	}
}
