package source.structural;

public class JSNI {

	int i;
	
	public native int m(int a, int b) /*-{
		var c = a + b;
		return c;
	}-*/;

	public native int m2(int a, 
			int b, 
			int c) /*-{
	    var c = a + this.@JSNI::i;
	    return c;
    }-*/;

	public native int m3() 
	/*-{
	    var c = 2;
	    return this.@JSNI::m(*)(1, c);
    }-*/;
	
}

