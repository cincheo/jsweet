package source.extension;

public class AnnotationTest {

	public void toBeErased() {
		// this will not be transpiled because the method will be erased
		javax.activity.InvalidActivityException exception = null;
	}

}
