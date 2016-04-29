package source.overload;

import static jsweet.util.Globals.$export;

public class OverloadWithStaticAndInstanceMethods {

	public static void main(String[] args) {
		m(new OverloadWithStaticAndInstanceMethods());
		new OverloadWithStaticAndInstanceMethods().m();
	}

	public static void m(OverloadWithStaticAndInstanceMethods o) {
		$export("static", true);
	}

	public void m() {
		$export("instance", true);
	}

}
