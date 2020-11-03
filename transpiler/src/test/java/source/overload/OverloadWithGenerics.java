package source.overload;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.array;

public class OverloadWithGenerics {
	public static String[] trace = {};

	public static void main(String[] args) {
        new WithGenerics2<String, Integer>().m("test", "test");
        new WithGenerics2<String, Integer>().m("test", 2, "2", true);
        

        OverloadWithGenerics.<String>m("test");
        m("test", 2);
        
		$export("trace", trace);
	}

	static <U> void m(U u) {
        array(OverloadWithGenerics.trace).push("2.1");
    }
    
	static <U, U2> void m(U u, U2 u2) {
        array(OverloadWithGenerics.trace).push("2.2");
    }
}

class WithGenerics2<T1, T2 extends Number> {

	<U> void m(T1 t, U u) {
        array(OverloadWithGenerics.trace).push("1");
    }
	
	<U, U2> void m(T1 t, U u, U2 u2, boolean w) {
        array(OverloadWithGenerics.trace).push("2");
    }

}
