package source.syntax;

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
	 * This is a constant field.
	 */
	public static final String FIELD = "test";
	
}

/**
 * A class comment to be erased.
 */
class CommentedC {
	/**
	 * A constructor for C.
	 */
	public CommentedC(String s) {
	}
}

/**
 * An enum test.
 */
enum E { 
	
}
