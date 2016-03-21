package source.structural;

public class JSNI {

	public native int m(int a, int b) /*-{
		var c = a + b;
		return c;
	}-*/;
	
}
