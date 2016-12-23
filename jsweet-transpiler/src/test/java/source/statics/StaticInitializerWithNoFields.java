package source.statics;

import static jsweet.util.Globals.$export;

public class StaticInitializerWithNoFields {

	static {
		$export("ok", true);
	}

}

