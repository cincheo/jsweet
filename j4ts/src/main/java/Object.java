import jsweet.lang.Ambient;
import jsweet.lang.Interface;


/**
 * Declares equals and hashCode on JavaScript objects, for compilation.
 */
@Ambient
@Interface
public class Object {

	public native boolean equals(Object object);
	public native int hashCode();

}
