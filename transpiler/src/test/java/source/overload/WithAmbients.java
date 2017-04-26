package source.overload;

import def.test.AmbientWithOverload;
import jsweet.lang.Ambient;

@Ambient
class AmbientWithOverload2 {
	public native void mm(String s);

	public native void mm(String s, int i);
}

public class WithAmbients {

	void m(AmbientWithOverload a, AmbientWithOverload2 a2) {
		a.m("a", 100);
		a2.mm("b", 200);
	}

}
